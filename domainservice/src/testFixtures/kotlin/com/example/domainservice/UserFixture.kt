package com.example.domainservice

import com.example.core.UserFixture.randomFullName
import com.example.core.UserFixture.randomPhone
import com.example.core.UserFixture.randomUpdatedFullName
import com.example.core.UserFixture.randomUpdatedPhone
import com.example.core.UserFixture.randomUserId
import com.example.core.exception.UserNotFoundException
import com.example.domainservice.user.domain.DomainUser
import com.example.domainservice.user.infrastructure.mongo.entity.MongoUser
import com.example.domainservice.user.infrastructure.mongo.mapper.UserMapper.toDomain
import com.example.internal.input.reqreply.user.CreateUserRequest
import com.example.internal.input.reqreply.user.DeleteUserRequest
import com.example.internal.input.reqreply.user.FindUserByIdRequest
import com.example.internal.input.reqreply.user.UpdateUserRequest
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Update

object UserFixture {

    val userNotFoundException = UserNotFoundException("User with id $randomUserId doesn't exists")

    val user = MongoUser(
        ObjectId(randomUserId),
        fullName = randomFullName,
        phone = randomPhone,
        password = "password",
    )

    val unsavedDomainUser = user.copy(id = null).toDomain()

    val domainUser = DomainUser(
        id = randomUserId,
        fullName = randomFullName,
        phone = randomPhone,
        password = "password",
    )

    val userUpdateObject = Update()
        .set("fullName", randomUpdatedFullName)
        .set("phone", randomUpdatedPhone)

    val updatedDomainUser =
        domainUser.copy(fullName = randomUpdatedFullName, phone = randomUpdatedPhone, password = "password")

    val partialUpdate =
        domainUser.copy(fullName = randomUpdatedFullName, phone = randomUpdatedPhone, password = "password")

    val updatedUser = user.copy(fullName = randomUpdatedFullName, phone = randomUpdatedPhone)

    val createUserRequest = CreateUserRequest.newBuilder().apply {
        fullName = randomFullName
        phone = randomPhone
        password = "password"
    }.build()

    fun buildFindUserByIdRequest(userId: String) = FindUserByIdRequest.newBuilder().setId(userId).build()

    fun buildUpdateUserRequest(userId: String) = UpdateUserRequest.newBuilder().apply {
        id = userId
        fullname = randomUpdatedFullName
        phone = randomUpdatedPhone
        password = "password"
    }.build()

    fun buildDeleteUserRequest(userId: String) = DeleteUserRequest.newBuilder().setId(userId).build()

}
