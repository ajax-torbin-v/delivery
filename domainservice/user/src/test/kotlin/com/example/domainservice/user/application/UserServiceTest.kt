package com.example.domainservice.user.application

import com.example.core.UserFixture.randomUserId
import com.example.core.exception.NotFoundException
import com.example.domainservice.UserFixture.domainUser
import com.example.domainservice.UserFixture.partialUpdate
import com.example.domainservice.UserFixture.unsavedDomainUser
import com.example.domainservice.UserFixture.updatedDomainUser
import com.example.domainservice.user.application.service.UserService
import com.example.domainservice.user.infrastructure.mongo.UserRepository
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
        every { userRepository.save(any()) } returns domainUser.toMono()

        // WHEN
        val actual = userService.save(unsavedDomainUser)

        // THEN
        actual
            .test()
            .expectNext(domainUser)
            .expectComplete()

        verify(exactly = 1) { userRepository.save(unsavedDomainUser) }
    }

    @Test
    fun `should return user when user exists`() {
        // GIVEN
        every { (userRepository.findById("2")) } returns domainUser.toMono()

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
        every { userRepository.findById(randomUserId) } returns domainUser.toMono()
        every { userRepository.update(partialUpdate) } returns Mono.just(updatedDomainUser)

        // WHEN
        val actual = userService.update(partialUpdate)

        // THEN
        actual
            .test()
            .expectNext(updatedDomainUser)
            .verifyComplete()
    }

    @Test
    fun `should throw exception when user doesn't exists on update`() {
        // GIVEN
        every { userRepository.findById(randomUserId) } returns Mono.empty()

        // WHEN
        val actual = userService.update(partialUpdate)

        // THEN
        actual
            .test()
            .verifyError(NotFoundException::class.java)
    }

    @Test
    fun `should delete is user exists`() {
        // GIVEN
        every { userRepository.deleteById(randomUserId) } returns Mono.empty()

        // WHEN
        val actual = userService.delete(randomUserId)

        // THEN
        actual
            .test()
            .verifyComplete()

        verify(exactly = 1) { userRepository.deleteById(randomUserId) }
    }
}
