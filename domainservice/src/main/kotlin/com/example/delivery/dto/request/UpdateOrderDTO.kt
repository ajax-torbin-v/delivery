package com.example.delivery.dto.request

import com.example.delivery.dto.response.ShipmentDetailsDTO

data class UpdateOrderDTO(
    val shipmentDetails: ShipmentDetailsDTO?,
)