package com.example.delivery.repository


import com.example.delivery.mongo.MongoProduct
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository {
    fun findById(id: String): MongoProduct?
    fun findAll(): List<MongoProduct>
    fun findByName(name: String): MongoProduct?
    fun save(product: MongoProduct): MongoProduct
    fun deleteById(id: String)
    fun existsById(id: String): Boolean
    fun update(id: String, update: Update): MongoProduct?
    fun updateProductsAmount(products: Map<String, Int>)
}
