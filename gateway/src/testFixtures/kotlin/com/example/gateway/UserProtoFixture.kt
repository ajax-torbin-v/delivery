package com.example.gateway

import com.example.core.UserFixture.randomFullName
import com.example.core.UserFixture.randomPhone
import com.example.core.UserFixture.randomUpdatedFullName
import com.example.core.UserFixture.randomUpdatedPhone
import com.example.core.UserFixture.randomUserId
import com.example.internal.input.reqreply.user.CreateUserResponse
import com.example.internal.input.reqreply.user.DeleteUserRequest
import com.example.internal.input.reqreply.user.DeleteUserResponse
import com.example.internal.input.reqreply.user.FindUserByIdResponse
import com.example.internal.input.reqreply.user.UpdateUserResponse

object UserProtoFixture {
    val createUserResponse = CreateUserResponse.newBuilder().apply {
        successBuilder.userBuilder.apply {
            id = randomUserId
            fullName = randomFullName
            phone = randomPhone
            password = "password"
        }
    }.build()

    val createUserResponseWithUnexpectedException = CreateUserResponse.newBuilder().apply {
        failureBuilder.message = "NPE"
    }.build()

    val findUserByIdResponse = FindUserByIdResponse.newBuilder().apply {
        successBuilder.userBuilder.apply {
            id = randomUserId
            fullName = randomFullName
            phone = randomPhone
            password = "password"
        }
    }.build()

    val findUserByIdResponseWithNotFoundException = FindUserByIdResponse.newBuilder().apply {
        failureBuilder.message = "User with id $randomUserId doesn't exist"
        failureBuilder.userNotFoundBuilder
    }.build()

    val findUserByIdResponseWithUnexpectedException = FindUserByIdResponse.newBuilder().apply {
        failureBuilder.message = "NPE"
    }.build()

    val updateUserResponse = UpdateUserResponse.newBuilder().apply {
        successBuilder.userBuilder.apply {
            id = randomUserId
            fullName = randomUpdatedFullName
            phone = randomUpdatedPhone
            password = "password"
        }
    }.build()

    val updateUserResponseWithUserNotFoundException = UpdateUserResponse.newBuilder().apply {
        failureBuilder.message = " USER NOT FOUND"
        failureBuilder.userNotFoundBuilder
    }.build()

    val updateUserResponseWithUnexpectedException = UpdateUserResponse.newBuilder().apply {
        failureBuilder.message = "NPE"
    }.build()

    val deleteUserRequest = DeleteUserRequest.newBuilder().setId(randomUserId).build()

    val deleteUserResponse = DeleteUserResponse.newBuilder().apply {
        successBuilder
    }.build()

    val deleteUserResponseWithUnexpectedException = DeleteUserResponse.newBuilder().apply {
        failureBuilder.message = "NPE"
    }.build()
}
