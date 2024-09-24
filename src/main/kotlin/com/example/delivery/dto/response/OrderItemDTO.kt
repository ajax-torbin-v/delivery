package com.example.delivery.dto.response

import java.math.BigDecimal

data class OrderItemDTO(
    val price: BigDecimal,
    val amount: Int,
    val productId: String,
)
