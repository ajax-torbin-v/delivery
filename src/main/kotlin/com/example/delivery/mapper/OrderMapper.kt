package com.example.delivery.mapper

import com.example.delivery.domain.DomainOrder
import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.response.OrderDTO
import com.example.delivery.dto.response.ShipmentDetailsDTO
import com.example.delivery.mongo.MongoOrder
import org.bson.types.ObjectId
import java.math.BigDecimal

object OrderMapper {
    fun CreateOrderDTO.toMongo(): MongoOrder = MongoOrder(
        items = items.mapKeys { ObjectId(it.key) },
        shipmentDetails = shipmentDetails.toModel(),
        userId = ObjectId(userId),
    )

    fun DomainOrder.toDTO(): OrderDTO = OrderDTO(
        id.toHexString(),
        items.mapKeys { it.key.toString() },
        totalPrice,
        shipmentDetails.toDTO(),
        status,
        id.toHexString(),
    )

    fun DomainOrder.toMongo(): MongoOrder = MongoOrder(
        id, items, totalPrice, shipmentDetails, MongoOrder.Status.valueOf(status)
    )

    fun MongoOrder.toDomain(): DomainOrder = DomainOrder(
        id!!,
        items = items ?: emptyMap(),
        totalPrice = totalPrice ?: BigDecimal.ZERO,
        shipmentDetails = shipmentDetails ?: MongoOrder.MongoShipmentDetails(),
        status = status?.name ?: "UNKNOWN",
        userId = userId,
    )

    fun ShipmentDetailsDTO.toModel(): MongoOrder.MongoShipmentDetails =
        MongoOrder.MongoShipmentDetails(city, street, building, index)

    fun MongoOrder.MongoShipmentDetails.toDTO(): ShipmentDetailsDTO = ShipmentDetailsDTO(
        city ?: "none",
        street ?: "none",
        building ?: "none",
        index ?: "none",
    )
}
