package com.example.delivery.repository

import com.example.delivery.mongo.MongoOrder
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository {
    fun existsById(id: String): Boolean
    fun findById(id: String): MongoOrder?
    fun save(order: MongoOrder): MongoOrder
    fun updateOrderStatus(id: String, status: MongoOrder.Status): MongoOrder?
    fun deleteById(id: String)
    fun findAll(id: String): List<MongoOrder>
}
