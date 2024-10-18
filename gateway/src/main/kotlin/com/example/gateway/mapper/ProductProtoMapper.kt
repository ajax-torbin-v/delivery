package com.example.gateway.mapper

import com.example.core.dto.request.CreateProductDTO
import com.example.core.dto.request.UpdateProductDTO
import com.example.core.dto.response.ProductDTO
import com.example.core.exception.ProductNotFoundException
import com.example.internal.commonmodels.input.reqreply.product.delete.DeleteProductResponse
import com.example.internal.commonmodels.product.product.Product
import com.example.internal.input.reqreply.product.create.CreateProductRequest
import com.example.internal.input.reqreply.product.create.CreateProductResponse
import com.example.internal.input.reqreply.product.find.FindProductByIdResponse
import com.example.internal.input.reqreply.product.update.UpdateProductRequest
import com.example.internal.input.reqreply.product.update.UpdateProductResponse
import java.math.BigDecimal

object ProductProtoMapper {
    fun CreateProductDTO.toCreateProductRequest(): CreateProductRequest {
        return CreateProductRequest.newBuilder().apply {
            productBuilder.price = price.toString()
            productBuilder.amount = amount
            productBuilder.measurement = measurement
            productBuilder.name = name
        }.build()
    }

    fun CreateProductResponse.toDTO(): ProductDTO {
        if (this == CreateProductResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (hasFailure()) {
            error(failure.message)
        }
        return success.product.toDTO()
    }

    fun FindProductByIdResponse.toDTO(): ProductDTO {
        if (this == FindProductByIdResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (hasFailure()) {
            when (failure.errorCase!!) {
                FindProductByIdResponse.Failure.ErrorCase.ERROR_NOT_SET ->
                    error(failure.message)

                FindProductByIdResponse.Failure.ErrorCase.PRODUCT_NOT_FOUND ->
                    throw ProductNotFoundException(failure.message)
            }
        }
        return success.product.toDTO()
    }

    fun UpdateProductResponse.toDTO(): ProductDTO {
        if (this == UpdateProductResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (hasFailure()) {
            when (failure.errorCase!!) {
                UpdateProductResponse.Failure.ErrorCase.ERROR_NOT_SET ->
                    error(failure.message)

                UpdateProductResponse.Failure.ErrorCase.PRODUCT_NOT_FOUND ->
                    throw ProductNotFoundException(failure.message)
            }
        }
        return success.product.toDTO()
    }

    fun DeleteProductResponse.toDTO() {
        if (this == DeleteProductResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (this.hasFailure()) {
            error(failure.message)
        }
    }

    fun updateProductRequest(id: String, updateProductDTO: UpdateProductDTO): UpdateProductRequest {
        return UpdateProductRequest.newBuilder().also { builder ->
            builder.id = id
            updateProductDTO.name?.let { builder.name = it }
            updateProductDTO.amountAvailable?.let { builder.amount = it }
            updateProductDTO.price?.let { builder.price = it.toPlainString() }
            updateProductDTO.measurement?.let { builder.measurement = it }
        }.build()
    }

    fun Product.toDTO(): ProductDTO {
        return ProductDTO(
            this.id,
            this.name,
            BigDecimal(price),
            amount,
            measurement
        )
    }
}
