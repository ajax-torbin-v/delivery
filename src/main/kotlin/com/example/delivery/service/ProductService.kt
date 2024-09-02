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

@Service
class ProductService(private val productRepository: ProductRepository) {
    fun getById(id: String): DomainProduct {
        return productRepository.findById(id)?.toDomain()
            ?: throw NotFoundException("Product with id $id not found")
    }

    fun findAll(): List<DomainProduct> {
        return productRepository.findAll().map { it.toDomain() }
    }

    fun add(createProductDTO: CreateProductDTO): DomainProduct {
        return productRepository.save(createProductDTO.toMongo()).toDomain()
    }

    fun deleteById(id: String) {
        return productRepository.deleteById(id)
    }

    fun update(id: String, updateProductDTO: UpdateProductDTO): DomainProduct {
        return productRepository.update(id, updateProductDTO.toUpdate())?.toDomain()
            ?: throw NotFoundException("Product with id $id doesn't exists")
    }
}
