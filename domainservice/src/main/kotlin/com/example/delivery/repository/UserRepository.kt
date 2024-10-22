package com.example.delivery.repository

import com.example.delivery.mongo.MongoUser
import org.springframework.data.mongodb.core.query.Update
import reactor.core.publisher.Mono

interface UserRepository {
    fun existsById(id: String): Mono<Boolean>
    fun save(user: MongoUser): Mono<MongoUser>
    fun findById(id: String): Mono<MongoUser>
    fun deleteById(id: String): Mono<Unit>
    fun update(id: String, update: Update): Mono<MongoUser>
}
