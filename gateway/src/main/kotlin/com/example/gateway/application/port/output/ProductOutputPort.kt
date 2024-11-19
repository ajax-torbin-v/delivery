package com.example.gateway.application.port.output

import com.example.internal.input.reqreply.product.CreateProductRequest
import com.example.internal.input.reqreply.product.CreateProductResponse
import com.example.internal.input.reqreply.product.DeleteProductRequest
import com.example.internal.input.reqreply.product.DeleteProductResponse
import com.example.internal.input.reqreply.product.FindProductByIdRequest
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import com.example.internal.input.reqreply.product.UpdateProductRequest
import com.example.internal.input.reqreply.product.UpdateProductResponse
import reactor.core.publisher.Mono

interface ProductOutputPort {
    fun create(request: CreateProductRequest): Mono<CreateProductResponse>
    fun findById(request: FindProductByIdRequest): Mono<FindProductByIdResponse>
    fun update(request: UpdateProductRequest): Mono<UpdateProductResponse>
    fun delete(request: DeleteProductRequest): Mono<DeleteProductResponse>
}
