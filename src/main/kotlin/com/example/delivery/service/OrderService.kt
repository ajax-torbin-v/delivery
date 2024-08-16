package com.example.delivery.service

import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.dto.response.OrderDTO
import com.example.delivery.exception.NotFoundException
import com.example.delivery.model.MongoOrder
import com.example.delivery.model.MongoProduct
import com.example.delivery.repository.OrderRepository
import com.example.delivery.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository) {

    fun findById(id: String): OrderDTO {
        val order: MongoOrder = orderRepository.findById(id) ?: throw NotFoundException("Order with id $id doesn't exists")
        return order.toDTO()
    }

    fun add(createOrderDTO: CreateOrderDTO): OrderDTO {
        var sum = 0.0
        createOrderDTO.items.mapNotNull { (productId, amount) ->
            val product: MongoProduct = productRepository.findById(productId)
                ?: throw NotFoundException("Product with id $productId doesn't exists")
            val availableAmount = product.amountAvailable ?:
            throw IllegalStateException("Product with id $productId has no available amount")
            if (availableAmount < amount) {
                throw ArithmeticException("Not enough items")
            }
            val price = product.price ?:
            throw IllegalStateException("Product with id $productId has no price")
            sum += price * amount
            product to amount
        }.toMap()
        val order: MongoOrder = orderRepository.save(MongoOrder(
            items = createOrderDTO.items,
            totalPrice = sum,
            shipmentDetails = createOrderDTO.shipmentDetails)
        )
        return order.toDTO()
    }

    fun updateStatus(updateOrderDTO: UpdateOrderDTO) {
        if (!orderRepository.existsById(updateOrderDTO.id))
            throw NotFoundException("Order with id " + updateOrderDTO.id + "doesn't exists")
        orderRepository.updateOrderStatus(updateOrderDTO.id, updateOrderDTO.status)
    }

    fun deleteById(id: String) =
        if (orderRepository.existsById(id)) orderRepository.deleteById(id)
        else throw  NotFoundException("Order with id $id doesn't exists")
}