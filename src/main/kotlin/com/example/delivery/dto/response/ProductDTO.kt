package com.example.delivery.dto.response

data class ProductDTO(
    val id: String,
    val name: String,
    val price: Double,
    val amount: Int,
    val measurement: String) {
}
