package com.quangkhai.getgo_application.domain.usecase.user.friend

import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

class DeleteFriendUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User, friendId: String): Result<User> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))

        val result = userRepository.deleteFriend(uid, friendId)

        if (result.isSuccess) {
            val updatedFriends = user.friends.filter { it.id != friendId }
            return Result.success(user.copy(friends = updatedFriends))
        } else {
            val error = result.exceptionOrNull() ?: Exception("Unknown error")
            return Result.failure(error)
        }
    }
}
