package com.example.delivery

import com.example.delivery.domain.DomainUser
import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.request.UpdateUserDTO
import com.example.delivery.dto.response.UserDTO
import com.example.delivery.exception.UserNotFoundException
import com.example.delivery.mongo.MongoUser
import com.example.internal.input.reqreply.user.create.CreateUserRequest
import com.example.internal.input.reqreply.user.delete.DeleteUserRequest
import com.example.internal.input.reqreply.user.find.FindUserByIdRequest
import com.example.internal.input.reqreply.user.update.UpdateUserRequest
import io.github.serpro69.kfaker.Faker
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Update

object UserFixture {
    val randomUserId = ObjectId().toString()
    val randomPhone = Faker().phoneNumber.phoneNumber()
    val randomFullName = Faker().funnyName.name()
    val randomUpdatedPhone = Faker().phoneNumber.phoneNumber()
    val randomUpdatedFullName = Faker().funnyName.name()
    val userNotFoundException = UserNotFoundException("User with id $randomUserId doesn't exists")
    val unexpectedError = NullPointerException()


    val createUserDTO = CreateUserDTO(
        fullName = randomFullName,
        phone = randomPhone,
        password = "password",
    )
    val user = MongoUser(
        ObjectId(randomUserId),
        fullName = randomFullName,
        phone = randomPhone,
        password = "password",
    )

    val unsavedUser = user.copy(id = null)

    val domainUser = DomainUser(
        id = randomUserId,
        fullName = randomFullName,
        phone = randomPhone,
        password = "password",
    )

    val userDTO = UserDTO(
        id = randomUserId,
        fullName = randomFullName,
        phone = randomPhone,
    )

    val userUpdateObject = Update()
        .set("fullName", randomUpdatedFullName)
        .set("phone", randomUpdatedPhone)

    val updatedDomainUser = domainUser.copy(fullName = randomUpdatedFullName, phone = randomUpdatedPhone)

    val updatedUser = user.copy(fullName = randomUpdatedFullName, phone = randomUpdatedPhone)

    val updateUserDTO = UpdateUserDTO(
        fullName = randomUpdatedFullName,
        phone = randomUpdatedPhone
    )

    val createUserRequest = CreateUserRequest.newBuilder().also {
        it.userBuilder
            .setFullName(randomFullName)
            .setPhone(randomPhone)
            .setPassword("password")
    }.build()

    fun buildFindUserByIdRequest(userId: String) = FindUserByIdRequest.newBuilder().setId(userId).build()

    fun buildUpdateUserRequest(userId: String) = UpdateUserRequest.newBuilder().also {
        it.setId(userId)
        it.setFullname(randomUpdatedFullName)
        it.setPhone(randomUpdatedPhone)
    }.build()

    fun buildDeleteUserRequest(userId: String) = DeleteUserRequest.newBuilder().setId(userId).build()

}
