package com.example.gateway

import com.example.delivery.UserFixture.buildDeleteUserRequest
import com.example.delivery.UserFixture.buildUpdateUserRequest
import com.example.delivery.UserFixture.createUserDTO
import com.example.delivery.UserFixture.domainUser
import com.example.delivery.UserFixture.randomUserId
import com.example.delivery.UserFixture.unexpectedError
import com.example.delivery.UserFixture.updateUserDTO
import com.example.delivery.UserFixture.updatedDomainUser
import com.example.delivery.UserFixture.userDTO
import com.example.delivery.UserFixture.userNotFoundException
import com.example.delivery.exception.UserNotFoundException
import com.example.delivery.mapper.UserProtoMapper.toCreateUserResponse
import com.example.delivery.mapper.UserProtoMapper.toDeleteUserResponse
import com.example.delivery.mapper.UserProtoMapper.toFailureCreateUserResponse
import com.example.delivery.mapper.UserProtoMapper.toFailureDeleteUserResponse
import com.example.delivery.mapper.UserProtoMapper.toFailureFindUserByIdResponse
import com.example.delivery.mapper.UserProtoMapper.toFailureUpdateUserResponse
import com.example.delivery.mapper.UserProtoMapper.toFindUserByIdResponse
import com.example.delivery.mapper.UserProtoMapper.toUpdateUserResponse
import com.example.gateway.client.NatsClient
import com.example.gateway.mapper.UserProtoMapper.toCreateUserRequest
import com.example.gateway.mapper.UserProtoMapper.toDTO
import com.example.gateway.rest.UserController
import com.example.internal.api.subject.UserNatsSubject
import com.example.internal.input.reqreply.user.create.CreateUserResponse
import com.example.internal.input.reqreply.user.delete.DeleteUserResponse
import com.example.internal.input.reqreply.user.find.FindUserByIdRequest
import com.example.internal.input.reqreply.user.find.FindUserByIdResponse
import com.example.internal.input.reqreply.user.update.UpdateUserResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.nats.client.Connection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toMono

@ExtendWith(MockKExtension::class)
class UserControllerTest {
    @SuppressWarnings("UnusedPrivateProperty")
    @MockK
    private lateinit var connection: Connection

    @MockK
    private lateinit var natsClient: NatsClient

    @InjectMockKs
    private lateinit var userController: UserController

    @Test
    fun `save should return user DTO`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.SAVE}",
                payload = createUserDTO.toCreateUserRequest(),
                parser = CreateUserResponse.parser()
            )
        } returns domainUser.toCreateUserResponse().toMono()

        // WHEN
        val actual = userController.add(createUserDTO).block()

        // THEN
        assertEquals(userDTO, actual)
    }

    @Test
    fun `save should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.SAVE}",
                payload = createUserDTO.toCreateUserRequest(),
                parser = CreateUserResponse.parser()
            )
        } returns unexpectedError.toFailureCreateUserResponse().toMono()

        // WHEN // THEN
        assertThrows<IllegalStateException> { userController.add(createUserDTO).block() }
    }

    @Test
    fun `save should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.SAVE}",
                payload = createUserDTO.toCreateUserRequest(),
                parser = CreateUserResponse.parser()
            )
        } returns CreateUserResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { userController.add(createUserDTO).block() }
    }

    @Test
    fun `findById should return existing user`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.FIND_BY_ID}",
                payload = FindUserByIdRequest.newBuilder().setId(randomUserId).build(),
                parser = FindUserByIdResponse.parser()
            )
        } returns domainUser.toFindUserByIdResponse().toMono()

        // WHEN
        val actual = userController.findById(randomUserId).block()

        // THEN
        assertEquals(userDTO, actual)
    }

    @Test
    fun `findById should throw exception when user doesn't exist`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.FIND_BY_ID}",
                payload = FindUserByIdRequest.newBuilder().setId(randomUserId).build(),
                parser = FindUserByIdResponse.parser()
            )
        } returns userNotFoundException.toFailureFindUserByIdResponse().toMono()

        // WHEN // THEN
        assertThrows<UserNotFoundException> { userController.findById(randomUserId).block() }
    }

    @Test
    fun `findById should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.FIND_BY_ID}",
                payload = FindUserByIdRequest.newBuilder().setId(randomUserId).build(),
                parser = FindUserByIdResponse.parser()
            )
        } returns unexpectedError.toFailureFindUserByIdResponse().toMono()

        // WHEN // THEN
        assertThrows<IllegalStateException> { userController.findById(randomUserId).block() }
    }

    @Test
    fun `findById should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.FIND_BY_ID}",
                payload = FindUserByIdRequest.newBuilder().setId(randomUserId).build(),
                parser = FindUserByIdResponse.parser()
            )
        } returns FindUserByIdResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { userController.findById(randomUserId).block() }
    }

    @Test
    fun `update should return updated user`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.UPDATE}",
                buildUpdateUserRequest(randomUserId),
                UpdateUserResponse.parser()
            )
        } returns updatedDomainUser.toUpdateUserResponse().toMono()

        // WHEN
        val actual = userController.update(randomUserId, updateUserDTO).block()

        // THEN
        assertEquals(updatedDomainUser.toUpdateUserResponse().toDTO(), actual)
    }

    @Test
    fun `update should throw exception when user doesn't exist`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.UPDATE}",
                buildUpdateUserRequest(randomUserId),
                UpdateUserResponse.parser()
            )
        } returns userNotFoundException.toFailureUpdateUserResponse().toMono()

        // WHEN // THEN
        assertThrows<UserNotFoundException> { userController.update(randomUserId, updateUserDTO).block() }
    }

    @Test
    fun `update should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.UPDATE}",
                buildUpdateUserRequest(randomUserId),
                UpdateUserResponse.parser()
            )
        } returns unexpectedError.toFailureUpdateUserResponse().toMono()

        // WHEN // THEN
        assertThrows<IllegalStateException> { userController.update(randomUserId, updateUserDTO).block() }
    }

    @Test
    fun `update should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.UPDATE}",
                buildUpdateUserRequest(randomUserId),
                UpdateUserResponse.parser()
            )
        } returns UpdateUserResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { userController.update(randomUserId, updateUserDTO).block() }
    }

    @Test
    fun `delete should delete user`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.DELETE}",
                buildDeleteUserRequest(randomUserId),
                DeleteUserResponse.parser()
            )
        } returns toDeleteUserResponse().toMono()

        // WHEN
        userController.delete(randomUserId).block()

        // THEN
        verify(exactly = 1) {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.DELETE}",
                buildDeleteUserRequest(randomUserId),
                DeleteUserResponse.parser()
            )
        }
    }

    @Test
    fun `delete should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.DELETE}",
                buildDeleteUserRequest(randomUserId),
                DeleteUserResponse.parser()
            )
        } returns unexpectedError.toFailureDeleteUserResponse().toMono()

        // WHEN // THEN
        assertThrows<IllegalStateException> { userController.delete(randomUserId).block() }
    }

    @Test
    fun `delete should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.DELETE}",
                buildDeleteUserRequest(randomUserId),
                DeleteUserResponse.parser()
            )
        } returns DeleteUserResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { userController.delete(randomUserId).block() }
    }
}
