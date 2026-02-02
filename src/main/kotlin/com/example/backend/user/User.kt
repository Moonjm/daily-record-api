package com.example.backend.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(nullable = false, length = 50, unique = true)
    var username: String,
    @Column(nullable = false, length = 100)
    var passwordHash: String,
    @Column(nullable = false, length = 30)
    var authority: String = "USER",
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null,
)
