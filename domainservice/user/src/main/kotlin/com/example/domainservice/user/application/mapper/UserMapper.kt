package com.example.domainservice.user.application.mapper

import com.example.domainservice.user.domain.DomainUser

object UserMapper {
    fun DomainUser.applyPartialUpdate(partialUpdate: DomainUser): DomainUser {
        return DomainUser(
            id = partialUpdate.id!!,
            fullName = partialUpdate.fullName.ifEmpty { fullName },
            phone = partialUpdate.phone.ifEmpty { phone },
            password = partialUpdate.password.ifEmpty { password },
        )
    }
}
