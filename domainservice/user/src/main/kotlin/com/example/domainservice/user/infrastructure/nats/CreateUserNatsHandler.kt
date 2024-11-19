package com.example.domainservice.user.infrastructure.nats

import com.example.domainservice.user.application.port.input.UserInputPort
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toCreateUserResponse
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toDomain
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toFailureCreateUserResponse
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
    private val userInputPort: UserInputPort,
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
        return userInputPort.save(inMsg.toDomain())
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
