package com.example.domainservice.product.application.service

import com.example.core.exception.ProductNotFoundException
import com.example.domainservice.product.application.mapper.ProductMapper.applyPartialUpdate
import com.example.domainservice.product.application.port.input.ProductInputPort
import com.example.domainservice.product.application.port.output.ProductRepositoryOutputPort
import com.example.domainservice.product.domain.DomainProduct
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class ProductService(
    @Qualifier("redisProductRepository")
    private val productRepositoryOutputPort: ProductRepositoryOutputPort,
) : ProductInputPort {

    override fun save(product: DomainProduct): Mono<DomainProduct> {
        return productRepositoryOutputPort.save(product)
    }

    override fun getById(id: String): Mono<DomainProduct> {
        return productRepositoryOutputPort.findById(id)
            .switchIfEmpty { Mono.error(ProductNotFoundException("Product with id $id doesn't exist")) }
    }

    override fun update(partialUpdate: DomainProduct): Mono<DomainProduct> {
        return productRepositoryOutputPort.findById(partialUpdate.id!!)
            .flatMap {
                val updated = it.applyPartialUpdate(partialUpdate)
                productRepositoryOutputPort.update(updated)
            }
            .switchIfEmpty { Mono.error(ProductNotFoundException("Product with id ${partialUpdate.id} doesn't exist")) }
    }

    override fun deleteById(id: String): Mono<Unit> {
        return productRepositoryOutputPort.deleteById(id)
    }
}
