package com.quangkhai.getgo_application.domain.usecase.user.location

import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

/**
 * Edits the user's saved location (rename, move the pin, ...).
 * It must already exist on the server, so it must already have an id.
 */
class UpdateLocationUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User, location: Location): Result<User> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))
        val locationId = location.id ?: return Result.failure(Exception("Location has no id"))

        val result = userRepository.updateLocation(uid, locationId, location)

        if (result.isSuccess) {
            return Result.success(user.copy(location = location))
        } else {
            val error = result.exceptionOrNull() ?: Exception("Unknown error")
            return Result.failure(error)
        }
    }
}
