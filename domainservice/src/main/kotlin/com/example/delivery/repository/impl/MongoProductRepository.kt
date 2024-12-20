package com.example.delivery.repository.impl

import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.MongoProduct
import com.example.delivery.repository.ProductRepository
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findAndRemove
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
internal class MongoProductRepository(private val mongoTemplate: ReactiveMongoTemplate) : ProductRepository {

    override fun update(product: MongoProduct): Mono<MongoProduct> {
        return mongoTemplate.save(product)
    }

    override fun findAllByIds(productIds: List<String>): Flux<MongoProduct> {
        val query = Query(Criteria.where("_id").`in`(productIds))
        return mongoTemplate.find<MongoProduct>(query)
    }

    override fun updateProductsAmount(products: List<MongoOrder.MongoOrderItem>): Mono<Unit> {
        val bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MongoProduct::class.java)
        products.forEach { (productId, _, requestedAmount) ->
            val query = Query(Criteria.where("_id").isEqualTo(productId))
            val update = Update().inc("amountAvailable", -requestedAmount!!)
            bulkOperations.updateOne(query, update)
        }
        return bulkOperations.execute().thenReturn(Unit)
    }

    override fun findById(id: String): Mono<MongoProduct> {
        return mongoTemplate.findById<MongoProduct>(id)
    }

    override fun save(product: MongoProduct): Mono<MongoProduct> {
        return mongoTemplate.save(product)
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findAndRemove<MongoProduct>(query).thenReturn(Unit)
    }
}
