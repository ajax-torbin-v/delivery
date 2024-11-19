package com.example.domainservice.user.application.port.input

import com.example.domainservice.user.domain.DomainUser
import reactor.core.publisher.Mono

interface UserInputPort {
    fun save(user: DomainUser): Mono<DomainUser>
    fun getById(id: String): Mono<DomainUser>
    fun update(user: DomainUser): Mono<DomainUser>
    fun delete(id: String): Mono<Unit>
}
