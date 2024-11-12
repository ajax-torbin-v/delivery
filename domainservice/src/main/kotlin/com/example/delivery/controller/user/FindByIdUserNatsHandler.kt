package com.example.delivery.controller.user

import com.example.core.exception.UserNotFoundException
import com.example.delivery.mapper.UserProtoMapper.toFailureFindUserByIdResponse
import com.example.delivery.mapper.UserProtoMapper.toFindUserByIdResponse
import com.example.delivery.service.UserService
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.user.FindUserByIdRequest
import com.example.internal.input.reqreply.user.FindUserByIdResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler


@Component
internal class FindByIdUserNatsHandler(
    private val userService: UserService,
) : ProtoNatsMessageHandler<FindUserByIdRequest, FindUserByIdResponse> {
    override val log: Logger = LoggerFactory.getLogger(FindByIdUserNatsHandler::class.java)
    override val parser: Parser<FindUserByIdRequest> = FindUserByIdRequest.parser()
    override val queue: String = "user_group"
    override val subject: String = NatsSubject.User.FIND_BY_ID

    override fun doOnUnexpectedError(inMsg: FindUserByIdRequest?, e: Exception): Mono<FindUserByIdResponse> {
        log.error("Error while executing findById for {}", inMsg, e)
        return FindUserByIdResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: FindUserByIdRequest): Mono<FindUserByIdResponse> {
        return userService.getById(inMsg.id)
            .map { user -> user.toFindUserByIdResponse() }
            .onErrorResume(isExpectedException) { error ->
                log.error("Error while executing findById for {}", inMsg, error)
                error.toFailureFindUserByIdResponse().toMono()
            }
    }

    private val isExpectedException: (Throwable) -> Boolean = {
        it::class in setOf(UserNotFoundException::class)
    }
}
