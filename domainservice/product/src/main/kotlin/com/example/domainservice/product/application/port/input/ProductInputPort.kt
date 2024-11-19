package com.example.domainservice.product.application.port.input

import com.example.domainservice.product.domain.DomainProduct
import reactor.core.publisher.Mono

interface ProductInputPort {
    fun save(product: DomainProduct): Mono<DomainProduct>
    fun getById(id: String): Mono<DomainProduct>
    fun update(partialUpdate: DomainProduct): Mono<DomainProduct>
    fun deleteById(id: String): Mono<Unit>
}
