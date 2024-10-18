package com.example.gateway

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
import com.example.internal.commonmodels.order.order.Order
import com.example.internal.commonmodels.order.order_item.OrderItem
import com.example.internal.commonmodels.order.shipment_details.ShipmentDetails
import com.example.internal.commonmodels.product.product.Product
import com.example.internal.input.reqreply.order.create.CreateOrderResponse
import com.example.internal.input.reqreply.order.delete.DeleteOrderRequest
import com.example.internal.input.reqreply.order.delete.DeleteOrderResponse
import com.example.internal.input.reqreply.order.find.FindOrderByIdRequest
import com.example.internal.input.reqreply.order.find.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.find_by_user_id.FindOrdersByUserIdRequest
import com.example.internal.input.reqreply.order.find_by_user_id.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.update.UpdateOrderResponse
import com.example.internal.input.reqreply.order.update_status.UpdateOrderStatusRequest
import com.example.internal.input.reqreply.order.update_status.UpdateOrderStatusResponse

object OrderProtoFixture {
    val randomShipmentDetails = ShipmentDetails.newBuilder().apply {
        city = randomCity
        street = randomStreet
        building = randomBuilding
        index = randomIndex
    }.build()

    val randomUpdatedShipmentDetails = ShipmentDetails.newBuilder().apply {
        city = randomUpdateCity
        street = randomUpdateStreet
        building = randomUpdateBuilding
        index = randomUpdateIndex
    }.build()

    val createOrderResponse = CreateOrderResponse.newBuilder().apply {
        successBuilder.orderBuilder.apply {
            id = randomOrderId
            shipmentDetails = randomShipmentDetails
            userId = randomUserId
            status = "NEW"
            addItems(OrderItem.newBuilder().apply {
                productId = randomProductId
                price = randomPrice.toString()
                amount = randomAmount
            })
        }
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
        successBuilder.orderBuilder.apply {
            id = randomOrderId
            shipmentDetails = randomShipmentDetails
            userId = randomUserId
            status = "NEW"
            addItems(OrderItem.newBuilder().apply {
                price = randomPrice.toString()
                productFull = Product.newBuilder()
                    .setId(randomProductId)
                    .setName(randomProductName)
                    .setAmount(randomAmountAvailable)
                    .setPrice(randomPrice.toString())
                    .setMeasurement(randomMeasurement)
                    .build()
                amount = randomAmount
            }.build())
        }.build()
    }.build()

    val findOrderByIdResponseWithOrderNotFoundException = FindOrderByIdResponse.newBuilder().apply {
        failureBuilder.message = "Order not found"
        failureBuilder.orderNotFoundBuilder
    }.build()

    val findOrderByIdResponseWithUnexpectedException = FindOrderByIdResponse.newBuilder().apply {
        failureBuilder.message = "NPE"
    }.build()

    val updateOrderResponse = UpdateOrderResponse.newBuilder().apply {
        successBuilder.orderBuilder.apply {
            id = randomOrderId
            shipmentDetails = randomUpdatedShipmentDetails
            userId = randomUserId
            status = "NEW"
            addItems(OrderItem.newBuilder().apply {
                productId = randomProductId
                price = randomPrice.toString()
                amount = randomAmount
            })
        }
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
        successBuilder.addOrder(Order.newBuilder().apply {
            id = randomOrderId
            shipmentDetails = randomShipmentDetails
            userId = randomUserId
            status = "NEW"
            addItems(OrderItem.newBuilder().apply {
                productId = randomProductId
                price = randomPrice.toString()
                amount = randomAmount
            }.build())
        }.build())
    }.build()


    val findOrdersByUserIdResponseWithUserNotFoundException = FindOrdersByUserIdResponse.newBuilder().apply {
        failureBuilder.message = "User not found"
        failureBuilder.userNotFoundBuilder
    }.build()

    val updateOrderStatusRequest =
        UpdateOrderStatusRequest.newBuilder().setId(randomOrderId).setStatus("COMPLETED").build()

    val updateOrderStatusResponse = UpdateOrderStatusResponse.newBuilder().apply {
        successBuilder.orderBuilder.apply {
            id = randomOrderId
            shipmentDetails = randomShipmentDetails
            userId = randomUserId
            status = "COMPLETED"
            addItems(OrderItem.newBuilder().apply {
                productId = randomProductId
                price = randomPrice.toString()
                amount = randomAmount
            }.build())
        }.build()
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
}