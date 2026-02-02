package com.example.backend.auth.security

import com.example.backend.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,
    @Column(nullable = false, length = 64, unique = true)
    var tokenHash: String,
    @Column(nullable = false)
    var expiresAt: LocalDateTime,
    @Column(nullable = true)
    var revokedAt: LocalDateTime? = null,
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,
)
