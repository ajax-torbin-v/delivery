package com.example.delivery.mapper

import com.example.core.dto.request.UpdateOrderDTO
import com.example.core.dto.response.ShipmentDetailsDTO
import com.example.delivery.domain.DomainOrder
import com.example.delivery.mongo.MongoOrder
import org.springframework.data.mongodb.core.query.Update
import java.math.BigDecimal

object OrderMapper {

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

    fun MongoOrder.Status.toDomain(): DomainOrder.Status {
        return when (this) {
            MongoOrder.Status.NEW -> DomainOrder.Status.NEW
            MongoOrder.Status.SHIPPING -> DomainOrder.Status.SHIPPING
            MongoOrder.Status.COMPLETED -> DomainOrder.Status.COMPLETED
            MongoOrder.Status.CANCELED -> DomainOrder.Status.CANCELED
        }
    }

    fun UpdateOrderDTO.toUpdate(): Update {
        val update = Update()
        shipmentDetails?.let { update.set(MongoOrder::shipmentDetails.name, it) }
        return update
    }
}
