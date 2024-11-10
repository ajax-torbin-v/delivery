package com.example.delivery.service

import com.example.core.dto.request.CreateProductDTO
import com.example.core.dto.request.UpdateProductDTO
import com.example.core.exception.ProductNotFoundException
import com.example.delivery.domain.DomainProduct
import com.example.delivery.mapper.ProductMapper.toDomain
import com.example.delivery.mapper.ProductMapper.toMongo
import com.example.delivery.mapper.ProductMapper.toPartialUpdate
import com.example.delivery.repository.ProductRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class ProductService(
    @Qualifier("redisProductRepository")
    private val productRepository: ProductRepository,
) {
    fun getById(id: String): Mono<DomainProduct> {
        return productRepository.findById(id)
            .map { it.toDomain() }
            .switchIfEmpty { Mono.error(ProductNotFoundException("Product with id $id doesn't exist")) }
    }

    fun add(createProductDTO: CreateProductDTO): Mono<DomainProduct> {
        return productRepository.save(
            createProductDTO.toMongo()
        ).map { it.toDomain() }
    }

    fun deleteById(id: String): Mono<Unit> {
        return productRepository.deleteById(id)
    }

    fun update(id: String, updateProductDTO: UpdateProductDTO): Mono<DomainProduct> {
        return productRepository.findById(id)
            .map { it.toPartialUpdate(updateProductDTO).toDomain() }
            .switchIfEmpty { Mono.error(ProductNotFoundException("Product with id $id doesn't exist")) }
    }
}
