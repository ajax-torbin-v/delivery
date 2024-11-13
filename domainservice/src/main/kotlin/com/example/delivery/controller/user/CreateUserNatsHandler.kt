package com.example.delivery.controller.user

import com.example.delivery.mapper.UserProtoMapper.toCreateUserDTO
import com.example.delivery.mapper.UserProtoMapper.toCreateUserResponse
import com.example.delivery.mapper.UserProtoMapper.toFailureCreateUserResponse
import com.example.delivery.service.UserService
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.user.CreateUserRequest
import com.example.internal.input.reqreply.user.CreateUserResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class CreateUserNatsHandler(
    private val userService: UserService,
) : ProtoNatsMessageHandler<CreateUserRequest, CreateUserResponse> {
    override val log: Logger = LoggerFactory.getLogger(CreateUserNatsHandler::class.java)
    override val parser: Parser<CreateUserRequest> = CreateUserRequest.parser()
    override val queue: String = USER_QUEUE_GROUP
    override val subject: String = NatsSubject.User.SAVE

    override fun doOnUnexpectedError(inMsg: CreateUserRequest?, e: Exception): Mono<CreateUserResponse> {
        return CreateUserResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: CreateUserRequest): Mono<CreateUserResponse> {
        return userService.add(inMsg.toCreateUserDTO())
            .map { it.toCreateUserResponse() }
            .onErrorResume { error ->
                log.error("Error while executing save for {}", inMsg, error)
                error.toFailureCreateUserResponse().toMono()
            }
    }

    companion object {
        private const val USER_QUEUE_GROUP = "userQueueGroup"
    }
}
