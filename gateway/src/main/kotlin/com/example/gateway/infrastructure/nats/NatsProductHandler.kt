package com.example.gateway.infrastructure.nats

import com.example.gateway.application.port.output.ProductOutputPort
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.product.CreateProductRequest
import com.example.internal.input.reqreply.product.CreateProductResponse
import com.example.internal.input.reqreply.product.DeleteProductRequest
import com.example.internal.input.reqreply.product.DeleteProductResponse
import com.example.internal.input.reqreply.product.FindProductByIdRequest
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import com.example.internal.input.reqreply.product.UpdateProductRequest
import com.example.internal.input.reqreply.product.UpdateProductResponse
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@Component
class NatsProductHandler(
    private val natsMessagePublisher: NatsMessagePublisher,
) : ProductOutputPort {
    override fun create(request: CreateProductRequest): Mono<CreateProductResponse> {
        return natsMessagePublisher.request(
            NatsSubject.Product.SAVE,
            request,
            CreateProductResponse.parser()
        )
    }

    override fun findById(request: FindProductByIdRequest): Mono<FindProductByIdResponse> {
        return natsMessagePublisher.request(
            NatsSubject.Product.FIND_BY_ID,
            request,
            FindProductByIdResponse.parser(),
        )
    }

    override fun update(request: UpdateProductRequest): Mono<UpdateProductResponse> {
        return natsMessagePublisher.request(
            NatsSubject.Product.UPDATE,
            request,
            UpdateProductResponse.parser()
        )
    }

    override fun delete(request: DeleteProductRequest): Mono<DeleteProductResponse> {
        return natsMessagePublisher.request(
            NatsSubject.Product.DELETE,
            request,
            DeleteProductResponse.parser()
        )
    }
}
