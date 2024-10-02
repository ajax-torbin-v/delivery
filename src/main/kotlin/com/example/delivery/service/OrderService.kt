package com.example.delivery.service

import com.example.delivery.annotaion.LogInvoke
import com.example.delivery.domain.DomainOrder
import com.example.delivery.domain.DomainProduct
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
import com.example.delivery.mongo.MongoProduct
import com.example.delivery.mongo.MongoUser
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
                val requestedProductsIds = createOrderDTO.items.map { it.productId }
                productRepository.findAllByIds(requestedProductsIds)
                    .collectList()
                    .flatMapMany { productsList ->
                        if (productsList.size == createOrderDTO.items.size) {
                            Flux.fromIterable(productsList.map { it.toDomain() })
                        } else {
                            checkProductAvailability(productsList, requestedProductsIds)
                        }
                    }
                    .flatMap { product -> checkProductAmount(createOrderDTO, product) }
                    .collectList()
                    .flatMap { items ->
                        saveOrder(items, createOrderDTO, user)
                    }
            }
    }

    fun deleteById(id: String): Mono<Void> {
        return orderRepository.deleteById(id)
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

    private fun checkProductAvailability(products: List<MongoProduct>, itemsIds: List<String>): Flux<DomainProduct> {
        val productIds = products.map { it.id.toString() }
        val missingProductId = itemsIds.first { it !in productIds }
        return Flux.error(NotFoundException("Product with id $missingProductId doesn't exist"))
    }

    private fun checkProductAmount(
        createOrderDTO: CreateOrderDTO,
        product: DomainProduct,
    ): Mono<MongoOrder.MongoOrderItem> {
        val orderItem = createOrderDTO.items.first { it.productId == product.id }
        if (orderItem.amount > product.amountAvailable) {
            return Mono.error(
                ProductAmountException(
                    "Insufficient stock for product ${product.name}. " +
                        "Available: ${product.amountAvailable}. " +
                        "Requested: ${orderItem.amount}"
                )
            )
        }
        return Mono.just(
            MongoOrder.MongoOrderItem(
                ObjectId(orderItem.productId),
                product.price,
                orderItem.amount
            )
        )
    }

    private fun saveOrder(
        items: MutableList<MongoOrder.MongoOrderItem>,
        createOrderDTO: CreateOrderDTO,
        user: MongoUser,
    ): Mono<DomainOrder> {
        productRepository.updateProductsAmount(items)
        val order = MongoOrder(
            items = items,
            shipmentDetails = createOrderDTO.shipmentDetails.toMongoModel(),
            status = MongoOrder.Status.NEW,
            userId = user.id
        )
        return orderRepository.save(order).map { it.toDomain() }
    }
}
