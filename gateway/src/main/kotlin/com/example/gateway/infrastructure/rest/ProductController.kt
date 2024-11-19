package com.example.gateway.infrastructure.rest

import com.example.core.dto.request.CreateProductDTO
import com.example.core.dto.request.UpdateProductDTO
import com.example.core.dto.response.ProductDTO
import com.example.gateway.application.port.output.ProductOutputPort
import com.example.gateway.infrastructure.mapper.ProductProtoMapper.toCreateProductRequest
import com.example.gateway.infrastructure.mapper.ProductProtoMapper.toDTO
import com.example.gateway.infrastructure.mapper.ProductProtoMapper.updateProductRequest
import com.example.internal.input.reqreply.product.DeleteProductRequest
import com.example.internal.input.reqreply.product.FindProductByIdRequest
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
class ProductController(private val productOutputPort: ProductOutputPort) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createProductDTO: CreateProductDTO): Mono<ProductDTO> {
        return productOutputPort.create(createProductDTO.toCreateProductRequest())
            .map { it.toDTO() }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<ProductDTO> {
        return productOutputPort.findById(FindProductByIdRequest.newBuilder().setId(id).build())
            .map { it.toDTO() }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody updateProductDTO: UpdateProductDTO): Mono<ProductDTO> {
        return productOutputPort.update(updateProductRequest(id, updateProductDTO))
            .map { it.toDTO() }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): Mono<Unit> {
        return productOutputPort.delete(DeleteProductRequest.newBuilder().setId(id).build())
            .map { it.toDTO() }
    }
}
