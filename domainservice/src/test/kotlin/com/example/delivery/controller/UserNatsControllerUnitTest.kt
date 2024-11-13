package com.example.delivery.controller

import com.example.delivery.controller.user.CreateUserNatsHandler
import com.example.delivery.service.UserService
import com.example.internal.input.reqreply.user.CreateUserResponse
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
class UserNatsControllerUnitTest {
    @SuppressWarnings("UnusedPrivateProperty")
    @MockK
    private lateinit var userService: UserService

    @InjectMockKs
    private lateinit var createUserNatsHandler: CreateUserNatsHandler

    @Test
    fun `create doOnUnexpectedError should return empty error message`() {
        // GIVEN
        val exception = KotlinNullPointerException()
        val message = CreateUserResponse.newBuilder().apply { failureBuilder }.build()

        // WHEN
        val actual = createUserNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `create doOnUnexpectedError should return error message`() {
        // GIVEN
        val exception = KotlinNullPointerException("Oops")
        val message = CreateUserResponse.newBuilder().apply { failureBuilder.message = "Oops" }.build()

        // WHEN
        val actual = createUserNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }
}
