package com.quangkhai.getgo_application.domain.usecase.user.friend

import com.quangkhai.getgo_application.domain.model.Friend
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

class UpdateFriendLocationUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User, friend: Friend, location: Location): Result<User> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))
        val friendId = friend.id ?: return Result.failure(Exception("Friend has no id"))

        val updatedFriend = friend.copy(location = location)
        val result = userRepository.updateFriendLocation(uid, friendId, updatedFriend)

        if (result.isSuccess) {
            val updatedFriends = user.friends.map { old ->
                if (old.id == friendId) updatedFriend else old
            }
            return Result.success(user.copy(friends = updatedFriends))
        } else {
            val error = result.exceptionOrNull() ?: Exception("Unknown error")
            return Result.failure(error)
        }
    }
}
