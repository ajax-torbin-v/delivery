package com.example.delivery.service

import com.example.delivery.createOrderDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.exception.NotFoundException
import com.example.delivery.model.MongoOrder
import com.example.delivery.order
import com.example.delivery.orderDTO
import com.example.delivery.product
import com.example.delivery.repository.OrderRepository
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

class OrderServiceTests {
    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var productRepository: ProductRepository

    @InjectMocks
    private lateinit var orderService: OrderService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @DisplayName("Find existing order")
    fun `should return order when order exists` () {
        Mockito.`when`(orderRepository.findById("1")).thenReturn(order)

        assertEquals(orderService.findById("1"), orderDTO)
    }

    @Test
    @DisplayName("Find nop existing order")
    fun `should throw exception when order doesn't exists while find` () {
        Mockito.`when`(orderRepository.findById("1")).thenReturn(null)
        assertThrows<NotFoundException>{orderService.findById("1")}
    }

    @Test
    @DisplayName("Add order with proper dto")
    fun `should add order with proper dto`() {
        Mockito.`when`(orderRepository.save(any())).thenReturn(order)
        Mockito.`when`(productRepository.findById("1")).thenReturn(product)
        val result = orderService.add(createOrderDTO)
        verify(orderRepository, times(1)).save(any())
        assertEquals(order.toDTO(), result)
    }

    @Test
    @DisplayName("Update existing order status")
    fun `should update when order exists` () {
        Mockito.`when`(orderRepository.existsById("1")).thenReturn(true)
        orderService.updateStatus(UpdateOrderDTO("1", MongoOrder.Status.CANCELED))
        verify(orderRepository, times(1)).updateOrderStatus("1", MongoOrder.Status.CANCELED)
    }

    @Test
    @DisplayName("Update non existing order status")
    fun `should throw exception when order doesn't exists while update` () {
        Mockito.`when`(orderRepository.existsById("1")).thenReturn(false)
        assertThrows<NotFoundException>{ orderService.updateStatus(UpdateOrderDTO("1", MongoOrder.Status.CANCELED)) }
    }

}
