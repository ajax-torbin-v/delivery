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
import com.example.delivery.mongo.MongoOrder
import com.example.delivery.repository.OrderRepository
import com.example.delivery.repository.ProductRepository
import com.example.delivery.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val productService: ProductService,
) {

    @LogInvoke
    fun getById(id: String): Mono<DomainOrderWithProduct> {
        return orderRepository.findById(id)
            .map { it.toDomain() }
            .switchIfEmpty(Mono.error(NotFoundException("Order with id $id doesn't exists")))
    }

    @SuppressWarnings("ThrowsCount")
    fun add(createOrderDTO: CreateOrderDTO): Mono<DomainOrder> {
        return userRepository.findById(createOrderDTO.userId)
            .switchIfEmpty(Mono.error(NotFoundException("User with id ${createOrderDTO.userId} doesn't exist")))
            .flatMap { user ->
                val requestedProductsIds = createOrderDTO.items.map { it.productId }.toMutableList()

                Flux.fromIterable(requestedProductsIds)
                    .concatMap { productId ->
                        productService.getById(productId)
                    }.concatMap { product ->
                        val orderItem = createOrderDTO.items.first { it.productId == product.id }
                        if (orderItem.amount > product.amountAvailable) {
                            return@concatMap Flux.error(
                                ProductAmountException(
                                    "Insufficient stock for product ${product.name}. " +
                                        "Available: ${product.amountAvailable}, " +
                                        "Requested: ${orderItem.amount}"
                                )
                            )
                        }
                        return@concatMap Flux.just(orderItem to product.price)
                    }.concatMap { (orderItem, price) ->
                        Flux.just(
                            MongoOrder.MongoOrderItem(
                                ObjectId(orderItem.productId),
                                price,
                                orderItem.amount
                            )
                        )
                    }
                    .collectList()
                    .flatMap { items ->
                        productRepository.updateProductsAmount(items)
                        val order = MongoOrder(
                            items = items,
                            shipmentDetails = createOrderDTO.shipmentDetails.toMongoModel(),
                            status = MongoOrder.Status.NEW,
                            userId = user.id
                        )
                        orderRepository.save(order).map { it.toDomain() }
                    }
            }
    }

    fun deleteById(id: String) {
        orderRepository.deleteById(id)
    }

    fun updateOrder(id: String, updateOrderDTO: UpdateOrderDTO): Mono<DomainOrder> {
        return orderRepository.updateOrder(id, updateOrderDTO.toUpdate())
            .map { it.toDomain() }
            .switchIfEmpty(Mono.error(NotFoundException("Order with id $id doesn't exists")))
    }

    fun updateOrderStatus(id: String, status: String): Mono<DomainOrder> {
        return orderRepository.updateOrderStatus(id, MongoOrder.Status.valueOf(status))
            .map { it.toDomain() }
            .switchIfEmpty(Mono.error(NotFoundException("Order with id $id doesn't exists")))
    }

    fun getAllByUserId(id: String): Flux<DomainOrder> {
        return orderRepository.findAllByUserId(id).map { it.toDomain() }
    }
}
