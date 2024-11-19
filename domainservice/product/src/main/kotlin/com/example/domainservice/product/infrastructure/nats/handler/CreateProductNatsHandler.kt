package com.example.domainservice.product.infrastructure.nats.handler

import com.example.domainservice.product.application.port.input.ProductInputPort
import com.example.domainservice.product.infrastructure.nats.mapper.ProductProtoMapper.toCreateProductResponse
import com.example.domainservice.product.infrastructure.nats.mapper.ProductProtoMapper.toDomain
import com.example.domainservice.product.infrastructure.nats.mapper.ProductProtoMapper.toFailureCreateProductResponse
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.product.CreateProductRequest
import com.example.internal.input.reqreply.product.CreateProductResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class CreateProductNatsHandler(
    private val productInputPort: ProductInputPort,
) : ProtoNatsMessageHandler<CreateProductRequest, CreateProductResponse> {
    override val log: Logger = LoggerFactory.getLogger(CreateProductNatsHandler::class.java)
    override val parser: Parser<CreateProductRequest> = CreateProductRequest.parser()
    override val queue: String = PRODUCT_QUEUE_GROUP
    override val subject: String = NatsSubject.Product.SAVE

    override fun doOnUnexpectedError(inMsg: CreateProductRequest?, e: Exception): Mono<CreateProductResponse> {
        return CreateProductResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: CreateProductRequest): Mono<CreateProductResponse> {
        return productInputPort.save(inMsg.toDomain())
            .map { it.toCreateProductResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureCreateProductResponse().toMono()
            }
    }

    companion object {
        private const val PRODUCT_QUEUE_GROUP = "productQueueGroup"
    }
}
