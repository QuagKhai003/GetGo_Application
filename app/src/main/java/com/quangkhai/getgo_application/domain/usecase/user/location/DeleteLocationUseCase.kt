package com.quangkhai.getgo_application.domain.usecase.user.location

import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

class DeleteLocationUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User, locationId: String): Result<User> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))

        val result = userRepository.deleteLocation(uid, locationId)

        if (result.isSuccess) {
            val updatedLocations = user.myLocations.filter { it.id != locationId }
            return Result.success(user.copy(myLocations = updatedLocations))
        } else {
            val error = result.exceptionOrNull() ?: Exception("Unknown error")
            return Result.failure(error)
        }
    }
}
