package com.example.gateway.infrastructure.rest

import com.example.core.dto.request.CreateOrderDTO
import com.example.core.dto.request.UpdateOrderDTO
import com.example.core.dto.response.OrderDTO
import com.example.core.dto.response.OrderWithProductDTO
import com.example.gateway.application.port.output.OrderOutputPort
import com.example.gateway.infrastructure.mapper.OrderProtoMapper.toCreateOrderRequest
import com.example.gateway.infrastructure.mapper.OrderProtoMapper.toDTO
import com.example.gateway.infrastructure.mapper.OrderProtoMapper.toDtoWithProduct
import com.example.gateway.infrastructure.mapper.OrderProtoMapper.updateOrderRequest
import com.example.internal.input.reqreply.order.DeleteOrderRequest
import com.example.internal.input.reqreply.order.FindOrderByIdRequest
import com.example.internal.input.reqreply.order.FindOrdersByUserIdRequest
import com.example.internal.input.reqreply.order.UpdateOrderStatusRequest
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
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orders")
class OrderController(private val orderOutputPort: OrderOutputPort) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createOrderDTO: CreateOrderDTO): Mono<OrderDTO> {
        return orderOutputPort.create(createOrderDTO.toCreateOrderRequest())
            .map { it.toDTO() }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<OrderWithProductDTO> {
        return orderOutputPort.findById(FindOrderByIdRequest.newBuilder().setId(id).build())
            .map { it.toDtoWithProduct() }
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody updateOrderDTO: UpdateOrderDTO,
    ): Mono<OrderDTO> {
        return orderOutputPort.update(updateOrderRequest(id, updateOrderDTO))
            .map { it.toDTO() }
    }

    @PatchMapping("/{id}")
    fun updateStatus(
        @PathVariable id: String,
        @RequestParam status: String,
    ): Mono<OrderDTO> {
        return orderOutputPort.updateStatus(
            UpdateOrderStatusRequest.newBuilder().also {
                it.status = status
                it.id = id
            }.build()
        )
            .map { it.toDTO() }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): Mono<Unit> {
        return orderOutputPort.delete(DeleteOrderRequest.newBuilder().setId(id).build())
            .map { it.toDTO() }
    }

    @GetMapping("/user/{id}")
    fun findAllByUserId(@PathVariable id: String): Mono<List<OrderDTO>> {
        return orderOutputPort.findAllByUserId(FindOrdersByUserIdRequest.newBuilder().setId(id).build())
            .map { it.toDTO() }
    }
}
