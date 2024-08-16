package com.example.delivery.controller

import com.example.delivery.createUserDTO
import com.example.delivery.service.UserService
import com.example.delivery.user
import com.example.delivery.userDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(MockitoExtension::class)
@WebMvcTest(UserController::class)
class UserControllerTests {
    @MockBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `should add user and return created` () {
        Mockito.`when`(userService.add(createUserDTO)).thenReturn(userDTO)

        mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createUserDTO)))
            .andExpect(jsonPath("$.fullName").value("FULL NAME"))
            .andExpect(jsonPath("$.phone").value("+31243123"))
            .andExpect(jsonPath("$.orderIds").isArray())
            .andExpect(jsonPath("$.orderIds").isNotEmpty)
    }

    @Test
    fun `should return user when user exists` () {
        Mockito.`when`(userService.findById("1")).thenReturn(userDTO)

        mockMvc.perform(get("/api/users/{id}", "1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fullName").value("FULL NAME"))
            .andExpect(jsonPath("$.phone").value("+31243123"))
            .andExpect(jsonPath("$.orderIds").isNotEmpty)
            .andExpect(jsonPath("$.orderIds").value(mutableListOf("1")))
    }

    @Test
    fun `should add order when user exists` () {
        val updatedOrderIds = user.orderIds?.toMutableList()
        updatedOrderIds?.add("5")
        val updatedUser = user.copy(orderIds = updatedOrderIds)
        Mockito.`when`(userService.addOrder("1", "5")).thenReturn(updatedUser.toDTO())

        mockMvc.perform(put("/api/users/{userId}/orders", "1")
            .param("orderId", "5")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.orderIds").value(updatedOrderIds))
    }

    @Test
    fun `should delete existing user and return no content` () {
        doNothing().`when`(userService).deleteById("1")

        mockMvc.perform(delete("/api/users/{id}", "1"))
            .andExpect(status().isNoContent)

        verify(userService).deleteById("1")
    }
}
