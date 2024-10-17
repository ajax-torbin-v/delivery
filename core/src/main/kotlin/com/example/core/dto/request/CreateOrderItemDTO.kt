package com.example.core.dto.request

data class CreateOrderItemDTO(
    val productId: String,
    val amount: Int,
)
