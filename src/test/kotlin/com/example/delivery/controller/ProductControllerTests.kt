package com.example.delivery.controller

import com.example.delivery.createProductDTO
import com.example.delivery.productDTO
import com.example.delivery.products
import com.example.delivery.service.ProductService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
@WebMvcTest(ProductController::class)
class ProductControllerTests {
    @MockBean
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should return product when product exists` () {
        Mockito.`when`(productService.findById("123")).thenReturn(productDTO)

        mockMvc.perform(get("/api/products/{id}", "123"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value(productDTO.name))
            .andExpect(jsonPath("$.price").value(productDTO.price))
            .andExpect(jsonPath("$.amount").value(productDTO.amount))
            .andExpect(jsonPath("$.measurement").value(productDTO.measurement))
    }

    @Test
    fun `should return list of all products`() {
        Mockito.`when`(productService.findAll()).thenReturn(products)

        mockMvc.perform(get("/api/products/all"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(products.size))
    }

    @Test
    fun `should add product and return status created`() {
        Mockito.`when`(productService.add(createProductDTO)).thenReturn(productDTO)

        mockMvc.perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProductDTO))
        )
            .andExpect(status().isCreated)

    }

    @Test
    fun `should delete product and return no content`() {
        doNothing().`when`(productService).deleteById("123")

        mockMvc.perform(delete("/api/products/{id}", "123"))
            .andExpect(status().isNoContent)

        verify(productService).deleteById("123")
    }
}
