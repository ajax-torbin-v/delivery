package com.example.delivery

import com.example.delivery.domain.DomainProduct
import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.request.UpdateProductDTO
import com.example.delivery.dto.response.ProductDTO
import com.example.delivery.mongo.MongoProduct
import io.github.serpro69.kfaker.Faker
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Update
import java.math.BigDecimal

object ProductFixture {
    val randomProductName = Faker().food.vegetables()
    val randomUpdateProductName = Faker().food.vegetables()
    val randomPrice = BigDecimal.valueOf(Faker().random.nextDouble() * 100)
    val randomUpdatePrice = BigDecimal.valueOf(Faker().random.nextDouble() * 100)
    val randomAmount = Faker().random.nextInt(10, 100)
    val randomUpdateAmount = Faker().random.nextInt(100, 1000)
    val randomMeasurement = Faker().food.measurements()
    val randomUpdateMeasurement = Faker().food.measurements()

    val product: MongoProduct =
        MongoProduct(
            id = ObjectId("123456789011121314151617"),
            name = randomProductName,
            price = randomPrice,
            amountAvailable = randomAmount,
            measurement = randomMeasurement
        )

    val unsavedProduct = product.copy(id = null)

    val domainProduct = DomainProduct(
        id = "123456789011121314151617",
        name = randomProductName,
        price = randomPrice,
        amountAvailable = randomAmount,
        measurement = randomMeasurement
    )

    val createProductDTO = CreateProductDTO(
        name = randomProductName,
        price = randomPrice,
        amount = randomAmount,
        measurement = randomMeasurement
    )

    val productDTO = ProductDTO(
        id = "123456789011121314151617",
        name = randomProductName,
        price = randomPrice,
        amount = randomAmount,
        measurement = randomMeasurement
    )

    val products = listOf(
        DomainProduct(
            "123456789011121314151617",
            randomProductName,
            randomPrice,
            randomAmount,
            randomMeasurement
        ),
        DomainProduct(
            "123456789011121314151617".reversed(),
            randomProductName,
            randomPrice,
            randomAmount,
            randomMeasurement
        )
    )

    val updateProductObject = Update()
        .set("name", randomUpdateProductName)
        .set("price", randomUpdatePrice)
        .set("amountAvailable", randomUpdateAmount)
        .set("measurement", randomUpdateMeasurement)

    val updatedProduct = product.copy(
        name = randomUpdateProductName,
        price = randomUpdatePrice,
        amountAvailable = randomUpdateAmount,
        measurement = randomUpdateMeasurement
    )

    val updatedDomainProduct = domainProduct.copy(
        name = randomUpdateProductName,
        price = randomUpdatePrice,
        amountAvailable = randomUpdateAmount,
        measurement = randomUpdateMeasurement
    )

    val updateProductDTO = UpdateProductDTO(
        name = randomUpdateProductName,
        price = randomUpdatePrice,
        amountAvailable = randomUpdateAmount,
        measurement = randomUpdateMeasurement
    )
}
