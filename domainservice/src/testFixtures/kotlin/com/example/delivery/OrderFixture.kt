package com.example.delivery

import com.example.delivery.ProductFixture.domainProduct
import com.example.delivery.ProductFixture.product
import com.example.delivery.ProductFixture.randomPrice
import com.example.delivery.ProductFixture.randomProductId
import com.example.delivery.UserFixture.randomUserId
import com.example.delivery.domain.DomainOrder
import com.example.delivery.domain.projection.DomainOrderWithProduct
import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.request.CreateOrderItemDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.dto.response.OrderDTO
import com.example.delivery.exception.OrderNotFoundException
import com.example.delivery.mapper.OrderMapper.toDTO
import com.example.delivery.mapper.OrderMapper.toDomain
import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.projection.MongoOrderWithProduct
import com.example.internal.input.reqreply.order.delete.DeleteOrderRequest
import com.example.internal.input.reqreply.order.find.FindOrderByIdRequest
import io.github.serpro69.kfaker.Faker
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Update

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

    val randomOrderId = ObjectId().toString()
    val randomDomainShipmentDetails = randomMongoShipmentDetails.toDomain()
    val randomUpdateDomainShipmentDetails = randomUpdateMongoShipmentDetails.toDomain()
    val randomDTOShipmentDetails = randomDomainShipmentDetails.toDTO()
    val randomUpdateDTOShipmentDetails = randomUpdateDomainShipmentDetails.toDTO()
    val randomAmount = Faker().random.nextInt(1, 10)
    val orderNotFoundException = OrderNotFoundException("Order with id $randomOrderId not found!")
    val unexpectedError = NullPointerException()

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

    val orderDTO = OrderDTO(
        id = randomOrderId,
        items = listOf(mongoOrderItem.toDomain().toDTO()),
        shipmentDetails = randomDTOShipmentDetails,
        status = "NEW",
        userId = randomUserId

    )

    val createOrderDTO = CreateOrderDTO(
        items = listOf(CreateOrderItemDTO(randomProductId, randomAmount)),
        shipmentDetails = randomDTOShipmentDetails,
        userId = randomUserId
    )

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
        status = "NEW",
        userId = randomUserId
    )

    val updateOrderDTO = UpdateOrderDTO(
        shipmentDetails = randomUpdateDTOShipmentDetails
    )

    val orderUpdateObject = Update()
        .set("shipmentDetails", randomUpdateDTOShipmentDetails)

    val updatedOrder = order.copy(
        shipmentDetails = randomUpdateMongoShipmentDetails
    )

    val updatedDomainOrder = domainOrder.copy(shipmentDetails = randomUpdateDomainShipmentDetails)

    fun buildFindOrderRequest(orderId: String): FindOrderByIdRequest {
        return FindOrderByIdRequest.newBuilder().setId(orderId).build()
    }

    fun buildDeleteOrderRequest(orderId: String) = DeleteOrderRequest.newBuilder().setId(orderId).build()
}
