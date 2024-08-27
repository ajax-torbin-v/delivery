package com.example.delivery.dto.request

import java.math.BigDecimal

data class UpdateProductDTO(
    val name: String? = null,
    val price: BigDecimal? = null,
    val amountAvailable: Int? = null,
    val measurement: String? = null,
)
