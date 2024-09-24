package com.example.delivery.controller

import com.example.delivery.ProductFixture.createProductDTO
import com.example.delivery.ProductFixture.domainProduct
import com.example.delivery.ProductFixture.updateProductDTO
import com.example.delivery.ProductFixture.updatedDomainProduct
import com.example.delivery.mapper.ProductMapper.toDTO
import com.example.delivery.service.ProductService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@WebMvcTest(ProductController::class)
internal class ProductControllerTest {
    @MockBean
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `should return product when product exists`() {
        // GIVEN
        Mockito.`when`(productService.getById("123")).thenReturn(domainProduct)

        // WHEN // THEN
        mockMvc.perform(get("/products/{id}", "123"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(domainProduct.toDTO())))
    }

    @Test
    fun `should add product and return status created`() {
        // GIVEN
        Mockito.`when`(productService.add(createProductDTO)).thenReturn(domainProduct)

        // WHEN // THEN
        mockMvc.perform(
            post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProductDTO))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(domainProduct.toDTO())))
    }

    @Test
    fun `should update product with proper dto`() {
        // GIVEN
        Mockito.`when`(productService.update("1", updateProductDTO)).thenReturn(updatedDomainProduct)

        // WHEN // THEN
        mockMvc.perform(
            put("/products/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProductDTO))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(updatedDomainProduct.toDTO())))
    }

    @Test
    fun `should delete product and return no content`() {
        // GIVEN
        doNothing().`when`(productService).deleteById("123")

        // WHEN // THEN
        mockMvc.perform(delete("/products/{id}", "123"))
            .andExpect(status().isNoContent)

        // AND THEN
        verify(productService).deleteById("123")
    }
}
