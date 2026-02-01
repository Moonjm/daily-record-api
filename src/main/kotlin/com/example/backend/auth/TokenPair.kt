package com.example.backend.auth

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)
