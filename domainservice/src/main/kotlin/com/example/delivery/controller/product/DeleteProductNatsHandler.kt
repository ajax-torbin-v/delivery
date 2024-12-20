package com.example.delivery.controller.product

import com.example.delivery.mapper.ProductProtoMapper.toDeleteProductResponse
import com.example.delivery.mapper.ProductProtoMapper.toFailureDeleteProductResponse
import com.example.delivery.service.ProductService
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.product.DeleteProductRequest
import com.example.internal.input.reqreply.product.DeleteProductResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class DeleteProductNatsHandler(
    private val productService: ProductService,
) : ProtoNatsMessageHandler<DeleteProductRequest, DeleteProductResponse> {
    override val log: Logger = LoggerFactory.getLogger(DeleteProductNatsHandler::class.java)
    override val parser: Parser<DeleteProductRequest> = DeleteProductRequest.parser()
    override val queue: String = PRODUCT_QUEUE_GROUP
    override val subject: String = NatsSubject.Product.DELETE

    override fun doOnUnexpectedError(inMsg: DeleteProductRequest?, e: Exception): Mono<DeleteProductResponse> {
        return DeleteProductResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: DeleteProductRequest): Mono<DeleteProductResponse> {
        return productService.deleteById(inMsg.id)
            .map { toDeleteProductResponse() }
            .onErrorResume { error ->
                log.error("Error while executing delete for {}", inMsg, error)
                error.toFailureDeleteProductResponse().toMono()
            }
    }

    companion object {
        private const val PRODUCT_QUEUE_GROUP = "productQueueGroup"
    }
}
