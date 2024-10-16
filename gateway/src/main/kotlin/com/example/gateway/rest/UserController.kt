package com.example.gateway.rest

import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.request.UpdateUserDTO
import com.example.delivery.dto.response.UserDTO
import com.example.gateway.client.NatsClient
import com.example.gateway.mapper.UserProtoMapper.toCreateUserRequest
import com.example.gateway.mapper.UserProtoMapper.toDTO
import com.example.gateway.mapper.UserProtoMapper.updateUserRequest
import com.example.internal.api.subject.UserNatsSubject
import com.example.internal.input.reqreply.user.create.CreateUserResponse
import com.example.internal.input.reqreply.user.delete.DeleteUserRequest
import com.example.internal.input.reqreply.user.delete.DeleteUserResponse
import com.example.internal.input.reqreply.user.find.FindUserByIdRequest
import com.example.internal.input.reqreply.user.find.FindUserByIdResponse
import com.example.internal.input.reqreply.user.update.UpdateUserResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
class UserController(private val natsClient: NatsClient) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createUserDTO: CreateUserDTO): Mono<UserDTO> {
        return natsClient.doRequest(
            "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.SAVE}",
            createUserDTO.toCreateUserRequest(),
            CreateUserResponse.parser(),
        ).map { it.toDTO() }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<UserDTO> {
        return natsClient.doRequest(
            "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.FIND_BY_ID}",
            FindUserByIdRequest.newBuilder().apply { setId(id) }.build(),
            FindUserByIdResponse.parser()
        ).map { it.toDTO() }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody updateUserDTO: UpdateUserDTO): Mono<UserDTO> {
        return natsClient.doRequest(
            "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.UPDATE}",
            updateUserRequest(id, updateUserDTO),
            UpdateUserResponse.parser()
        ).map { it.toDTO() }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): Mono<Unit> {
        return natsClient.doRequest(
            "${UserNatsSubject.USER_PREFIX}.${UserNatsSubject.DELETE}",
            DeleteUserRequest.newBuilder().setId(id).build(),
            DeleteUserResponse.parser()
        ).map { it.toDTO() }
    }
}