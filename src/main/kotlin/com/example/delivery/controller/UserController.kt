package com.example.delivery.controller

import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.request.UpdateUserDTO
import com.example.delivery.dto.response.UserDTO
import com.example.delivery.mapper.UserMapper.toDTO
import com.example.delivery.service.UserService
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
class UserController(private val userService: UserService) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun add(@RequestBody createUserDTO: CreateUserDTO): Mono<UserDTO> {
        return userService.add(createUserDTO).map { it.toDTO() }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<UserDTO> {
        return userService.getById(id).map { it.toDTO() }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody updateUserDTO: UpdateUserDTO): Mono<UserDTO> {
        return userService.update(id, updateUserDTO).map { it.toDTO() }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String): Mono<Unit> {
        return userService.deleteById(id)
    }
}
