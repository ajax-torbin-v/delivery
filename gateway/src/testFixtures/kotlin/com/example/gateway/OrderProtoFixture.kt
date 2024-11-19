package com.example.gateway

import com.example.commonmodels.order.Order
import com.example.commonmodels.order.OrderItem
import com.example.commonmodels.order.ShipmentDetails
import com.example.commonmodels.product.Product
import com.example.core.OrderFixture.randomAmount
import com.example.core.OrderFixture.randomBuilding
import com.example.core.OrderFixture.randomCity
import com.example.core.OrderFixture.randomIndex
import com.example.core.OrderFixture.randomOrderId
import com.example.core.OrderFixture.randomStreet
import com.example.core.OrderFixture.randomUpdateBuilding
import com.example.core.OrderFixture.randomUpdateCity
import com.example.core.OrderFixture.randomUpdateIndex
import com.example.core.OrderFixture.randomUpdateStreet
import com.example.core.ProductFixture.randomAmountAvailable
import com.example.core.ProductFixture.randomMeasurement
import com.example.core.ProductFixture.randomPrice
import com.example.core.ProductFixture.randomProductId
import com.example.core.ProductFixture.randomProductName
import com.example.core.UserFixture.randomUserId
import com.example.core.dto.request.CreateOrderDTO
import com.example.core.dto.request.CreateOrderItemDTO
import com.example.core.dto.request.UpdateOrderDTO
import com.example.core.dto.response.OrderDTO
import com.example.core.dto.response.OrderItemDTO
import com.example.core.dto.response.ShipmentDetailsDTO
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.DeleteOrderRequest
import com.example.internal.input.reqreply.order.DeleteOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdRequest
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.FindOrdersByUserIdRequest
import com.example.internal.input.reqreply.order.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.UpdateOrderResponse
import com.example.internal.input.reqreply.order.UpdateOrderStatusRequest
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import com.example.grpcapi.reqres.order.CreateOrderRequest as GrpcCreateOrderRequest
import com.example.grpcapi.reqres.order.CreateOrderResponse as GrpcCreateOrderResponse
import com.example.grpcapi.reqres.order.FindOrderByIdRequest as GrpcFindOrderByIdRequest
import com.example.grpcapi.reqres.order.FindOrderByIdResponse as GrpcFindOrderByIdResponse

object OrderProtoFixture {

    val randomShipmentDetailsDTO = ShipmentDetailsDTO(
        city = randomCity,
        street = randomStreet,
        building = randomBuilding,
        index = randomIndex
    )

    val randomUpdateShipmentDetailsDTO = ShipmentDetailsDTO(
        city = randomUpdateCity,
        street = randomUpdateStreet,
        building = randomUpdateBuilding,
        index = randomUpdateIndex
    )

    val updateOrderDTO = UpdateOrderDTO(
        shipmentDetails = randomUpdateShipmentDetailsDTO
    )

    val orderDTO = OrderDTO(
        id = randomOrderId,
        items = listOf(
            OrderItemDTO(
                price = randomPrice,
                amount = randomAmount,
                productId = randomProductId
            )
        ),
        shipmentDetails = randomShipmentDetailsDTO,
        status = "NEW",
        userId = randomUserId

    )

    val createOrderDTO = CreateOrderDTO(
        items = listOf(CreateOrderItemDTO(randomProductId, randomAmount)),
        shipmentDetails = randomShipmentDetailsDTO,
        userId = randomUserId
    )


    val randomShipmentDetails = ShipmentDetails.newBuilder().apply {
        city = randomCity
        street = randomStreet
        building = randomBuilding
        index = randomIndex
    }.build()

    val createOrderResponse = CreateOrderResponse.newBuilder().apply {
        successBuilder.order = createOrder()
    }.build()

    val createOrderResponseWithUnexpectedException = CreateOrderResponse.newBuilder().apply {
        failureBuilder.setMessage("NPE")
    }.build()

    val createOrderResponseWithUserNotFoundException = CreateOrderResponse.newBuilder().apply {
        failureBuilder.setMessage("NPE")
        failureBuilder.userNotFoundBuilder
    }.build()

    val createOrderResponseWithProductNotFoundException = CreateOrderResponse.newBuilder().apply {
        failureBuilder.setMessage("NPE")
        failureBuilder.productNotFoundBuilder
    }.build()

    val findOrderByIdRequest = FindOrderByIdRequest.newBuilder().setId(randomOrderId).build()

