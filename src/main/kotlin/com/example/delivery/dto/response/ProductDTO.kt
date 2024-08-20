package com.example.delivery.dto.response

import java.math.BigDecimal

data class ProductDTO(
    val id: String,
    val name: String,
    val price: BigDecimal,
    val amount: Int,
    val measurement: String
)
