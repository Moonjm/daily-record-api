package com.example.backend.admin

import com.example.backend.auth.AuthService
import com.example.backend.auth.RegisterRequest
import com.example.backend.common.response.ErrorResponseBody
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "관리자", description = "관리자 API")
@RestController
@RequestMapping("/admin")
class AdminAuthController(
    private val authService: AuthService,
) {
    @PostMapping("/register")
    @Operation(summary = "관리자 등록")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "생성됨"),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = [Content(schema = Schema(implementation = ErrorResponseBody::class))],
            ),
        ],
    )
    fun register(
        @Valid @RequestBody request: RegisterRequest,
    ): ResponseEntity<Long> = ResponseEntity.ok(authService.register(request))
}
