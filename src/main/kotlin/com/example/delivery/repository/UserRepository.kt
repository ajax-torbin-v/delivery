package com.example.delivery.repository

import com.example.delivery.mongo.MongoUser
import org.springframework.stereotype.Repository

@Repository
interface UserRepository {
    fun existsById(id: String): Boolean
    fun save(user: MongoUser): MongoUser
    fun findById(id: String): MongoUser?
    fun deleteById(id: String)
}
