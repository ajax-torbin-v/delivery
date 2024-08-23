package com.example.delivery.domain

import org.bson.types.ObjectId
import java.math.BigDecimal

data class DomainProduct(
    val id: ObjectId,
    val name: String,
    val price: BigDecimal,
    val amountAvailable: Int,
    val measurement: String,
)

