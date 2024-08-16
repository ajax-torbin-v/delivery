package com.example.delivery.model

import com.example.delivery.dto.response.OrderDTO
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Order")
@Document(collection = MongoOrder.COLLECTION_NAME)
data class MongoOrder(
    @Id val id: String? = null,
    val items: Map<String, Int>?,
    val totalPrice: Double?,
    val shipmentDetails: ShipmentDetails?,
    val status: Status? = Status.NEW) {
    enum class Status {
        NEW, SHIPMENT, COMPLETED, CANCELED, UNKNOWN
    }
    companion object {
        const val COLLECTION_NAME = "order"
    }

    fun toDTO(): OrderDTO = OrderDTO(
        id ?: "none",
        items ?: emptyMap(),
        totalPrice?: 0.0,
        shipmentDetails?: ShipmentDetails(),
        status ?: Status.UNKNOWN)

    data class ShipmentDetails(
        val city: String = "none",
        val street: String = "none",
        val building: String = "none",
        val index: String = "none") {
    }
}
