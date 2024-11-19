package com.example.domainservice.product.infrastructure.mongo

import com.example.domainservice.product.application.port.output.ProductRepositoryOutputPort
import com.example.domainservice.product.domain.DomainProduct
import com.example.domainservice.product.infrastructure.mongo.entity.MongoProduct
import com.example.domainservice.product.infrastructure.mongo.mapper.ProductMapper.toDomain
import com.example.domainservice.product.infrastructure.mongo.mapper.ProductMapper.toMongo
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
class MongoProductRepository(private val mongoTemplate: ReactiveMongoTemplate) : ProductRepositoryOutputPort {

    override fun update(product: DomainProduct): Mono<DomainProduct> {
        return mongoTemplate.save(product.toMongo()).map { it.toDomain() }
    }

    override fun findAllByIds(productIds: List<String>): Flux<DomainProduct> {
        val query = Query(Criteria.where("_id").`in`(productIds))
        return mongoTemplate.find<MongoProduct>(query).map { it.toDomain() }
    }

    override fun updateProductsAmount(products: Map<String, Int>): Mono<Unit> {
        val bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, MongoProduct::class.java)
        products.forEach { (productId, requestedAmount) ->
            val query = Query(Criteria.where("_id").isEqualTo(productId))
            val update = Update().inc("amountAvailable", -requestedAmount)
            bulkOperations.updateOne(query, update)
        }
        return bulkOperations.execute().thenReturn(Unit)
    }

    override fun findById(id: String): Mono<DomainProduct> {
        return mongoTemplate.findById<MongoProduct>(id).map { it.toDomain() }
    }

    override fun save(product: DomainProduct): Mono<DomainProduct> {
        return mongoTemplate.save(product.toMongo()).map { it.toDomain() }
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findAndRemove<MongoProduct>(query).thenReturn(Unit)
    }
}
