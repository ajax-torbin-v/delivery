package com.example.gateway.controller

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
import com.example.gateway.application.port.output.UserOutputPort
import com.example.gateway.infrastructure.mapper.UserProtoMapper.toCreateUserRequest
import com.example.gateway.infrastructure.mapper.UserProtoMapper.toDTO
import com.example.gateway.infrastructure.mapper.UserProtoMapper.updateUserRequest
import com.example.gateway.infrastructure.rest.UserController
import com.example.internal.input.reqreply.user.CreateUserResponse
import com.example.internal.input.reqreply.user.DeleteUserResponse
import com.example.internal.input.reqreply.user.FindUserByIdRequest
import com.example.internal.input.reqreply.user.FindUserByIdResponse
import com.example.internal.input.reqreply.user.UpdateUserResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

@ExtendWith(MockKExtension::class)
class UserControllerTest {
    @MockK
    private lateinit var userOutputPort: UserOutputPort

    @InjectMockKs
    private lateinit var userController: UserController

    @Test
    fun `save should return user DTO`() {
        // GIVEN
        every {
            userOutputPort.create(createUserDTO.toCreateUserRequest())
        } returns createUserResponse.toMono()

        // WHEN // THEN
        userController.add(createUserDTO)
            .test()
            .expectNext(createUserResponse.toDTO())
            .verifyComplete()
    }

    @Test
    fun `save should rethrow unexpected exception`() {
        // GIVEN
        every {
            userOutputPort.create(createUserDTO.toCreateUserRequest())
        } returns createUserResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        userController.add(createUserDTO)
            .test()
            .verifyError(IllegalStateException::class)
    }

    @Test
    fun `save should throw exception when message is empty`() {
        // GIVEN
        every {
            userOutputPort.create(createUserDTO.toCreateUserRequest())
        } returns CreateUserResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        userController.add(createUserDTO)
            .test()
            .verifyError(RuntimeException::class)
    }

    @Test
    fun `findById should return existing user`() {
        // GIVEN
        every {
            userOutputPort.findById(FindUserByIdRequest.newBuilder().setId(randomUserId).build())
        } returns findUserByIdResponse.toMono()

        // WHEN
        userController.findById(randomUserId)
            .test()
            .expectNext(findUserByIdResponse.toDTO())
            .verifyComplete()
    }

    @Test
    fun `findById should throw exception when user doesn't exist`() {
        // GIVEN
        every {
            userOutputPort.findById(FindUserByIdRequest.newBuilder().setId(randomUserId).build())
        } returns findUserByIdResponseWithNotFoundException.toMono()

        // WHEN // THEN
        userController.findById(randomUserId)
            .test()
            .verifyError(UserNotFoundException::class)
    }

    @Test
    fun `findById should rethrow unexpected exception`() {
        // GIVEN
        every {
            userOutputPort.findById(FindUserByIdRequest.newBuilder().setId(randomUserId).build())
        } returns findUserByIdResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        userController.findById(randomUserId)
            .test()
            .verifyError(IllegalStateException::class)
    }

    @Test
    fun `findById should throw exception when message is empty`() {
        // GIVEN
        every {
            userOutputPort.findById(FindUserByIdRequest.newBuilder().setId(randomUserId).build())
        } returns FindUserByIdResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        userController.findById(randomUserId)
            .test()
            .verifyError(RuntimeException::class)
    }

    @Test
    fun `update should return updated user`() {
        // GIVEN
        every {
            userOutputPort.update(updateUserRequest(randomUserId, updateUserDTO))
        } returns updateUserResponse.toMono()

        // WHEN // THEN
        userController.update(randomUserId, updateUserDTO)
            .test()
            .expectNext(updateUserResponse.toDTO())
            .verifyComplete()
    }

    @Test
    fun `update should throw exception when user doesn't exist`() {
        // GIVEN
        every {
            userOutputPort.update(updateUserRequest(randomUserId, updateUserDTO))
        } returns updateUserResponseWithUserNotFoundException.toMono()

        // WHEN // THEN
        userController.update(randomUserId, updateUserDTO)
            .test()
            .verifyError(UserNotFoundException::class)
    }

    @Test
    fun `update should rethrow unexpected exception`() {
        // GIVEN
        every {
            userOutputPort.update(updateUserRequest(randomUserId, updateUserDTO))
        } returns updateUserResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        userController.update(randomUserId, updateUserDTO)
            .test()
            .verifyError(IllegalStateException::class)
    }

    @Test
    fun `update should throw exception when message is empty`() {
        // GIVEN
        every {
            userOutputPort.update(updateUserRequest(randomUserId, updateUserDTO))
        } returns UpdateUserResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        userController.update(randomUserId, updateUserDTO)
            .test()
            .verifyError(RuntimeException::class)
    }

    @Test
    fun `delete should delete user`() {
        // GIVEN
        every {
            userOutputPort.delete(deleteUserRequest)
        } returns deleteUserResponse.toMono()

        // WHEN
        userController.delete(randomUserId).block()

        // THEN
        verify(exactly = 1) {
            userOutputPort.delete(deleteUserRequest)
        }
    }

    @Test
    fun `delete should rethrow unexpected exception`() {
        // GIVEN
        every {
            userOutputPort.delete(deleteUserRequest)
        } returns deleteUserResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        userController.delete(randomUserId)
            .test()
            .verifyError(IllegalStateException::class)
    }

    @Test
    fun `delete should throw exception when message is empty`() {
        // GIVEN
        every {
            userOutputPort.delete(deleteUserRequest)
        } returns DeleteUserResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        userController.delete(randomUserId)
            .test()
            .verifyError(RuntimeException::class)
    }
}
