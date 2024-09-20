package com.example.delivery.repository.impl

import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.MongoProduct
import com.example.delivery.repository.ProductRepository
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(val mongoTemplate: MongoTemplate) : ProductRepository {
    private val className = MongoProduct::class.java

    override fun existsById(id: String): Boolean {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.exists(query, className)
    }

    override fun update(id: String, update: Update): MongoProduct? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), className)
    }

    override fun updateProductsAmount(products: List<MongoOrder.MongoOrderItem>) {
        val bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, className)
        products.forEach { (productId, _, requestedAmount) ->
            val query = Query(Criteria.where("_id").isEqualTo(productId))
            val update = Update().inc("amountAvailable", -requestedAmount!!)
            bulkOperations.updateOne(query, update)
        }

        bulkOperations.execute()
    }

    override fun findById(id: String): MongoProduct? {
        return mongoTemplate.findById(id, className)
    }

    override fun findAll(): List<MongoProduct> {
        return mongoTemplate.findAll(className)
    }

    override fun save(product: MongoProduct): MongoProduct {
        return mongoTemplate.save(product)
    }

    override fun deleteById(id: String) {
        val query = Query(Criteria.where("_id").isEqualTo(id))
        mongoTemplate.findAndRemove(query, className)
    }
}
