package com.example.delivery.controller

import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.response.ProductDTO
import com.example.delivery.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<ProductDTO> {
        return ResponseEntity.ok(productService.findById(id))
    }

    @GetMapping("/all")
    fun getAll(): ResponseEntity<List<ProductDTO>> {
        return ResponseEntity.ok(productService.findAll())
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createProductDTO: CreateProductDTO): ResponseEntity<ProductDTO> {
        return ResponseEntity(productService.add(createProductDTO), HttpStatus.CREATED)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        productService.deleteById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
