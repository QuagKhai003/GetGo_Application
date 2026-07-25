package com.quangkhai.getgo_application.domain.usecase.user.friend

import com.quangkhai.getgo_application.domain.model.Friend
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

class GetFriendsUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User): Result<List<Friend>> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))
        return userRepository.getFriends(uid)
    }
}
