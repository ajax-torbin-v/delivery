package com.example.domainservice.product.infrastructure.nats

import com.example.core.ProductFixture.randomProductId
import com.example.core.exception.ProductNotFoundException
import com.example.domainservice.ProductFixture.buildDeleteProductRequest
import com.example.domainservice.ProductFixture.buildFindProductByIdRequest
import com.example.domainservice.ProductFixture.buildUpdateProductRequest
import com.example.domainservice.ProductFixture.createProductRequest
import com.example.domainservice.ProductFixture.domainProduct
import com.example.domainservice.ProductFixture.productNotFoundException
import com.example.domainservice.ProductFixture.unsavedDomainProduct
import com.example.domainservice.ProductFixture.updatedDomainProduct
import com.example.domainservice.product.AbstractIntegrationTest
import com.example.domainservice.product.application.port.output.ProductRepositoryOutputPort
import com.example.domainservice.product.infrastructure.nats.mapper.ProductProtoMapper.toCreateProductResponse
import com.example.domainservice.product.infrastructure.nats.mapper.ProductProtoMapper.toDeleteProductResponse
import com.example.domainservice.product.infrastructure.nats.mapper.ProductProtoMapper.toFailureFindProductByIdResponse
import com.example.domainservice.product.infrastructure.nats.mapper.ProductProtoMapper.toFailureUpdateProductResponse
import com.example.domainservice.product.infrastructure.nats.mapper.ProductProtoMapper.toFindProductByIdResponse
import com.example.domainservice.product.infrastructure.nats.mapper.ProductProtoMapper.toUpdateProductResponse
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.product.CreateProductResponse
import com.example.internal.input.reqreply.product.DeleteProductResponse
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import com.example.internal.input.reqreply.product.UpdateProductResponse
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class ProductNatsControllerTest : AbstractIntegrationTest() {

    @Autowired
    @Qualifier("redisProductRepository")
    private lateinit var productRepositoryOutputPort: ProductRepositoryOutputPort

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
        val product = productRepositoryOutputPort.save(unsavedDomainProduct).block()!!

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
        val nonExistingId = ObjectId.get().toString()
        val actual = natsMessagePublisher.request(
            NatsSubject.Product.FIND_BY_ID,
            buildFindProductByIdRequest(nonExistingId),
            FindProductByIdResponse.parser()
        )

        // THEN
        actual.test()
            .expectNext(
                ProductNotFoundException("Product with id $nonExistingId doesn't exist")
                    .toFailureFindProductByIdResponse()
            )
            .verifyComplete()
    }

    @Test
    fun `update should return updated product`() {
        // GIVEN
        val product = productRepositoryOutputPort.save(unsavedDomainProduct).block()!!

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
        val product = productRepositoryOutputPort.save(unsavedDomainProduct).block()!!

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
