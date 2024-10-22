package com.example.core.dto.response

data class OrderDTO(
    val id: String,
    val items: List<OrderItemDTO>,
    val shipmentDetails: ShipmentDetailsDTO,
    val status: String,
    val userId: String,
)
