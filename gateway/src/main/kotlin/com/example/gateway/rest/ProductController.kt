package com.example.gateway.rest

import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.request.UpdateProductDTO
import com.example.delivery.dto.response.ProductDTO
import com.example.gateway.client.NatsClient
import com.example.gateway.mapper.ProductProtoMapper.toCreateProductRequest
import com.example.gateway.mapper.ProductProtoMapper.toDTO
import com.example.gateway.mapper.ProductProtoMapper.updateProductRequest
import com.example.gateway.mapper.UserProtoMapper.toDTO
import com.example.internal.api.subject.ProductsNatsSubject
import com.example.internal.commonmodels.input.reqreply.product.delete.DeleteProductRequest
import com.example.internal.input.reqreply.product.create.CreateProductResponse
import com.example.internal.input.reqreply.product.find.FindProductByIdRequest
import com.example.internal.input.reqreply.product.find.FindProductByIdResponse
import com.example.internal.input.reqreply.product.update.UpdateProductResponse
import com.example.internal.input.reqreply.user.delete.DeleteUserResponse
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
class ProductController(private val natsClient: NatsClient) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createProductDTO: CreateProductDTO): Mono<ProductDTO> {
        return natsClient.doRequest(
            "${ProductsNatsSubject.PRODUCT_PREFIX}.${ProductsNatsSubject.SAVE}",
            createProductDTO.toCreateProductRequest(),
            CreateProductResponse.parser()
        ).map { it.toDTO() }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<ProductDTO> {
        return natsClient.doRequest(
            "${ProductsNatsSubject.PRODUCT_PREFIX}.${ProductsNatsSubject.FIND_BY_ID}",
            FindProductByIdRequest.newBuilder().setId(id).build(),
            FindProductByIdResponse.parser(),
        ).map { it.toDTO() }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody updateProductDTO: UpdateProductDTO): Mono<ProductDTO> {
        return natsClient.doRequest(
            "${ProductsNatsSubject.PRODUCT_PREFIX}.${ProductsNatsSubject.UPDATE}",
            updateProductRequest(id, updateProductDTO),
            UpdateProductResponse.parser()
        ).map { it.toDTO() }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): Mono<Unit> {
        return natsClient.doRequest(
            "${ProductsNatsSubject.PRODUCT_PREFIX}.${ProductsNatsSubject.DELETE}",
            DeleteProductRequest.newBuilder().setId(id).build(),
            DeleteUserResponse.parser()
        ).map { it.toDTO() }
    }
}
