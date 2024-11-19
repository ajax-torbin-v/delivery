package com.example.domainservice.user.application

import com.example.core.UserFixture.randomUserId
import com.example.domainservice.UserFixture.domainUser
import com.example.domainservice.user.application.mapper.UserMapper.applyPartialUpdate
import com.example.domainservice.user.domain.DomainUser
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class UserMapperTest {
    @Test
    fun `should handle empty fields for partial update`() {
        // GIVEN
        val partialUpdate = DomainUser(id = randomUserId, "", "", "")
        // WHEN
        val updatedUser = domainUser.applyPartialUpdate(partialUpdate)
        // THEN
        assertEquals(updatedUser, domainUser)
    }
}
