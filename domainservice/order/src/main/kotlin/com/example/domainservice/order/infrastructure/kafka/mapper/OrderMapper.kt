package com.example.domainservice.order.infrastructure.kafka.mapper

import com.example.commonmodels.order.Order
import com.example.commonmodels.order.Order.Status
import com.example.commonmodels.order.OrderItem
import com.example.commonmodels.order.OrderStatusUpdateNotification
import com.example.commonmodels.order.ShipmentDetails
import com.example.core.exception.NotificationException
import com.example.domainservice.order.domain.DomainOrder
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import java.time.Instant

object OrderMapper {
    fun Order.toNotification(): OrderStatusUpdateNotification {
        return OrderStatusUpdateNotification.newBuilder().also {
            it.orderId = id
            it.userId = userId
            it.status = status.toNotificationStatus()
            it.timestampBuilder.seconds = Instant.now().epochSecond
        }.build()
    }

    fun Status.toNotificationStatus(): OrderStatusUpdateNotification.Status {
        return when (this) {
            Status.STATUS_UNSPECIFIED -> OrderStatusUpdateNotification.Status.STATUS_UNSPECIFIED
            Status.STATUS_NEW -> OrderStatusUpdateNotification.Status.STATUS_NEW
            Status.STATUS_SHIPPING -> OrderStatusUpdateNotification.Status.STATUS_SHIPPING
            Status.STATUS_COMPLETED -> OrderStatusUpdateNotification.Status.STATUS_COMPLETED
            Status.STATUS_CANCELED -> OrderStatusUpdateNotification.Status.STATUS_CANCELED
            Status.STATUS_UNKNOWN, Status.UNRECOGNIZED ->
                throw NotificationException("Couldn't send notification with status $this")
        }
    }

    fun DomainOrder.toUpdateOrderStatusResponse(): UpdateOrderStatusResponse {
        return UpdateOrderStatusResponse.newBuilder().also { builder ->
            builder.successBuilder.order = Order.newBuilder().also {
                it.id = id
                it.status = status.toProto()
                it.userId = userId
                it.shipmentDetails = shipmentDetails.toShipmentDetails()
                it.addAllItems(items.map { item -> item.toOrderItem() })
            }.build()
        }.build()
    }

    private fun DomainOrder.Status.toProto(): Status {
        return when (this) {
            DomainOrder.Status.NEW -> Status.STATUS_NEW
            DomainOrder.Status.SHIPPING -> Status.STATUS_SHIPPING
            DomainOrder.Status.COMPLETED -> Status.STATUS_COMPLETED
            DomainOrder.Status.CANCELED -> Status.STATUS_CANCELED
            DomainOrder.Status.UNKNOWN -> Status.STATUS_UNKNOWN
        }
    }

    private fun DomainOrder.DomainShipmentDetails.toShipmentDetails(): ShipmentDetails {
        return ShipmentDetails.newBuilder().also {
            it.city = city
            it.street = street
            it.building = building
            it.index = index
        }.build()
    }

    private fun DomainOrder.DomainOrderItem.toOrderItem(): OrderItem {
        return OrderItem.newBuilder().also {
            it.productId = productId
            it.amount = amount
            it.price = price.toString()
        }.build()
    }
}
