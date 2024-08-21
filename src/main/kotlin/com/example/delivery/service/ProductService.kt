package com.example.delivery.service

import com.example.delivery.domain.DomainProduct
import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.exception.NotFoundException
import com.example.delivery.mapper.ProductMapper.toDomain
import com.example.delivery.mapper.ProductMapper.toMongo
import com.example.delivery.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun findById(id: String): DomainProduct {
        val product = productRepository.findById(id) ?: throw NotFoundException("Product with id $id not found")
        return product.toDomain()
    }

    fun findAll(): List<DomainProduct> = productRepository.findAll().map { it.toDomain() }

    fun add(createProductDTO: CreateProductDTO): DomainProduct =
        productRepository.save(createProductDTO.toMongo()).toDomain()

    fun deleteById(id: String) =
        if (productRepository.existsById(id)) productRepository.deleteById(id)
        else throw NotFoundException("Product with id $id not found")
}
