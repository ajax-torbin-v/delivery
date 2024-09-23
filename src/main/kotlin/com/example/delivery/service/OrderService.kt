package com.example.delivery.service

import com.example.delivery.annotaion.LogInvoke
import com.example.delivery.domain.DomainOrder
import com.example.delivery.domain.projection.DomainOrderWithProduct
import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.exception.NotFoundException
import com.example.delivery.exception.ProductAmountException
import com.example.delivery.mapper.OrderMapper.toDomain
import com.example.delivery.mapper.OrderMapper.toMongoModel
import com.example.delivery.mapper.OrderMapper.toUpdate
import com.example.delivery.mapper.OrderWithProductMapper.toDomain
import com.example.delivery.mapper.ProductMapper.toDomain
import com.example.delivery.mongo.MongoOrder
import com.example.delivery.repository.OrderRepository
import com.example.delivery.repository.ProductRepository
import com.example.delivery.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
) {

    @LogInvoke
    fun getById(id: String): DomainOrderWithProduct {
        return orderRepository.findById(id)?.toDomain()
            ?: throw NotFoundException("Order with id $id doesn't exists")
    }

    @SuppressWarnings("ThrowsCount")
    fun add(createOrderDTO: CreateOrderDTO): DomainOrder {
        val user = userRepository.findById(createOrderDTO.userId)
            ?: throw NotFoundException("User with id ${createOrderDTO.userId} doesn't exist")

        val products = orderRepository.fetchProducts(createOrderDTO.items.map { it.productId }).map { it.toDomain() }

        if (products.size != createOrderDTO.items.size) {
            val missingProductId = createOrderDTO.items
                .map { it.productId }
                .first { itemId -> !products.map { it.id.toString() }.contains(itemId) }
            throw NotFoundException("Product with id $missingProductId")
        }

        createOrderDTO.items.forEach { orderItem ->
            val product = products.first { orderItem.productId == it.id.toString() }
            if (orderItem.amount > product.amountAvailable) {
                throw ProductAmountException(
                    "Insufficient stock for product ${product.name}. " +
                        "Available: ${product.amountAvailable}, " +
                        "Requested: ${orderItem.amount}"
                )
            }
        }

        productRepository.updateProductsAmount(
            createOrderDTO.items.map {
                MongoOrder.MongoOrderItem(
                    ObjectId(it.productId),
                    null,
                    it.amount
                )
            }
        )

        val mongoOrderItems = createOrderDTO.items.map { item ->
            MongoOrder.MongoOrderItem(
                ObjectId(item.productId),
                products.first { it.id.toString() == item.productId }.price,
                item.amount
            )
        }

        productRepository.updateProductsAmount(mongoOrderItems)

        val order = MongoOrder(
            items = mongoOrderItems,
            shipmentDetails = createOrderDTO.shipmentDetails.toMongoModel(),
            status = MongoOrder.Status.NEW,
            userId = user.id
        )

        return orderRepository.save(order).toDomain()
    }

    fun deleteById(id: String) {
        orderRepository.deleteById(id)
    }

    fun updateOrder(id: String, updateOrderDTO: UpdateOrderDTO): DomainOrder {
        return orderRepository.updateOrder(id, updateOrderDTO.toUpdate())?.toDomain()
            ?: throw NotFoundException("Order with id $id doesn't exist")
    }

    fun updateOrderStatus(id: String, status: String): DomainOrder {
        return orderRepository.updateOrderStatus(id, MongoOrder.Status.valueOf(status))?.toDomain()
            ?: throw NotFoundException("Order with id $id doesn't exist")
    }
}
