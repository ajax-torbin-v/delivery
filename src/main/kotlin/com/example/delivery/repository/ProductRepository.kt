package com.example.delivery.repository


import com.example.delivery.mongo.MongoProduct
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository {
    fun findById(id: String): MongoProduct?
    fun findAll(): List<MongoProduct>
    fun findByName(name: String): MongoProduct?
    fun save(product: MongoProduct): MongoProduct
    fun deleteById(id: String)
    fun existsById(id: String): Boolean
    fun updateAmount(id: String, amount: Int): MongoProduct
}
