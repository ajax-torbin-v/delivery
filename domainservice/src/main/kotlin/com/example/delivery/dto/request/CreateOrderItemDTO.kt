package com.example.delivery.dto.request

data class CreateOrderItemDTO(
    val productId: String,
    val amount: Int,
)
