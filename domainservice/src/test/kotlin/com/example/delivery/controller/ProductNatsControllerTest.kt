package com.example.delivery.controller

import com.example.core.ProductFixture.randomProductId
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
import com.example.internal.api.subject.NatsSubject
import com.example.internal.commonmodels.input.reqreply.product.delete.DeleteProductResponse
import com.example.internal.input.reqreply.product.create.CreateProductResponse
import com.example.internal.input.reqreply.product.find.FindProductByIdResponse
import com.example.internal.input.reqreply.product.update.UpdateProductResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

class ProductNatsControllerTest : AbstractNatsControllerTest() {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Test
    fun `save should return saved product`() {
        // GIVEN // WHEN
        val actual = doRequest(
            NatsSubject.Product.SAVE,
            createProductRequest,
            CreateProductResponse.parser()
        )

        // THEN
        assertEquals(domainProduct.copy(id = actual.success.product.id).toCreateProductResponse(), actual)
    }

    @Test
    fun `findById should return existing product`() {
        // GIVEN
        val product = productRepository.save(unsavedProduct).block()!!

        // WHEN
        val actual = doRequest(
            NatsSubject.Product.FIND_BY_ID,
            buildFindProductByIdRequest(product.id.toString()),
            FindProductByIdResponse.parser()
        )

        // THEN
        assertEquals(domainProduct.copy(id = actual.success.product.id).toFindProductByIdResponse(), actual)
    }

    @Test
    fun `findById should return message with exception when product not found`() {
        // GIVEN // WHEN
        val actual = doRequest(
            NatsSubject.Product.FIND_BY_ID,
            buildFindProductByIdRequest(randomProductId),
            FindProductByIdResponse.parser()
        )

        // THEN
        assertEquals(productNotFoundException.toFailureFindProductByIdResponse(), actual)
    }

    @Test
    fun `update should return updated product`() {
        // GIVEN
        val product = productRepository.save(unsavedProduct).block()!!

        // WHEN
        val actual = doRequest(
            NatsSubject.Product.UPDATE,
            buildUpdateProductRequest(product.id.toString()),
            UpdateProductResponse.parser()
        )

        // THEN
        assertEquals(updatedDomainProduct.copy(id = product.id.toString()).toUpdateProductResponse(), actual)
    }

    @Test
    fun `update should return message with exception when product doesn't exist`() {
        // GIVEN // WHEN
        val actual = doRequest(
            NatsSubject.Product.UPDATE,
            buildUpdateProductRequest(randomProductId),
            UpdateProductResponse.parser()
        )

        // THEN
        assertEquals(productNotFoundException.toFailureUpdateProductResponse(), actual)
    }

    @Test
    fun `delete should delete product`() {
        // GIVEN
        val product = productRepository.save(unsavedProduct).block()!!

        // WHEN
        val actual = doRequest(
            NatsSubject.Product.DELETE,
            buildDeleteProductRequest(product.id.toString()),
            DeleteProductResponse.parser()
        )

        // THEN
        assertEquals(toDeleteProductResponse(), actual)
        productRepository.existsById(product.id.toString())
            .test()
            .expectNext(false)
            .verifyComplete()
    }
}
