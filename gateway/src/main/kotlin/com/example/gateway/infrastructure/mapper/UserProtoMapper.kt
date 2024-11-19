package com.example.gateway.infrastructure.mapper

import com.example.commonmodels.user.User
import com.example.core.dto.request.CreateUserDTO
import com.example.core.dto.request.UpdateUserDTO
import com.example.core.dto.response.UserDTO
import com.example.core.exception.UserNotFoundException
import com.example.internal.input.reqreply.user.CreateUserRequest
import com.example.internal.input.reqreply.user.CreateUserResponse
import com.example.internal.input.reqreply.user.DeleteUserResponse
import com.example.internal.input.reqreply.user.FindUserByIdResponse
import com.example.internal.input.reqreply.user.FindUserByIdResponse.Failure.ErrorCase
import com.example.internal.input.reqreply.user.UpdateUserRequest
import com.example.internal.input.reqreply.user.UpdateUserResponse

object UserProtoMapper {

    fun CreateUserResponse.toDTO(): UserDTO {
        when (this.responseCase!!) {
            CreateUserResponse.ResponseCase.SUCCESS -> return success.user.toDTO()
            CreateUserResponse.ResponseCase.FAILURE -> error(failure.message)
            CreateUserResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
        }
    }

    fun FindUserByIdResponse.toDTO(): UserDTO {
        return when (this.responseCase!!) {
            FindUserByIdResponse.ResponseCase.SUCCESS -> success.user.toDTO()
            FindUserByIdResponse.ResponseCase.FAILURE -> failure.asException()
            FindUserByIdResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
        }
    }

    fun UpdateUserResponse.toDTO(): UserDTO {
        return when (this.responseCase!!) {
            UpdateUserResponse.ResponseCase.SUCCESS -> success.user.toDTO()
            UpdateUserResponse.ResponseCase.FAILURE -> failure.asException()
            UpdateUserResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
        }
    }

    fun DeleteUserResponse.toDTO() {
        return when (this.responseCase!!) {
            DeleteUserResponse.ResponseCase.SUCCESS -> Unit
            DeleteUserResponse.ResponseCase.FAILURE -> error(failure.message)
            DeleteUserResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
        }
    }

    fun updateUserRequest(id: String, updateUserDTO: UpdateUserDTO): UpdateUserRequest {
        return UpdateUserRequest.newBuilder().also { builder ->
            builder.id = id
            updateUserDTO.phone?.let { builder.phone = it }
            updateUserDTO.fullName?.let { builder.fullname = it }
        }.build()
    }

    fun CreateUserDTO.toCreateUserRequest(): CreateUserRequest {
        return CreateUserRequest.newBuilder().also {
            it.fullName = fullName
            it.phone = phone
            it.password = password
        }.build()
    }

    private fun User.toDTO(): UserDTO {
        return UserDTO(
            id,
            fullName,
            phone
        )
    }

    private fun FindUserByIdResponse.Failure.asException(): Nothing {
        throw when (errorCase!!) {
            ErrorCase.USER_NOT_FOUND -> UserNotFoundException(message)
            ErrorCase.ERROR_NOT_SET -> error(message)
        }
    }

    private fun UpdateUserResponse.Failure.asException(): Nothing {
        throw when (errorCase!!) {
            UpdateUserResponse.Failure.ErrorCase.USER_NOT_FOUND -> UserNotFoundException(message)
            UpdateUserResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(message)
        }
    }
}
