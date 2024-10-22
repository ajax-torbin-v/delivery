package com.example.delivery.mapper

import com.example.core.dto.request.CreateOrderDTO
import com.example.core.dto.request.CreateOrderItemDTO
import com.example.core.dto.request.UpdateOrderDTO
import com.example.core.dto.response.ShipmentDetailsDTO
import com.example.core.exception.OrderNotFoundException
import com.example.core.exception.ProductAmountException
import com.example.core.exception.ProductNotFoundException
import com.example.core.exception.UserNotFoundException
import com.example.delivery.domain.DomainOrder
import com.example.delivery.domain.projection.DomainOrderWithProduct
import com.example.delivery.mapper.ProductProtoMapper.toProto
import com.example.internal.commonmodels.order.Order
import com.example.internal.commonmodels.order.OrderItem
import com.example.internal.commonmodels.order.ShipmentDetails
import com.example.internal.input.reqreply.order.CreateOrderRequest
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.DeleteOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.UpdateOrderRequest
import com.example.internal.input.reqreply.order.UpdateOrderResponse
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse

object OrderProtoMapper {
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

    fun DomainOrderWithProduct.toFindOrderByIdResponse(): FindOrderByIdResponse {
        return FindOrderByIdResponse.newBuilder().apply {
            successBuilder.orderBuilder.also {
                it.id = id
                it.status = Order.Status.valueOf("STATUS_$status")
                it.userId = userId
                it.shipmentDetails = shipmentDetails.toShipmentDetails()
                it.addAllItems(items.map { item -> item.toOrderItemFull() })
            }
        }.build()
    }

    fun CreateOrderRequest.toCreateOrderDTO(): CreateOrderDTO {
        return CreateOrderDTO(itemsList.map { it.toCreateOrderItemDTO() }, shipmentDetails.toDTO(), userId)
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

    fun UpdateOrderRequest.toUpdateOrderDTO(): UpdateOrderDTO {
        return UpdateOrderDTO(shipmentDetails.toDTO())
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

    fun OrderItem.toCreateOrderItemDTO(): CreateOrderItemDTO {
        return CreateOrderItemDTO(productId, amount)
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
        return OrderItem.newBuilder().also {
            it.price = price.toString()
            it.amount = amount
            it.productFull = product.toProto()
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

    fun ShipmentDetails.toDTO(): ShipmentDetailsDTO {
        return ShipmentDetailsDTO(city, street, building, index)
    }

    fun DomainOrder.toProto(): Order {
        return Order.newBuilder().also {
            it.id = id
            it.status = Order.Status.valueOf("STATUS_${status.name}")
            it.userId = userId
            it.shipmentDetails = shipmentDetails.toShipmentDetails()
            it.addAllItems(items.map { item -> item.toOrderItem() })
        }.build()
    }
}
