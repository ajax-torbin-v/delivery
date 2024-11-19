package com.example.domainservice.user.infrastructure.nats

import com.example.domainservice.user.application.port.input.UserInputPort
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toDeleteUserResponse
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toFailureDeleteUserResponse
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.user.DeleteUserRequest
import com.example.internal.input.reqreply.user.DeleteUserResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class DeleteUserNatsHandler(
    private val userInputPort: UserInputPort,
) : ProtoNatsMessageHandler<DeleteUserRequest, DeleteUserResponse> {
    override val log: Logger = LoggerFactory.getLogger(DeleteUserNatsHandler::class.java)
    override val parser: Parser<DeleteUserRequest> = DeleteUserRequest.parser()
    override val queue: String = USER_QUEUE_GROUP
    override val subject: String = NatsSubject.User.DELETE

    override fun doOnUnexpectedError(inMsg: DeleteUserRequest?, e: Exception): Mono<DeleteUserResponse> {
        return DeleteUserResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: DeleteUserRequest): Mono<DeleteUserResponse> {
        return userInputPort.delete(inMsg.id)
            .map { toDeleteUserResponse() }
            .onErrorResume { error ->
                log.error("Error while executing delete for {}", inMsg, error)
                error.toFailureDeleteUserResponse().toMono()
            }
    }

    companion object {
        private const val USER_QUEUE_GROUP = "userQueueGroup"
    }
}