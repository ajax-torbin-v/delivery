package com.example.delivery

import com.example.delivery.domain.DomainProduct
import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.request.UpdateProductDTO
import com.example.delivery.dto.response.ProductDTO
import com.example.delivery.exception.ProductAmountException
import com.example.delivery.exception.ProductNotFoundException
import com.example.delivery.mongo.MongoProduct
import com.example.internal.commonmodels.input.reqreply.product.delete.DeleteProductRequest
import com.example.internal.input.reqreply.product.create.CreateProductRequest
import com.example.internal.input.reqreply.product.find.FindProductByIdRequest
import com.example.internal.input.reqreply.product.update.UpdateProductRequest
import io.github.serpro69.kfaker.Faker
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Update
import java.math.BigDecimal

object ProductFixture {
    val randomProductId = ObjectId().toString()
    val randomProductName = Faker().food.vegetables()
    val randomUpdateProductName = Faker().food.vegetables()
    val randomPrice = BigDecimal.valueOf(Faker().random.nextDouble() * 100)
    val randomUpdatePrice = BigDecimal.valueOf(Faker().random.nextDouble() * 100)
    val randomAmountAvailable = Faker().random.nextInt(10, 100)
    val randomUpdateAmountAvailable = Faker().random.nextInt(100, 1000)
    val randomMeasurement = Faker().food.measurements()
    val randomUpdateMeasurement = Faker().food.measurements()
    val productNotFoundException = ProductNotFoundException("Product with id $randomProductId doesn't exist")
    val productAmountException = ProductAmountException("Not enough of product $randomProductId")
    val unexpectedError = NullPointerException()

    val product: MongoProduct =
        MongoProduct(
            id = ObjectId(randomProductId),
            name = randomProductName,
            price = randomPrice,
            amountAvailable = randomAmountAvailable,
            measurement = randomMeasurement
        )

    val unsavedProduct = product.copy(id = null)

    val domainProduct = DomainProduct(
        id = randomProductId,
        name = randomProductName,
        price = randomPrice,
        amountAvailable = randomAmountAvailable,
        measurement = randomMeasurement
    )

    val createProductDTO = CreateProductDTO(
        name = randomProductName,
        price = randomPrice,
        amount = randomAmountAvailable,
        measurement = randomMeasurement
    )

    val productDTO = ProductDTO(
        id = randomProductId,
        name = randomProductName,
        price = randomPrice,
        amount = randomAmountAvailable,
        measurement = randomMeasurement
    )

    val products = listOf(
        DomainProduct(
            randomProductId,
            randomProductName,
            randomPrice,
            randomAmountAvailable,
            randomMeasurement
        ),
        DomainProduct(
            randomProductId.reversed(),
            randomProductName,
            randomPrice,
            randomAmountAvailable,
            randomMeasurement
        )
    )

    val updateProductObject = Update()
        .set("name", randomUpdateProductName)
        .set("price", randomUpdatePrice)
        .set("amountAvailable", randomUpdateAmountAvailable)
        .set("measurement", randomUpdateMeasurement)

    val updatedProduct = product.copy(
        name = randomUpdateProductName,
        price = randomUpdatePrice,
        amountAvailable = randomUpdateAmountAvailable,
        measurement = randomUpdateMeasurement
    )

    val updatedDomainProduct = domainProduct.copy(
        name = randomUpdateProductName,
        price = randomUpdatePrice,
        amountAvailable = randomUpdateAmountAvailable,
        measurement = randomUpdateMeasurement
    )

    val updateProductDTO = UpdateProductDTO(
        name = randomUpdateProductName,
        price = randomUpdatePrice,
        amountAvailable = randomUpdateAmountAvailable,
        measurement = randomUpdateMeasurement
    )

    val createProductRequest = CreateProductRequest.newBuilder().also {
        it.productBuilder
            .setAmount(randomAmountAvailable)
            .setPrice(randomPrice.toPlainString())
            .setName(randomProductName)
            .setMeasurement(randomMeasurement)
    }.build()

    fun buildFindProductByIdRequest(productId: String): FindProductByIdRequest {
        return FindProductByIdRequest.newBuilder().setId(productId).build()
    }

    fun buildUpdateProductRequest(productId: String): UpdateProductRequest {
        return UpdateProductRequest.newBuilder().also {
            it.setId(productId)
                .setName(randomUpdateProductName)
                .setPrice(randomUpdatePrice.toPlainString())
                .setMeasurement(randomUpdateMeasurement)
                .setAmount(randomUpdateAmountAvailable)
        }.build()
    }

    fun buildDeleteProductRequest(productId: String): DeleteProductRequest {
        return DeleteProductRequest.newBuilder().setId(productId).build()
    }
}
