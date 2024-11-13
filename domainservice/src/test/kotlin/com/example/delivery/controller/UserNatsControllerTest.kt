package com.example.delivery.controller

import com.example.core.UserFixture.randomUserId
import com.example.delivery.AbstractIntegrationTest
import com.example.delivery.UserFixture.buildDeleteUserRequest
import com.example.delivery.UserFixture.buildFindUserByIdRequest
import com.example.delivery.UserFixture.buildUpdateUserRequest
import com.example.delivery.UserFixture.createUserRequest
import com.example.delivery.UserFixture.domainUser
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
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.user.CreateUserResponse
import com.example.internal.input.reqreply.user.DeleteUserResponse
import com.example.internal.input.reqreply.user.FindUserByIdResponse
import com.example.internal.input.reqreply.user.UpdateUserResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class UserNatsControllerTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var natsMessagePublisher: NatsMessagePublisher

    @Test
    fun `save should return saved user`() {
        // GIVEN // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.User.SAVE,
            createUserRequest,
            CreateUserResponse.parser()
        ).block()!!

        // THEN
        assertEquals(
            domainUser.toCreateUserResponse().success.user.toBuilder().clearId().build(),
            actual.success.user.toBuilder().clearId().build()
        )
    }

    @Test
    fun `findById should return existing user`() {
        // GIVEN
        val user = userRepository.save(unsavedUser).block()!!

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.User.FIND_BY_ID,
            buildFindUserByIdRequest(user.id.toString()),
            FindUserByIdResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(domainUser.copy(id = user.id.toString()).toFindUserByIdResponse())
            .verifyComplete()
    }

    @Test
    fun `findById should return message with exception when user doesn't exist`() {
        // GIVEN // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.User.FIND_BY_ID,
            buildFindUserByIdRequest(randomUserId),
            FindUserByIdResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(userNotFoundException.toFailureFindUserByIdResponse())
            .verifyComplete()
    }

    @Test
    fun `update should return updated product`() {
        // GIVEN
        val user = userRepository.save(unsavedUser).block()!!

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.User.UPDATE,
            buildUpdateUserRequest(user.id.toString()),
            UpdateUserResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(updatedDomainUser.copy(id = user.id.toString()).toUpdateUserResponse())
            .verifyComplete()
    }

    @Test
    fun `update should throw exception when user doesn't exist`() {
        // GIVEN // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.User.UPDATE,
            buildUpdateUserRequest(randomUserId),
            UpdateUserResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(userNotFoundException.toFailureUpdateUserResponse())
            .verifyComplete()
    }

    @Test
    fun `delete should delete product`() {
        // GIVEN
        val user = userRepository.save(unsavedUser).block()!!

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.User.DELETE,
            buildDeleteUserRequest(user.id.toString()),
            DeleteUserResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(toDeleteUserResponse())
            .verifyComplete()
        userRepository.existsById(user.id.toString())
            .test()
            .expectNext(false)
            .verifyComplete()
    }
}
