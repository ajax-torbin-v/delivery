package com.example.domainservice

import com.example.core.OrderFixture.randomAmount
import com.example.core.OrderFixture.randomOrderId
import com.example.core.ProductFixture.randomPrice
import com.example.core.ProductFixture.randomProductId
import com.example.core.UserFixture.randomUserId
import com.example.domainservice.ProductFixture.domainProduct
import com.example.domainservice.ProductFixture.product
import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.domain.projection.DomainOrderWithProduct
import com.example.domainservice.order.infrastructure.mongo.entity.MongoOrder
import com.example.domainservice.order.infrastructure.mongo.entity.projection.MongoOrderWithProduct
import com.example.domainservice.order.infrastructure.mongo.mapper.OrderMapper.toDomain
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.UpdateOrderResponse
import io.github.serpro69.kfaker.Faker
import org.bson.types.ObjectId

object OrderFixture {

    val randomMongoShipmentDetails = MongoOrder.MongoShipmentDetails(
        city = Faker().address.city(),
        street = Faker().address.streetName(),
        building = Faker().address.buildingNumber(),
        index = Faker().address.countryCodeLong()
    )

    val randomUpdateMongoShipmentDetails = MongoOrder.MongoShipmentDetails(
        city = Faker().address.city(),
        street = Faker().address.streetName(),
        building = Faker().address.buildingNumber(),
        index = Faker().address.countryCodeLong()
    )

    val randomDomainShipmentDetails = randomMongoShipmentDetails.toDomain()
    val randomUpdateDomainShipmentDetails = randomUpdateMongoShipmentDetails.toDomain()

    val mongoOrderItem = MongoOrder.MongoOrderItem(
        ObjectId(randomProductId),
        randomPrice,
        randomAmount
    )

    val mongoOrderItemWithProduct = MongoOrderWithProduct.MongoOrderItemWithProduct(
        product,
        randomPrice,
        randomAmount
    )

    val mongoOrderWithProduct = MongoOrderWithProduct(
        id = ObjectId(randomOrderId),
        items = listOf(mongoOrderItemWithProduct),
        shipmentDetails = randomMongoShipmentDetails,
        status = MongoOrder.Status.NEW,
        userId = ObjectId(randomUserId)
    )

    val order = MongoOrder(
        id = ObjectId(randomOrderId),
        items = listOf(mongoOrderItem),
        shipmentDetails = randomMongoShipmentDetails,
        status = MongoOrder.Status.NEW,
        userId = ObjectId(randomUserId)
    )

    val unsavedOrder = order.copy(id = null)
    val unsavedDomainOrder = order.copy(id = null).toDomain()

    val domainOrder = DomainOrder(
        id = randomOrderId,
        items = listOf(mongoOrderItem.toDomain()),
        shipmentDetails = randomDomainShipmentDetails,
        status = DomainOrder.Status.NEW,
        userId = randomUserId
    )


    val domainOrderWithProduct = DomainOrderWithProduct(
        id = randomOrderId,
        items = listOf(
            DomainOrderWithProduct.DomainOrderItemWithProduct(
                price = randomPrice,
                amount = randomAmount,
                product = domainProduct,
            ),
        ),
        shipmentDetails = randomDomainShipmentDetails,
        status = DomainOrder.Status.NEW,
        userId = randomUserId
    )

    val updatedOrder = order.copy(
        shipmentDetails = randomUpdateMongoShipmentDetails
    )

    val updatedDomainOrder = domainOrder.copy(shipmentDetails = randomUpdateDomainShipmentDetails)

    val failureCreateOrderResponseWithProductNotFoundException = CreateOrderResponse.newBuilder().apply {
        failureBuilder.message = "Product not found"
        failureBuilder.productNotFoundBuilder
    }.build()

    val failureCreateOrderResponseWithUserNotFoundException = CreateOrderResponse.newBuilder().apply {
        failureBuilder.message = "User not found"
        failureBuilder.userNotFoundBuilder
    }.build()

    val failureCreateOrderResponseWithProductAmountException = CreateOrderResponse.newBuilder().apply {
        failureBuilder.message = "Not enough product"
        failureBuilder.productNotSufficientAmountBuilder
    }.build()

    val failureCreateOrderResponseWithUnexpectedException = CreateOrderResponse.newBuilder().apply {
        failureBuilder
    }.build()

    val failureFindOrderByIdWithOrderNotFoundException = FindOrderByIdResponse.newBuilder().apply {
        failureBuilder.message = "Order not found"
        failureBuilder.orderNotFoundBuilder
    }.build()

    val failureFindOrderByIdWithUnexpectedException = FindOrderByIdResponse.newBuilder().apply {
        failureBuilder
    }.build()

    val failureUpdateOrderResponseWithOrderNotFoundException = UpdateOrderResponse.newBuilder().apply {
        failureBuilder.message = "Order not found"
        failureBuilder.orderNotFoundBuilder
    }.build()

    val failureUpdateOrderResponseWitUnexpectedException = UpdateOrderResponse.newBuilder().apply {
        failureBuilder
    }.build()
}