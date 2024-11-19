package com.example.domainservice.order.infrastructure.mongo.mapper

import com.example.core.dto.request.UpdateOrderDTO
import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.domain.projection.DomainOrderWithProduct
import com.example.domainservice.order.infrastructure.mongo.entity.MongoOrder
import com.example.domainservice.order.infrastructure.mongo.entity.projection.MongoOrderWithProduct
import com.example.domainservice.product.infrastructure.mongo.mapper.ProductMapper.toDomain
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Update
import java.math.BigDecimal

object OrderMapper {
    fun MongoOrderWithProduct.toDomain(): DomainOrderWithProduct = DomainOrderWithProduct(
        id.toString(),
        items = items?.map { it.toDomain() } ?: emptyList(),
        shipmentDetails = shipmentDetails?.toDomain() ?: DomainOrder.DomainShipmentDetails(),
        status = status?.toDomain() ?: DomainOrder.Status.UNKNOWN,
        userId = userId.toString(),
    )

    fun MongoOrderWithProduct.MongoOrderItemWithProduct.toDomain(): DomainOrderWithProduct.DomainOrderItemWithProduct =
        DomainOrderWithProduct.DomainOrderItemWithProduct(
            product!!.toDomain(),
            price ?: BigDecimal.ZERO,
            amount ?: 0,
        )

    fun DomainOrder.toMongo(): MongoOrder = MongoOrder(
        id = id?.let { ObjectId(id) },
        items = items.map { it.toMongo() },
        shipmentDetails = shipmentDetails.toMongo(),
        status = status.toMongo(),
        userId = ObjectId(userId),
    )

    fun MongoOrder.toDomain(): DomainOrder = DomainOrder(
        id?.toString(),
        items = items?.map { it.toDomain() } ?: emptyList(),
        shipmentDetails = shipmentDetails?.toDomain() ?: DomainOrder.DomainShipmentDetails(),
        status = DomainOrder.Status.valueOf(status?.name ?: "UNKNOWN"),
        userId = userId.toString(),
    )

    fun DomainOrder.DomainShipmentDetails.toMongo(): MongoOrder.MongoShipmentDetails =
        MongoOrder.MongoShipmentDetails(city, street, building, index)

    fun MongoOrder.MongoShipmentDetails.toDomain(): DomainOrder.DomainShipmentDetails =
        DomainOrder.DomainShipmentDetails(
            city ?: "",
            street ?: "",
            building ?: "",
            index ?: "",
        )

    fun DomainOrder.DomainOrderItem.toMongo(): MongoOrder.MongoOrderItem =
        MongoOrder.MongoOrderItem(
            productId = ObjectId(productId),
            price = price,
            amount = amount
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

    fun DomainOrder.Status.toMongo(): MongoOrder.Status? {
        return when (this) {
            DomainOrder.Status.NEW -> MongoOrder.Status.NEW
            DomainOrder.Status.SHIPPING -> MongoOrder.Status.SHIPPING
            DomainOrder.Status.COMPLETED -> MongoOrder.Status.COMPLETED
            DomainOrder.Status.CANCELED -> MongoOrder.Status.CANCELED
            DomainOrder.Status.UNKNOWN -> null
        }
    }

    fun UpdateOrderDTO.toUpdate(): Update {
        val update = Update()
        shipmentDetails?.let { update.set(MongoOrder::shipmentDetails.name, it) }
        return update
    }
}
