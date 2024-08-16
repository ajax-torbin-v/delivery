package com.example.delivery.controller;

import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.response.UserDTO
import com.example.delivery.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

//TODO: wrap into ResponseEntity
@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun add(@RequestBody createUserDTO: CreateUserDTO): UserDTO {
        return userService.add(createUserDTO)
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): UserDTO {
        return userService.findById(id)
    }

    @PutMapping("{userId}/orders")
    fun addOrder(@PathVariable userId: String, @RequestParam orderId: String): UserDTO {
        return userService.addOrder(userId, orderId)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String) {
        userService.deleteById(id)
    }

}
