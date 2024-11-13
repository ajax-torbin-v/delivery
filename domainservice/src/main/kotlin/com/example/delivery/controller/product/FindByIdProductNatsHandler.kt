package com.example.delivery.controller.product

import com.example.delivery.mapper.ProductProtoMapper.toFailureFindProductByIdResponse
import com.example.delivery.mapper.ProductProtoMapper.toFindProductByIdResponse
import com.example.delivery.service.ProductService
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.product.FindProductByIdRequest
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class FindByIdProductNatsHandler(
    private val productService: ProductService,
) : ProtoNatsMessageHandler<FindProductByIdRequest, FindProductByIdResponse> {
    override val log: Logger = LoggerFactory.getLogger(FindByIdProductNatsHandler::class.java)
    override val parser: Parser<FindProductByIdRequest> = FindProductByIdRequest.parser()
    override val queue: String = PRODUCT_QUEUE_GROUP
    override val subject: String = NatsSubject.Product.FIND_BY_ID

    override fun doOnUnexpectedError(inMsg: FindProductByIdRequest?, e: Exception): Mono<FindProductByIdResponse> {
        return FindProductByIdResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: FindProductByIdRequest): Mono<FindProductByIdResponse> {
        return productService.getById(inMsg.id)
            .map { product -> product.toFindProductByIdResponse() }
            .onErrorResume { error ->
                log.error("Error while executing findById for {}", inMsg, error)
                error.toFailureFindProductByIdResponse().toMono()
            }
    }

    companion object {
        private const val PRODUCT_QUEUE_GROUP = "productQueueGroup"
    }
}
