package com.example.core.dto.response

import java.math.BigDecimal

data class OrderItemDTO(
    val price: BigDecimal,
    val amount: Int,
    val productId: String,
)
