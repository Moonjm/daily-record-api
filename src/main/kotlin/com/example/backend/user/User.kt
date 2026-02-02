package com.example.backend.user

import com.example.backend.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, length = 50, unique = true)
    var username: String,
    @Column(nullable = false, length = 100)
    var passwordHash: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var authority: Authority = Authority.USER,
) : BaseEntity() {
    fun updateCredentials(
        passwordHash: String,
        authority: Authority,
    ) {
        this.passwordHash = passwordHash
        this.authority = authority
    }
}
