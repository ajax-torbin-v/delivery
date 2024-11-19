package com.example.domainservice.user.application.port.output

import com.example.domainservice.user.domain.DomainUser
import reactor.core.publisher.Mono

interface UserRepositoryOutputPort {
    fun save(user: DomainUser): Mono<DomainUser>
    fun findById(id: String): Mono<DomainUser>
    fun update(user: DomainUser): Mono<DomainUser>
    fun deleteById(id: String): Mono<Unit>
}
