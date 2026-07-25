package com.quangkhai.getgo_application.domain.usecase.user.favorite

import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

class DeleteFavoriteUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User, locationId: String): Result<User> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))

        val result = userRepository.deleteFavorite(uid, locationId)

        if (result.isSuccess) {
            val updatedFavorites = user.favorites.filter { it.id != locationId }
            return Result.success(user.copy(favorites = updatedFavorites))
        } else {
            val error = result.exceptionOrNull() ?: Exception("Unknown error")
            return Result.failure(error)
        }
    }
}
