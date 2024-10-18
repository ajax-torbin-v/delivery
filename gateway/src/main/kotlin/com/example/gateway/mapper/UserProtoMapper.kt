package com.example.gateway.mapper

import com.example.core.dto.request.CreateUserDTO
import com.example.core.dto.request.UpdateUserDTO
import com.example.core.dto.response.UserDTO
import com.example.core.exception.UserNotFoundException
import com.example.internal.commonmodels.user.user.User
import com.example.internal.input.reqreply.user.create.CreateUserRequest
import com.example.internal.input.reqreply.user.create.CreateUserResponse
import com.example.internal.input.reqreply.user.delete.DeleteUserResponse
import com.example.internal.input.reqreply.user.find.FindUserByIdResponse
import com.example.internal.input.reqreply.user.find.FindUserByIdResponse.Failure.ErrorCase
import com.example.internal.input.reqreply.user.update.UpdateUserRequest
import com.example.internal.input.reqreply.user.update.UpdateUserResponse

object UserProtoMapper {

    fun CreateUserResponse.toDTO(): UserDTO {
        if (this == CreateUserResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (hasFailure()) {
            error(failure.message)
        }
        return success.user.toDTO()
    }

    fun FindUserByIdResponse.toDTO(): UserDTO {
        if (this == FindUserByIdResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (hasFailure()) {
            when (failure.errorCase!!) {
                ErrorCase.USER_NOT_FOUND -> throw UserNotFoundException(failure.message)
                ErrorCase.ERROR_NOT_SET -> error(failure.message)
            }
        }
        return success.user.toDTO()
    }

    fun UpdateUserResponse.toDTO(): UserDTO {
        if (this == UpdateUserResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (hasFailure()) {
            when (failure.errorCase!!) {
                UpdateUserResponse.Failure.ErrorCase.USER_NOT_FOUND -> throw UserNotFoundException(failure.message)
                UpdateUserResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(failure.message)
            }
        }
        return success.user.toDTO()
    }

    fun DeleteUserResponse.toDTO() {
        if (this == DeleteUserResponse.getDefaultInstance()) {
            throw RuntimeException("Acquired message is empty!")
        }
        if (this.hasFailure()) {
            error(failure.message)
        }
    }

    fun updateUserRequest(id: String, updateUserDTO: UpdateUserDTO): UpdateUserRequest {
        return UpdateUserRequest.newBuilder().also { builder ->
            builder.setId(id)
            updateUserDTO.phone?.let { builder.setPhone(it) }
            updateUserDTO.fullName?.let { builder.setFullname(it) }
        }.build()
    }

    fun CreateUserDTO.toCreateUserRequest(): CreateUserRequest {
        return CreateUserRequest.newBuilder()
            .also {
                it.userBuilder
                    .setFullName(this.fullName)
                    .setPhone(this.phone)
                    .setPassword(this.password)
            }.build()
    }

    private fun User.toDTO(): UserDTO {
        return UserDTO(
            id,
            fullName,
            phone
        )
    }
}
