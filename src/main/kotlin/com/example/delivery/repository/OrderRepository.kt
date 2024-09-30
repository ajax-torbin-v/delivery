package com.example.delivery.repository

import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.projection.MongoOrderWithProduct
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface OrderRepository {
    fun existsById(id: String): Mono<Boolean>
    fun findById(id: String): Mono<MongoOrderWithProduct>
    fun save(order: MongoOrder): Mono<MongoOrder>
    fun updateOrderStatus(id: String, status: MongoOrder.Status): Mono<MongoOrder>
    fun deleteById(id: String)
    fun updateOrder(id: String, update: Update): Mono<MongoOrder>
    fun findAllByUserId(userId: String): Flux<MongoOrder>
}
