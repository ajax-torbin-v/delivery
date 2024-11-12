package com.example.delivery.controller.product

import com.example.internal.input.reqreply.product.FindProductByIdRequest
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class FindByIdProductNatsHandler : ProtoNatsMessageHandler<FindProductByIdRequest, FindProductByIdResponse> {
    override val log: Logger = LoggerFactory.getLogger(FindByIdProductNatsHandler::class.java)
    override val parser: Parser<FindProductByIdRequest> = FindProductByIdRequest.parser()
    override val queue: String = "product_group"
    override val subject: String
        get() = TODO("Not yet implemented")

    override fun doOnUnexpectedError(inMsg: FindProductByIdRequest?, e: Exception): Mono<FindProductByIdResponse> {
        TODO("Not yet implemented")
    }

    override fun doHandle(inMsg: FindProductByIdRequest): Mono<FindProductByIdResponse> {
        TODO("Not yet implemented")
    }
}
