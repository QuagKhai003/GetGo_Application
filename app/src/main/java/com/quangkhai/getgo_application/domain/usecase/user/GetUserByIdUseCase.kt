package com.quangkhai.getgo_application.domain.usecase.user

import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

class GetUserByIdUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Result<User> {
        if (userId.isBlank()) {
            return Result.failure(Exception("User id is empty"))
        }
        return userRepository.getUserById(userId)
    }
}
