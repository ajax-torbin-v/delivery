package com.example.delivery.service

import com.example.delivery.domain.DomainOrder
import com.example.delivery.domain.DomainUser
import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.exception.NotFoundException
import com.example.delivery.mapper.OrderMapper.toDomain
import com.example.delivery.mapper.UserMapper.toDomain
import com.example.delivery.mapper.UserMapper.toMongo
import com.example.delivery.repository.OrderRepository
import com.example.delivery.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
) {

    fun add(createUserDTO: CreateUserDTO): DomainUser {
        return userRepository.save(createUserDTO.toMongo()).toDomain()
    }

    fun getById(id: String): DomainUser {
        return userRepository.findById(id)?.toDomain()
            ?: throw NotFoundException("User with id $id doesn't exists")
    }

    fun findAllOrders(id: String): List<DomainOrder> {
        if (!userRepository.existsById(id)) throw NotFoundException("User with id $id doesn't exists")
        return orderRepository.findAll(id).map { it.toDomain() }
    }

    fun deleteById(id: String) {
        userRepository.deleteById(id)
    }
}
