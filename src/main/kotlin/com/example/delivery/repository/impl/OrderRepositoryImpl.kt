package com.example.delivery.repository.impl

import com.example.delivery.mongo.MongoOrder
import com.example.delivery.repository.OrderRepository
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl(var mongoTemplate: MongoTemplate) : OrderRepository {
    private val className = MongoOrder::class.java

    override fun existsById(id: String): Boolean {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.exists(query, className)
    }

    //TODO: use aggregation to fetch all products
    override fun findById(id: String): MongoOrder? {
        return mongoTemplate.findById(id, className)
    }

    override fun save(order: MongoOrder): MongoOrder {
        return mongoTemplate.save(order)
    }

    override fun updateOrderStatus(id: String, status: MongoOrder.Status): MongoOrder? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        val update = Update.update("status", MongoOrder::status.name)
        return mongoTemplate.findAndModify(
            query, update, FindAndModifyOptions().returnNew(true), className
        )
    }

    override fun deleteById(id: String) {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        mongoTemplate.remove(query)
    }

    override fun findAll(id: String): List<MongoOrder> {
        val query = Query.query(Criteria.where(MongoOrder::userId.name).isEqualTo(id))
        return mongoTemplate.find(query, className)
    }
}
