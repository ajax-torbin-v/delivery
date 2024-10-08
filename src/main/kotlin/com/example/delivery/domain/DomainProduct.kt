package com.example.delivery.domain

import java.math.BigDecimal

data class DomainProduct(
    val id: String,
    val name: String,
    val price: BigDecimal,
    val amountAvailable: Int,
    val measurement: String,
)
