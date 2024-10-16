package com.example.delivery.dto.request

import java.math.BigDecimal

data class UpdateProductDTO(
    val name: String?,
    val price: BigDecimal?,
    val amountAvailable: Int?,
    val measurement: String?,
)
