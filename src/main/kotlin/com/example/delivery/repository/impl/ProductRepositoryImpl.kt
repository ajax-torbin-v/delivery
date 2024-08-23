package com.example.delivery.repository.impl

import com.example.delivery.exception.NotFoundException
import com.example.delivery.mongo.MongoProduct
import com.example.delivery.repository.ProductRepository
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

    override fun updateAmount(id: String, amount: Int): MongoProduct {
        val query = Query(Criteria.where("_id").isEqualTo(id))
        val update = Update().inc("stock", amount)

        return mongoTemplate.findAndModify(query, update, className)
            ?: throw NotFoundException("Product with id $id doesn't exist")
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
        val query = Query().addCriteria(Criteria.where("_id").isEqualTo(id))
        mongoTemplate.remove(query)
    }

    override fun findByName(name: String): MongoProduct? {
        val query = Query().addCriteria(Criteria.where(MongoProduct::name.name).isEqualTo(name))
        return mongoTemplate.findOne(query, className)
    }
}
