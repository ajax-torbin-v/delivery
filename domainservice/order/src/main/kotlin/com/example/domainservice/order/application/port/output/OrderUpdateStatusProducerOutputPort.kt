package com.example.domainservice.order.application.port.output

import com.example.domainservice.order.domain.DomainOrder
import reactor.core.publisher.Mono

interface OrderUpdateStatusProducerOutputPort {
    fun sendOrderUpdateStatus(msg: DomainOrder): Mono<Unit>
}
