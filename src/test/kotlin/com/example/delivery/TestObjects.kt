package com.example.delivery

import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.response.OrderDTO
import com.example.delivery.dto.response.ProductDTO
import com.example.delivery.model.MongoOrder
import com.example.delivery.model.MongoUser

val createProductDTO = CreateProductDTO("Coca-cola", 24.50, 69, "0.5L")
val product = createProductDTO.toModel()
val products = listOf(
    ProductDTO("123", "Coca-cola", 24.50, 69, "0.5L"),
    ProductDTO("456", "Pepsi", 23.00, 50, "0.5L")
)
val mongoProduct = createProductDTO.toModel()
val productDTO = mongoProduct.toDTO()


val order = MongoOrder(
    id = null,
    items = mutableMapOf("1" to 2),
    totalPrice = 100.0,
    shipmentDetails = MongoOrder.ShipmentDetails(
        city = "city",
        street = "street",
        building = "3a",
        index = "54890",
    ),
    status = MongoOrder.Status.NEW
)

val orderDTO = OrderDTO (
    id = "none",
    items = mutableMapOf("1" to 2),
    totalPrice = 100.0,
    shipmentDetails = MongoOrder.ShipmentDetails(
        city = "city",
        street = "street",
        building = "3a",
        index = "54890",
    ),
    status = MongoOrder.Status.NEW
)

val createOrderDTO = CreateOrderDTO(
    items = mutableMapOf("1" to 2),
    shipmentDetails = MongoOrder.ShipmentDetails(
        city = "city",
        street = "street",
        building = "3a",
        index = "54890",
    ),
)

val createUserDTO = CreateUserDTO(
    fullName = "FULL NAME",
    phone = "+389023923",
    password = "password",
)
val user = MongoUser(
    null,
    fullName = "FULL NAME",
    phone = "+31243123",
    password = "password",
    orderIds = mutableListOf("1"),
)
val userDTO = user.toDTO()

