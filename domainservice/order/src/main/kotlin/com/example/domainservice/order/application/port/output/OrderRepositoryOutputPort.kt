package com.example.domainservice.order.application.port.output

import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.domain.projection.DomainOrderWithProduct
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderRepositoryOutputPort {
    fun existsById(id: String): Mono<Boolean>
    fun findById(id: String): Mono<DomainOrder>
    fun findByIdFull(id: String): Mono<DomainOrderWithProduct>
    fun save(order: DomainOrder): Mono<DomainOrder>
    fun updateOrderStatus(id: String, status: DomainOrder.Status): Mono<DomainOrder>
    fun deleteById(id: String): Mono<Unit>
    fun updateOrder(order: DomainOrder): Mono<DomainOrder>
    fun findAllByUserId(userId: String): Flux<DomainOrder>
}
