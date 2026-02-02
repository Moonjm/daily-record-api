package com.example.backend.user

import com.example.backend.common.constant.ErrorCode
import com.example.backend.common.exception.CustomException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
) {
    fun me(username: String): UserResponse =
        userRepository.findByUsername(username.trim())?.toResponse()
            ?: throw CustomException(ErrorCode.RESOURCE_NOT_FOUND, username)
}
