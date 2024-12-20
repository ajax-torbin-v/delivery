package com.example.delivery.repository

import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.MongoProduct
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ProductRepository {
    fun findById(id: String): Mono<MongoProduct>
    fun save(product: MongoProduct): Mono<MongoProduct>
    fun deleteById(id: String): Mono<Unit>
    fun update(product: MongoProduct): Mono<MongoProduct>
    fun findAllByIds(productIds: List<String>): Flux<MongoProduct>
    fun updateProductsAmount(products: List<MongoOrder.MongoOrderItem>): Mono<Unit>
}
