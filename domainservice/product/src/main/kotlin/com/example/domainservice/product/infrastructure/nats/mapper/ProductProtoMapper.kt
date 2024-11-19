package com.example.domainservice.product.infrastructure.nats.mapper

import com.example.commonmodels.product.Product
import com.example.core.exception.ProductNotFoundException
import com.example.domainservice.product.domain.DomainProduct
import com.example.internal.input.reqreply.product.CreateProductRequest
import com.example.internal.input.reqreply.product.CreateProductResponse
import com.example.internal.input.reqreply.product.DeleteProductResponse
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import com.example.internal.input.reqreply.product.UpdateProductRequest
import com.example.internal.input.reqreply.product.UpdateProductResponse
import java.math.BigDecimal

object ProductProtoMapper {

    fun CreateProductRequest.toDomain(): DomainProduct = DomainProduct(
        id = null,
        name = name,
        price = BigDecimal(price),
        amountAvailable = amount,
        measurement = measurement,
    )

    fun DomainProduct.toFindProductByIdResponse(): FindProductByIdResponse {
        return FindProductByIdResponse.newBuilder().also {
            it.successBuilder.product = this.toProto()
        }.build()
    }

    fun DomainProduct.toCreateProductResponse(): CreateProductResponse {
        return CreateProductResponse.newBuilder().also {
            it.successBuilder.product = this.toProto()
        }.build()
    }

    fun DomainProduct.toUpdateProductResponse(): UpdateProductResponse {
        return UpdateProductResponse.newBuilder().also {
            it.successBuilder.product = this.toProto()
        }.build()
    }

    fun UpdateProductRequest.toDomain(): DomainProduct {
        return DomainProduct(
            id = id,
            name = name,
            price = if (hasPrice()) BigDecimal(price) else BigDecimal.valueOf(-1L),
            amountAvailable = if (hasAmount()) amount else -1,
            measurement = measurement
        )
    }

    fun Throwable.toFailureFindProductByIdResponse(): FindProductByIdResponse {
        return FindProductByIdResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
            when (this@toFailureFindProductByIdResponse) {
                is ProductNotFoundException -> failureBuilder.productNotFoundBuilder
            }
        }.build()
    }

    fun Throwable.toFailureCreateProductResponse(): CreateProductResponse {
        return CreateProductResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
        }.build()
    }

    fun Throwable.toFailureUpdateProductResponse(): UpdateProductResponse {
        return UpdateProductResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
            when (this@toFailureUpdateProductResponse) {
                is ProductNotFoundException -> failureBuilder.productNotFoundBuilder
            }
        }.build()
    }

    fun Throwable.toFailureDeleteProductResponse(): DeleteProductResponse {
        return DeleteProductResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
        }.build()
    }

    fun toDeleteProductResponse(): DeleteProductResponse {
        return DeleteProductResponse.newBuilder().also {
            it.successBuilder
        }.build()
    }

    fun DomainProduct.toProto(): Product {
        return Product.newBuilder().also {
            it.id = this.id
            it.name = this.name
            it.price = this.price.toPlainString()
            it.amount = this.amountAvailable
            it.measurement = this.measurement
        }.build()
    }
}
