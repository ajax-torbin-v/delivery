package com.example.delivery.service

import com.example.delivery.UserFixture.createUserDTO
import com.example.delivery.UserFixture.domainUser
import com.example.delivery.UserFixture.user
import com.example.delivery.exception.NotFoundException
import com.example.delivery.repository.OrderRepository
import com.example.delivery.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
internal class UserServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var orderRepository: OrderRepository

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    fun `should add user with proper DTO`() {
        //GIVEN
        val userWithoutId = user.copy(id = null)
        Mockito.`when`(userRepository.save(any())).thenReturn(user)

        //WHEN
        userService.add(createUserDTO)

        //THEN
        verify(userRepository, times(1)).save(userWithoutId)
    }

    @Test
    fun `should return user when user exists`() {
        //GIVEN
        Mockito.`when`(userRepository.findById("2")).thenReturn(user)

        //WHEN //THEN
        assertEquals(domainUser, userService.getById("2"))
    }

    @Test
    fun `should throw exception when user doesn't exists`() {
        //GIVEN
        Mockito.`when`(userRepository.findById("3")).thenReturn(null)

        //WHEN //THEN
        assertThrows<NotFoundException> { userService.getById("3") }
    }

    @Test
    fun `should delete is user exists`() {
        //GIVEN //WHEN
        userService.deleteById("8")

        //THEN
        verify(userRepository, times(1)).deleteById("8")
    }
}
