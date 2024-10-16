package com.example.delivery.dto.request

import com.example.delivery.dto.response.ShipmentDetailsDTO

data class CreateOrderDTO(
    val items: List<CreateOrderItemDTO>,
    val shipmentDetails: ShipmentDetailsDTO,
    val userId: String,
)
