package com.example.delivery.service

import com.example.delivery.OrderFixture.createOrderDTO
import com.example.delivery.OrderFixture.domainOrder
import com.example.delivery.OrderFixture.domainOrderWithProduct
import com.example.delivery.OrderFixture.mongoOrderWithProduct
import com.example.delivery.OrderFixture.order
import com.example.delivery.OrderFixture.orderUpdateObject
import com.example.delivery.OrderFixture.updateOrderDTO
import com.example.delivery.OrderFixture.updatedDomainOrder
import com.example.delivery.OrderFixture.updatedOrder
import com.example.delivery.ProductFixture.product
import com.example.delivery.UserFixture.user
import com.example.delivery.exception.NotFoundException
import com.example.delivery.exception.ProductAmountException
import com.example.delivery.repository.OrderRepository
import com.example.delivery.repository.ProductRepository
import com.example.delivery.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
internal class OrderServiceTest {
    @Mock
    private lateinit var orderRepository: OrderRepository

    @SuppressWarnings("UnusedPrivateProperty")
    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var orderService: OrderService

    @Test
    fun `should return order when order exists`() {
        // GIVEN
        Mockito.`when`(orderRepository.findById("1")).thenReturn(mongoOrderWithProduct)

        // WHEN // THEN
        assertEquals(orderService.getById("1"), domainOrderWithProduct)
    }

    @Test
    fun `should throw exception when order doesn't exists while find`() {
        // GIVEN
        Mockito.`when`(orderRepository.findById("1")).thenReturn(null)

        // WHEN // THEN
        assertThrows<NotFoundException> { orderService.getById("1") }
    }

    @Test
    fun `should add order with proper dto`() {
        // GIVEN
        Mockito.`when`(orderRepository.save(any())).thenReturn(order)
        Mockito.`when`(userRepository.findById("123456789011121314151617")).thenReturn(user)
        Mockito.`when`(productRepository.findAllByIds(listOf("123456789011121314151617")))
            .thenReturn(listOf(product))

        // WHEN
        val actual = orderService.add(createOrderDTO)

        // THEN
        verify(orderRepository, times(1)).save(any())
        assertEquals(domainOrder, actual)
    }

    @Test
    fun `should throw exception when product doesn't exist`() {
        // GIVEN
        Mockito.`when`(userRepository.findById("123456789011121314151617")).thenReturn(user)
        Mockito.`when`(productRepository.findAllByIds(listOf("123456789011121314151617")))
            .thenReturn(emptyList())

        // WHEN // THEN
        assertThrows<NotFoundException> { orderService.add(createOrderDTO) }
    }

    @Test
    fun `should throw exception when not sufficient product`() {
        // GIVEN
        Mockito.`when`(userRepository.findById("123456789011121314151617")).thenReturn(user)
        Mockito.`when`(productRepository.findAllByIds(listOf("123456789011121314151617")))
            .thenReturn(listOf(product.copy(amountAvailable = -1)))

        // WHEN // THEN
        assertThrows<ProductAmountException> { orderService.add(createOrderDTO) }
    }

    @Test
    fun `should update order with proper dto when product exists`() {
        // GIVEN
        Mockito.`when`(orderRepository.updateOrder("1", orderUpdateObject)).thenReturn(updatedOrder)

        // WHEN
        val actual = orderService.updateOrder("1", updateOrderDTO)

        // THEN
        verify(orderRepository, times(1)).updateOrder("1", orderUpdateObject)
        assertEquals(updatedDomainOrder, actual)
    }

    @Test
    fun `should throw exception if order not exists on update`() {
        // GIVEN
        Mockito.`when`(orderRepository.updateOrder("1", orderUpdateObject)).thenReturn(null)

        // WHEN // THEN
        assertThrows<NotFoundException> { orderService.updateOrder("1", updateOrderDTO) }
    }

    @Test
    fun `should delete order`() {
        // GIVEN
        doNothing().`when`(orderRepository).deleteById("1")

        // WHEN
        orderService.deleteById("1")

        // THEN
        verify(orderRepository, times(1)).deleteById("1")
    }
}
