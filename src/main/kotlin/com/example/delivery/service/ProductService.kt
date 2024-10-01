package com.example.delivery.service

import com.example.delivery.domain.DomainProduct
import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.request.UpdateProductDTO
import com.example.delivery.exception.NotFoundException
import com.example.delivery.mapper.ProductMapper.toDomain
import com.example.delivery.mapper.ProductMapper.toMongo
import com.example.delivery.mapper.ProductMapper.toUpdate
import com.example.delivery.repository.ProductRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ProductService(private val productRepository: ProductRepository) {
    fun getById(id: String): Mono<DomainProduct> {
        return productRepository.findById(id)
            .map { it.toDomain() }
            .switchIfEmpty(Mono.error(NotFoundException("Product with id $id not found")))
    }

    fun add(createProductDTO: CreateProductDTO): Mono<DomainProduct> {
        return productRepository.save(createProductDTO.toMongo()).map { it.toDomain() }
    }

    fun deleteById(id: String): Mono<Void> {
        return productRepository.deleteById(id)
    }

    fun update(id: String, updateProductDTO: UpdateProductDTO): Mono<DomainProduct> {
        return productRepository.update(id, updateProductDTO.toUpdate())
            .map { it.toDomain() }
            .switchIfEmpty(Mono.error(NotFoundException("Product with id $id not found")))
    }
}
