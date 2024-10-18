package com.example.gateway

import com.example.core.UserFixture.createUserDTO
import com.example.core.UserFixture.randomUserId
import com.example.core.UserFixture.updateUserDTO
import com.example.core.exception.UserNotFoundException
import com.example.gateway.UserProtoFixture.createUserResponse
import com.example.gateway.UserProtoFixture.createUserResponseWithUnexpectedException
import com.example.gateway.UserProtoFixture.deleteUserRequest
import com.example.gateway.UserProtoFixture.deleteUserResponse
import com.example.gateway.UserProtoFixture.deleteUserResponseWithUnexpectedException
import com.example.gateway.UserProtoFixture.findUserByIdResponse
import com.example.gateway.UserProtoFixture.findUserByIdResponseWithNotFoundException
import com.example.gateway.UserProtoFixture.findUserByIdResponseWithUnexpectedException
import com.example.gateway.UserProtoFixture.updateUserResponse
import com.example.gateway.UserProtoFixture.updateUserResponseWithUnexpectedException
import com.example.gateway.UserProtoFixture.updateUserResponseWithUserNotFoundException
import com.example.gateway.client.NatsClient
import com.example.gateway.mapper.UserProtoMapper.toCreateUserRequest
import com.example.gateway.mapper.UserProtoMapper.toDTO
import com.example.gateway.mapper.UserProtoMapper.updateUserRequest
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
        } returns createUserResponse.toMono()

        // WHEN
        val actual = userController.add(createUserDTO).block()

        // THEN
        assertEquals(createUserResponse.toDTO(), actual)
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
        } returns createUserResponseWithUnexpectedException.toMono()

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
        assertThrows<RuntimeException> { userController.add(createUserDTO).block() }
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
        } returns findUserByIdResponse.toMono()

        // WHEN
        val actual = userController.findById(randomUserId).block()

        // THEN
        assertEquals(findUserByIdResponse.toDTO(), actual)
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
        } returns findUserByIdResponseWithNotFoundException.toMono()

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
        } returns findUserByIdResponseWithUnexpectedException.toMono()

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
        assertThrows<RuntimeException> { userController.findById(randomUserId).block() }
    }

    @Test
    fun `update should return updated user`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.UPDATE}",
                updateUserRequest(randomUserId, updateUserDTO),
                UpdateUserResponse.parser()
            )
        } returns updateUserResponse.toMono()

        // WHEN
        val actual = userController.update(randomUserId, updateUserDTO).block()

        // THEN
        assertEquals(updateUserResponse.toDTO(), actual)
    }

    @Test
    fun `update should throw exception when user doesn't exist`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.UPDATE}",
                updateUserRequest(randomUserId, updateUserDTO),
                UpdateUserResponse.parser()
            )
        } returns updateUserResponseWithUserNotFoundException.toMono()

        // WHEN // THEN
        assertThrows<UserNotFoundException> { userController.update(randomUserId, updateUserDTO).block() }
    }

    @Test
    fun `update should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.UPDATE}",
                updateUserRequest(randomUserId, updateUserDTO),
                UpdateUserResponse.parser()
            )
        } returns updateUserResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        assertThrows<IllegalStateException> { userController.update(randomUserId, updateUserDTO).block() }
    }

    @Test
    fun `update should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.UPDATE}",
                updateUserRequest(randomUserId, updateUserDTO),
                UpdateUserResponse.parser()
            )
        } returns UpdateUserResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<RuntimeException> { userController.update(randomUserId, updateUserDTO).block() }
    }

    @Test
    fun `delete should delete user`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.DELETE}",
                deleteUserRequest,
                DeleteUserResponse.parser()
            )
        } returns deleteUserResponse.toMono()

        // WHEN
        userController.delete(randomUserId).block()

        // THEN
        verify(exactly = 1) {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.DELETE}",
                deleteUserRequest,
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
                deleteUserRequest,
                DeleteUserResponse.parser()
            )
        } returns deleteUserResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        assertThrows<IllegalStateException> { userController.delete(randomUserId).block() }
    }

    @Test
    fun `delete should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.DELETE}",
                deleteUserRequest,
                DeleteUserResponse.parser()
            )
        } returns DeleteUserResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<RuntimeException> { userController.delete(randomUserId).block() }
    }
}
