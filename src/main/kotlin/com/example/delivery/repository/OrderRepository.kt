package com.example.delivery.repository

import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.projection.MongoOrderWithProduct
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository {
    fun existsById(id: String): Boolean
    fun findById(id: String): MongoOrderWithProduct?
    fun save(order: MongoOrder): MongoOrder
    fun updateOrderStatus(id: String, status: MongoOrder.Status): MongoOrder?
    fun deleteById(id: String)
    fun updateOrder(id: String, update: Update): MongoOrder?
    fun findAllByUserId(userId: String): List<MongoOrder>
}
