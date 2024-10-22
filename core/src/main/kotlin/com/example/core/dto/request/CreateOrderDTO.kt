package com.example.core.dto.request

import com.example.core.dto.response.ShipmentDetailsDTO

data class CreateOrderDTO(
    val items: List<CreateOrderItemDTO>,
    val shipmentDetails: ShipmentDetailsDTO,
    val userId: String,
)
