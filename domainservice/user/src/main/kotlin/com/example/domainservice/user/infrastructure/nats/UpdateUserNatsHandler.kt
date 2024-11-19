package com.example.domainservice.user.infrastructure.nats

import com.example.domainservice.user.application.port.input.UserInputPort
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toDomain
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toFailureUpdateUserResponse
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toUpdateUserResponse
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.user.UpdateUserRequest
import com.example.internal.input.reqreply.user.UpdateUserResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class UpdateUserNatsHandler(
    private val userInputPort: UserInputPort,
) : ProtoNatsMessageHandler<UpdateUserRequest, UpdateUserResponse> {
    override val log: Logger = LoggerFactory.getLogger(UpdateUserNatsHandler::class.java)
    override val parser: Parser<UpdateUserRequest> = UpdateUserRequest.parser()
    override val queue: String = USER_QUEUE_GROUP
    override val subject: String = NatsSubject.User.UPDATE

    override fun doOnUnexpectedError(inMsg: UpdateUserRequest?, e: Exception): Mono<UpdateUserResponse> {
        return UpdateUserResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: UpdateUserRequest): Mono<UpdateUserResponse> {
        return userInputPort.update(inMsg.toDomain())
            .map { user -> user.toUpdateUserResponse() }
            .onErrorResume { error ->
                log.error("Error while executing update for {}", inMsg, error)
                error.toFailureUpdateUserResponse().toMono()
            }
    }

    companion object {
        private const val USER_QUEUE_GROUP = "userQueueGroup"
    }
}
