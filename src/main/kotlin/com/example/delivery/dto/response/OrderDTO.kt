package com.example.delivery.dto.response

import java.math.BigDecimal

data class OrderDTO(
    val id: String,
    val items: Map<String, Int>,
    val totalPrice: BigDecimal,
    val shipmentDetails: ShipmentDetailsDTO,
    val status: String,
    val userId: String,
)
