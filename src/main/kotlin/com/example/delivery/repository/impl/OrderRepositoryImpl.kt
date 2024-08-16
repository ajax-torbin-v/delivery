package com.example.delivery.repository.impl

import com.example.delivery.model.MongoOrder
import com.example.delivery.repository.OrderRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl(var mongoTemplate: MongoTemplate) : OrderRepository {
    private val className = MongoOrder::class.java
    override fun existsById(id: String): Boolean {
        val query = Query.query(Criteria.where("_id").`is`(id))
        return mongoTemplate.exists(query, className)
    }

    //TODO: use aggregation to fetch all products
    override fun findById(id: String): MongoOrder? = mongoTemplate.findById(id, className)

    override fun save(order: MongoOrder): MongoOrder {
        return mongoTemplate.save(order)
    }

    override fun updateOrderStatus(id: String, status: MongoOrder.Status) {
        val query = Query.query(Criteria.where("_id").`is`(id))
        val update = Update.update("status", status)
        mongoTemplate.updateFirst(query, update, className)
    }

    override fun deleteById(id: String) {
        val query = Query.query(Criteria.where("_id").`is`(id))
        mongoTemplate.remove(query)
    }

}