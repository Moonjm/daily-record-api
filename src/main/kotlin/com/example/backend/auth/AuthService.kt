package com.example.backend.auth

import com.example.backend.auth.security.JwtProperties
import com.example.backend.auth.security.JwtService
import com.example.backend.auth.security.RefreshToken
import com.example.backend.auth.security.RefreshTokenRepository
import com.example.backend.common.constant.ErrorCode
import com.example.backend.common.exception.CustomException
import com.example.backend.common.utils.TokenHasher
import com.example.backend.user.User
import com.example.backend.user.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val jwtProperties: JwtProperties,
    private val cookieService: AuthCookieService,
) {
    @Transactional
    fun register(request: RegisterRequest): Long {
        val username = request.username
        val password = request.password
        if (userRepository.existsByUsername(username)) {
            throw CustomException(ErrorCode.DUPLICATE_RESOURCE, username)
        }
        val encoded =
            passwordEncoder.encode(password)
                ?: throw CustomException(ErrorCode.INVALID_REQUEST, "password")
        val user =
            User(
                username = username,
                passwordHash = encoded,
            )
        return userRepository.save(user).id!!
    }

    @Transactional
    fun login(
        request: LoginRequest,
        response: HttpServletResponse,
    ) {
        val username = request.username
        val password = request.password
        val user =
            userRepository.findByUsername(username)
                ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, username)
        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw CustomException(ErrorCode.INVALID_REQUEST, "password")
        }
        val pair = issueTokens(user)
        cookieService.applyAuthCookies(
            response = response,
            accessToken = pair.accessToken,
            refreshToken = pair.refreshToken,
            accessMaxAgeSeconds = jwtService.accessTokenMaxAgeSeconds(),
            refreshMaxAgeSeconds = jwtService.refreshTokenMaxAgeSeconds(),
        )
    }

    @Transactional
    fun refresh(
        rawRefreshToken: String,
        response: HttpServletResponse,
    ) {
        val hashed = TokenHasher.sha256(rawRefreshToken)
        val token =
            refreshTokenRepository.findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(
                hashed,
                LocalDateTime.now(),
            ) ?: throw CustomException(ErrorCode.INVALID_REQUEST, "refreshToken")

        val pair = issueTokens(token.user)
        cookieService.applyAuthCookies(
            response = response,
            accessToken = pair.accessToken,
            refreshToken = pair.refreshToken,
            accessMaxAgeSeconds = jwtService.accessTokenMaxAgeSeconds(),
            refreshMaxAgeSeconds = jwtService.refreshTokenMaxAgeSeconds(),
        )
    }

    @Transactional
    fun logout(
        rawRefreshToken: String?,
        response: HttpServletResponse,
    ) {
        if (!rawRefreshToken.isNullOrBlank()) {
            val hashed = TokenHasher.sha256(rawRefreshToken)
            val token =
                refreshTokenRepository.findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(
                    hashed,
                    LocalDateTime.now(),
                )
            token?.user?.id?.let { refreshTokenRepository.deleteAllByUserId(it) }
        }
        cookieService.clearAuthCookies(response)
    }

    private fun issueTokens(user: User): TokenPair {
        val userId = user.id ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, "user")
        refreshTokenRepository.deleteAllByUserId(userId)
        val accessToken = jwtService.createAccessToken(user.username)
        val refreshToken = UUID.randomUUID().toString().replace("-", "")
        val expiresAt = LocalDateTime.now().plusDays(jwtProperties.refreshTokenExpireDays)
        refreshTokenRepository.save(
            RefreshToken(
                user = user,
                tokenHash = TokenHasher.sha256(refreshToken),
                expiresAt = expiresAt,
            ),
        )
        return TokenPair(accessToken = accessToken, refreshToken = refreshToken)
    }

    private data class TokenPair(
        val accessToken: String,
        val refreshToken: String,
    )
}
