package com.example.delivery.controller

import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.request.UpdateProductDTO
import com.example.delivery.dto.response.ProductDTO
import com.example.delivery.mapper.ProductMapper.toDTO
import com.example.delivery.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ProductDTO {
        return productService.getById(id).toDTO()
    }

    @GetMapping
    fun getAll(): List<ProductDTO> {
        return productService.findAll().map { it.toDTO() }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createProductDTO: CreateProductDTO): ProductDTO {
        return productService.add(createProductDTO).toDTO()
    }

    @PatchMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody updateProductDTO: UpdateProductDTO): ProductDTO {
        return productService.update(id, updateProductDTO).toDTO()
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String) {
        productService.deleteById(id)
    }
}
