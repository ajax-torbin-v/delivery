package com.example.delivery

import com.example.delivery.domain.DomainOrder
import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.response.OrderDTO
import com.example.delivery.dto.response.ShipmentDetailsDTO
import com.example.delivery.mongo.MongoOrder
import org.bson.types.ObjectId
import java.math.BigDecimal

object OrderFixture {
    val order = MongoOrder(
        id = ObjectId("123456789011121314151617"),
        items = mutableMapOf(ObjectId("123456789011121314151617") to 2),
        totalPrice = BigDecimal.valueOf(100),
        shipmentDetails = MongoOrder.MongoShipmentDetails(
            city = "city",
            street = "street",
            building = "3a",
            index = "54890",
        ),
        status = MongoOrder.Status.NEW,
        userId = ObjectId("123456789011121314151617")
    )

    val orderDTO = OrderDTO(
        id = "123456789011121314151617",
        items = mutableMapOf("1" to 2),
        totalPrice = BigDecimal.valueOf(100),
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
        items = mutableMapOf("123456789011121314151617" to 2),
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
        items = mutableMapOf(ObjectId("123456789011121314151617") to 2),
        totalPrice = BigDecimal.valueOf(100),
        shipmentDetails = MongoOrder.MongoShipmentDetails(
            city = "city",
            street = "street",
            building = "3a",
            index = "54890",
        ),
        status = "NEW",
        userId = ObjectId("123456789011121314151617")
    )

}
