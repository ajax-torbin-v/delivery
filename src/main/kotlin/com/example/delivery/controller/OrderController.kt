package com.example.delivery.controller

import com.example.delivery.annotaion.InvokeLog
import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.dto.response.OrderDTO
import com.example.delivery.mapper.OrderMapper.toDTO
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

@InvokeLog
@RestController
@RequestMapping("/orders")
class OrderController(private val orderService: OrderService) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): OrderDTO {
        return orderService.getById(id).toDTO()
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createOrderDTO: CreateOrderDTO): OrderDTO {
        return orderService.add(createOrderDTO).toDTO()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody updateOrderDTO: UpdateOrderDTO,
    ): OrderDTO {
        return orderService.updateOrder(id, updateOrderDTO).toDTO()
    }

    @PatchMapping("/{id}")
    fun updateStatus(
        @PathVariable id: String,
        @RequestParam status: String,
    ): OrderDTO {
        return orderService.updateOrderStatus(id, status).toDTO()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) {
        return orderService.deleteById(id)
    }
}
