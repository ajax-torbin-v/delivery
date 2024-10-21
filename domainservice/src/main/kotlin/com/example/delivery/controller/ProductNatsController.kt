package com.example.delivery.controller

import com.example.delivery.annotaion.NatsController
import com.example.delivery.annotaion.NatsHandler
import com.example.delivery.controller.ProductNatsController.Companion.QUEUE_GROUP
import com.example.delivery.mapper.ProductProtoMapper.toCreateProductDTO
import com.example.delivery.mapper.ProductProtoMapper.toCreateProductResponse
import com.example.delivery.mapper.ProductProtoMapper.toDeleteProductResponse
import com.example.delivery.mapper.ProductProtoMapper.toFailureCreateProductResponse
import com.example.delivery.mapper.ProductProtoMapper.toFailureDeleteProductResponse
import com.example.delivery.mapper.ProductProtoMapper.toFailureFindProductByIdResponse
import com.example.delivery.mapper.ProductProtoMapper.toFailureUpdateProductResponse
import com.example.delivery.mapper.ProductProtoMapper.toFindProductByIdResponse
import com.example.delivery.mapper.ProductProtoMapper.toUpdateProductDTO
import com.example.delivery.mapper.ProductProtoMapper.toUpdateProductResponse
import com.example.delivery.service.ProductService
import com.example.internal.api.subject.NatsSubject
import com.example.internal.commonmodels.input.reqreply.product.delete.DeleteProductRequest
import com.example.internal.commonmodels.input.reqreply.product.delete.DeleteProductResponse
import com.example.internal.input.reqreply.product.create.CreateProductRequest
import com.example.internal.input.reqreply.product.create.CreateProductResponse
import com.example.internal.input.reqreply.product.find.FindProductByIdRequest
import com.example.internal.input.reqreply.product.find.FindProductByIdResponse
import com.example.internal.input.reqreply.product.update.UpdateProductRequest
import com.example.internal.input.reqreply.product.update.UpdateProductResponse
import io.nats.client.Connection
import io.nats.client.Dispatcher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
@NatsController(queueGroup = QUEUE_GROUP)
class ProductNatsController(
    private val productService: ProductService,
    connection: Connection,
    dispatcher: Dispatcher,
) : AbstractNatsController(connection, dispatcher) {
    @NatsHandler(subject = NatsSubject.Product.PRODUCT_SAVE)
    fun add(request: CreateProductRequest): Mono<CreateProductResponse> {
        return productService.add(request.toCreateProductDTO())
            .map { it.toCreateProductResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureCreateProductResponse().toMono()
            }
    }

    @NatsHandler(subject = NatsSubject.Product.PRODUCT_FIND_BY_ID)
    fun findById(request: FindProductByIdRequest): Mono<FindProductByIdResponse> {
        return productService.getById(request.id)
            .map { product -> product.toFindProductByIdResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureFindProductByIdResponse().toMono()
            }
    }

    @NatsHandler(subject = NatsSubject.Product.PRODUCT_UPDATE)
    fun update(request: UpdateProductRequest): Mono<UpdateProductResponse> {
        return productService.update(request.id, request.toUpdateProductDTO())
            .map { product -> product.toUpdateProductResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureUpdateProductResponse().toMono()
            }
    }

    @NatsHandler(subject = NatsSubject.Product.PRODUCT_DELETE)
    fun delete(request: DeleteProductRequest): Mono<DeleteProductResponse> {
        return productService.deleteById(request.id)
            .map { toDeleteProductResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureDeleteProductResponse().toMono()
            }
    }

    companion object {
        const val QUEUE_GROUP = "product_group"
        private val log = LoggerFactory.getLogger(ProductNatsController::class.java)
    }
}
