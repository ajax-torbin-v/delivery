package com.example.delivery.service

import com.example.delivery.domain.DomainProduct
import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.request.UpdateProductDTO
import com.example.delivery.exception.NotFoundException
import com.example.delivery.mapper.ProductMapper.toDomain
import com.example.delivery.mapper.ProductMapper.toMongo
import com.example.delivery.mongo.MongoProduct
import com.example.delivery.repository.ProductRepository
import org.springframework.data.mongodb.core.query.Update
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
        return productRepository.update(id, createUpdateObject(updateProductDTO))?.toDomain()
            ?: throw NotFoundException("Product with id $id doesn't exists")
    }

    private fun createUpdateObject(updateProductDTO: UpdateProductDTO): Update {
        val update = Update()
        with(updateProductDTO) {
            name?.let { update.set(MongoProduct::name.name, this) }
            price?.let { update.set(MongoProduct::price.name, this) }
            amountAvailable?.let { update.set(MongoProduct::amountAvailable.name, this) }
        }
        return update
    }
}
