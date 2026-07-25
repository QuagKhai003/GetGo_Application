package com.quangkhai.getgo_application.domain.usecase.user.favorite

import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

/**
 * Saves a place the user likes. Adding the same place twice is not an error,
 * we just do nothing so the list stays clean.
 */
class AddFavoriteUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User, location: Location): Result<User> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))

        val alreadySaved = user.favorites.any { it.id != null && it.id == location.id }
        if (alreadySaved) {
            return Result.success(user)
        }

        val result = userRepository.addFavorite(uid, location)

        if (result.isSuccess) {
            return Result.success(user.copy(favorites = user.favorites + location))
        } else {
            val error = result.exceptionOrNull() ?: Exception("Unknown error")
            return Result.failure(error)
        }
    }
}
