package com.example.delivery.dto.request

import java.math.BigDecimal

data class CreateProductDTO(
    val name: String,
    val price: BigDecimal,
    val amount: Int,
    val measurement: String
)
