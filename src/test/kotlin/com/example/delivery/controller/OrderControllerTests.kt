package com.example.delivery.controller

import com.example.delivery.createOrderDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.model.MongoOrder
import com.example.delivery.order
import com.example.delivery.service.OrderService
import com.example.delivery.user
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

@WebMvcTest(OrderController::class)
class OrderControllerTests {
    @MockBean
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @DisplayName("Add valid order")
    fun `should add order and return status is created` () {
        Mockito.`when`(orderService.add(createOrderDTO)).thenReturn(order.toDTO())

        mockMvc.perform(post("/api/orders/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createOrderDTO)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.items").value(mutableMapOf("1" to 2)))
            .andExpect(jsonPath("$.totalPrice").value(100.0))
            .andExpect(jsonPath("$.shipmentDetails").value(mutableMapOf(
                "city" to "city",
                "street" to "street",
                "building" to "3a",
                "index" to "54890")
            ))
            .andExpect(jsonPath("$.status").value("NEW"))
    }

    @Test
    @DisplayName("Find existing order")
    fun `should return order when order exists` () {
        Mockito.`when`(orderService.findById("1")).thenReturn(order.toDTO())

        mockMvc.perform(get("/api/orders/find/{id}", "1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.items").value(mutableMapOf("1" to 2)))
            .andExpect(jsonPath("$.totalPrice").value(100.0))
            .andExpect(jsonPath("$.shipmentDetails").value(mutableMapOf(
                "city" to "city",
                "street" to "street",
                "building" to "3a",
                "index" to "54890")
            ))
            .andExpect(jsonPath("$.status").value("NEW"))
    }

    @Test
    @DisplayName("Update status of existing user")
    fun `should update status when user exists` () {
        Mockito.`when`(orderService.findById("1")).thenReturn(order.toDTO())
        val updateOrderDTO = UpdateOrderDTO("1", MongoOrder.Status.CANCELED)

        mockMvc.perform(put("/api/orders/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateOrderDTO)))
            .andExpect(status().isOk)

        verify(orderService).updateStatus(updateOrderDTO)
    }

    @Test
    @DisplayName("Delete order when order exists")
    fun `should delete order when order exists` () {
        doNothing().`when`(orderService).deleteById("1")

        mockMvc.perform(delete("/api/orders/deleteById/{id}", "1"))
            .andExpect(status().isNoContent)

        verify(orderService).deleteById("1")
    }
}