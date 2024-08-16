package com.example.delivery.controller

import com.example.delivery.dto.request.CreateOrderDTO
import com.example.delivery.dto.request.UpdateOrderDTO
import com.example.delivery.dto.response.OrderDTO
import com.example.delivery.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderService: OrderService) {

    @GetMapping("/find/{id}")
    fun findById(@PathVariable id: String): OrderDTO {
        return orderService.findById(id)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add")
    fun add(@RequestBody createOrderDTO: CreateOrderDTO): OrderDTO {
        return orderService.add(createOrderDTO)
    }

    @PutMapping("/update")
    fun update(@RequestBody updateOrderDTO: UpdateOrderDTO) {
        orderService.updateStatus(updateOrderDTO)
    }

    @DeleteMapping("/deleteById/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) {
        return orderService.deleteById(id)
    }
}