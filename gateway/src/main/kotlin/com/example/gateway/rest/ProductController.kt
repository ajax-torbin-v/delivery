package com.example.gateway.rest

import com.example.core.dto.request.CreateProductDTO
import com.example.core.dto.request.UpdateProductDTO
import com.example.core.dto.response.ProductDTO
import com.example.gateway.mapper.ProductProtoMapper.toCreateProductRequest
import com.example.gateway.mapper.ProductProtoMapper.toDTO
import com.example.gateway.mapper.ProductProtoMapper.updateProductRequest
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.product.CreateProductResponse
import com.example.internal.input.reqreply.product.DeleteProductRequest
import com.example.internal.input.reqreply.product.DeleteProductResponse
import com.example.internal.input.reqreply.product.FindProductByIdRequest
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import com.example.internal.input.reqreply.product.UpdateProductResponse
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
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@RestController
@RequestMapping("/products")
class ProductController(private val natsPublisher: NatsMessagePublisher) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createProductDTO: CreateProductDTO): Mono<ProductDTO> {
        return natsPublisher.request(
            NatsSubject.Product.SAVE,
            createProductDTO.toCreateProductRequest(),
            CreateProductResponse.parser()
        ).map { it.toDTO() }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<ProductDTO> {
        return natsPublisher.request(
            NatsSubject.Product.FIND_BY_ID,
            FindProductByIdRequest.newBuilder().setId(id).build(),
            FindProductByIdResponse.parser(),
        ).map { it.toDTO() }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody updateProductDTO: UpdateProductDTO): Mono<ProductDTO> {
        return natsPublisher.request(
            NatsSubject.Product.UPDATE,
            updateProductRequest(id, updateProductDTO),
            UpdateProductResponse.parser()
        ).map { it.toDTO() }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): Mono<Unit> {
        return natsPublisher.request(
            NatsSubject.Product.DELETE,
            DeleteProductRequest.newBuilder().setId(id).build(),
            DeleteProductResponse.parser()
        ).map { it.toDTO() }
    }
}
