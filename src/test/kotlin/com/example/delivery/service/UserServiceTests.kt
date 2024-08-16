package com.example.delivery.service

import com.example.delivery.exception.NotFoundException
import com.example.delivery.createUserDTO
import com.example.delivery.repository.OrderRepository
import com.example.delivery.repository.UserRepository
import com.example.delivery.user
import com.example.delivery.userDTO
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class UserServiceTests {
    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var orderRepository: OrderRepository

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    fun `should add user with proper DTO` () {
        Mockito.`when`(userRepository.save(any())).thenReturn(createUserDTO.toEntity())
        userService.add(createUserDTO)
        verify(userRepository, times(1)).save(createUserDTO.toEntity())
    }

    @Test
    fun `should return user when user exists` () {
        Mockito.`when`(userRepository.findById("2")).thenReturn(user)
        assertEquals(userService.findById("2"), userDTO)
    }

    @Test
    fun `should throw exception when user doesn't exists` () {
        Mockito.`when`(userRepository.findById("3")).thenReturn(null)
        assertThrows<NotFoundException> { userService.findById("3") }
    }

    @Test
    fun `should be ok when user and order exists` () {
        userRepository.addOrder("1", "2")
        verify(userRepository, times(1)).addOrder("1", "2")
    }

    @Test
    fun `should throw exception if user or order doesn't exists` () {
        assertThrows<NotFoundException> ("Order with id 2 doesn't exists") { userService.addOrder("1", "2")}
    }

    @Test
    fun `should delete is user exists` () {
        Mockito.`when`(userRepository.existsById("8")).thenReturn(true)
        userService.deleteById("8")
        verify(userRepository, times(1)).deleteById("8")
    }

    @Test
    fun `should throw exception is user doesn't exists` () {
        Mockito.`when`(userRepository.existsById("8")).thenReturn(false)
        assertThrows<NotFoundException> { userService.deleteById("8") }
    }

}
