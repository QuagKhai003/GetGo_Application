package com.quangkhai.getgo_application.domain.usecase.user.friend

import com.quangkhai.getgo_application.domain.model.Friend
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

/**
 * Saves a friend with their one location.
 * The name identifies a friend, so two friends cannot share a name.
 */
class AddFriendUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User, friendName: String, location: Location): Result<User> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))

        if (friendName.isBlank()) {
            return Result.failure(Exception("Friend name cannot be empty"))
        }

        val inputName = user.friends.any { it.name == friendName }

        if (inputName) {
            return Result.failure(Exception("You already have a friend called $friendName"))
        }

        val newFriend = Friend(name = friendName, location = location)

        val result = userRepository.addFriend(uid, newFriend)

        if (result.isSuccess) {
            return Result.success(user.copy(friends = user.friends + newFriend))
        } else {
            val error = result.exceptionOrNull() ?: Exception("Unknown error")
            return Result.failure(error)
        }
    }
}
