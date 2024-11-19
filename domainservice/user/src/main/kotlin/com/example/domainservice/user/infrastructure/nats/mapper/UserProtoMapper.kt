package com.example.domainservice.user.infrastructure.nats.mapper

import com.example.commonmodels.user.User
import com.example.core.exception.UserNotFoundException
import com.example.domainservice.user.domain.DomainUser
import com.example.internal.input.reqreply.user.CreateUserRequest
import com.example.internal.input.reqreply.user.CreateUserResponse
import com.example.internal.input.reqreply.user.DeleteUserResponse
import com.example.internal.input.reqreply.user.FindUserByIdResponse
import com.example.internal.input.reqreply.user.UpdateUserRequest
import com.example.internal.input.reqreply.user.UpdateUserResponse

object UserProtoMapper {

    fun CreateUserRequest.toDomain(): DomainUser {
        return DomainUser(
            id = null,
            fullName = fullName,
            phone = phone,
            password = password
        )
    }

    fun UpdateUserRequest.toDomain(): DomainUser {
        return DomainUser(
            id = id,
            fullName = fullname,
            phone = phone,
            password = password
        )
    }

    fun toDeleteUserResponse(): DeleteUserResponse {
        return DeleteUserResponse.newBuilder().apply { successBuilder }.build()
    }

    fun DomainUser.toUpdateUserResponse(): UpdateUserResponse {
        return UpdateUserResponse.newBuilder().also {
            it.successBuilder.user = this.toProto()
        }.build()
    }

    fun DomainUser.toFindUserByIdResponse(): FindUserByIdResponse {
        return FindUserByIdResponse.newBuilder().also {
            it.successBuilder.user = this.toProto()
        }.build()
    }

    fun DomainUser.toCreateUserResponse(): CreateUserResponse {
        return CreateUserResponse.newBuilder().also {
            it.successBuilder.user = this.toProto()
        }.build()
    }

    fun Throwable.toFailureFindUserByIdResponse(): FindUserByIdResponse {
        return FindUserByIdResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
            when (this@toFailureFindUserByIdResponse) {
                is UserNotFoundException -> failureBuilder.userNotFoundBuilder
            }
        }.build()
    }

    fun Throwable.toFailureCreateUserResponse(): CreateUserResponse {
        return CreateUserResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
        }.build()
    }

    fun Throwable.toFailureUpdateUserResponse(): UpdateUserResponse {
        return UpdateUserResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
            when (this@toFailureUpdateUserResponse) {
                is UserNotFoundException -> failureBuilder.userNotFoundBuilder
            }
        }.build()
    }

    fun Throwable.toFailureDeleteUserResponse(): DeleteUserResponse {
        return DeleteUserResponse.newBuilder().apply {
            failureBuilder.message = message.orEmpty()
        }.build()
    }

    fun DomainUser.toProto(): User {
        return User.newBuilder().also {
            it.id = id
            it.password = password
            it.phone = phone
            it.fullName = fullName
        }.build()
    }
}
