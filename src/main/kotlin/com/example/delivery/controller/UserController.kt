package com.example.delivery.controller

import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.response.UserDTO
import com.example.delivery.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun add(@RequestBody createUserDTO: CreateUserDTO): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(userService.add(createUserDTO))
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): ResponseEntity<UserDTO> {
        return ResponseEntity.ok(userService.findById(id))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String): ResponseEntity<Void> {
        userService.deleteById(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

}
