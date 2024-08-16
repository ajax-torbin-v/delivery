package com.example.delivery.repository.impl

import com.example.delivery.model.MongoProduct
import com.example.delivery.repository.ProductRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(val mongoTemplate: MongoTemplate) : ProductRepository {
    private val className = MongoProduct::class.java
    override fun existsById(id: String): Boolean {
        val query = Query.query(Criteria.where("_id").`is`(id))
        return mongoTemplate.exists(query, className)
    }

    override fun findById(id: String): MongoProduct? = mongoTemplate.findById(id, className)

    override fun findAll(): List<MongoProduct> {
        return mongoTemplate.findAll(className)
    }

    override fun save(product: MongoProduct): MongoProduct {
        return mongoTemplate.save(product)
    }

    override fun deleteById(id: String) {
        val query = Query().addCriteria(Criteria.where("_id").`is`(id))
        mongoTemplate.remove(query)
    }

    override fun findByName(name: String): MongoProduct? {
        val query = Query().addCriteria(Criteria.where("name").`is`(name))
        return mongoTemplate.findOne(query, className)
    }
}