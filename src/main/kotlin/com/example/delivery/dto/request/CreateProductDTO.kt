package com.example.delivery.dto.request

import com.example.delivery.model.MongoProduct

data class CreateProductDTO(
    val name: String,
    val price: Double,
    val amount: Int,
    val measurement: String
) {
    fun toModel(): MongoProduct = MongoProduct(null, name, price, amount, measurement)
}