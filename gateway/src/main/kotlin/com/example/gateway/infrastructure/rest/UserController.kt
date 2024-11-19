package com.example.gateway.infrastructure.rest

import com.example.core.dto.request.CreateUserDTO
import com.example.core.dto.request.UpdateUserDTO
import com.example.core.dto.response.UserDTO
import com.example.gateway.application.port.output.UserOutputPort
import com.example.gateway.infrastructure.mapper.UserProtoMapper.toCreateUserRequest
import com.example.gateway.infrastructure.mapper.UserProtoMapper.toDTO
import com.example.gateway.infrastructure.mapper.UserProtoMapper.updateUserRequest
import com.example.internal.input.reqreply.user.DeleteUserRequest
import com.example.internal.input.reqreply.user.FindUserByIdRequest
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
class UserController(private val userOutputPort: UserOutputPort) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createUserDTO: CreateUserDTO): Mono<UserDTO> {
        return userOutputPort.create(createUserDTO.toCreateUserRequest())
            .map { it.toDTO() }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<UserDTO> {
        return userOutputPort.findById(FindUserByIdRequest.newBuilder().setId(id).build())
            .map { it.toDTO() }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody updateUserDTO: UpdateUserDTO): Mono<UserDTO> {
        return userOutputPort.update(updateUserRequest(id, updateUserDTO))
            .map { it.toDTO() }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): Mono<Unit> {
        return userOutputPort.delete(DeleteUserRequest.newBuilder().setId(id).build())
            .map { it.toDTO() }
    }
}
