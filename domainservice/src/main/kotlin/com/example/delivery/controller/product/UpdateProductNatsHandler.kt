package com.example.delivery.controller.product

import com.example.core.exception.ProductNotFoundException
import com.example.delivery.controller.user.UpdateUserNatsHandler
import com.example.delivery.mapper.ProductProtoMapper.toFailureUpdateProductResponse
import com.example.delivery.mapper.ProductProtoMapper.toUpdateProductDTO
import com.example.delivery.mapper.ProductProtoMapper.toUpdateProductResponse
import com.example.delivery.service.ProductService
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.product.UpdateProductRequest
import com.example.internal.input.reqreply.product.UpdateProductResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class UpdateProductNatsHandler(
    private val productService: ProductService,
) : ProtoNatsMessageHandler<UpdateProductRequest, UpdateProductResponse> {
    override val log: Logger = LoggerFactory.getLogger(UpdateUserNatsHandler::class.java)
    override val parser: Parser<UpdateProductRequest> = UpdateProductRequest.parser()
    override val queue: String = "product_group"
    override val subject: String = NatsSubject.Product.UPDATE

    override fun doOnUnexpectedError(inMsg: UpdateProductRequest?, e: Exception): Mono<UpdateProductResponse> {
        log.error("Error while executing update for {}", inMsg, e)
        return UpdateProductResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: UpdateProductRequest): Mono<UpdateProductResponse> {
        return productService.update(inMsg.id, inMsg.toUpdateProductDTO())
            .map { product -> product.toUpdateProductResponse() }
            .onErrorResume(isExpectedException) { error ->
                log.error("Error while executing update for {}", inMsg, error)
                error.toFailureUpdateProductResponse().toMono()
            }
    }

    private val isExpectedException: (Throwable) -> Boolean = {
        it::class in setOf(ProductNotFoundException::class)
    }
}
