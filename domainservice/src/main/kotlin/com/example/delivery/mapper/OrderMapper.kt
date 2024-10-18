package com.example.delivery.mapper

import com.example.core.dto.request.UpdateOrderDTO
import com.example.core.dto.response.OrderDTO
import com.example.core.dto.response.OrderItemDTO
import com.example.core.dto.response.ShipmentDetailsDTO
import com.example.delivery.domain.DomainOrder
import com.example.delivery.mongo.MongoOrder
import org.springframework.data.mongodb.core.query.Update
import java.math.BigDecimal

object OrderMapper {

    fun DomainOrder.toDTO(): OrderDTO = OrderDTO(
        id,
        items.map { it.toDTO() },
        shipmentDetails.toDTO(),
        status.name,
        userId
    )

    fun MongoOrder.toDomain(): DomainOrder = DomainOrder(
        id.toString(),
        items = items?.map { it.toDomain() } ?: emptyList(),
        shipmentDetails = shipmentDetails?.toDomain() ?: DomainOrder.DomainShipmentDetails(),
        status = DomainOrder.Status.valueOf(status?.name ?: "UNKNOWN"),
        userId = userId.toString(),
    )

    fun ShipmentDetailsDTO.toMongoModel(): MongoOrder.MongoShipmentDetails =
        MongoOrder.MongoShipmentDetails(city, street, building, index)

    fun MongoOrder.MongoShipmentDetails.toDomain(): DomainOrder.DomainShipmentDetails =
        DomainOrder.DomainShipmentDetails(
            city ?: "",
            street ?: "",
            building ?: "",
            index ?: "",
        )

    fun MongoOrder.MongoOrderItem.toDomain(): DomainOrder.DomainOrderItem =
        DomainOrder.DomainOrderItem(
            productId = productId.toString(),
            price = price ?: BigDecimal.ZERO,
            amount = amount ?: 0
        )

    fun DomainOrder.DomainShipmentDetails.toDTO(): ShipmentDetailsDTO = ShipmentDetailsDTO(
        city,
        street,
        building,
        index
    )

    fun DomainOrder.DomainOrderItem.toDTO(): OrderItemDTO = OrderItemDTO(
        price,
        amount,
        productId
    )

    fun UpdateOrderDTO.toUpdate(): Update {
        val update = Update()
        shipmentDetails?.let { update.set(MongoOrder::shipmentDetails.name, it) }
        return update
    }
}
