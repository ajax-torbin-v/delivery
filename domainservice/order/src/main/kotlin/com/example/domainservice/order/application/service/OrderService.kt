package com.example.domainservice.order.application.service

import com.example.core.exception.OrderNotFoundException
import com.example.core.exception.ProductAmountException
import com.example.core.exception.ProductNotFoundException
import com.example.core.exception.UserNotFoundException
import com.example.domainservice.order.application.mapper.OrderMapper.applyPartialUpdate
import com.example.domainservice.order.application.port.input.OrderInputPort
import com.example.domainservice.order.application.port.output.OrderRepositoryOutputPort
import com.example.domainservice.order.application.port.output.OrderUpdateStatusProducerOutputPort
import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.domain.projection.DomainOrderWithProduct
import com.example.domainservice.product.application.port.output.ProductRepositoryOutputPort
import com.example.domainservice.product.domain.DomainProduct
import com.example.domainservice.user.application.port.output.UserRepositoryOutputPort
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toFlux

@Service
class OrderService(
    private val orderRepositoryOutputPort: OrderRepositoryOutputPort,
    @Qualifier("redisProductRepository")
    private val productRepositoryOutputPort: ProductRepositoryOutputPort,
    private val userRepositoryOutputPort: UserRepositoryOutputPort,
    private val orderUpdateStatusProducerOutputPort: OrderUpdateStatusProducerOutputPort,
) : OrderInputPort {
    override fun save(order: DomainOrder): Mono<DomainOrder> {
        return userRepositoryOutputPort.findById(order.userId)
            .switchIfEmpty { Mono.error(UserNotFoundException("User with id ${order.userId} doesn't exist")) }
            .flatMap {
                verifyProducts(order)
                    .flatMap { items ->
                        saveOrder(items, order)
                    }
            }
    }

    override fun getById(id: String): Mono<DomainOrder> {
        return orderRepositoryOutputPort.findById(id)
            .switchIfEmpty { Mono.error(OrderNotFoundException("Order with id $id doesn't exists")) }
    }

    override fun getByIdFull(id: String): Mono<DomainOrderWithProduct> {
        return orderRepositoryOutputPort.findByIdFull(id)
            .switchIfEmpty { Mono.error(OrderNotFoundException("Order with id $id doesn't exists")) }
    }

    override fun updateOrder(domainOrder: DomainOrder): Mono<DomainOrder> {
        return orderRepositoryOutputPort.findById(domainOrder.id!!)
            .switchIfEmpty { Mono.error(OrderNotFoundException("Order with id ${domainOrder.id} doesn't exists")) }
            .flatMap {
                val updatedOrder = it.applyPartialUpdate(domainOrder)
                orderRepositoryOutputPort.updateOrder(updatedOrder)
            }
    }

    override fun updateOrderStatus(id: String, status: DomainOrder.Status): Mono<DomainOrder> {
        return orderRepositoryOutputPort.updateOrderStatus(id, status)
            .flatMap {
                orderUpdateStatusProducerOutputPort.sendOrderUpdateStatus(it)
                    .doOnError { error ->
                        log.error("Couldn't send message to Kafka for order ID: $id with status $status", error)
                    }
                    .thenReturn(it)
            }
    }

    override fun getAllByUserId(id: String): Flux<DomainOrder> {
        return orderRepositoryOutputPort.findAllByUserId(id)
    }

    override fun delete(id: String): Mono<Unit> {
        return orderRepositoryOutputPort.deleteById(id)
    }

    private fun verifyProducts(
        requested: DomainOrder,
    ): Mono<MutableList<DomainOrder.DomainOrderItem>> {
        val requestedProductsIds = requested.items.map { it.productId }
        return productRepositoryOutputPort.findAllByIds(requestedProductsIds)
            .collectList()
            .flatMapMany { productsList ->
                if (productsList.size == requested.items.size) {
                    productsList.toFlux()
                } else {
                    checkProductAvailability(productsList, requestedProductsIds)
                }
            }
            .map { product -> checkProductAmount(requested, product) }
            .collectList()
    }

    private fun checkProductAvailability(products: List<DomainProduct>, itemsIds: List<String>): Flux<DomainProduct> {
        val productIds = products.map { it.id.toString() }
        val missingProductId = itemsIds.first { it !in productIds }
        return Flux.error(ProductNotFoundException("Product with id $missingProductId doesn't exist"))
    }

    private fun checkProductAmount(
        order: DomainOrder,
        product: DomainProduct,
    ): DomainOrder.DomainOrderItem {
        val orderItem = order.items.first { it.productId == product.id }
        if (orderItem.amount > product.amountAvailable) {
            throw ProductAmountException(
                "Insufficient stock for product ${product.name}. " +
                    "Available: ${product.amountAvailable}. " +
                    "Requested: ${orderItem.amount}"
            )
        }
        return DomainOrder.DomainOrderItem(orderItem.productId, product.price, orderItem.amount)
    }

    private fun saveOrder(
        items: List<DomainOrder.DomainOrderItem>,
        domainOrder: DomainOrder,
    ): Mono<DomainOrder> {
        return productRepositoryOutputPort.updateProductsAmount(items.associate { it.productId to it.amount })
            .flatMap {
                val order = DomainOrder(
                    id = null,
                    items = items,
                    shipmentDetails = domainOrder.shipmentDetails,
                    status = DomainOrder.Status.NEW,
                    userId = domainOrder.userId
                )
                orderRepositoryOutputPort.save(order)
            }
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderService::class.java)
    }
}
