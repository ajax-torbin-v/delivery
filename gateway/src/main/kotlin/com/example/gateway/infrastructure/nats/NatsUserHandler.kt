package com.example.gateway.infrastructure.nats

import com.example.gateway.application.port.output.UserOutputPort
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.user.CreateUserRequest
import com.example.internal.input.reqreply.user.CreateUserResponse
import com.example.internal.input.reqreply.user.DeleteUserRequest
import com.example.internal.input.reqreply.user.DeleteUserResponse
import com.example.internal.input.reqreply.user.FindUserByIdRequest
import com.example.internal.input.reqreply.user.FindUserByIdResponse
import com.example.internal.input.reqreply.user.UpdateUserRequest
import com.example.internal.input.reqreply.user.UpdateUserResponse
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@Component
class NatsUserHandler(
    private val natsMessagePublisher: NatsMessagePublisher,
) : UserOutputPort {
    override fun create(request: CreateUserRequest): Mono<CreateUserResponse> {
        return natsMessagePublisher.request(
            NatsSubject.User.SAVE,
            request,
            CreateUserResponse.parser(),
        )
    }

    override fun findById(request: FindUserByIdRequest): Mono<FindUserByIdResponse> {
        return natsMessagePublisher.request(
            NatsSubject.User.FIND_BY_ID,
            request,
            FindUserByIdResponse.parser()
        )
    }

    override fun update(request: UpdateUserRequest): Mono<UpdateUserResponse> {
        return natsMessagePublisher.request(
            NatsSubject.User.UPDATE,
            request,
            UpdateUserResponse.parser()
        )
    }

    override fun delete(request: DeleteUserRequest): Mono<DeleteUserResponse> {
        return natsMessagePublisher.request(
            NatsSubject.User.DELETE,
            request,
            DeleteUserResponse.parser()
        )
    }
}
