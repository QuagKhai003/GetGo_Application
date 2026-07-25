package com.quangkhai.getgo_application.domain.usecase.user

import com.quangkhai.getgo_application.domain.repository.UserRepository

class DeleteUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        if (userId.isBlank()) {
            return Result.failure(Exception("User id is empty"))
        }
        return userRepository.deleteUser(userId)
    }
}
