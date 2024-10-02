package com.example.delivery

import com.example.delivery.ProductFixture.domainProduct
import com.example.delivery.ProductFixture.product
import com.example.delivery.domain.DomainOrder
import com.example.delivery.domain.projection.DomainOrderWithProduct
import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.request.CreateOrderItemDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.dto.response.OrderDTO
import com.example.delivery.mapper.OrderMapper.toDTO
import com.example.delivery.mapper.OrderMapper.toDomain
import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.projection.MongoOrderWithProduct
import io.github.serpro69.kfaker.Faker
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Update
import java.math.BigDecimal

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
    val randomDTOShipmentDetails = randomDomainShipmentDetails.toDTO()
    val randomUpdateDTOShipmentDetails = randomUpdateDomainShipmentDetails.toDTO()
    val randomPrice = BigDecimal.valueOf(Faker().random.nextDouble() * 100)
    val randomAmount = Faker().random.nextInt(1, 10)

    val mongoOrderItem = MongoOrder.MongoOrderItem(
        ObjectId("123456789011121314151617"),
        randomPrice,
        randomAmount
    )

    val mongoOrderItemWithProduct = MongoOrderWithProduct.MongoOrderItemWithProduct(
        product,
        randomPrice,
        randomAmount
    )

    val mongoOrderWithProduct = MongoOrderWithProduct(
        id = ObjectId("123456789011121314151617"),
        items = listOf(mongoOrderItemWithProduct),
        shipmentDetails = randomMongoShipmentDetails,
        status = MongoOrder.Status.NEW,
        userId = ObjectId("123456789011121314151617")
    )

    val order = MongoOrder(
        id = ObjectId("123456789011121314151617"),
        items = listOf(mongoOrderItem),
        shipmentDetails = randomMongoShipmentDetails,
        status = MongoOrder.Status.NEW,
        userId = ObjectId("123456789011121314151617")
    )

    val unsavedOrder = order.copy(id = null)

    val orderDTO = OrderDTO(
        id = "123456789011121314151617",
        items = listOf(mongoOrderItem.toDomain().toDTO()),
        shipmentDetails = randomDTOShipmentDetails,
        status = "NEW",
        userId = "123456789011121314151617"

    )

    val createOrderDTO = CreateOrderDTO(
        items = listOf(CreateOrderItemDTO("123456789011121314151617", 0)),
        shipmentDetails = randomDTOShipmentDetails,
        userId = "123456789011121314151617"
    )

    val domainOrder = DomainOrder(
        id = "123456789011121314151617",
        items = listOf(mongoOrderItem.toDomain()),
        shipmentDetails = randomDomainShipmentDetails,
        status = DomainOrder.Status.NEW,
        userId = "123456789011121314151617"
    )


    val domainOrderWithProduct = DomainOrderWithProduct(
        id = "123456789011121314151617",
        items = listOf(
            DomainOrderWithProduct.DomainOrderItemWithProduct(
                price = randomPrice,
                amount = randomAmount,
                product = domainProduct,
            ),
        ),
        shipmentDetails = randomDomainShipmentDetails,
        status = "NEW",
        userId = "123456789011121314151617"
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
}
