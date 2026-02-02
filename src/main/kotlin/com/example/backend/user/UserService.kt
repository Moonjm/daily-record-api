package com.example.backend.user

import com.example.backend.common.constant.ErrorCode
import com.example.backend.common.exception.CustomException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun me(username: String): UserResponse =
        userRepository.findByUsername(username)?.toResponse()
            ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, username)

    @Transactional
    fun updateMe(
        username: String,
        request: UserUpdateRequest,
    ) {
        val user =
            userRepository.findByUsername(username)
                ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, username)
        request.name?.let { user.updateProfile(it) }
        request.password?.let { newPassword ->
            val currentPassword =
                request.currentPassword
                    ?: throw CustomException(ErrorCode.INVALID_REQUEST, "currentPassword")
            if (!passwordEncoder.matches(currentPassword, user.passwordHash)) {
                throw CustomException(ErrorCode.INVALID_REQUEST, "currentPassword")
            }
            val encoded =
                passwordEncoder.encode(newPassword)
                    ?: throw CustomException(ErrorCode.INVALID_REQUEST, "password")
            user.updatePassword(encoded)
        }
    }
}
