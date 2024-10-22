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
            FindProductByIdResponse.ResponseCase.FAILURE -> failureCase()
            FindProductByIdResponse.ResponseCase.RESPONSE_NOT_SET ->
                throw RuntimeException("Acquired message is empty!")
        }
    }

    fun UpdateProductResponse.toDTO(): ProductDTO {
        return when (this.responseCase!!) {
            UpdateProductResponse.ResponseCase.SUCCESS -> success.product.toDTO()
            UpdateProductResponse.ResponseCase.FAILURE -> failureCase()
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

    private fun FindProductByIdResponse.failureCase(): Nothing {
        when (failure.errorCase!!) {
            FindProductByIdResponse.Failure.ErrorCase.PRODUCT_NOT_FOUND ->
                throw ProductNotFoundException(failure.message)

            FindProductByIdResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(failure.message)
        }
    }

    private fun UpdateProductResponse.failureCase(): Nothing {
        when (failure.errorCase!!) {
            UpdateProductResponse.Failure.ErrorCase.ERROR_NOT_SET ->
                error(failure.message)

            UpdateProductResponse.Failure.ErrorCase.PRODUCT_NOT_FOUND ->
                throw ProductNotFoundException(failure.message)
        }
    }

}
