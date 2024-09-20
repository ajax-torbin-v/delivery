package com.example.delivery.dto.response

data class OrderWithProductDTO(
    val id: String,
    val items: List<OrderItemWithProductDTO>,
    val shipmentDetails: ShipmentDetailsDTO,
    val status: String,
    val userId: String,
)
