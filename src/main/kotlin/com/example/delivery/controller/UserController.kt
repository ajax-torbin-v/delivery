package com.example.delivery.controller;

import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.response.UserDTO
import com.example.delivery.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping("/add")
    fun add(@RequestBody createUserDTO: CreateUserDTO): UserDTO {
        return userService.add(createUserDTO)
    }

    @GetMapping("/findById/{id}")
    fun findById(@PathVariable id: String): UserDTO {
        return userService.findById(id)
    }

    @PutMapping("/add-order")
    fun addOrder(@RequestParam userId: String, @RequestParam orderId: String) {
        userService.addOrder(userId, orderId)
    }

    @DeleteMapping("/deleteById/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String) {
        userService.deleteById(id)
    }

}
