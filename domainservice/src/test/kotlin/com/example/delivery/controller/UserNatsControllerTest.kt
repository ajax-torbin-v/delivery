package com.example.delivery.controller

import com.example.delivery.UserFixture.buildDeleteUserRequest
import com.example.delivery.UserFixture.buildFindUserByIdRequest
import com.example.delivery.UserFixture.buildUpdateUserRequest
import com.example.delivery.UserFixture.createUserRequest
import com.example.delivery.UserFixture.domainUser
import com.example.delivery.UserFixture.randomUserId
import com.example.delivery.UserFixture.unsavedUser
import com.example.delivery.UserFixture.updatedDomainUser
import com.example.delivery.UserFixture.userNotFoundException
import com.example.delivery.mapper.UserProtoMapper.toCreateUserResponse
import com.example.delivery.mapper.UserProtoMapper.toDeleteUserResponse
import com.example.delivery.mapper.UserProtoMapper.toFailureFindUserByIdResponse
import com.example.delivery.mapper.UserProtoMapper.toFailureUpdateUserResponse
import com.example.delivery.mapper.UserProtoMapper.toFindUserByIdResponse
import com.example.delivery.mapper.UserProtoMapper.toUpdateUserResponse
import com.example.delivery.repository.UserRepository
import com.example.internal.api.subject.UserNatsSubject
import com.example.internal.input.reqreply.user.create.CreateUserResponse
import com.example.internal.input.reqreply.user.delete.DeleteUserResponse
import com.example.internal.input.reqreply.user.find.FindUserByIdResponse
import com.example.internal.input.reqreply.user.update.UpdateUserResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

class UserNatsControllerTest : AbstractNatsControllerTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `save should return saved user`() {
        // GIVEN
        val subject = "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.SAVE}"

        // WHEN
        val actual = doRequest(
            subject,
            createUserRequest,
            CreateUserResponse.parser()
        )

        // THEN
        assertEquals(domainUser.copy(id = actual.success.user.id).toCreateUserResponse(), actual)
    }

    @Test
    fun `findById should return existing user`() {
        // GIVEN
        val user = userRepository.save(unsavedUser).block()!!
        val subject = "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.FIND_BY_ID}"

        // WHEN
        val actual = doRequest(
            subject,
            buildFindUserByIdRequest(user.id.toString()),
            FindUserByIdResponse.parser()
        )

        // THEN
        assertEquals(domainUser.copy(id = user.id.toString()).toFindUserByIdResponse(), actual)
    }

    @Test
    fun `findById should return message with exception when user doesn't exist`() {
        // GIVEN
        val subject = "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.FIND_BY_ID}"

        // WHEN
        val actual = doRequest(
            subject,
            buildFindUserByIdRequest(randomUserId),
            FindUserByIdResponse.parser()
        )

        // THEN
        assertEquals(userNotFoundException.toFailureFindUserByIdResponse(), actual)
    }

    @Test
    fun `update should return updated product`() {
        // GIVEN
        val user = userRepository.save(unsavedUser).block()!!
        val subject = "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.UPDATE}"

        // WHEN
        val actual = doRequest(
            subject,
            buildUpdateUserRequest(user.id.toString()),
            UpdateUserResponse.parser()
        )

        // THEN
        assertEquals(updatedDomainUser.copy(id = user.id.toString()).toUpdateUserResponse(), actual)
    }

    @Test
    fun `update should throw exception when user doesn't exist`() {
        // GIVEN
        val subject = "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.UPDATE}"

        // WHEN
        val actual = doRequest(
            subject,
            buildUpdateUserRequest(randomUserId),
            UpdateUserResponse.parser()
        )

        // THEN
        assertEquals(userNotFoundException.toFailureUpdateUserResponse(), actual)
    }

    @Test
    fun `delete should delete product`() {
        // GIVEN
        val user = userRepository.save(unsavedUser).block()!!
        val subject = "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.DELETE}"

        // WHEN
        val actual = doRequest(
            subject,
            buildDeleteUserRequest(user.id.toString()),
            DeleteUserResponse.parser()
        )

        // THEN
        assertEquals(toDeleteUserResponse(), actual)
        userRepository.existsById(user.id.toString())
            .test()
            .expectNext(false)
            .verifyComplete()
    }
}
