package com.example.delivery.service

import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.response.UserDTO
import com.example.delivery.exception.NotFoundException
import com.example.delivery.model.MongoUser
import com.example.delivery.repository.OrderRepository
import com.example.delivery.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository) {

    fun add(createUserDTO: CreateUserDTO): UserDTO {
        return userRepository.save(createUserDTO.toEntity()).toDTO()
    }

    fun findById(id: String): UserDTO {
        val user: MongoUser = userRepository.findById(id) ?: throw NotFoundException("User with id $id doesn't exists")
        return user.toDTO()
    }

    fun addOrder(userId: String, orderId: String) {
        if (!userRepository.existsById(userId)) throw NotFoundException("User with id $userId doesn't exists")
        if (!orderRepository.existsById(orderId)) throw NotFoundException("Order with id $orderId doesn't exists")
        userRepository.addOrder(userId, orderId)
    }

    fun deleteById(id: String) =
        if (userRepository.existsById(id)) userRepository.deleteById(id)
        else throw NotFoundException("User with id $id doesn't exists")
}
