package com.example.delivery.repository

import com.example.core.ProductFixture.randomProductId
import com.example.delivery.ProductFixture.product
import com.example.delivery.ProductFixture.unsavedProduct
import com.example.delivery.repository.impl.MongoProductRepository
import com.example.delivery.repository.impl.RedisProductRepository
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

    @Test
    fun `fallbackSave should return saved mongo product when redis fails`() {
        // GIVEN
        val exception = RedisConnectionException("Redis error")
        every { reactiveRedisTemplate.opsForValue().get(randomProductId) } throws exception
        every { mongoProductRepository.save(product) } returns product.toMono()

        // WHEN // THEN
        redisProductRepository.fallbackSave(product, exception)
            .test()
            .expectNext(product)
            .verifyComplete()
    }

    @Test
    fun `fallbackSave should rethrow unexpected error`() {
        // GIVEN
        val exception = NullPointerException()
        every { reactiveRedisTemplate.opsForValue().get(randomProductId) } throws exception
        every { mongoProductRepository.save(unsavedProduct) } returns product.toMono()

        // WHEN // THEN
        redisProductRepository.fallbackSave(unsavedProduct, exception)
            .test()
            .verifyError(NullPointerException::class.java)
    }

    @Test
    fun `fallbackFindById should return product from mongo when redis fails`() {
        // GIVEN
        val exception = RedisConnectionException("Redis error")
        every { reactiveRedisTemplate.opsForValue().get(randomProductId) } throws exception
        every { mongoProductRepository.findById(randomProductId) } returns product.toMono()

        // WHEN // THEN
        redisProductRepository.fallbackFindById(randomProductId, exception)
            .test()
            .expectNext(product)
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
        val mongoProducts = listOf(product, product.copy(id = ObjectId(randomProductId.reversed())))
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
