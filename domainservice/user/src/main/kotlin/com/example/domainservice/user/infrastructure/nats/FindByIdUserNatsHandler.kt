package com.example.domainservice.user.infrastructure.nats

import com.example.domainservice.user.application.port.input.UserInputPort
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toFailureFindUserByIdResponse
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toFindUserByIdResponse
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
    private val userInputPort: UserInputPort,
) : ProtoNatsMessageHandler<FindUserByIdRequest, FindUserByIdResponse> {
    override val log: Logger = LoggerFactory.getLogger(FindByIdUserNatsHandler::class.java)
    override val parser: Parser<FindUserByIdRequest> = FindUserByIdRequest.parser()
    override val queue: String = USER_QUEUE_GROUP
    override val subject: String = NatsSubject.User.FIND_BY_ID

    override fun doOnUnexpectedError(inMsg: FindUserByIdRequest?, e: Exception): Mono<FindUserByIdResponse> {
        return FindUserByIdResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: FindUserByIdRequest): Mono<FindUserByIdResponse> {
        return userInputPort.getById(inMsg.id)
            .map { user -> user.toFindUserByIdResponse() }
            .onErrorResume { error ->
                log.error("Error while executing findById for {}", inMsg, error)
                error.toFailureFindUserByIdResponse().toMono()
            }
    }

    companion object {
        private const val USER_QUEUE_GROUP = "userQueueGroup"
    }
}