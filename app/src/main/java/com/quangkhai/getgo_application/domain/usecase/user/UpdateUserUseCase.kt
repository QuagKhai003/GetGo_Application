package com.quangkhai.getgo_application.domain.usecase.user

import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

/**
 * Saves profile changes (name, username, ...).
 * On success it hands back the same user so the screen can keep using it.
 */
class UpdateUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User): Result<User> {
        if (user.id == null) {
            return Result.failure(Exception("User has no id"))
        }

        if (user.name.isBlank()) {
            return Result.failure(Exception("Name cannot be empty"))
        }

        val result = userRepository.updateUser(user)

        if (result.isSuccess) {
            return Result.success(user)
        } else {
            val error = result.exceptionOrNull() ?: Exception("Unknown error")
            return Result.failure(error)
        }
    }
}
