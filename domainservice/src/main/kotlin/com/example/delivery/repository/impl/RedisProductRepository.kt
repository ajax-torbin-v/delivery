package com.example.delivery.repository.impl

import com.example.core.exception.ProductNotFoundException
import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.MongoProduct
import com.example.delivery.repository.ProductRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.lettuce.core.RedisConnectionException
import org.slf4j.LoggerFactory
import org.springframework.dao.QueryTimeoutException
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.net.SocketException
import java.time.Duration

@SuppressWarnings("UnusedPrivateMember")
@Repository
internal class RedisProductRepository(
    val mongoProductRepository: MongoProductRepository,
    val reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>,
    val mapper: ObjectMapper,
) : ProductRepository by mongoProductRepository {

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackSave")
    override fun save(product: MongoProduct): Mono<MongoProduct> {
        return mongoProductRepository.save(product)
            .flatMap {
                reactiveRedisTemplate.opsForValue()
                    .set(createProductKey(it.id.toString()), mapper.writeValueAsBytes(it), TIMEOUT_DURATION)
                    .thenReturn(it)
            }
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackFindAllByIds")
    override fun findAllByIds(productIds: List<String>): Flux<MongoProduct> {
        return reactiveRedisTemplate.opsForValue()
            .multiGet(productIds.map { createProductKey(it) })
            .map { list -> list.filterNotNull().map { mapper.readValue<MongoProduct>(it) } }
            .flatMapMany { cachedProducts ->
                val cachedProductIds = cachedProducts.map { it.id.toString() }
                val nonCachedIds = productIds - cachedProductIds
                if (nonCachedIds.isEmpty()) {
                    cachedProducts.toFlux()
                } else {
                    mongoProductRepository.findAllByIds(nonCachedIds).mergeWith(cachedProducts.toFlux())
                }
            }
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackFindById")
    override fun findById(id: String): Mono<MongoProduct> {
        return reactiveRedisTemplate.opsForValue()
            .get(createProductKey(id))
            .handle { product, sink ->
                if (product.isEmpty()) {
                    sink.error(ProductNotFoundException("Product with id $id doesn't exist"))
                } else {
                    runCatching { mapper.readValue<MongoProduct>(product) }
                        .onSuccess(sink::next)
                        .onFailure(sink::error)
                }
            }.switchIfEmpty {
                mongoProductRepository.findById(id).writeToRedis(id)
            }
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackUpdate")
    override fun update(product: MongoProduct): Mono<MongoProduct> {
        return reactiveRedisTemplate.delete(createProductKey(product.id.toString()))
            .then(mongoProductRepository.update(product))
    }

    @SuppressWarnings("SpreadOperator")
    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackUpdateProductsAmount")
    override fun updateProductsAmount(products: List<MongoOrder.MongoOrderItem>): Mono<Unit> {
        return reactiveRedisTemplate.opsForValue()
            .multiGet(products.map { createProductKey(it.productId.toString()) })
            .map { list -> list.filterNotNull().map { mapper.readValue<MongoProduct>(it) } }
            .handle { item, sink ->
                if (item.isEmpty()) {
                    sink.complete()
                } else {
                    sink.next(item)
                }
            }
            .flatMap { productsList ->
                val keys = productsList.map { createProductKey(it.id.toString()) }
                reactiveRedisTemplate.delete(*keys.toTypedArray()).then(Mono.empty<Unit>())
            }.switchIfEmpty {
                mongoProductRepository.updateProductsAmount(products).then(Unit.toMono())
            }
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackDeleteById")
    override fun deleteById(id: String): Mono<Unit> {
        return reactiveRedisTemplate.opsForValue()
            .delete(id)
            .flatMap {
                mongoProductRepository.deleteById(id)
            }
    }

    fun Throwable.isRedisError(): Boolean {
        return this::class in hashSetOf(
            RedisConnectionException::class,
            QueryTimeoutException::class,
            SocketException::class
        )
    }

    fun fallbackSave(
        product: MongoProduct,
        e: Exception,
    ): Mono<MongoProduct> {
        log.error("Error while trying to execute save", e)
        return if (e.isRedisError()) {
            product.toMono()
        } else {
            Mono.error(e)
        }
    }

    fun fallbackFindById(
        id: String,
        e: Exception,
    ): Mono<MongoProduct> {
        log.error("Error while trying execute findById", e)
        return if (e.isRedisError()) {
            mongoProductRepository.findById(id)
        } else {
            Mono.error(e)
        }
    }

    fun fallbackFindAllByIds(
        productIds: List<String>,
        e: Exception,
    ): Flux<MongoProduct> {
        log.error("Error while trying to execute findAllByIds", e)
        return if (e.isRedisError()) {
            return mongoProductRepository.findAllByIds(productIds)
        } else {
            Flux.error(e)
        }
    }

    fun fallbackUpdate(product: MongoProduct, e: Exception): Mono<MongoProduct> {
        log.error("Error while trying to execute update", e)
        return if (e.isRedisError()) {
            return mongoProductRepository.update(product)
        } else {
            Mono.error(e)
        }
    }

    fun fallbackUpdateProductsAmount(
        products: List<MongoOrder.MongoOrderItem>,
        e: Exception,
    ): Mono<Unit> {
        log.error("Error while trying to execute updateProductsAmount", e)
        return if (e.isRedisError()) {
            return mongoProductRepository.updateProductsAmount(products)
        } else {
            Mono.error(e)
        }
    }

    fun fallbackDeleteById(
        id: String,
        e: Exception,
    ): Mono<Unit> {
        log.error("Error while trying to execute findAllByIds", e)
        return if (e.isRedisError()) {
            return mongoProductRepository.deleteById(id)
        } else {
            Mono.error(e)
        }
    }

    private fun Mono<MongoProduct>.writeToRedis(id: String): Mono<MongoProduct> {
        return this.flatMap { item ->
            reactiveRedisTemplate.opsForValue()
                .set(createProductKey(id), mapper.writeValueAsBytes(item), TIMEOUT_DURATION)
                .thenReturn(item)
        }.switchIfEmpty {
            reactiveRedisTemplate.opsForValue()
                .set(createProductKey(id), byteArrayOf(), SHORT_TIMEOUT_DURATION)
                .then(Mono.empty())
        }
    }

    private fun createProductKey(key: String): String = "$KEY_PREFIX:$key"

    companion object {
        private const val KEY_PREFIX = "product"
        private val log = LoggerFactory.getLogger(RedisProductRepository::class.java)
        private val TIMEOUT_DURATION = Duration.ofHours(1)
        private val SHORT_TIMEOUT_DURATION = Duration.ofMinutes(5)
    }
}
