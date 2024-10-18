package com.example.core.dto.request

import com.example.core.dto.response.ShipmentDetailsDTO

data class UpdateOrderDTO(
    val shipmentDetails: ShipmentDetailsDTO?,
)