    val findOrderByIdResponse = FindOrderByIdResponse.newBuilder().apply {
        successBuilder.order = createFullOrder()

    }.build()

    val findOrderByIdResponseWithOrderNotFoundException = FindOrderByIdResponse.newBuilder().apply {
        failureBuilder.message = "Order not found"
        failureBuilder.orderNotFoundBuilder
    }.build()

    val findOrderByIdResponseWithUnexpectedException = FindOrderByIdResponse.newBuilder().apply {
        failureBuilder.message = "NPE"
    }.build()

    val updateOrderResponse = UpdateOrderResponse.newBuilder().apply {
        successBuilder.order = createOrder()
    }.build()

    val updateOrderResponseWithOrderNotFoundException = UpdateOrderResponse.newBuilder().apply {
        failureBuilder.message = "Order not found"
        failureBuilder.orderNotFoundBuilder
    }.build()

    val updateOrderResponseWithUnexpectedException = UpdateOrderResponse.newBuilder().apply {
        failureBuilder.message = "Order not found"
    }.build()

    val findOrdersByUserIdRequest = FindOrdersByUserIdRequest.newBuilder().setId(randomUserId).build()

    val findOrdersByUserIdResponse = FindOrdersByUserIdResponse.newBuilder().apply {
        successBuilder.addOrder(createOrder())
    }.build()

    val findOrdersByUserIdResponseWithUserNotFoundException = FindOrdersByUserIdResponse.newBuilder().apply {
        failureBuilder.message = "User not found"
        failureBuilder.userNotFoundBuilder
    }.build()

    val updateOrderStatusRequest =
        UpdateOrderStatusRequest.newBuilder().setId(randomOrderId).setStatus("COMPLETED").build()

    val updateOrderStatusResponse = UpdateOrderStatusResponse.newBuilder().apply {
        successBuilder.order = createOrder()
        successBuilder.orderBuilder.status = Order.Status.STATUS_COMPLETED
    }.build()

    val updateOrderStatusResponseWithOrNotFoundException = UpdateOrderStatusResponse.newBuilder().apply {
        failureBuilder.message = "Order not found"
        failureBuilder.orderNotFoundBuilder
    }.build()

    val updateOrderStatusResponseWithUnexpectedException = UpdateOrderStatusResponse.newBuilder().apply {
        failureBuilder.message = "NPE"
    }.build()

    val deleteOrderRequest = DeleteOrderRequest.newBuilder().setId(randomOrderId).build()

    val deleteOrderResponse = DeleteOrderResponse.newBuilder().apply {
        successBuilder
    }.build()

    val deleteOrderResponseWithUnexpectedException = DeleteOrderResponse.newBuilder().apply {
        failureBuilder.message = "NPE"
    }.build()

    val grpcCreateOrderRequest = GrpcCreateOrderRequest.newBuilder().also {
        it.shipmentDetails = randomShipmentDetails
        it.userId = randomUserId
        it.addItems(OrderItem.newBuilder().apply {
            productId = randomProductId
            amount = randomAmount
        }.build())
    }.build()

    val grpcCreateOrderResponse = GrpcCreateOrderResponse.newBuilder().also {
        it.successBuilder.order = createOrder()
    }.build()

    val grpcFindOrderByIdRequest = GrpcFindOrderByIdRequest.newBuilder().also {
        it.id = randomUserId
    }.build()

    val grpcFindOrderByIdResponse = GrpcFindOrderByIdResponse.newBuilder().apply {
        successBuilder.order = createFullOrder()
    }.build()

    private fun createFullOrder(): Order {
        return Order.newBuilder().also {
            it.id = randomOrderId
            it.shipmentDetails = randomShipmentDetails
            it.userId = randomUserId
            it.status = Order.Status.STATUS_NEW
            it.addItems(OrderItem.newBuilder().apply {
                price = randomPrice.toString()
                amount = randomAmount
                productFull = Product.newBuilder().apply {
                    id = randomProductId
                    name = randomProductName
                    amount = randomAmountAvailable
                    price = randomPrice.toString()
                    measurement = randomMeasurement
                }.build()
            }.build())
        }.build()
    }

    private fun createOrder(): Order {
        return Order.newBuilder().also {
            it.id = randomOrderId
            it.status = Order.Status.STATUS_NEW
            it.userId = randomUserId
            it.shipmentDetails = randomShipmentDetails
            it.addItems(OrderItem.newBuilder().apply {
                productId = randomProductId
                amount = randomAmount
                price = randomPrice.toString()
            }.build())
        }.build()
    }
}
