package com.example.gateway

import com.example.core.ProductFixture.randomAmountAvailable
import com.example.core.ProductFixture.randomMeasurement
import com.example.core.ProductFixture.randomPrice
import com.example.core.ProductFixture.randomProductId
import com.example.core.ProductFixture.randomProductName
import com.example.core.ProductFixture.randomUpdateAmountAvailable
import com.example.core.ProductFixture.randomUpdateMeasurement
import com.example.core.ProductFixture.randomUpdatePrice
import com.example.core.ProductFixture.randomUpdateProductName
import com.example.internal.input.reqreply.product.CreateProductResponse
import com.example.internal.input.reqreply.product.DeleteProductRequest
import com.example.internal.input.reqreply.product.DeleteProductResponse
import com.example.internal.input.reqreply.product.FindProductByIdRequest
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import com.example.internal.input.reqreply.product.UpdateProductResponse

object ProductProtoFixture {
    val createProductResponse = CreateProductResponse.newBuilder().apply {
        successBuilder.productBuilder.apply {
            name = randomProductName
            price = randomPrice.toString()
            amount = randomAmountAvailable
            measurement = randomMeasurement
        }
    }.build()

    val createProductResponseWithUnexpectedException = CreateProductResponse.newBuilder().apply {
        failureBuilder.setMessage("NPE")
    }.build()

    val findProductByIdRequest = FindProductByIdRequest.newBuilder().setId(randomProductId).build()

    val findProductByIdResponse = FindProductByIdResponse.newBuilder().apply {
        successBuilder.productBuilder.apply {
            name = randomProductName
            price = randomPrice.toString()
            amount = randomAmountAvailable
            measurement = randomMeasurement
        }
    }.build()

    val findProductByIdResponseWithProductNotFoundException = FindProductByIdResponse.newBuilder().apply {
        failureBuilder.message = "Product not found"
        failureBuilder.productNotFoundBuilder
    }.build()

    val findProductByIdResponseWithUnexpectedException = FindProductByIdResponse.newBuilder().apply {
        failureBuilder.message = "NPE"
    }.build()

    val updateProductResponse = UpdateProductResponse.newBuilder().apply {
        successBuilder.productBuilder.apply {
            name = randomUpdateProductName
            price = randomUpdatePrice.toString()
            amount = randomUpdateAmountAvailable
            measurement = randomUpdateMeasurement
        }
    }.build()

    val deleteProductRequest = DeleteProductRequest.newBuilder().setId(randomProductId).build()

    val deleteProductResponseWithUnexpectedException = DeleteProductResponse.newBuilder().apply {
        failureBuilder.message = "NPE"
    }.build()
}
