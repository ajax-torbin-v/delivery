package com.example.core

import com.example.core.ProductFixture.randomPrice
import com.example.core.ProductFixture.randomProductId
import com.example.core.UserFixture.randomUserId
import com.example.core.dto.request.CreateOrderDTO
import com.example.core.dto.request.CreateOrderItemDTO
import com.example.core.dto.request.UpdateOrderDTO
import com.example.core.dto.response.OrderDTO
import com.example.core.dto.response.OrderItemDTO
import com.example.core.dto.response.ShipmentDetailsDTO
import io.github.serpro69.kfaker.Faker
import org.bson.types.ObjectId

object OrderFixture {
    val randomOrderId = ObjectId().toString()
    val randomAmount = Faker().random.nextInt(1, 10)
    val randomCity = Faker().address.city()
    val randomStreet = Faker().address.streetName()
    val randomBuilding = Faker().address.buildingNumber()
    val randomIndex = Faker().address.countryCodeLong()
    val randomUpdateCity = Faker().address.city()
    val randomUpdateStreet = Faker().address.streetName()
    val randomUpdateBuilding = Faker().address.buildingNumber()
    val randomUpdateIndex = Faker().address.countryCodeLong()

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
}
