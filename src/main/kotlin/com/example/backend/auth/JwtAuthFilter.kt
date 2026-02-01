package com.example.backend.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val headerToken = request.getHeader("Authorization")?.takeIf { it.startsWith("Bearer ") }
            ?.removePrefix("Bearer ")
            ?.trim()

        val cookieToken = request.cookies
            ?.firstOrNull { it.name == "access_token" }
            ?.value

        val token = headerToken ?: cookieToken

        if (token.isNullOrBlank()) {
            filterChain.doFilter(request, response)
            return
        }

        runCatching {
            val claims = jwtService.parseClaims(token)
            val username = claims.subject ?: return@runCatching
            if (SecurityContextHolder.getContext().authentication == null) {
                val user = userRepository.findByUsername(username) ?: return@runCatching
                val principal: UserDetails =
                    org.springframework.security.core.userdetails.User
                        .withUsername(user.username)
                        .password(user.passwordHash)
                        .authorities(user.authority)
                        .build()
                val auth =
                    UsernamePasswordAuthenticationToken(principal, null, principal.authorities).apply {
                        details = WebAuthenticationDetailsSource().buildDetails(request)
                    }
                SecurityContextHolder.getContext().authentication = auth
            }
        }
        filterChain.doFilter(request, response)
    }
}
