package com.example.backend.common.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cors")
data class CorsProperties(
    val allowedOriginPatterns: List<String> = listOf("http://localhost:*"),
)
