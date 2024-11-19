package com.example.domainservice.product.application.port.output

import com.example.domainservice.product.domain.DomainProduct
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ProductRepositoryOutputPort {
    fun findById(id: String): Mono<DomainProduct>
    fun save(product: DomainProduct): Mono<DomainProduct>
    fun deleteById(id: String): Mono<Unit>
    fun update(product: DomainProduct): Mono<DomainProduct>
    fun findAllByIds(productIds: List<String>): Flux<DomainProduct>
    fun updateProductsAmount(products: Map<String, Int>): Mono<Unit>
}
