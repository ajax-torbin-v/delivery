package com.example.delivery.service

import com.example.delivery.createProductDTO
import com.example.delivery.exception.NotFoundException
import com.example.delivery.product
import com.example.delivery.productDTO
import com.example.delivery.repository.ProductRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

class ProductServiceTests {
    @Mock
    lateinit var productRepository: ProductRepository

    @InjectMocks
    lateinit var productService: ProductService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @DisplayName("Add valid product")
    fun `should add product with proper dto` () {
        Mockito.`when`(productRepository.save(product)).thenReturn(product)
        productService.add(createProductDTO)
        verify(productRepository, times(1)).save(product)
    }

    @Test
    @DisplayName("Find existing product")
    fun `should return product when product exists` (){
        Mockito.`when`(productRepository.findById("1")).thenReturn(product)
        assertEquals(productService.findById("1"), productDTO)
    }

    @Test
    @DisplayName("Find non existing product")
    fun `should throw an exception when product don't exist` () {
        Mockito.`when`(productRepository.findById("13")).thenReturn(null)
        assertThrows<NotFoundException> { productService.findById("13") }
    }

    @Test
    @DisplayName("Delete existing product")
    fun `should be okay when deleting existing product` () {
        Mockito.`when`(productRepository.existsById("1")).thenReturn(true)
        productService.deleteById("1")
        verify(productRepository, times(1)).deleteById("1")
    }

    @Test
    @DisplayName("Delete non existing product")
    fun `should throw exception when deleting existing product` () {
        Mockito.`when`(productRepository.existsById("1")).thenReturn(false)
        assertThrows<NotFoundException> { productService.deleteById("1") }
    }
}