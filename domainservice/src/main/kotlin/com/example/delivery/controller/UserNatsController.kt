package com.example.delivery.controller

import com.example.delivery.annotaion.NatsController
import com.example.delivery.annotaion.NatsHandler
import com.example.delivery.controller.UserNatsController.Companion.QUEUE_GROUP
import com.example.delivery.mapper.UserProtoMapper.toCreateUserDTO
import com.example.delivery.mapper.UserProtoMapper.toCreateUserResponse
import com.example.delivery.mapper.UserProtoMapper.toDeleteUserResponse
import com.example.delivery.mapper.UserProtoMapper.toFailureCreateUserResponse
import com.example.delivery.mapper.UserProtoMapper.toFailureDeleteUserResponse
import com.example.delivery.mapper.UserProtoMapper.toFailureFindUserByIdResponse
import com.example.delivery.mapper.UserProtoMapper.toFailureUpdateUserResponse
import com.example.delivery.mapper.UserProtoMapper.toFindUserByIdResponse
import com.example.delivery.mapper.UserProtoMapper.toUpdateUserDTO
import com.example.delivery.mapper.UserProtoMapper.toUpdateUserResponse
import com.example.delivery.service.UserService
import com.example.internal.api.subject.UserNatsSubject
import com.example.internal.input.reqreply.user.create.CreateUserRequest
import com.example.internal.input.reqreply.user.create.CreateUserResponse
import com.example.internal.input.reqreply.user.delete.DeleteUserRequest
import com.example.internal.input.reqreply.user.delete.DeleteUserResponse
import com.example.internal.input.reqreply.user.find.FindUserByIdRequest
import com.example.internal.input.reqreply.user.find.FindUserByIdResponse
import com.example.internal.input.reqreply.user.update.UpdateUserRequest
import com.example.internal.input.reqreply.user.update.UpdateUserResponse
import io.nats.client.Connection
import io.nats.client.Dispatcher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
@NatsController(subjectPrefix = UserNatsSubject.USER_PREFIX, queueGroup = QUEUE_GROUP)
class UserNatsController(
    private val userService: UserService,
    connection: Connection,
    dispatcher: Dispatcher,
) : AbstractNatsController(connection, dispatcher) {

    @NatsHandler(subject = UserNatsSubject.SAVE)
    fun save(response: CreateUserRequest): Mono<CreateUserResponse> {
        return userService.add(response.toCreateUserDTO())
            .map { user -> user.toCreateUserResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureCreateUserResponse().toMono()
            }
    }

    @NatsHandler(subject = UserNatsSubject.FIND_BY_ID)
    fun findById(request: FindUserByIdRequest): Mono<FindUserByIdResponse> {
        return userService.getById(request.id)
            .map { user -> user.toFindUserByIdResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureFindUserByIdResponse().toMono()
            }
    }

    @NatsHandler(subject = UserNatsSubject.UPDATE)
    fun update(request: UpdateUserRequest): Mono<UpdateUserResponse> {
        return userService.update(request.id, request.toUpdateUserDTO())
            .map { user -> user.toUpdateUserResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureUpdateUserResponse().toMono()
            }
    }

    @NatsHandler(subject = UserNatsSubject.DELETE)
    fun delete(request: DeleteUserRequest): Mono<DeleteUserResponse> {
        return userService.deleteById(request.id)
            .map { toDeleteUserResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureDeleteUserResponse().toMono()
            }
    }

    companion object {
        const val QUEUE_GROUP = "user_group"
        private val log = LoggerFactory.getLogger(UserNatsController::class.java)
    }
}
