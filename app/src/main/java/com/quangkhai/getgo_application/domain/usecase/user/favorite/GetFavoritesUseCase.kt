package com.quangkhai.getgo_application.domain.usecase.user.favorite

import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

class GetFavoritesUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User): Result<List<Location>> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))
        return userRepository.getFavorites(uid)
    }
}
