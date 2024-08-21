package com.example.delivery.service

import com.example.delivery.ProductFixture.createProductDTO
import com.example.delivery.ProductFixture.domainProduct
import com.example.delivery.ProductFixture.product
import com.example.delivery.exception.NotFoundException
import com.example.delivery.repository.ProductRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
internal class ProductServiceTest {
    @Mock
    lateinit var productRepository: ProductRepository

    @InjectMocks
    lateinit var productService: ProductService


    @Test
    @DisplayName("Add valid product")
    fun `should add product with proper dto`() {
        //GIVEN
        Mockito.`when`(productRepository.save(product.copy(id = null))).thenReturn(product)
        //THEN
        productService.add(createProductDTO)
        verify(productRepository, times(1)).save(product.copy(id = null))
    }

    @Test
    @DisplayName("Find existing product")
    fun `should return product when product exists`() {
        //GIVEN
        Mockito.`when`(productRepository.findById("1")).thenReturn(product)

        //THEN
        assertEquals(productService.findById("1"), domainProduct)
    }

    @Test
    @DisplayName("Find non existing product")
    fun `should throw an exception when product don't exist`() {
        //GIVEN
        Mockito.`when`(productRepository.findById("13")).thenReturn(null)

        //THEN
        assertThrows<NotFoundException> { productService.findById("13") }
    }

    @Test
    @DisplayName("Delete existing product")
    fun `should be okay when deleting existing product`() {
        //GIVEN
        Mockito.`when`(productRepository.existsById("1")).thenReturn(true)

        //WHEN
        productService.deleteById("1")

        //THEN
        verify(productRepository, times(1)).deleteById("1")
    }

    @Test
    @DisplayName("Delete non existing product")
    fun `should throw exception when deleting existing product`() {
        //GIVEN
        Mockito.`when`(productRepository.existsById("1")).thenReturn(false)

        //THEN
        assertThrows<NotFoundException> { productService.deleteById("1") }
    }
}
