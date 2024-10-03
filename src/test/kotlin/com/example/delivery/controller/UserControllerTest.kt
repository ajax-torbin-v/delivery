package com.example.delivery.controller

import com.example.delivery.UserFixture.createUserDTO
import com.example.delivery.UserFixture.domainUser
import com.example.delivery.UserFixture.updateUserDTO
import com.example.delivery.UserFixture.updatedDomainUser
import com.example.delivery.mapper.UserMapper.toDTO
import com.example.delivery.service.UserService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
internal class UserControllerTest {
    @MockK
    private lateinit var userService: UserService

    @InjectMockKs
    private lateinit var useController: UserController

    @Test
    fun `should add user and return created`() {
        // GIVEN
        every { userService.add(createUserDTO) } returns domainUser.toMono()

        // WHEN
        val actual = useController.add(createUserDTO)

        // THEN
        actual
            .test()
            .expectNext(domainUser.toDTO())
            .verifyComplete()
    }

    @Test
    fun `should return user when user exists`() {
        // GIVEN
        every { userService.getById("1") } returns domainUser.toMono()

        // WHEN
        val actual = useController.findById("1")

        // THEN
        actual
            .test()
            .expectNext(domainUser.toDTO())
            .verifyComplete()
    }

    @Test
    fun `should update user`() {
        // GIVEN
        every { userService.update("1", updateUserDTO) } returns updatedDomainUser.toMono()

        // WHEN
        val actual = useController.update("1", updateUserDTO)

        // THEN
        actual
            .test()
            .expectNext(updatedDomainUser.toDTO())
            .verifyComplete()
    }

    @Test
    fun `should delete existing user and return no content`() {
        // GIVEN
        every { userService.deleteById("1") } returns Mono.empty()

        // WHEN
        val actual = useController.deleteById("1")

        // THEN
        actual
            .test()
            .verifyComplete()

        verify(exactly = 1) { userService.deleteById("1") }
    }
}
