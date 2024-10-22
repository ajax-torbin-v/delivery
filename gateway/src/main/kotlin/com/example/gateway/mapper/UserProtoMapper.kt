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
        when (this.responseCase!!) {
            CreateUserResponse.ResponseCase.SUCCESS -> return success.user.toDTO()
            CreateUserResponse.ResponseCase.FAILURE -> error(failure.message)
            CreateUserResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
        }
    }

    fun FindUserByIdResponse.toDTO(): UserDTO {
        return when (this.responseCase!!) {
            FindUserByIdResponse.ResponseCase.SUCCESS -> success.user.toDTO()
            FindUserByIdResponse.ResponseCase.FAILURE -> failureCase()
            FindUserByIdResponse.ResponseCase.RESPONSE_NOT_SET -> throw RuntimeException("Acquired message is empty!")
        }
    }

    fun UpdateUserResponse.toDTO(): UserDTO {
        return when (this.responseCase!!) {
            UpdateUserResponse.ResponseCase.SUCCESS -> success.user.toDTO()
            UpdateUserResponse.ResponseCase.FAILURE -> {
                failureCase()
            }

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

    private fun FindUserByIdResponse.failureCase(): Nothing {
        when (failure.errorCase!!) {
            ErrorCase.USER_NOT_FOUND -> throw UserNotFoundException(failure.message)
            ErrorCase.ERROR_NOT_SET -> error(failure.message)
        }
    }

    private fun UpdateUserResponse.failureCase(): Nothing {
        when (failure.errorCase!!) {
            UpdateUserResponse.Failure.ErrorCase.USER_NOT_FOUND -> throw UserNotFoundException(failure.message)
            UpdateUserResponse.Failure.ErrorCase.ERROR_NOT_SET -> error(failure.message)
        }
    }
}
