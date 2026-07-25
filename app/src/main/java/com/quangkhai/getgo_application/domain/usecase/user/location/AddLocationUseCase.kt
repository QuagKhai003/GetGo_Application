package com.quangkhai.getgo_application.domain.usecase.user.location

import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

class AddLocationUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User, location: Location): Result<User> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))

        val result = userRepository.addLocation(uid, location)

        if (result.isSuccess) {
            // saved on the server, so build the updated user for the screen
            val updatedLocations = user.myLocations + location
            val updatedUser = user.copy(myLocations = updatedLocations)
            return Result.success(updatedUser)
        } else {
            // saving failed - pass the same error up
            val error = result.exceptionOrNull() ?: Exception("Unknown error")
            return Result.failure(error)
        }
    }
}