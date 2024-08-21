package com.example.delivery.dto.request

import com.example.delivery.dto.response.ShipmentDetailsDTO

data class CreateOrderDTO(
    val items: Map<String, Int>,
    val shipmentDetails: ShipmentDetailsDTO,
    val userId: String,
)
