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
import java.math.BigDecimal

object OrderProtoMapper {

    @SuppressWarnings("ThrowsCount")
    fun CreateOrderResponse.toDTO(): OrderDTO {
        return when (this.responseCase!!) {
            CreateOrderResponse.ResponseCase.SUCCESS -> success.order.toDTO()
            CreateOrderResponse.ResponseCase.FAILURE -> failure.asException()
            CreateOrderResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
        }
    }

    fun FindOrderByIdResponse.toDtoWithProduct(): OrderWithProductDTO {
        return when (this.responseCase!!) {
            FindOrderByIdResponse.ResponseCase.SUCCESS -> success.order.toDtoWithProduct()
            FindOrderByIdResponse.ResponseCase.FAILURE -> failure.asException()
            FindOrderByIdResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
        }
    }

    fun UpdateOrderResponse.toDTO(): OrderDTO {
        return when (this.responseCase!!) {
            UpdateOrderResponse.ResponseCase.SUCCESS -> success.order.toDTO()
            UpdateOrderResponse.ResponseCase.FAILURE -> failure.asException()
            UpdateOrderResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
        }
    }

    fun UpdateOrderStatusResponse.toDTO(): OrderDTO {
        return when (this.responseCase!!) {
            UpdateOrderStatusResponse.ResponseCase.SUCCESS -> success.order.toDTO()
            UpdateOrderStatusResponse.ResponseCase.FAILURE -> failure.asException()
            UpdateOrderStatusResponse.ResponseCase.RESPONSE_NOT_SET ->
                throw RuntimeException("Acquired message is empty!")
        }
    }

    fun FindOrdersByUserIdResponse.toDTO(): List<OrderDTO> {
        return when (this.responseCase!!) {
            FindOrdersByUserIdResponse.ResponseCase.SUCCESS -> success.orderList.map { it.toDTO() }
            FindOrdersByUserIdResponse.ResponseCase.FAILURE -> failure.asException()
            FindOrdersByUserIdResponse.ResponseCase.RESPONSE_NOT_SET ->
                throw RuntimeException("Acquired message is empty!")
        }
    }

    fun CreateOrderDTO.toCreateOrderRequest(): CreateOrderRequest {
        return CreateOrderRequest.newBuilder().also {
            it.addAllItems(items.map { item -> item.toOrderItem() })
            it.userId = userId
            it.shipmentDetails = shipmentDetails.toProto()
        }.build()
    }

    fun CreateOrderItemDTO.toOrderItem(): OrderItem {
        return OrderItem.newBuilder().also {
            it.amount = this.amount
            it.productId = this.productId
        }.build()
    }

    fun DeleteOrderResponse.toDTO() {
        return when (this.responseCase!!) {
            DeleteOrderResponse.ResponseCase.SUCCESS -> Unit
            DeleteOrderResponse.ResponseCase.FAILURE -> error(failure.message)
            DeleteOrderResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
        }
    }

    fun updateOrderRequest(id: String, updateOrderDTO: UpdateOrderDTO): UpdateOrderRequest {
        return UpdateOrderRequest.newBuilder().also { builder ->
            builder.id = id
            updateOrderDTO.shipmentDetails?.let { shipment ->
                builder.setShipmentDetails(
                    ShipmentDetails.newBuilder().also { shipmentBuilder ->
                        shipment.city.let { shipmentBuilder.city = it }
                        shipment.street.let { shipmentBuilder.street = it }
                        shipment.building.let { shipmentBuilder.building = it }
                        shipment.index.let { shipmentBuilder.index = it }
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
            status.toString().substringAfter("_"),
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
        return OrderDTO(
            id,
            itemsList.map { it.toDTO() },
            shipmentDetails.toDTO(),
            status.toString().substringAfter("_"),
            userId
        )
    }

    private fun ShipmentDetails.toDTO(): ShipmentDetailsDTO {
        return ShipmentDetailsDTO(city, street, building, index)
    }

    private fun ShipmentDetailsDTO.toProto(): ShipmentDetails {
        return ShipmentDetails.newBuilder().also {
            it.city = city
            it.street = street
            it.building = building
            it.index = index
        }.build()
    }

    private fun CreateOrderResponse.Failure.asException(): Nothing {
        throw when (errorCase!!) {
            CreateOrderResponse.Failure.ErrorCase.USER_NOT_FOUND -> UserNotFoundException(message)
            CreateOrderResponse.Failure.ErrorCase.PRODUCT_NOT_FOUND -> ProductNotFoundException(message)
            CreateOrderResponse.Failure.ErrorCase.PRODUCT_NOT_SUFFICIENT_AMOUNT -> ProductAmountException(message)
            CreateOrderResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(message)
        }
    }

    private fun FindOrderByIdResponse.Failure.asException(): Nothing {
        throw when (errorCase!!) {
            FindOrderByIdResponse.Failure.ErrorCase.ORDER_NOT_FOUND -> OrderNotFoundException(message)
            FindOrderByIdResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(message)
        }
    }

    private fun UpdateOrderResponse.Failure.asException(): Nothing {
        throw when (errorCase!!) {
            UpdateOrderResponse.Failure.ErrorCase.ORDER_NOT_FOUND -> OrderNotFoundException(message)
            UpdateOrderResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(message)
        }
    }

    private fun UpdateOrderStatusResponse.Failure.asException(): Nothing {
        throw when (errorCase!!) {
            UpdateOrderStatusResponse.Failure.ErrorCase.ORDER_NOT_FOUND -> OrderNotFoundException(message)
            UpdateOrderStatusResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(message)
        }
    }

    private fun FindOrdersByUserIdResponse.Failure.asException(): Nothing {
        throw when (errorCase!!) {
            FindOrdersByUserIdResponse.Failure.ErrorCase.USER_NOT_FOUND -> UserNotFoundException(message)
            FindOrdersByUserIdResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(message)
        }
    }
}
