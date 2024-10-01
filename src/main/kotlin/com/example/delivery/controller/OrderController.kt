package com.example.delivery.controller

import com.example.delivery.annotaion.LogInvoke
import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.dto.response.OrderDTO
import com.example.delivery.dto.response.OrderWithProductDTO
import com.example.delivery.mapper.OrderMapper.toDTO
import com.example.delivery.mapper.OrderWithProductMapper.toDTO
import com.example.delivery.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {

    @LogInvoke
    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<OrderWithProductDTO> {
        return orderService.getById(id).map { it.toDTO() }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createOrderDTO: CreateOrderDTO): Mono<OrderDTO> {
        return orderService.add(createOrderDTO).map { it.toDTO() }
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody updateOrderDTO: UpdateOrderDTO,
    ): Mono<OrderDTO> {
        return orderService.updateOrder(id, updateOrderDTO).map { it.toDTO() }
    }

    @PatchMapping("/{id}")
    fun updateStatus(
        @PathVariable id: String,
        @RequestParam status: String,
    ): Mono<OrderDTO> {
        return orderService.updateOrderStatus(id, status).map { it.toDTO() }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): Mono<Void> {
        return orderService.deleteById(id)
    }

    @GetMapping("/user/{id}")
    fun findAllByUserId(@PathVariable id: String): Flux<OrderDTO> {
        return orderService.getAllByUserId(id).map { it.toDTO() }
    }
}
