package com.example.domainservice.product.infrastructure.repository

import com.example.core.ProductFixture.randomProductId
import com.example.domainservice.ProductFixture.domainProduct
import com.example.domainservice.ProductFixture.product
import com.example.domainservice.ProductFixture.unsavedDomainProduct
import com.example.domainservice.product.infrastructure.mongo.MongoProductRepository
import com.example.domainservice.product.infrastructure.mongo.mapper.ProductMapper.toDomain
import com.example.domainservice.product.infrastructure.redis.RedisProductRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.RedisConnectionException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
class ProductRepositoryFallbackUnitTest {
    @InjectMockKs
    private lateinit var redisProductRepository: RedisProductRepository

    @MockK
    private lateinit var mongoProductRepository: MongoProductRepository

    @MockK
    private lateinit var reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>

    @MockK
    @SuppressWarnings("UnusedPrivateProperty")
    private lateinit var mapper: ObjectMapper

    @Test
    fun `fallbackSave should return saved mongo product when redis fails`() {
        // GIVEN
        val exception = RedisConnectionException("Redis error")
        every { reactiveRedisTemplate.opsForValue().get(randomProductId) } throws exception
        every { mongoProductRepository.save(unsavedDomainProduct) } returns domainProduct.toMono()

        // WHEN // THEN
        redisProductRepository.fallbackSave(domainProduct, exception)
            .test()
            .expectNext(domainProduct)
            .verifyComplete()
    }

    @Test
    fun `fallbackSave should rethrow unexpected error`() {
        // GIVEN
        val exception = NullPointerException()
        every { reactiveRedisTemplate.opsForValue().get(randomProductId) } throws exception
        every { mongoProductRepository.save(unsavedDomainProduct) } returns domainProduct.toMono()

        // WHEN // THEN
        redisProductRepository.fallbackSave(unsavedDomainProduct, exception)
            .test()
            .verifyError(NullPointerException::class.java)
    }

    @Test
    fun `fallbackFindById should return product from mongo when redis fails`() {
        // GIVEN
        val exception = RedisConnectionException("Redis error")
        every { reactiveRedisTemplate.opsForValue().get(randomProductId) } throws exception
        every { mongoProductRepository.findById(randomProductId) } returns domainProduct.toMono()

        // WHEN // THEN
        redisProductRepository.fallbackFindById(randomProductId, exception)
            .test()
            .expectNext(domainProduct)
            .verifyComplete()

        verify(exactly = 1) { mongoProductRepository.findById(randomProductId) }
    }

    @Test
    fun `fallbackFindById should rethrow unexpected exception`() {
        // GIVEN
        val exception = NullPointerException()
        every { reactiveRedisTemplate.opsForValue().get(randomProductId) } throws exception

        // WHEN // THEN
        redisProductRepository.fallbackFindById(randomProductId, exception)
            .test()
            .verifyError(NullPointerException::class.java)
    }

    @Test
    fun `fallbackFindAllByIds should return products from mongo when redis fails`() {
        // GIVEN
        val productIds = listOf(randomProductId, randomProductId.reversed())
        val mongoProducts =
            listOf(
                product,
                product.copy(id = ObjectId(randomProductId.reversed()))
            ).map { it.toDomain() }
        val exception = RedisConnectionException("Redis error")

        every { reactiveRedisTemplate.opsForValue().multiGet(any()) } throws exception
        every { mongoProductRepository.findAllByIds(productIds) } returns Flux.fromIterable(mongoProducts)

        // WHEN // THEN
        redisProductRepository.fallbackFindAllByIds(productIds, exception)
            .test()
            .expectNext(*mongoProducts.toTypedArray())
            .verifyComplete()

        verify(exactly = 1) { mongoProductRepository.findAllByIds(productIds) }
    }

    @Test
    fun `fallbackFindAllByIds should rethrow unexpected exception`() {
        // GIVEN
        val productIds = listOf(randomProductId, randomProductId.reversed())
        val exception = NullPointerException()

        every { reactiveRedisTemplate.opsForValue().multiGet(any()) } throws exception

        // WHEN // THEN
        redisProductRepository.fallbackFindAllByIds(productIds, exception)
            .test()
            .verifyError(NullPointerException::class.java)
    }

    @Test
    fun `fallbackDeleteById should call mongo when redis fails`() {
        // GIVEN
        val exception = RedisConnectionException("Redis error")
        every { reactiveRedisTemplate.opsForValue().delete(any()) } throws exception
        every { mongoProductRepository.deleteById(randomProductId) } returns Mono.just(Unit)

        // WHEN //THEN
        redisProductRepository.fallbackDeleteById(randomProductId, exception)
            .test()
            .expectNext(Unit)
            .verifyComplete()

        verify(exactly = 1) { mongoProductRepository.deleteById(randomProductId) }
    }

    @Test
    fun `fallbackDeleteById should rethrow unexpected exception`() {
        // GIVEN
        val exception = NullPointerException()
        every { reactiveRedisTemplate.opsForValue().delete(any()) } throws exception

        // WHEN //THEN
        redisProductRepository.fallbackDeleteById(randomProductId, exception)
            .test()
            .verifyError(NullPointerException::class.java)
    }
}
