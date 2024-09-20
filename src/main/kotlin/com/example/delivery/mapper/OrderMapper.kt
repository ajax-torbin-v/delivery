package com.example.delivery.mapper

import com.example.delivery.domain.DomainOrder
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.dto.response.OrderDTO
import com.example.delivery.dto.response.OrderItemDTO
import com.example.delivery.dto.response.ShipmentDetailsDTO
import com.example.delivery.mongo.MongoOrder
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Update
import java.math.BigDecimal

object OrderMapper {

    fun DomainOrder.toDTO(): OrderDTO = OrderDTO(
        id.toHexString(),
        items.map { it.toDTO() },
        shipmentDetails.toDTO(),
        status,
        userId.toHexString(),
    )

    fun MongoOrder.toDomain(): DomainOrder = DomainOrder(
        id!!,
        items = items?.map { it.toDomain() } ?: emptyList(),
        shipmentDetails = shipmentDetails?.toDomain() ?: DomainOrder.DomainShipmentDetails(),
        status = status?.name ?: "UNKNOWN",
        userId = userId!!,
    )

    fun ShipmentDetailsDTO.toMongo(): MongoOrder.MongoShipmentDetails =
        MongoOrder.MongoShipmentDetails(city, street, building, index)

    fun DomainOrder.DomainShipmentDetails.toMongo() = MongoOrder.MongoShipmentDetails(
        city, street, building, index
    )

    fun MongoOrder.MongoShipmentDetails.toDomain(): DomainOrder.DomainShipmentDetails =
        DomainOrder.DomainShipmentDetails(
            city ?: "none",
            street ?: "none",
            building ?: "none",
            index ?: "none",
        )

    fun MongoOrder.MongoOrderItem.toDomain(): DomainOrder.DomainOrderItem =
        DomainOrder.DomainOrderItem(
            productId = productId ?: ObjectId(),
            price = price ?: BigDecimal.ZERO,
            amount = amount ?: 0
        )

    fun DomainOrder.DomainShipmentDetails.toDTO(): ShipmentDetailsDTO = ShipmentDetailsDTO(
        city, street, building, index
    )

    fun DomainOrder.DomainOrderItem.toDTO(): OrderItemDTO = OrderItemDTO(
        price, amount, productId.toString()
    )

    fun UpdateOrderDTO.toUpdate(): Update {
        val update = Update()
        shipmentDetails?.let { update.set(MongoOrder::shipmentDetails.name, it) }
        return update
    }
}
