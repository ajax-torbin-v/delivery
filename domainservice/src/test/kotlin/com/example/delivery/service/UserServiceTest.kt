package com.example.delivery.service

import com.example.core.UserFixture.createUserDTO
import com.example.core.UserFixture.updateUserDTO
import com.example.core.exception.NotFoundException
import com.example.delivery.UserFixture.domainUser
import com.example.delivery.UserFixture.updatedDomainUser
import com.example.delivery.UserFixture.updatedUser
import com.example.delivery.UserFixture.user
import com.example.delivery.UserFixture.userUpdateObject
import com.example.delivery.repository.UserRepository
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
internal class UserServiceTest {
    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var userService: UserService

    @Test
    fun `should add user with proper DTO`() {
        // GIVEN
        val userWithoutId = user.copy(id = null)
        every { userRepository.save(any()) } returns user.toMono()

        // WHEN
        val actual = userService.add(createUserDTO)

        // THEN
        actual
            .test()
            .expectNext(domainUser)
            .expectComplete()

        verify(exactly = 1) { userRepository.save(userWithoutId) }
    }

    @Test
    fun `should return user when user exists`() {
        // GIVEN
        every { (userRepository.findById("2")) } returns user.toMono()

        // WHEN
        val actual = userService.getById("2")

        // THEN
        actual
            .test()
            .expectNext(domainUser)
            .verifyComplete()
    }

    @Test
    fun `should throw exception when user doesn't exists`() {
        // GIVEN
        every { userRepository.findById("3") } returns Mono.empty()

        // WHEN
        val actual = userService.getById("3")

        // THEN
        actual
            .test()
            .expectError(NotFoundException::class.java)
    }

    @Test
    fun `should update user with proper dto when user exists`() {
        // GIVEN
        every { userRepository.update("1", userUpdateObject) } returns Mono.just(updatedUser)

        // WHEN
        val actual = userService.update("1", updateUserDTO)

        // THEN
        actual
            .test()
            .expectNext(updatedDomainUser)

        verify(exactly = 1) { userRepository.update("1", userUpdateObject) }
    }

    @Test
    fun `should throw exception when user doesn't exists on update`() {
        // GIVEN
        every { userRepository.update("1", userUpdateObject) } returns Mono.empty()

        // WHEN
        val actual = userService.update("1", updateUserDTO)

        // THEN
        actual
            .test()
            .expectError(NotFoundException::class.java)
    }

    @Test
    fun `should delete is user exists`() {
        // GIVEN
        every { userRepository.deleteById("8") } returns Mono.empty()

        // WHEN
        val actual = userService.deleteById("8")

        // THEN
        actual
            .test()
            .verifyComplete()

        verify(exactly = 1) { userRepository.deleteById("8") }
    }
}
