package com.example.delivery.service

import com.example.delivery.domain.DomainOrder
import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.exception.NotFoundException
import com.example.delivery.mapper.OrderMapper.toDomain
import com.example.delivery.mapper.ProductMapper.toDomain
import com.example.delivery.mongo.MongoOrder
import com.example.delivery.repository.OrderRepository
import com.example.delivery.repository.ProductRepository
import com.example.delivery.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val productReserveService: ProductReserveService,
    private val productRepository: ProductRepository,
) {

    fun findById(id: String): DomainOrder {
        val order: MongoOrder = orderRepository.findById(id)
            ?: throw NotFoundException("Order with id $id doesn't exists")
        return order.toDomain()
    }

    fun add(createOrderDTO: CreateOrderDTO): DomainOrder {
        val user = userRepository.findById(createOrderDTO.userId)
            ?: throw NotFoundException("User with id ${createOrderDTO.userId} doesn't exist")

        val products = createOrderDTO.items.mapKeys {
            productRepository.findById(it.key)?.toDomain()
                ?: throw NotFoundException("Product with id ${it.key} doesn't exist")
        }

        productReserveService.reserveProducts(products)
        products.forEach { productRepository.updateAmount(it.key.id.toString(), -it.value) }

        val totalPrice = productReserveService.calculateTotalPrice(products)

        val order = MongoOrder(
            userId = user.id,
            items = products.mapKeys { it.key.id!! },
            totalPrice = totalPrice
        )

        val savedOrder: MongoOrder = orderRepository.save(order)
        return savedOrder.toDomain()
    }

    fun updateStatus(id: String, updateOrderDTO: UpdateOrderDTO): DomainOrder {
        val updatedOrder =
            orderRepository.updateOrderStatus(id, MongoOrder.Status.valueOf(updateOrderDTO.status))
                ?: throw NotFoundException("Order with id " + id + "doesn't exists")
        return updatedOrder.toDomain()
    }

    fun deleteById(id: String) =
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id)
        } else {
            throw NotFoundException("Order with id $id doesn't exists")
        }
}
