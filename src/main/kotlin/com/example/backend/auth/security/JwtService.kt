package com.example.backend.auth.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.util.Date

@Service
class JwtService(
    private val jwtProperties: JwtProperties,
) {
    private fun signingKey() = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))

    fun createAccessToken(username: String): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpireMinutes * 60_000)
        return Jwts
            .builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(signingKey(), Jwts.SIG.HS256)
            .compact()
    }

    fun parseClaims(token: String): Claims =
        Jwts
            .parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)
            .payload

    fun accessTokenMaxAgeSeconds(): Long = jwtProperties.accessTokenExpireMinutes * 60

    fun refreshTokenMaxAgeSeconds(): Long = jwtProperties.refreshTokenExpireDays * 24 * 60 * 60
}
