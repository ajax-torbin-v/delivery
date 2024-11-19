package com.example.domainservice.order.application.port.input

import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.domain.projection.DomainOrderWithProduct
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderInputPort {
    fun save(order: DomainOrder): Mono<DomainOrder>
    fun getById(id: String): Mono<DomainOrder>
    fun getByIdFull(id: String): Mono<DomainOrderWithProduct>
    fun updateOrder(domainOrder: DomainOrder): Mono<DomainOrder>
    fun updateOrderStatus(id: String, status: DomainOrder.Status): Mono<DomainOrder>
    fun getAllByUserId(id: String): Flux<DomainOrder>
    fun delete(id: String): Mono<Unit>
}
