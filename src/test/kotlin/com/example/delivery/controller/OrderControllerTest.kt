package com.example.delivery.controller

import com.example.delivery.OrderFixture.createOrderDTO
import com.example.delivery.OrderFixture.domainOrder
import com.example.delivery.OrderFixture.domainOrderWithProduct
import com.example.delivery.OrderFixture.updateOrderDTO
import com.example.delivery.OrderFixture.updatedDomainOrder
import com.example.delivery.domain.DomainOrder
import com.example.delivery.mapper.OrderMapper.toDTO
import com.example.delivery.mapper.OrderWithProductMapper.toDTO
import com.example.delivery.service.OrderService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(OrderController::class)
internal class OrderControllerTest {
    @MockBean
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `should add order and return status is created`() {
        // GIVEN
        Mockito.`when`(orderService.add(any())).thenReturn(domainOrder)

        // WHEN // THEN
        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderDTO))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(domainOrder.toDTO())))
    }

    @Test
    fun `should return order when order exists`() {
        // GIVEN
        Mockito.`when`(orderService.getById("1")).thenReturn(domainOrderWithProduct)

        // WHEN // THEN
        mockMvc.perform(get("/orders/{id}", "1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(domainOrderWithProduct.toDTO())))
    }

    @Test
    fun `should update order with proper dto`() {
        // GIVEN
        Mockito.`when`(orderService.updateOrder("1", updateOrderDTO)).thenReturn(updatedDomainOrder)

        // WHEN // THEN
        mockMvc.perform(
            put("/orders/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateOrderDTO))
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(updatedDomainOrder.toDTO())))
    }

    @Test
    fun `should update order's status`() {
        // GIVEN
        Mockito.`when`(orderService.updateOrderStatus("1", "CANCELED"))
            .thenReturn(domainOrder.copy(status = DomainOrder.Status.CANCELED))

        // WHEN // THEN
        mockMvc.perform(
            patch("/orders/{id}?status={status}", "1", "CANCELED")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateOrderDTO))
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    objectMapper.writeValueAsString(
                        domainOrder.copy(status = DomainOrder.Status.CANCELED).toDTO()
                    )
                )
            )
    }

    @Test
    fun `should delete order when order exists`() {
        // GIVEN
        doNothing().`when`(orderService).deleteById("1")

        // WHEN // THEN
        mockMvc.perform(delete("/orders/{id}", "1"))
            .andExpect(status().isNoContent)

        // AND THEN
        verify(orderService).deleteById("1")
    }
}
