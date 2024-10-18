package com.example.core.dto.response

data class OrderWithProductDTO(
    val id: String = "",
    val items: List<OrderItemWithProductDTO> = emptyList(),
    val shipmentDetails: ShipmentDetailsDTO = ShipmentDetailsDTO(),
    val status: String = "",
    val userId: String = "",
)
