package com.example.delivery.controller.user

import com.example.core.exception.UserNotFoundException
import com.example.delivery.mapper.UserProtoMapper.toFailureUpdateUserResponse
import com.example.delivery.mapper.UserProtoMapper.toUpdateUserDTO
import com.example.delivery.mapper.UserProtoMapper.toUpdateUserResponse
import com.example.delivery.service.UserService
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
    private val userService: UserService,
) : ProtoNatsMessageHandler<UpdateUserRequest, UpdateUserResponse> {
    override val log: Logger = LoggerFactory.getLogger(UpdateUserNatsHandler::class.java)
    override val parser: Parser<UpdateUserRequest> = UpdateUserRequest.parser()
    override val queue: String = "user_group"
    override val subject: String = NatsSubject.User.UPDATE

    override fun doOnUnexpectedError(inMsg: UpdateUserRequest?, e: Exception): Mono<UpdateUserResponse> {
        log.error("Error while executing update for {}", inMsg, e)
        return UpdateUserResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: UpdateUserRequest): Mono<UpdateUserResponse> {
        return userService.update(inMsg.id, inMsg.toUpdateUserDTO())
            .map { user -> user.toUpdateUserResponse() }
            .onErrorResume(isExpectedError) { error ->
                log.error("Error while executing update for {}", inMsg, error)
                error.toFailureUpdateUserResponse().toMono()
            }
    }

    val isExpectedError: (Throwable) -> Boolean = {
        it::class in setOf(UserNotFoundException::class)
    }
}
