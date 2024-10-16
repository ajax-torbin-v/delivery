package com.example.delivery.mapper

import com.example.delivery.domain.DomainUser
import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.request.UpdateUserDTO
import com.example.delivery.exception.UserNotFoundException
import com.example.internal.commonmodels.Error
import com.example.internal.commonmodels.user.user.User
import com.example.internal.input.reqreply.user.create.CreateUserRequest
import com.example.internal.input.reqreply.user.create.CreateUserResponse
import com.example.internal.input.reqreply.user.delete.DeleteUserResponse
import com.example.internal.input.reqreply.user.find.FindUserByIdResponse
import com.example.internal.input.reqreply.user.update.UpdateUserRequest
import com.example.internal.input.reqreply.user.update.UpdateUserResponse

object UserProtoMapper {
    fun CreateUserRequest.toCreateUserDTO(): CreateUserDTO {
        return CreateUserDTO(user.fullName, user.phone, user.password)
    }

    fun UpdateUserRequest.toUpdateUserDTO(): UpdateUserDTO {
        return UpdateUserDTO(fullname, phone)
    }

    fun toDeleteUserResponse(): DeleteUserResponse {
        return DeleteUserResponse.newBuilder().apply { successBuilder }.build()
    }

    fun DomainUser.toUpdateUserResponse(): UpdateUserResponse {
        return UpdateUserResponse.newBuilder().also {
            buildUser(this, it.successBuilder.userBuilder)
        }.build()
    }

    fun DomainUser.toFindUserByIdResponse(): FindUserByIdResponse {
        return FindUserByIdResponse.newBuilder().also {
            buildUser(this, it.successBuilder.userBuilder)
        }.build()
    }

    fun DomainUser.toCreateUserResponse(): CreateUserResponse {
        return CreateUserResponse.newBuilder().also {
            buildUser(this, it.successBuilder.userBuilder)
        }.build()
    }

    fun Throwable.toFailureFindUserByIdResponse(): FindUserByIdResponse {
        return FindUserByIdResponse.newBuilder().also {
            it.failureBuilder.setMessage(this.message.orEmpty())
            when (this) {
                is UserNotFoundException -> it.failureBuilder.setUserNotFound(Error.getDefaultInstance())
            }
        }.build()
    }

    fun Throwable.toFailureCreateUserResponse(): CreateUserResponse {
        return CreateUserResponse.newBuilder().also {
            it.failureBuilder.setMessage(this.message.orEmpty())
        }.build()
    }

    fun Throwable.toFailureUpdateUserResponse(): UpdateUserResponse {
        return UpdateUserResponse.newBuilder().also {
            it.failureBuilder.setMessage(this.message.orEmpty())
            when (this) {
                is UserNotFoundException -> it.failureBuilder.setUserNotFound(Error.getDefaultInstance())
            }
        }.build()
    }

    fun Throwable.toFailureDeleteUserResponse(): DeleteUserResponse {
        return DeleteUserResponse.newBuilder().also {
            it.failureBuilder.setMessage(this.message.orEmpty())
        }.build()
    }

    private fun buildUser(domainUser: DomainUser, userBuilder: User.Builder) {
        userBuilder.apply {
            setId(domainUser.id)
            setPassword(domainUser.password)
            setPhone(domainUser.phone)
            setFullName(domainUser.fullName)
        }
    }
}
