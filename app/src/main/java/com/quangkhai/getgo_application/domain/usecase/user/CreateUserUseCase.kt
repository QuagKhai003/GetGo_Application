package com.quangkhai.getgo_application.domain.usecase.user

import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

/**
 * Creates a new account (register).
 * The server gives back the user with its new id, so we return that one -
 * not the one we sent, which still has id = null.
 */
class CreateUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(name: String, username: String, password: String): Result<User> {
        if (name.isBlank() || username.isBlank()) {
            return Result.failure(Exception("Name and username cannot be empty"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }

        val newUser = User(
            id = null,                  // the server assigns this
            name = name,
            username = username,
            password = password,
            myLocations = emptyList(),
            friends = emptyList(),
            favorites = emptyList()
        )

        return userRepository.createUser(newUser)
    }
}
