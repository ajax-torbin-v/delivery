package com.example.delivery.controller.product

import com.example.delivery.mapper.ProductProtoMapper.toCreateProductDTO
import com.example.delivery.mapper.ProductProtoMapper.toCreateProductResponse
import com.example.delivery.mapper.ProductProtoMapper.toFailureCreateProductResponse
import com.example.delivery.service.ProductService
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
import kotlin.reflect.KClass

@Component
internal class CreateProductNatsHandler(
    private val productService: ProductService,
) : ProtoNatsMessageHandler<CreateProductRequest, CreateProductResponse> {
    override val log: Logger = LoggerFactory.getLogger(CreateProductNatsHandler::class.java)
    override val parser: Parser<CreateProductRequest> = CreateProductRequest.parser()
    override val queue: String = "product_group"
    override val subject: String = NatsSubject.Product.SAVE

    override fun doOnUnexpectedError(inMsg: CreateProductRequest?, e: Exception): Mono<CreateProductResponse> {
        log.error("Error while executing save for {}", inMsg, e)
        return CreateProductResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: CreateProductRequest): Mono<CreateProductResponse> {
        return productService.add(inMsg.toCreateProductDTO())
            .map { it.toCreateProductResponse() }
            .onErrorResume(isExpectedException) { error ->
                log.error("Error while executing", error)
                error.toFailureCreateProductResponse().toMono()
            }
    }

    private val isExpectedException: (Throwable) -> Boolean = {
        it::class in setOf<KClass<Throwable>>()
    }
}
