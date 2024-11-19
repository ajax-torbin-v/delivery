package com.example.domainservice.user.infrastructure.nats

import com.example.core.exception.UserNotFoundException
import com.example.domainservice.UserFixture.buildDeleteUserRequest
import com.example.domainservice.UserFixture.buildFindUserByIdRequest
import com.example.domainservice.UserFixture.buildUpdateUserRequest
import com.example.domainservice.UserFixture.createUserRequest
import com.example.domainservice.UserFixture.domainUser
import com.example.domainservice.UserFixture.unsavedDomainUser
import com.example.domainservice.UserFixture.updatedDomainUser
import com.example.domainservice.user.AbstractIntegrationTest
import com.example.domainservice.user.infrastructure.mongo.UserRepository
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toCreateUserResponse
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toDeleteUserResponse
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toFailureFindUserByIdResponse
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toFailureUpdateUserResponse
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toFindUserByIdResponse
import com.example.domainservice.user.infrastructure.nats.mapper.UserProtoMapper.toUpdateUserResponse
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.user.CreateUserResponse
import com.example.internal.input.reqreply.user.DeleteUserResponse
import com.example.internal.input.reqreply.user.FindUserByIdResponse
import com.example.internal.input.reqreply.user.UpdateUserResponse
import org.bson.types.ObjectId
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
        println(actual.success.user.id.toString())

        // THEN
        assertEquals(
            domainUser.toCreateUserResponse().success.user.toBuilder().clearId().build(),
            actual.success.user.toBuilder().clearId().build()
        )
    }

    @Test
    fun `findById should return existing user`() {
        // GIVEN
        val user = userRepository.save(unsavedDomainUser).block()!!

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
        val nonExistingId = ObjectId().toString()
        val actual = natsMessagePublisher.request(
            NatsSubject.User.FIND_BY_ID,
            buildFindUserByIdRequest(nonExistingId),
            FindUserByIdResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(
                UserNotFoundException("User with id $nonExistingId doesn't exists")
                    .toFailureFindUserByIdResponse()
            )
            .verifyComplete()
    }

    @Test
    fun `update should return updated user`() {
        // GIVEN
        val user = userRepository.save(unsavedDomainUser).block()!!

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
        val nonExistingId = ObjectId().toString()
        val actual = natsMessagePublisher.request(
            NatsSubject.User.UPDATE,
            buildUpdateUserRequest(nonExistingId),
            UpdateUserResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(
                UserNotFoundException("User with id $nonExistingId doesn't exists")
                    .toFailureUpdateUserResponse()
            )
            .verifyComplete()
    }

    @Test
    fun `delete should delete user`() {
        // GIVEN
        val user = userRepository.save(unsavedDomainUser).block()!!

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

        userRepository.findById(user.id.toString())
            .test()
            .verifyComplete()
    }
}
