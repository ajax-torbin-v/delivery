package com.example.delivery.controller.user

import com.example.delivery.mapper.UserProtoMapper.toDeleteUserResponse
import com.example.delivery.mapper.UserProtoMapper.toFailureDeleteUserResponse
import com.example.delivery.service.UserService
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.user.DeleteUserRequest
import com.example.internal.input.reqreply.user.DeleteUserResponse
import com.google.protobuf.Parser
import kotlin.reflect.KClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class DeleteUserNatsHandler(
    private val userService: UserService,
) : ProtoNatsMessageHandler<DeleteUserRequest, DeleteUserResponse> {
    override val log: Logger = LoggerFactory.getLogger(DeleteUserNatsHandler::class.java)
    override val parser: Parser<DeleteUserRequest> = DeleteUserRequest.parser()
    override val queue: String = "user_group"
    override val subject: String = NatsSubject.User.DELETE
    override fun doOnUnexpectedError(inMsg: DeleteUserRequest?, e: Exception): Mono<DeleteUserResponse> {
        log.error("Error while executing delete for {}", inMsg, e)
        return DeleteUserResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: DeleteUserRequest): Mono<DeleteUserResponse> {
        return userService.deleteById(inMsg.id)
            .map { toDeleteUserResponse() }
            .onErrorResume(isExpectedException) { error ->
                log.error("Error while executing delete for {}", inMsg, error)
                error.toFailureDeleteUserResponse().toMono()
            }
    }

    private val isExpectedException: (Throwable) -> Boolean = {
        it::class in setOf<KClass<Throwable>>()
    }
}
