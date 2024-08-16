package com.example.delivery.controller

import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.response.ProductDTO
import com.example.delivery.service.ProductService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    @GetMapping("/findById/{id}")
    fun findById(@PathVariable id: String): ProductDTO {
        return productService.findById(id)
    }

    @GetMapping("/find/all")
    fun getAll(): List<ProductDTO> {
        return productService.findAll()
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add")
    fun add(@RequestBody createProductDTO: CreateProductDTO): ProductDTO {
        return productService.add(createProductDTO)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/deleteById/{id}")
    fun delete(@PathVariable id: String) {
        productService.deleteById(id)
    }
}