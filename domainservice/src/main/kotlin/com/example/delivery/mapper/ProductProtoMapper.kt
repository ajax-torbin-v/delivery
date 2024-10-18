package com.example.delivery.mapper

import com.example.core.dto.request.CreateProductDTO
import com.example.core.dto.request.UpdateProductDTO
import com.example.core.exception.ProductNotFoundException
import com.example.delivery.domain.DomainProduct
import com.example.internal.commonmodels.input.reqreply.product.delete.DeleteProductResponse
import com.example.internal.commonmodels.product.product.Product
import com.example.internal.input.reqreply.product.create.CreateProductRequest
import com.example.internal.input.reqreply.product.create.CreateProductResponse
import com.example.internal.input.reqreply.product.find.FindProductByIdResponse
import com.example.internal.input.reqreply.product.update.UpdateProductRequest
import com.example.internal.input.reqreply.product.update.UpdateProductResponse
import java.math.BigDecimal

object ProductProtoMapper {

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

    fun CreateProductRequest.toCreateProductDTO(): CreateProductDTO {
        return CreateProductDTO(
            product.name,
            BigDecimal(product.price),
            product.amount,
            product.measurement
        )
    }

    fun UpdateProductRequest.toUpdateProductDTO(): UpdateProductDTO {
        return UpdateProductDTO(name, BigDecimal(price), amount, measurement)
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
