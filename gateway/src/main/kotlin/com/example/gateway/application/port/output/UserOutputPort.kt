package com.example.gateway.application.port.output

import com.example.internal.input.reqreply.user.CreateUserRequest
import com.example.internal.input.reqreply.user.CreateUserResponse
import com.example.internal.input.reqreply.user.DeleteUserRequest
import com.example.internal.input.reqreply.user.DeleteUserResponse
import com.example.internal.input.reqreply.user.FindUserByIdRequest
import com.example.internal.input.reqreply.user.FindUserByIdResponse
import com.example.internal.input.reqreply.user.UpdateUserRequest
import com.example.internal.input.reqreply.user.UpdateUserResponse
import reactor.core.publisher.Mono

interface UserOutputPort {
    fun create(request: CreateUserRequest): Mono<CreateUserResponse>
    fun findById(request: FindUserByIdRequest): Mono<FindUserByIdResponse>
    fun update(request: UpdateUserRequest): Mono<UpdateUserResponse>
    fun delete(request: DeleteUserRequest): Mono<DeleteUserResponse>
}
