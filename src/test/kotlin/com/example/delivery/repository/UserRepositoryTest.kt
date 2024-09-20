package com.example.delivery.repository

import com.example.delivery.UserFixture.unsavedUser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Update
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserRepositoryTest : AbstractMongoTestContainer {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `save should save user and assign id`() {
        val actual = userRepository.save(unsavedUser)
        assertTrue(actual.id != null, "Id should not be null after save!")
    }

    @Test
    fun `findById should return saved user`() {
        // GIVEN
        val savedUser = userRepository.save(unsavedUser)

        // WHEN
        val actual = userRepository.findById(savedUser.id.toString())

        // THEN
        assertEquals(savedUser, actual)
    }

    @Test
    fun `existsById should return if user exists`() {
        // GIVEN
        val savedUser = userRepository.save(unsavedUser)

        // WHEN
        val actual = userRepository.existsById(savedUser.id.toString())

        // THEN
        assertTrue(actual, "User should exist!")
    }

    @Test
    fun `deleteById should delete user by id`() {
        // GIVEN
        val savedUser = userRepository.save(unsavedUser)

        // WHEN
        userRepository.deleteById(savedUser.id.toString())

        // THEN
        assertFalse(userRepository.existsById(savedUser.id.toString()))
    }

    @Test
    fun `update should update user`() {
        // GIVEN
        val savedUser = userRepository.save(unsavedUser)
        val update = Update()
            .set("fullName", "UpdatedName")
            .set("phone", "+38-new-phone")

        // WHEN
        val actual = userRepository.update(
            savedUser.id.toString(),
            update = update
        )

        // THEN
        assertEquals(
            savedUser.copy(
                id = savedUser.id,
                fullName = "UpdatedName",
                phone = "+38-new-phone"
            ),
            actual
        )
    }
}
