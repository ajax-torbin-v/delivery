package com.example.delivery.repository


import com.example.delivery.mongo.MongoProduct
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository {
    fun findById(id: String): MongoProduct?
    fun findAll(): List<MongoProduct>
    fun save(product: MongoProduct): MongoProduct
    fun deleteById(id: String)
    fun findByName(name: String): MongoProduct?
    fun existsById(id: String): Boolean
}
