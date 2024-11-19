package com.example.domainservice.order.infrastructure.nats.mapper

import com.example.commonmodels.order.Order
import com.example.commonmodels.order.Order.Status
import com.example.commonmodels.order.OrderItem
import com.example.commonmodels.order.ShipmentDetails
import com.example.commonmodels.product.Product
import com.example.core.exception.OrderNotFoundException
import com.example.core.exception.ProductAmountException
import com.example.core.exception.ProductNotFoundException
import com.example.core.exception.UserNotFoundException
import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.domain.projection.DomainOrderWithProduct
import com.example.internal.input.reqreply.order.CreateOrderRequest
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.DeleteOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.UpdateOrderRequest
import com.example.internal.input.reqreply.order.UpdateOrderResponse
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse

object OrderProtoMapper {
    fun UpdateOrderRequest.toDomain(): DomainOrder {
        return DomainOrder(
            id = id,
            items = emptyList(),
            shipmentDetails = shipmentDetails.toDomain(),
            status = DomainOrder.Status.UNKNOWN,
            userId = "",
        )
    }

    fun DomainOrder.toCreateOrderResponse(): CreateOrderResponse {
        return CreateOrderResponse.newBuilder().also {
            it.successBuilder.order = this.toProto()
        }.build()
    }

    fun DomainOrder.toUpdateOrderResponse(): UpdateOrderResponse {
        return UpdateOrderResponse.newBuilder().also {
            it.successBuilder.order = this.toProto()
        }.build()
    }

    fun DomainOrder.toUpdateOrderStatusResponse(): UpdateOrderStatusResponse {
        return UpdateOrderStatusResponse.newBuilder().also {
            it.successBuilder.order = this.toProto()
        }.build()
    }

    fun DomainOrder.toFindOrderByIdResponse(): FindOrderByIdResponse {
        return FindOrderByIdResponse.newBuilder().apply {
            successBuilder.orderBuilder.also {
                it.id = id
                it.status = status.toProto()
                it.userId = userId
                it.shipmentDetails = shipmentDetails.toShipmentDetails()
                it.addAllItems(items.map { item -> item.toOrderItem() })
            }
        }.build()
    }

    fun DomainOrderWithProduct.toFindOrderByIdResponse(): FindOrderByIdResponse {
        return FindOrderByIdResponse.newBuilder().apply {
            successBuilder.orderBuilder.also {
                it.id = id
                it.status = status.toProto()
                it.userId = userId
                it.shipmentDetails = shipmentDetails.toShipmentDetails()
                it.addAllItems(items.map { item -> item.toOrderItemFull() })
            }
        }.build()
    }

    fun CreateOrderRequest.toDomain(): DomainOrder {
        return DomainOrder(
            id = null,
            items = itemsList.map { it.toDomain() },
            shipmentDetails = shipmentDetails.toDomain(),
            userId = userId,
            status = DomainOrder.Status.NEW
        )
    }

    fun Throwable.toFailureFindOrderByIdResponse(): FindOrderByIdResponse {
        return FindOrderByIdResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
            when (this@toFailureFindOrderByIdResponse) {
                is OrderNotFoundException -> failureBuilder.orderNotFoundBuilder
            }
        }.build()
    }

    fun Throwable.toFailureUpdateOrderResponse(): UpdateOrderResponse {
        return UpdateOrderResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
            when (this@toFailureUpdateOrderResponse) {
                is OrderNotFoundException -> failureBuilder.orderNotFoundBuilder
            }
        }.build()
    }

    fun Throwable.toFailureUpdateStatusOrderResponse(): UpdateOrderStatusResponse {
        return UpdateOrderStatusResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
            when (this@toFailureUpdateStatusOrderResponse) {
                is OrderNotFoundException -> failureBuilder.orderNotFoundBuilder
            }
        }.build()
    }

    fun Throwable.toFailureDeleteOrderResponse(): DeleteOrderResponse {
        return DeleteOrderResponse.newBuilder().also {
            it.failureBuilder.setMessage(message.orEmpty())
        }.build()
    }

    fun Throwable.toFailureFindOrdersByUserIdResponse(): FindOrdersByUserIdResponse {
        return FindOrdersByUserIdResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
            when (this@toFailureFindOrdersByUserIdResponse) {
                is UserNotFoundException -> failureBuilder.userNotFoundBuilder
            }
        }.build()
    }

    fun Throwable.toFailureCreateOrderResponse(): CreateOrderResponse {
        return CreateOrderResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
            when (this@toFailureCreateOrderResponse) {
                is ProductNotFoundException -> failureBuilder.productNotFoundBuilder
                is UserNotFoundException -> failureBuilder.userNotFoundBuilder
                is ProductAmountException -> failureBuilder.productNotSufficientAmountBuilder
            }
        }.build()
    }

    fun toDeleteOrderResponse(): DeleteOrderResponse {
        return DeleteOrderResponse.newBuilder().also {
            it.successBuilder
        }.build()
    }

    fun OrderItem.toDomain(): DomainOrder.DomainOrderItem {
        return DomainOrder.DomainOrderItem(productId = productId, amount = amount)
    }

    fun DomainOrder.DomainShipmentDetails.toShipmentDetails(): ShipmentDetails {
        return ShipmentDetails.newBuilder().also {
            it.city = city
            it.street = street
            it.building = building
            it.index = index
        }.build()
    }

    fun DomainOrderWithProduct.DomainOrderItemWithProduct.toOrderItemFull(): OrderItem {
        return OrderItem.newBuilder().also { builder ->
            builder.price = price.toString()
            builder.amount = amount
            builder.productFull = Product.newBuilder().also {
                it.id = product.id
                it.name = product.name
                it.price = product.price.toPlainString()
                it.amount = product.amountAvailable
                it.measurement = product.measurement
            }.build()
        }.build()
    }

    fun DomainOrder.DomainOrderItem.toOrderItem(): OrderItem {
        return OrderItem.newBuilder().also {
            it.productId = productId
            it.amount = amount
            it.price = price.toString()
        }.build()
    }

    fun toFindOrdersByUserIdResponse(orders: List<DomainOrder>): FindOrdersByUserIdResponse {
        return FindOrdersByUserIdResponse.newBuilder().apply {
            successBuilder.addAllOrder(orders.map { it.toProto() })
        }.build()
    }

    fun ShipmentDetails.toDomain(): DomainOrder.DomainShipmentDetails {
        return DomainOrder.DomainShipmentDetails(city, street, building, index)
    }

    fun DomainOrder.toProto(): Order {
        return Order.newBuilder().also {
            it.id = id
            it.status = status.toProto()
            it.userId = userId
            it.shipmentDetails = shipmentDetails.toShipmentDetails()
            it.addAllItems(items.map { item -> item.toOrderItem() })
        }.build()
    }

    fun DomainOrder.Status.toProto(): Status {
        return when (this) {
            DomainOrder.Status.NEW -> Status.STATUS_NEW
            DomainOrder.Status.SHIPPING -> Status.STATUS_SHIPPING
            DomainOrder.Status.COMPLETED -> Status.STATUS_COMPLETED
            DomainOrder.Status.CANCELED -> Status.STATUS_CANCELED
            DomainOrder.Status.UNKNOWN -> Status.STATUS_UNKNOWN
        }
    }
}
