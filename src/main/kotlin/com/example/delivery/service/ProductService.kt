package com.example.delivery.service

import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.response.ProductDTO
import com.example.delivery.exception.NotFoundException
import com.example.delivery.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun findById(id: String) : ProductDTO {
        val product = productRepository.findById(id) ?: throw NotFoundException("Product with id $id not found")
        return product.toDTO()
    }

    fun findAll() : List<ProductDTO> {
        return productRepository.findAll().map {it.toDTO()}
    }

    fun add(createProductDTO: CreateProductDTO): ProductDTO {
        return productRepository.save(createProductDTO.toModel()).toDTO()
    }

    fun deleteById(id: String) =
        if (productRepository.existsById(id)) productRepository.deleteById(id)
        else throw NotFoundException("Product with id $id not found")
}