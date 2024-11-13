package com.example.delivery.controller

import com.example.core.ProductFixture.randomProductId
import com.example.delivery.AbstractIntegrationTest
import com.example.delivery.ProductFixture.buildDeleteProductRequest
import com.example.delivery.ProductFixture.buildFindProductByIdRequest
import com.example.delivery.ProductFixture.buildUpdateProductRequest
import com.example.delivery.ProductFixture.createProductRequest
import com.example.delivery.ProductFixture.domainProduct
import com.example.delivery.ProductFixture.productNotFoundException
import com.example.delivery.ProductFixture.unsavedProduct
import com.example.delivery.ProductFixture.updatedDomainProduct
import com.example.delivery.mapper.ProductProtoMapper.toCreateProductResponse
import com.example.delivery.mapper.ProductProtoMapper.toDeleteProductResponse
import com.example.delivery.mapper.ProductProtoMapper.toFailureFindProductByIdResponse
import com.example.delivery.mapper.ProductProtoMapper.toFailureUpdateProductResponse
import com.example.delivery.mapper.ProductProtoMapper.toFindProductByIdResponse
import com.example.delivery.mapper.ProductProtoMapper.toUpdateProductResponse
import com.example.delivery.repository.ProductRepository
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.product.CreateProductResponse
import com.example.internal.input.reqreply.product.DeleteProductResponse
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import com.example.internal.input.reqreply.product.UpdateProductResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class ProductNatsControllerTest : AbstractIntegrationTest() {

    @Autowired
    @Qualifier("redisProductRepository")
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var natsMessagePublisher: NatsMessagePublisher

    @Test
    fun `save should return saved product`() {
        // GIVEN // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Product.SAVE,
            createProductRequest,
            CreateProductResponse.parser()
        ).block()!!

        // THEN
        assertEquals(
            domainProduct.toCreateProductResponse().success.product.toBuilder().clearId().build(),
            actual.success.product.toBuilder().clearId().build()
        )
    }

    @Test
    fun `findById should return existing product`() {
        // GIVEN
        val product = productRepository.save(unsavedProduct).block()!!

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Product.FIND_BY_ID,
            buildFindProductByIdRequest(product.id.toString()),
            FindProductByIdResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(domainProduct.copy(id = product.id.toString()).toFindProductByIdResponse())
            .verifyComplete()
    }

    @Test
    fun `findById should return message with exception when product not found`() {
        // GIVEN // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Product.FIND_BY_ID,
            buildFindProductByIdRequest(randomProductId),
            FindProductByIdResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(productNotFoundException.toFailureFindProductByIdResponse())
            .verifyComplete()
    }

    @Test
    fun `update should return updated product`() {
        // GIVEN
        val product = productRepository.save(unsavedProduct).block()!!

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Product.UPDATE,
            buildUpdateProductRequest(product.id.toString()),
            UpdateProductResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(updatedDomainProduct.copy(id = product.id.toString()).toUpdateProductResponse())
            .verifyComplete()
    }

    @Test
    fun `update should return message with exception when product doesn't exist`() {
        // GIVEN // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Product.UPDATE,
            buildUpdateProductRequest(randomProductId),
            UpdateProductResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(productNotFoundException.toFailureUpdateProductResponse())
            .verifyComplete()
    }

    @Test
    fun `delete should return message with success`() {
        // GIVEN
        val product = productRepository.save(unsavedProduct).block()!!

        // WHEN
        val actual = natsMessagePublisher.request(
            NatsSubject.Product.DELETE,
            buildDeleteProductRequest(product.id.toString()),
            DeleteProductResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(toDeleteProductResponse())
            .verifyComplete()
    }
}
