package com.example.delivery.service

import com.example.delivery.domain.DomainUser
import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.request.UpdateUserDTO
import com.example.delivery.exception.NotFoundException
import com.example.delivery.mapper.UserMapper.toDomain
import com.example.delivery.mapper.UserMapper.toMongo
import com.example.delivery.mapper.UserMapper.toUpdate
import com.example.delivery.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    fun add(createUserDTO: CreateUserDTO): Mono<DomainUser> {
        return userRepository.save(createUserDTO.toMongo()).map { it.toDomain() }
    }

    fun getById(id: String): Mono<DomainUser> {
        return userRepository.findById(id).map { it.toDomain() }
            .switchIfEmpty(Mono.error(NotFoundException("User with id $id doesn't exists")))
    }

    fun update(id: String, updateUserDTO: UpdateUserDTO): Mono<DomainUser> {
        return userRepository.update(id, updateUserDTO.toUpdate()).map { it.toDomain() }
            .switchIfEmpty(Mono.error(NotFoundException("User with id $id doesn't exists")))
    }

    fun deleteById(id: String) {
        userRepository.deleteById(id)
    }
}
