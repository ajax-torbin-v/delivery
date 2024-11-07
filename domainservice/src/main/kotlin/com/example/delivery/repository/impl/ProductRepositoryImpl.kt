package com.example.delivery.repository.impl

import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.MongoProduct
import com.example.delivery.repository.ProductRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.lettuce.core.RedisConnectionException
import org.slf4j.LoggerFactory
import org.springframework.dao.QueryTimeoutException
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findAndRemove
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.Duration


@Repository
internal class ProductRepositoryImpl(
    val mongoTemplate: ReactiveMongoTemplate,
    val reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>,
) : ProductRepository {
    private val mapper = jacksonObjectMapper()

    override fun save(product: MongoProduct): Mono<MongoProduct> {
        return mongoTemplate.save(product)
            .flatMap {
                reactiveRedisTemplate.opsForValue()
                    .set(it.id.toString(), mapper.writeValueAsBytes(it))
                    .thenReturn(it)
            }
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackFindAllByIds")
    override fun findAllByIds(productIds: List<String>): Flux<MongoProduct> {
        return reactiveRedisTemplate.opsForValue()
            .multiGet(productIds)
            .map { list -> list.filterNotNull().map { mapper.readValue<MongoProduct>(it) } }
            .flatMapMany { list ->
                if (list.size < productIds.size) {
                    val cachedProductIds = list.map { it.id.toString() }
                    val nonExistingIds = productIds.filter { cachedProductIds.contains(it) }
                    val query = Query(Criteria.where("_id").`in`(nonExistingIds))
                    mongoTemplate.find<MongoProduct>(query)
                        .flatMap {
                            reactiveRedisTemplate.opsForValue()
                                .set(it.id.toString(), mapper.writeValueAsBytes(it))
                                .subscribeOn(Schedulers.boundedElastic())
                                .thenReturn(it)
                        }.mergeWith(Flux.fromIterable(list))
                }
                Flux.fromIterable(list)
            }
    }


    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackFindById")
    override fun findById(id: String): Mono<MongoProduct> {
        return reactiveRedisTemplate.opsForValue()
            .get(id)
            .map { mapper.readValue<MongoProduct>(it) }
            .switchIfEmpty {
                mongoTemplate.findById<MongoProduct>(id)
                    .flatMap { product ->
                        reactiveRedisTemplate.opsForValue()
                            .set(id, mapper.writeValueAsBytes(product))
                            .thenReturn(product)
                    }
            }
    }

    override fun update(id: String, update: Update): Mono<MongoProduct> {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions.options().returnNew(true),
            MongoProduct::class.java
        )
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackUpdateProductsAmount")
    override fun updateProductsAmount(products: List<MongoOrder.MongoOrderItem>): Mono<Unit> {
        return reactiveRedisTemplate.opsForValue()
            .multiGet(products.map { it.productId.toString() })
            .flatMapMany { list -> Flux.fromIterable(list.filterNotNull().map { mapper.readValue<MongoProduct>(it) }) }
            .flatMap { item ->
                val neededAmount = products.find { it.productId == item.id }?.amount!!
                val currentAmount = item.amountAvailable!!
                val byteArray = mapper.writeValueAsBytes(item.copy(amountAvailable = currentAmount - neededAmount))
                val durationInSeconds = Duration.ofMinutes(60).seconds
                val script = """
                    local key = KEYS[1]
                    local value = unpack(ARGV)
                    redis.call("SET", key, value, "EX", tonumber(${durationInSeconds}))
                """
                reactiveRedisTemplate.execute(
                    RedisScript.of<Boolean>(script),
                    listOf(item.id.toString()),
                    listOf(byteArray)
                ).then(item.toMono())
            }.map {
                updateProductAmountInMongo(products)
            }.then(Unit.toMono())
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackDeleteById")
    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findAndRemove<MongoProduct>(query).thenReturn(Unit)
    }


    private fun Throwable.isRedisError(): Boolean {
        return this::class in setOf(
            RedisConnectionException::class,
            QueryTimeoutException::class
        )
    }

    private fun fallbackFindById(
        id: String,
        e: Exception,
    ): Mono<MongoProduct> {
        if (!e.isRedisError()) {
            log.error("Error while trying to call redis for findById", e)
        }
        return mongoTemplate.findById<MongoProduct>(id)
    }

    private fun fallbackFindAllByIds(
        productIds: List<String>,
        e: Exception,
    ): Flux<MongoProduct> {
        if (!e.isRedisError()) {
            log.error("Error while trying to call redis for findAllByIds", e)
        }
        val query = Query(Criteria.where("_id").`in`(productIds))
        return mongoTemplate.find<MongoProduct>(query)
    }

    private fun fallbackUpdateProductsAmount(
        products: List<MongoOrder.MongoOrderItem>,
        e: Exception,
    ): Mono<Unit> {
        if (!e.isRedisError()) {
            log.error("Error while trying to call redis for findAllByIds", e)
        }
        return updateProductAmountInMongo(products)
    }

    private fun updateProductAmountInMongo(products: List<MongoOrder.MongoOrderItem>): Mono<Unit> {
        val bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MongoProduct::class.java)
        products.forEach { (productId, _, requestedAmount) ->
            val query = Query(Criteria.where("_id").isEqualTo(productId))
            val update = Update().inc("amountAvailable", -requestedAmount!!)
            bulkOperations.updateOne(query, update)
        }
        return bulkOperations.execute().thenReturn(Unit)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ProductRepositoryImpl::class.java)
    }
}
