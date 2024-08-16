package com.example.delivery.dto.request

import com.example.delivery.model.MongoOrder

data class CreateOrderDTO(
    val items: Map<String, Int>,
    val shipmentDetails: MongoOrder.ShipmentDetails
) {

}
