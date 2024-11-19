package com.example.domainservice.user.infrastructure.mongo

import com.example.domainservice.UserFixture.partialUpdate
import com.example.domainservice.UserFixture.unsavedDomainUser
import com.example.domainservice.UserFixture.updatedDomainUser
import com.example.domainservice.user.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

class UserRepositoryTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `save should save user and assign id`() {
        // GIVEN //WHEN
        val actual = userRepository.save(unsavedDomainUser)

        // THEN
        actual
            .test()
            .assertNext { user ->
                assertTrue(user.id != null, "Id should not be null after save!")
            }
            .verifyComplete()
    }

    @Test
    fun `findById should return saved user`() {
        // GIVEN
        val savedUser = userRepository.save(unsavedDomainUser).block()

        // WHEN
        val actual = userRepository.findById(savedUser?.id.toString())

        // THEN
        actual
            .test()
            .assertNext { user ->
                assertNotNull(user.id)
                assertEquals(unsavedDomainUser, user.copy(id = null))
            }
            .verifyComplete()
    }

    @Test
    fun `deleteById should delete user by id`() {
        // GIVEN
        val savedUser = userRepository.save(unsavedDomainUser).block()

        // WHEN //THEN
        userRepository.deleteById(savedUser?.id.toString())
            .test()
            .expectNext(Unit)
            .verifyComplete()
        // AND THEN
        userRepository.findById(savedUser?.id.toString())
            .test()
            .verifyComplete()
    }

    @Test
    fun `update should update user`() {
        // GIVEN
        val savedUser = userRepository.save(unsavedDomainUser).block()

        // WHEN
        val actual = userRepository.update(partialUpdate)

        // THEN
        actual
            .test()
            .assertNext { user -> assertEquals(updatedDomainUser.copy(id = null), user.copy(id = null)) }
            .verifyComplete()
    }
}
