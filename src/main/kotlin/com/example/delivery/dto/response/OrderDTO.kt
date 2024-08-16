package com.example.delivery.dto.response

import com.example.delivery.model.MongoOrder

data class OrderDTO(
    val id: String,
    val items: Map<String, Int>, //TODO: Use List<Product> instead, fetched from a db
    val totalPrice: Double,
    val shipmentDetails: MongoOrder.ShipmentDetails,
    val status: MongoOrder.Status
) {

}
