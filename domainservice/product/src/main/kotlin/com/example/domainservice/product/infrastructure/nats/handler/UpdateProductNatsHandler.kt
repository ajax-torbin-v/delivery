package com.example.domainservice.product.infrastructure.nats.handler

import com.example.domainservice.product.application.port.input.ProductInputPort
import com.example.domainservice.product.infrastructure.nats.mapper.ProductProtoMapper.toDomain
import com.example.domainservice.product.infrastructure.nats.mapper.ProductProtoMapper.toFailureUpdateProductResponse
import com.example.domainservice.product.infrastructure.nats.mapper.ProductProtoMapper.toUpdateProductResponse
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
    private val productInputPort: ProductInputPort,
) : ProtoNatsMessageHandler<UpdateProductRequest, UpdateProductResponse> {
    override val log: Logger = LoggerFactory.getLogger(UpdateProductNatsHandler::class.java)
    override val parser: Parser<UpdateProductRequest> = UpdateProductRequest.parser()
    override val queue: String = PRODUCT_QUEUE_GROUP
    override val subject: String = NatsSubject.Product.UPDATE

    override fun doOnUnexpectedError(inMsg: UpdateProductRequest?, e: Exception): Mono<UpdateProductResponse> {
        return UpdateProductResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: UpdateProductRequest): Mono<UpdateProductResponse> {
        return productInputPort.update(inMsg.toDomain())
            .map { product -> product.toUpdateProductResponse() }
            .onErrorResume { error ->
                log.error("Error while executing update for {}", inMsg, error)
                error.toFailureUpdateProductResponse().toMono()
            }
    }

    companion object {
        private const val PRODUCT_QUEUE_GROUP = "productQueueGroup"
    }
}
