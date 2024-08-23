package com.example.delivery

import com.example.delivery.domain.DomainProduct
import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.response.ProductDTO
import com.example.delivery.mongo.MongoProduct
import org.bson.types.ObjectId
import java.math.BigDecimal

object ProductFixture {
    val product: MongoProduct =
        MongoProduct(
            id = ObjectId("123456789011121314151617"),
            name = "Coca-cola",
            price = BigDecimal.valueOf(24.50),
            amountAvailable = 69,
            measurement = "0.5L"
        )

    val domainProduct = DomainProduct(
        id = ObjectId("123456789011121314151617"),
        name = "Coca-cola",
        price = BigDecimal.valueOf(24.50),
        amountAvailable = 69,
        measurement = "0.5L"
    )

    val createProductDTO = CreateProductDTO(
        name = "Coca-cola",
        price = BigDecimal.valueOf(24.50),
        amount = 69,
        measurement = "0.5L"
    )

    val productDTO = ProductDTO(
        id = "123456789011121314151617",
        name = "Coca-cola",
        price = BigDecimal.valueOf(24.50),
        amount = 69,
        measurement = "0.5L"
    )

    val products = listOf(
        DomainProduct(
            ObjectId("123456789011121314151617"),
            "Coca-cola",
            BigDecimal.valueOf(24.50),
            69,
            "0.5L"
        ),
        DomainProduct(
            ObjectId("123456789011121314151617".reversed()),
            "Pepsi",
            BigDecimal.valueOf(23.00),
            50,
            "0.5L"
        )
    )
}
