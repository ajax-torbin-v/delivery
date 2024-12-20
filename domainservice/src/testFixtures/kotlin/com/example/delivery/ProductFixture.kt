package com.example.delivery

import com.example.core.ProductFixture.randomAmountAvailable
import com.example.core.ProductFixture.randomMeasurement
import com.example.core.ProductFixture.randomPrice
import com.example.core.ProductFixture.randomProductId
import com.example.core.ProductFixture.randomProductName
import com.example.core.ProductFixture.randomUpdateAmountAvailable
import com.example.core.ProductFixture.randomUpdateMeasurement
import com.example.core.ProductFixture.randomUpdatePrice
import com.example.core.ProductFixture.randomUpdateProductName
import com.example.core.exception.ProductNotFoundException
import com.example.delivery.domain.DomainProduct
import com.example.delivery.mongo.MongoProduct
import com.example.internal.input.reqreply.product.CreateProductRequest
import com.example.internal.input.reqreply.product.DeleteProductRequest
import com.example.internal.input.reqreply.product.FindProductByIdRequest
import com.example.internal.input.reqreply.product.UpdateProductRequest
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Update

object ProductFixture {
    val productNotFoundException = ProductNotFoundException("Product with id $randomProductId doesn't exist")

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


    val createProductRequest = CreateProductRequest.newBuilder().apply {
        amount = randomAmountAvailable
        price = randomPrice.toPlainString()
        name = randomProductName
        measurement = randomMeasurement
    }.build()

    fun buildFindProductByIdRequest(productId: String): FindProductByIdRequest {
        return FindProductByIdRequest.newBuilder().setId(productId).build()
    }

    fun buildUpdateProductRequest(productId: String): UpdateProductRequest {
        return UpdateProductRequest.newBuilder().apply {
            id = productId
            name = randomUpdateProductName
            price = randomUpdatePrice.toPlainString()
            measurement = randomUpdateMeasurement
            amount = randomUpdateAmountAvailable
        }.build()
    }

    fun buildDeleteProductRequest(productId: String): DeleteProductRequest {
        return DeleteProductRequest.newBuilder().setId(productId).build()
    }
}
