package com.example.delivery

import com.example.delivery.ProductFixture.domainProduct
import com.example.delivery.ProductFixture.product
import com.example.delivery.domain.DomainOrder
import com.example.delivery.domain.DomainOrderWithProduct
import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.request.CreateOrderItemDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.dto.response.OrderDTO
import com.example.delivery.dto.response.ShipmentDetailsDTO
import com.example.delivery.mapper.OrderMapper.toDTO
import com.example.delivery.mapper.OrderMapper.toDomain
import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.MongoOrderWithProduct
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Update
import java.math.BigDecimal

object OrderFixture {
    val mongoOrderItem = MongoOrder.MongoOrderItem(
        ObjectId("123456789011121314151617"),
        BigDecimal.TEN,
        2
    )

    val mongoOrderItemWithProduct = MongoOrderWithProduct.MongoOrderItemWithProduct(
        product,
        BigDecimal.ZERO,
        amount = 0
    )

    val mongoOrderWithProduct = MongoOrderWithProduct(
        id = ObjectId("123456789011121314151617"),
        items = listOf(mongoOrderItemWithProduct),
        shipmentDetails = MongoOrder.MongoShipmentDetails(
            city = "city",
            street = "street",
            building = "3a",
            index = "54890",
        ),
        status = MongoOrder.Status.NEW,
        userId = ObjectId("123456789011121314151617")
    )

    val order = MongoOrder(
        id = ObjectId("123456789011121314151617"),
        items = listOf(mongoOrderItem),
        shipmentDetails = MongoOrder.MongoShipmentDetails(
            city = "city",
            street = "street",
            building = "3a",
            index = "54890",
        ),
        status = MongoOrder.Status.NEW,
        userId = ObjectId("123456789011121314151617")
    )

    val unsavedOrder = order.copy(id = null)

    val orderDTO = OrderDTO(
        id = "123456789011121314151617",
        items = listOf(mongoOrderItem.toDomain().toDTO()),
        shipmentDetails = ShipmentDetailsDTO(
            city = "city",
            street = "street",
            building = "3a",
            index = "54890",
        ),
        status = "NEW",
        userId = "123456789011121314151617"

    )

    val createOrderDTO = CreateOrderDTO(
        items = listOf(CreateOrderItemDTO("123456789011121314151617", 0)),
        shipmentDetails = ShipmentDetailsDTO(
            city = "city",
            street = "street",
            building = "3a",
            index = "54890",
        ),
        userId = "123456789011121314151617"
    )

    val domainOrder = DomainOrder(
        id = ObjectId("123456789011121314151617"),
        items = listOf(mongoOrderItem.toDomain()),
        shipmentDetails = DomainOrder.DomainShipmentDetails(
            city = "city",
            street = "street",
            building = "3a",
            index = "54890",
        ),
        status = DomainOrder.Status.NEW,
        userId = ObjectId("123456789011121314151617")
    )


    val domainOrderWithProduct = DomainOrderWithProduct(
        id = ObjectId("123456789011121314151617"),
        items = listOf(
            DomainOrderWithProduct.DomainOrderItemWithProduct(
                price = BigDecimal.ZERO,
                amount = 0,
                product = domainProduct,
            ),
        ),
        shipmentDetails = DomainOrder.DomainShipmentDetails(
            city = "city",
            street = "street",
            building = "3a",
            index = "54890",
        ),
        status = "NEW",
        userId = ObjectId("123456789011121314151617")
    )


    val updatedShipmentDetails = MongoOrder.MongoShipmentDetails(
        city = "Dnipro",
        street = "street",
        building = "1b",
        index = "01222"
    )

    val updateOrderDTO = UpdateOrderDTO(
        shipmentDetails = updatedShipmentDetails.toDomain().toDTO()
    )

    val orderUpdateObject = Update()
        .set("shipmentDetails", updatedShipmentDetails.toDomain().toDTO())

    val updatedOrder = order.copy(
        shipmentDetails = updatedShipmentDetails
    )

    val updatedDomainOrder = domainOrder.copy(shipmentDetails = updatedShipmentDetails.toDomain())
}
