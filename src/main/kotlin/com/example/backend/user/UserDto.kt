package com.example.backend.user

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 응답")
data class UserResponse(
    @field:Schema(description = "ID", example = "1")
    val id: Long,
    @field:Schema(description = "아이디", example = "admin")
    val username: String,
    @field:Schema(description = "권한", example = "USER")
    val authority: String,
)

fun User.toResponse(): UserResponse =
    UserResponse(
        id = id ?: 0,
        username = username,
        authority = authority,
    )
