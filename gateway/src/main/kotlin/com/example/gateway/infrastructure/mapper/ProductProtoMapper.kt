package com.example.gateway.infrastructure.mapper

import com.example.commonmodels.product.Product
import com.example.core.dto.request.CreateProductDTO
import com.example.core.dto.request.UpdateProductDTO
import com.example.core.dto.response.ProductDTO
import com.example.core.exception.ProductNotFoundException
import com.example.internal.input.reqreply.product.CreateProductRequest
import com.example.internal.input.reqreply.product.CreateProductResponse
import com.example.internal.input.reqreply.product.DeleteProductResponse
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import com.example.internal.input.reqreply.product.UpdateProductRequest
import com.example.internal.input.reqreply.product.UpdateProductResponse
import java.math.BigDecimal

object ProductProtoMapper {
    fun CreateProductDTO.toCreateProductRequest(): CreateProductRequest {
        return CreateProductRequest.newBuilder().also {
            it.price = price.toString()
            it.amount = amount
            it.measurement = measurement
            it.name = name
        }.build()
    }

    fun CreateProductResponse.toDTO(): ProductDTO {
        return when (this.responseCase!!) {
            CreateProductResponse.ResponseCase.SUCCESS -> success.product.toDTO()
            CreateProductResponse.ResponseCase.FAILURE -> error(failure.message)
            CreateProductResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
        }
    }

    fun FindProductByIdResponse.toDTO(): ProductDTO {
        return when (this.responseCase!!) {
            FindProductByIdResponse.ResponseCase.SUCCESS -> success.product.toDTO()
            FindProductByIdResponse.ResponseCase.FAILURE -> failure.asException()
            FindProductByIdResponse.ResponseCase.RESPONSE_NOT_SET ->
                throw RuntimeException("Acquired message is empty!")
        }
    }

    fun UpdateProductResponse.toDTO(): ProductDTO {
        return when (this.responseCase!!) {
            UpdateProductResponse.ResponseCase.SUCCESS -> success.product.toDTO()
            UpdateProductResponse.ResponseCase.FAILURE -> failure.asException()
            UpdateProductResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
        }
    }

    fun DeleteProductResponse.toDTO() {
        return when (this.responseCase!!) {
            DeleteProductResponse.ResponseCase.SUCCESS -> Unit
            DeleteProductResponse.ResponseCase.FAILURE -> error(failure.message)
            DeleteProductResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
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

    private fun FindProductByIdResponse.Failure.asException(): Nothing {
        throw when (errorCase!!) {
            FindProductByIdResponse.Failure.ErrorCase.PRODUCT_NOT_FOUND -> ProductNotFoundException(message)
            FindProductByIdResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(message)
        }
    }

    private fun UpdateProductResponse.Failure.asException(): Nothing {
        throw when (errorCase!!) {
            UpdateProductResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(message)
            UpdateProductResponse.Failure.ErrorCase.PRODUCT_NOT_FOUND -> ProductNotFoundException(message)
        }
    }
}
