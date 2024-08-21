package com.example.delivery.controller

import com.example.delivery.OrderFixture.createOrderDTO
import com.example.delivery.OrderFixture.domainOrder
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.service.OrderService
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

@WebMvcTest(OrderController::class)
internal class OrderControllerTest {
    @MockBean
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `should add order and return status is created`() {
        //GIVEN
        Mockito.`when`(orderService.add(createOrderDTO)).thenReturn(domainOrder)

        //THEN
        mockMvc.perform(
            post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderDTO))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.items").value(mutableMapOf("123456789011121314151617" to 2)))
            .andExpect(jsonPath("$.totalPrice").value(100.0))
            .andExpect(
                jsonPath("$.shipmentDetails").value(
                    mutableMapOf(
                        "city" to "city",
                        "street" to "street",
                        "building" to "3a",
                        "index" to "54890"
                    )
                )
            )
            .andExpect(jsonPath("$.status").value("NEW"))
    }

    @Test
    fun `should return order when order exists`() {
        //GIVEN
        Mockito.`when`(orderService.findById("1")).thenReturn(domainOrder)

        //THEN
        mockMvc.perform(get("/orders/{id}", "1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.items").value(mutableMapOf("123456789011121314151617" to 2)))
            .andExpect(jsonPath("$.totalPrice").value(100.0))
            .andExpect(
                jsonPath("$.shipmentDetails").value(
                    mutableMapOf(
                        "city" to "city",
                        "street" to "street",
                        "building" to "3a",
                        "index" to "54890"
                    )
                )
            )
            .andExpect(jsonPath("$.status").value("NEW"))
    }

    @Test
    fun `should update status when user exists and return it`() {
        //GIVEN
        val updateOrderDTO = UpdateOrderDTO("CANCELED")
        val updateDomainOrder = domainOrder.copy(status = "CANCELED")
        Mockito.`when`(orderService.updateStatus("1", updateOrderDTO)).thenReturn(updateDomainOrder)

        //THEN
        mockMvc.perform(
            put("/orders/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateOrderDTO))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("CANCELED"))

        verify(orderService).updateStatus("1", updateOrderDTO)
    }

    @Test
    fun `should delete order when order exists`() {
        //GIVEN
        doNothing().`when`(orderService).deleteById("1")

        //THEN
        mockMvc.perform(delete("/orders/{id}", "1"))
            .andExpect(status().isNoContent)

        verify(orderService).deleteById("1")
    }
}
