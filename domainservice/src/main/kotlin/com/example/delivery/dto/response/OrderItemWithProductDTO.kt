package com.example.delivery.dto.response

import java.math.BigDecimal

data class OrderItemWithProductDTO(
    val product: ProductDTO,
    val price: BigDecimal,
    val amount: Int,
)
