package com.example.gateway.mapper

import com.example.core.dto.request.CreateOrderDTO
import com.example.core.dto.request.CreateOrderItemDTO
import com.example.core.dto.request.UpdateOrderDTO
import com.example.core.dto.response.OrderDTO
import com.example.core.dto.response.OrderItemDTO
import com.example.core.dto.response.OrderItemWithProductDTO
import com.example.core.dto.response.OrderWithProductDTO
import com.example.core.dto.response.ShipmentDetailsDTO
import com.example.core.exception.OrderNotFoundException
import com.example.core.exception.ProductAmountException
import com.example.core.exception.ProductNotFoundException
import com.example.core.exception.UserNotFoundException
import com.example.gateway.mapper.ProductProtoMapper.toDTO
import com.example.internal.commonmodels.order.order.Order
import com.example.internal.commonmodels.order.order_item.OrderItem
import com.example.internal.commonmodels.order.shipment_details.ShipmentDetails
import com.example.internal.input.reqreply.order.create.CreateOrderRequest
import com.example.internal.input.reqreply.order.create.CreateOrderResponse
import com.example.internal.input.reqreply.order.delete.DeleteOrderResponse
import com.example.internal.input.reqreply.order.find.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.find_by_user_id.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.update.UpdateOrderRequest
import com.example.internal.input.reqreply.order.update.UpdateOrderResponse
import com.example.internal.input.reqreply.order.update_status.UpdateOrderStatusResponse
import java.math.BigDecimal

object OrderProtoMapper {

    @SuppressWarnings("ThrowsCount")
    fun CreateOrderResponse.toDTO(): OrderDTO {
        if (this == CreateOrderResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (hasFailure()) {
            when (failure.errorCase!!) {
                CreateOrderResponse.Failure.ErrorCase.USER_NOT_FOUND ->
                    throw UserNotFoundException(failure.message)

                CreateOrderResponse.Failure.ErrorCase.PRODUCT_NOT_FOUND ->
                    throw ProductNotFoundException(failure.message)

                CreateOrderResponse.Failure.ErrorCase.PRODUCT_NOT_SUFFICIENT_AMOUNT ->
                    throw ProductAmountException(
                        failure.message
                    )

                CreateOrderResponse.Failure.ErrorCase.ERROR_NOT_SET ->
                    error(failure.message)
            }
        }
        return success.order.toDTO()
    }

    fun FindOrderByIdResponse.toDtoWithProduct(): OrderWithProductDTO {
        if (this == FindOrderByIdResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (hasFailure()) {
            when (failure.errorCase!!) {
                FindOrderByIdResponse.Failure.ErrorCase.ORDER_NOT_FOUND ->
                    throw OrderNotFoundException(failure.message)

                FindOrderByIdResponse.Failure.ErrorCase.ERROR_NOT_SET ->
                    error(failure.message)
            }
        }
        return success.order.toDtoWithProduct()
    }

    @SuppressWarnings("ThrowsCount")
    fun UpdateOrderResponse.toDTO(): OrderDTO {
        if (this == UpdateOrderResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (hasFailure()) {
            when (failure.errorCase!!) {
                UpdateOrderResponse.Failure.ErrorCase.ORDER_NOT_FOUND -> throw OrderNotFoundException(failure.message)
                UpdateOrderResponse.Failure.ErrorCase.ERROR_NOT_SET -> throw IllegalStateException(failure.message)
            }
        }
        return success.order.toDTO()
    }

    fun UpdateOrderStatusResponse.toDTO(): OrderDTO {
        if (this == UpdateOrderStatusResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (hasFailure()) {
            when (failure.errorCase!!) {
                UpdateOrderStatusResponse.Failure.ErrorCase.ORDER_NOT_FOUND ->
                    throw OrderNotFoundException(failure.message)

                UpdateOrderStatusResponse.Failure.ErrorCase.ERROR_NOT_SET ->
                    error(failure.message)
            }
        }
        return success.order.toDTO()
    }

    fun FindOrdersByUserIdResponse.toDTO(): List<OrderDTO> {
        if (this == FindOrdersByUserIdResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (hasFailure()) {
            when (failure.errorCase!!) {
                FindOrdersByUserIdResponse.Failure.ErrorCase.USER_NOT_FOUND ->
                    throw UserNotFoundException(failure.message)

                FindOrdersByUserIdResponse.Failure.ErrorCase.ERROR_NOT_SET ->
                    error(failure.message)
            }
        }
        return success.orderList.map { it.toDTO() }
    }

    fun CreateOrderDTO.toCreateOrderRequest(): CreateOrderRequest {
        return CreateOrderRequest.newBuilder().also { builder ->
            builder.addAllItems(this.items.map { it.toOrderItem() })
                .setUserId(userId)
                .setShipmentDetails(shipmentDetails.toProto())
        }.build()
    }

    fun CreateOrderItemDTO.toOrderItem(): OrderItem {
        return OrderItem.newBuilder().also {
            it.setAmount(amount)
                .setProductId(productId)
        }.build()
    }

    fun DeleteOrderResponse.toDTO() {
        if (this == DeleteOrderResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (hasFailure()) {
            error(failure.message)
        }
    }

    fun updateOrderRequest(id: String, updateOrderDTO: UpdateOrderDTO): UpdateOrderRequest {
        return UpdateOrderRequest.newBuilder().also { builder ->
            builder.setId(id)
            updateOrderDTO.shipmentDetails?.let { shipment ->
                builder.setShipmentDetails(
                    ShipmentDetails.newBuilder().also { shipmentBuilder ->
                        shipment.city.let { shipmentBuilder.setCity(it) }
                        shipment.street.let { shipmentBuilder.setStreet(it) }
                        shipment.building.let { shipmentBuilder.setBuilding(it) }
                        shipment.index.let { shipmentBuilder.setIndex(it) }
                    }.build()
                )
            }
        }.build()
    }

    private fun Order.toDtoWithProduct(): OrderWithProductDTO {
        return OrderWithProductDTO(
            id,
            itemsList.map { it.toDtoWithProduct() },
            shipmentDetails.toDTO(),
            status,
            userId,
        )
    }

    fun OrderItem.toDtoWithProduct(): OrderItemWithProductDTO {
        return OrderItemWithProductDTO(productFull.toDTO(), BigDecimal(price), amount)
    }

    fun OrderItem.toDTO(): OrderItemDTO {
        return OrderItemDTO(BigDecimal(price), amount, productId)
    }

    fun Order.toDTO(): OrderDTO {
        return OrderDTO(id, itemsList.map { it.toDTO() }, shipmentDetails.toDTO(), status, userId)
    }

    private fun ShipmentDetails.toDTO(): ShipmentDetailsDTO {
        return ShipmentDetailsDTO(city, street, building, index)
    }

    private fun ShipmentDetailsDTO.toProto(): ShipmentDetails {
        return ShipmentDetails.newBuilder().also {
            it.setCity(city)
                .setStreet(street)
                .setBuilding(building)
                .setIndex(index)
        }.build()
    }
}
