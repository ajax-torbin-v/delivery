package com.example.delivery.mapper

import com.example.delivery.domain.DomainOrder
import com.example.delivery.domain.DomainProduct
import com.example.delivery.domain.projection.DomainOrderWithProduct
import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.request.CreateOrderItemDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.dto.response.ShipmentDetailsDTO
import com.example.delivery.exception.OrderNotFoundException
import com.example.delivery.exception.ProductAmountException
import com.example.delivery.exception.ProductNotFoundException
import com.example.delivery.exception.UserNotFoundException
import com.example.internal.commonmodels.Error
import com.example.internal.commonmodels.order.order.Order
import com.example.internal.commonmodels.order.order_item.OrderItem
import com.example.internal.commonmodels.order.shipment_details.ShipmentDetails
import com.example.internal.commonmodels.product.product.Product
import com.example.internal.input.reqreply.order.create.CreateOrderRequest
import com.example.internal.input.reqreply.order.create.CreateOrderResponse
import com.example.internal.input.reqreply.order.delete.DeleteOrderResponse
import com.example.internal.input.reqreply.order.find.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.find_by_user_id.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.update.UpdateOrderRequest
import com.example.internal.input.reqreply.order.update.UpdateOrderResponse
import com.example.internal.input.reqreply.order.update_status.UpdateOrderStatusResponse

object OrderProtoMapper {
    fun DomainOrder.toCreateOrderResponse(): CreateOrderResponse {
        return CreateOrderResponse.newBuilder().also { builder ->
            builder.successBuilder.orderBuilder.also { buildOrder(it) }
        }.build()
    }

    fun DomainOrder.toUpdateOrderResponse(): UpdateOrderResponse {
        return UpdateOrderResponse.newBuilder().also { builder ->
            builder.successBuilder.orderBuilder.also { buildOrder(it) }
        }.build()
    }

    fun DomainOrder.toUpdateOrderStatusResponse(): UpdateOrderStatusResponse {
        return UpdateOrderStatusResponse.newBuilder().also { builder ->
            builder.successBuilder.orderBuilder.also { buildOrder(it) }
        }.build()
    }

    fun DomainOrderWithProduct.toFindOrderByIdResponse(): FindOrderByIdResponse {
        return FindOrderByIdResponse.newBuilder()
            .also { builder ->
                builder.successBuilder.orderBuilder.also { protoBuilder ->
                    protoBuilder
                        .setId(id)
                        .setStatus(status)
                        .setUserId(userId)
                        .setShipmentDetails(shipmentDetails.toShipmentDetails())
                        .addAllItems(items.map { it.toOrderItemFull() })
                }
            }
            .build()
    }

    fun CreateOrderRequest.toCreateOrderDTO(): CreateOrderDTO {
        return CreateOrderDTO(itemsList.map { it.toCreateOrderItemDTO() }, shipmentDetails.toDTO(), userId)
    }

    fun Throwable.toFailureFindOrderByIdResponse(): FindOrderByIdResponse {
        return FindOrderByIdResponse.newBuilder().also {
            it.failureBuilder.setMessage(this.message.orEmpty())
            when (this) {
                is OrderNotFoundException -> it.failureBuilder.setOrderNotFound(Error.getDefaultInstance())
            }
        }.build()
    }

    fun Throwable.toFailureUpdateOrderResponse(): UpdateOrderResponse {
        return UpdateOrderResponse.newBuilder().also {
            it.failureBuilder.setMessage(this.message.toString())
            when (this) {
                is OrderNotFoundException -> it.failureBuilder.setOrderNotFound(Error.getDefaultInstance())
            }
        }.build()
    }

    fun Throwable.toFailureUpdateStatusOrderResponse(): UpdateOrderStatusResponse {
        return UpdateOrderStatusResponse.newBuilder().also {
            it.failureBuilder.setMessage(this.message.toString())
            when (this) {
                is OrderNotFoundException -> it.failureBuilder.setOrderNotFound(Error.getDefaultInstance())
            }
        }.build()
    }

    fun Throwable.toFailureDeleteOrderResponse(): DeleteOrderResponse {
        return DeleteOrderResponse.newBuilder().also {
            it.failureBuilder.setMessage(this.message.orEmpty())
        }.build()
    }

    fun Throwable.toFailureFindOrdersByUserIdResponse(): FindOrdersByUserIdResponse {
        return FindOrdersByUserIdResponse.newBuilder().also {
            it.failureBuilder.setMessage(this.message.orEmpty())
            when (this) {
                is UserNotFoundException -> it.failureBuilder.setUserNotFound(Error.getDefaultInstance())
            }
        }.build()
    }

    fun UpdateOrderRequest.toUpdateOrderDTO(): UpdateOrderDTO {
        return UpdateOrderDTO(shipmentDetails.toDTO())
    }

    fun Throwable.toFailureCreateOrderResponse(): CreateOrderResponse {
        return CreateOrderResponse.newBuilder().also {
            it.failureBuilder.setMessage(this.message.orEmpty())
            when (this) {
                is ProductNotFoundException -> it.failureBuilder.setProductNotFound(Error.getDefaultInstance())
                is UserNotFoundException -> it.failureBuilder.setUserNotFound(Error.getDefaultInstance())
                is ProductAmountException -> it.failureBuilder.setProductNotSufficientAmount(Error.getDefaultInstance())
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
        return ShipmentDetails.newBuilder()
            .setCity(city)
            .setStreet(street)
            .setBuilding(building)
            .setIndex(index)
            .build()
    }

    fun DomainOrderWithProduct.DomainOrderItemWithProduct.toOrderItemFull(): OrderItem {
        return OrderItem.newBuilder().also {
            it.setPrice(price.toString())
                .setAmount(amount)
                .setProductFull(product.toProtoProduct())
        }.build()
    }

    fun DomainOrder.DomainOrderItem.toOrderItem(): OrderItem {
        return OrderItem.newBuilder().also {
            it.setProductId(productId)
                .setAmount(amount)
                .setPrice(price.toString())
        }.build()
    }

    fun DomainProduct.toProtoProduct(): Product {
        return Product.newBuilder().also {
            it.setId(this.id)
                .setName(this.name)
                .setPrice(this.price.toString())
                .setAmount(this.amountAvailable)
                .setMeasurement(this.measurement)
        }.build()
    }

    fun toFindOrdersByUserIdResponse(orders: List<DomainOrder>): FindOrdersByUserIdResponse {
        return FindOrdersByUserIdResponse.newBuilder().also { responseBuilder ->
            val successBuilder = FindOrdersByUserIdResponse.Success.newBuilder()
            successBuilder.addAllOrder(orders.map { it.toProto() })
            responseBuilder.success = successBuilder.build()
        }.build()
    }

    fun DomainOrder.toProto(): Order {
        return Order.newBuilder().also { builder ->
            builder
                .addAllItems(this.items.map { it.toOrderItem() })
                .setId(id)
                .setStatus(status.toString())
                .setShipmentDetails(shipmentDetails.toShipmentDetails())
                .setUserId(userId)
        }.build()
    }

    fun ShipmentDetails.toDTO(): ShipmentDetailsDTO {
        return ShipmentDetailsDTO(city, street, building, index)
    }

    fun DomainOrder.buildOrder(protoBuilder: Order.Builder) {
        protoBuilder
            .setId(id)
            .setStatus(status.toString())
            .setUserId(userId)
            .setShipmentDetails(shipmentDetails.toShipmentDetails())
            .addAllItems(items.map { it.toOrderItem() })
    }
}
