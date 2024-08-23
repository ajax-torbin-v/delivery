package com.example.delivery.service

import com.example.delivery.ProductFixture.createProductDTO
import com.example.delivery.ProductFixture.domainProduct
import com.example.delivery.ProductFixture.product
import com.example.delivery.exception.NotFoundException
import com.example.delivery.repository.ProductRepository
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
    fun `should add product with proper dto`() {
        //GIVEN
        Mockito.`when`(productRepository.save(product.copy(id = null))).thenReturn(product)

        //WHEN
        productService.add(createProductDTO)

        //THEN
        verify(productRepository, times(1)).save(product.copy(id = null))
    }

    @Test
    fun `should return product when product exists`() {
        //GIVEN
        Mockito.`when`(productRepository.findById("1")).thenReturn(product)

        //WHEN
        val expected = productService.getById("1")

        //THEN
        assertEquals(expected, domainProduct)
    }

    @Test
    fun `should throw an exception when product don't exist`() {
        //GIVEN
        Mockito.`when`(productRepository.findById("13")).thenReturn(null)

        //WHEN //THEN
        assertThrows<NotFoundException> { productService.getById("13") }
    }

    @Test
    fun `should be okay when deleting existing product`() {
        //GIVEN //WHEN
        productService.deleteById("1")

        //THEN
        verify(productRepository, times(1)).deleteById("1")
    }
}