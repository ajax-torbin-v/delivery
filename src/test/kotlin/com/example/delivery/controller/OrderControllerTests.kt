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
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
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
    fun `should add order and return status is created` () {
        Mockito.`when`(orderService.add(createOrderDTO)).thenReturn(order.toDTO())

        mockMvc.perform(post("/api/orders")
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
    fun `should return order when order exists` () {
        Mockito.`when`(orderService.findById("1")).thenReturn(order.toDTO())

        mockMvc.perform(get("/api/orders/{id}", "1"))
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
    fun `should update status when user exists and return it` () {
        val updateOrderDTO = UpdateOrderDTO("1", MongoOrder.Status.CANCELED)
        val updatedOrder = order.toDTO().copy(status = MongoOrder.Status.CANCELED)
        Mockito.`when`(orderService.updateStatus(updateOrderDTO)).thenReturn(updatedOrder)

        mockMvc.perform(put("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateOrderDTO)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("CANCELED"))

        verify(orderService).updateStatus(updateOrderDTO)
    }

    @Test
    fun `should delete order when order exists` () {
        doNothing().`when`(orderService).deleteById("1")

        mockMvc.perform(delete("/api/orders/{id}", "1"))
            .andExpect(status().isNoContent)

        verify(orderService).deleteById("1")
    }
}
