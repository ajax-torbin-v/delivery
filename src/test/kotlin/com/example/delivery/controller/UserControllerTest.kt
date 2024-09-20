package com.example.delivery.controller

import com.example.delivery.UserFixture.createUserDTO
import com.example.delivery.UserFixture.domainUser
import com.example.delivery.UserFixture.updateUserDTO
import com.example.delivery.UserFixture.updatedDomainUser
import com.example.delivery.service.UserService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
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

@WebMvcTest(UserController::class)
internal class UserControllerTest {
    @MockBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `should add user and return created`() {
        // GIVEN
        Mockito.`when`(userService.add(createUserDTO)).thenReturn(domainUser)

        // WHEN // THEN
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDTO))
        )
            .andExpect(jsonPath("$.fullName").value("FULL NAME"))
            .andExpect(jsonPath("$.phone").value("+31243123"))
    }

    @Test
    fun `should return user when user exists`() {
        // GIVEN
        Mockito.`when`(userService.getById("1")).thenReturn(domainUser)

        // WHEN // THEN
        mockMvc.perform(get("/users/{id}", "1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fullName").value("FULL NAME"))
            .andExpect(jsonPath("$.phone").value("+31243123"))
    }

    @Test
    fun `should update user`() {
        // GIVEN
        Mockito.`when`(userService.update("1", updateUserDTO)).thenReturn(updatedDomainUser)

        // WHEN // THEN
        mockMvc.perform(
            put("/users/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserDTO))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.fullName").value("new full name"))
            .andExpect(jsonPath("$.phone").value("new phone"))
    }

    @Test
    fun `should delete existing user and return no content`() {
        // GIVEN
        doNothing().`when`(userService).deleteById("1")

        // WHEN // THEN
        mockMvc.perform(delete("/users/{id}", "1"))
            .andExpect(status().isNoContent)

        // AND THEN
        verify(userService).deleteById("1")
    }
}
