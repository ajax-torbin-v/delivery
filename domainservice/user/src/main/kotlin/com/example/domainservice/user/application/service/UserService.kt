package com.example.domainservice.user.application.service

import com.example.core.exception.UserNotFoundException
import com.example.domainservice.user.application.mapper.UserMapper.applyPartialUpdate
import com.example.domainservice.user.application.port.input.UserInputPort
import com.example.domainservice.user.application.port.output.UserRepositoryOutputPort
import com.example.domainservice.user.domain.DomainUser
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class UserService(
    private val userRepositoryOutputPort: UserRepositoryOutputPort,
) : UserInputPort {
    override fun save(user: DomainUser): Mono<DomainUser> {
        return userRepositoryOutputPort.save(user)
    }

    override fun getById(id: String): Mono<DomainUser> {
        return userRepositoryOutputPort.findById(id)
            .switchIfEmpty { Mono.error(UserNotFoundException("User with id $id doesn't exists")) }
    }

    override fun update(user: DomainUser): Mono<DomainUser> {
        return userRepositoryOutputPort
            .findById(user.id!!)
            .flatMap { userRepositoryOutputPort.update(it.applyPartialUpdate(user)) }
            .switchIfEmpty { Mono.error(UserNotFoundException("User with id ${user.id} doesn't exists")) }
    }

    override fun delete(id: String): Mono<Unit> {
        return userRepositoryOutputPort.deleteById(id)
    }
}
