package com.example.delivery.controller

import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.dto.response.OrderDTO
import com.example.delivery.mapper.OrderMapper.toDTO
import com.example.delivery.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderService: OrderService) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<OrderDTO> {
        return ResponseEntity.ok(orderService.findById(id).toDTO())
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createOrderDTO: CreateOrderDTO): ResponseEntity<OrderDTO> {
        return ResponseEntity(orderService.add(createOrderDTO).toDTO(), HttpStatus.CREATED)
    }

    @PutMapping
    fun update(@RequestBody updateOrderDTO: UpdateOrderDTO): ResponseEntity<OrderDTO> {
        return ResponseEntity.ok(orderService.updateStatus(updateOrderDTO).toDTO())
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        orderService.deleteById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
