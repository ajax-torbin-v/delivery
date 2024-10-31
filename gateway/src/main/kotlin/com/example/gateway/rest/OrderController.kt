package com.example.gateway.rest

import com.example.core.dto.request.CreateOrderDTO
import com.example.core.dto.request.UpdateOrderDTO
import com.example.core.dto.response.OrderDTO
import com.example.core.dto.response.OrderWithProductDTO
import com.example.gateway.client.NatsClient
import com.example.gateway.mapper.OrderProtoMapper.toCreateOrderRequest
import com.example.gateway.mapper.OrderProtoMapper.toDTO
import com.example.gateway.mapper.OrderProtoMapper.toDtoWithProduct
import com.example.gateway.mapper.OrderProtoMapper.updateOrderRequest
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.DeleteOrderRequest
import com.example.internal.input.reqreply.order.DeleteOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdRequest
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.FindOrdersByUserIdRequest
import com.example.internal.input.reqreply.order.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.UpdateOrderResponse
import com.example.internal.input.reqreply.order.UpdateOrderStatusRequest
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
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
class OrderController(private val natsClient: NatsClient) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createOrderDTO: CreateOrderDTO): Mono<OrderDTO> {
        return natsClient.doRequest(
            NatsSubject.Order.SAVE,
            createOrderDTO.toCreateOrderRequest(),
            CreateOrderResponse.parser()
        ).map { it.toDTO() }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<OrderWithProductDTO> {
        return natsClient.doRequest(
            NatsSubject.Order.FIND_BY_ID,
            FindOrderByIdRequest.newBuilder().setId(id).build(),
            FindOrderByIdResponse.parser()
        ).map { it.toDtoWithProduct() }
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @RequestBody updateOrderDTO: UpdateOrderDTO,
    ): Mono<OrderDTO> {
        return natsClient.doRequest(
            NatsSubject.Order.UPDATE,
            updateOrderRequest(id, updateOrderDTO),
            UpdateOrderResponse.parser()
        ).map { it.toDTO() }
    }

    @PatchMapping("/{id}")
    fun updateStatus(
        @PathVariable id: String,
        @RequestParam status: String,
    ): Mono<OrderDTO> {
        return natsClient.doRequest(
            NatsSubject.Order.UPDATE_STATUS,
            UpdateOrderStatusRequest.newBuilder().setStatus(status).setId(id).build(),
            UpdateOrderStatusResponse.parser()
        ).map { it.toDTO() }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): Mono<Unit> {
        return natsClient.doRequest(
            NatsSubject.Order.DELETE,
            DeleteOrderRequest.newBuilder().setId(id).build(),
            DeleteOrderResponse.parser()
        ).map { it.toDTO() }
    }

    @GetMapping("/user/{id}")
    fun findAllByUserId(@PathVariable id: String): Mono<List<OrderDTO>> {
        return natsClient.doRequest(
            NatsSubject.Order.FIND_ALL_BY_USER_ID,
            FindOrdersByUserIdRequest.newBuilder().setId(id).build(),
            FindOrdersByUserIdResponse.parser()
        ).map { it.toDTO() }
    }
}
