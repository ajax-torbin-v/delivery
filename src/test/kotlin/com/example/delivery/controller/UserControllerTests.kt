package com.example.delivery.controller

import com.example.delivery.createUserDTO
import com.example.delivery.service.UserService
import com.example.delivery.userDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(UserController::class)
class UserControllerTests {
    @MockBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @DisplayName("Add user")
    fun `should add user and return created` () {
        Mockito.`when`(userService.add(createUserDTO)).thenReturn(userDTO)

        mockMvc.perform(post("/api/users/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createUserDTO)))
            .andExpect(jsonPath("$.fullName").value("FULL NAME"))
            .andExpect(jsonPath("$.phone").value("+31243123"))
            .andExpect(jsonPath("$.orderIds").isArray())
            .andExpect(jsonPath("$.orderIds").isNotEmpty)
    }

    @Test
    @DisplayName("Find existing user")
    fun `should return user when user exists` () {
        Mockito.`when`(userService.findById("1")).thenReturn(userDTO)

        mockMvc.perform(get("/api/users/findById/{id}", "1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fullName").value("FULL NAME"))
            .andExpect(jsonPath("$.phone").value("+31243123"))
            .andExpect(jsonPath("$.orderIds").isNotEmpty)
            .andExpect(jsonPath("$.orderIds").value(mutableListOf("1")))
    }

    @Test
    @DisplayName("Add order for existing user")
    fun `should add order when user exists` () {
        doNothing().`when`(userService).addOrder("1", "2")

        mockMvc.perform(put("/api/users/add-order")
            .param("userId", "1")
            .param("orderId", "2")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("Delete existing user by id")
    fun `should delete existing user and return no content` () {
        doNothing().`when`(userService).deleteById("1")

        mockMvc.perform(delete("/api/users/deleteById/{id}", "1"))
            .andExpect(status().isNoContent)

        verify(userService).deleteById("1")
    }
}
