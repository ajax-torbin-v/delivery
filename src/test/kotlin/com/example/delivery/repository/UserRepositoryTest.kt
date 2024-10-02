package com.example.delivery.repository

import com.example.delivery.UserFixture.unsavedUser
import com.example.delivery.UserFixture.updatedUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Update
import reactor.kotlin.test.test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserRepositoryTest : AbstractMongoTestContainer {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `save should save user and assign id`() {
        // GIVEN //WHEN
        val actual = userRepository.save(unsavedUser)

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
        val savedUser = userRepository.save(unsavedUser).block()

        // WHEN
        val actual = userRepository.findById(savedUser?.id.toString())

        // THEN
        actual
            .test()
            .assertNext { user ->
                assertNotNull(user.id)
                assertEquals(unsavedUser, user.copy(id = null))
            }
            .verifyComplete()
    }

    @Test
    fun `existsById should return if user exists`() {
        // GIVEN
        val savedUser = userRepository.save(unsavedUser).block()

        // WHEN
        val actual = userRepository.existsById(savedUser?.id.toString())

        // THEN
        actual
            .test()
            .expectNext(true)
            .verifyComplete()
    }

    @Test
    fun `deleteById should delete user by id`() {
        // GIVEN
        val savedUser = userRepository.save(unsavedUser).block()

        // WHEN //THEN
        userRepository.deleteById(savedUser?.id.toString())
            .test()
            .verifyComplete()
        // AND THEN
        userRepository.existsById(savedUser?.id.toString())
            .test()
            .expectNext(false)
            .verifyComplete()
    }

    @Test
    fun `update should update user`() {
        // GIVEN
        val savedUser = userRepository.save(unsavedUser).block()
        val update = Update()
            .set("fullName", "UpdatedName")
            .set("phone", "+38-new-phone")

        // WHEN
        val actual = userRepository.update(
            savedUser?.id.toString(),
            update = update
        )

        // THEN
        actual
            .test()
            .assertNext { user -> assertEquals(updatedUser.copy(id = null), user.copy(id = null)) }
            .verifyComplete()
    }
}
