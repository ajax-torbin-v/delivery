package com.example.delivery.controller

import com.example.delivery.annotaion.LogInvoke
import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.request.UpdateProductDTO
import com.example.delivery.dto.response.ProductDTO
import com.example.delivery.mapper.ProductMapper.toDTO
import com.example.delivery.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {
    @LogInvoke
    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<ProductDTO> {
        return productService.getById(id).map { it.toDTO() }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createProductDTO: CreateProductDTO): Mono<ProductDTO> {
        return productService.add(createProductDTO).map { it.toDTO() }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody updateProductDTO: UpdateProductDTO): Mono<ProductDTO> {
        return productService.update(id, updateProductDTO).map { it.toDTO() }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): Mono<Unit> {
        return productService.deleteById(id)
    }
}
