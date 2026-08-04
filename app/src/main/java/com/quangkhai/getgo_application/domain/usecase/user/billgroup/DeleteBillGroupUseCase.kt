package com.quangkhai.getgo_application.domain.usecase.user.billgroup

import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

class DeleteBillGroupUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User, groupId: String): Result<User> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))

        val result = userRepository.deleteBillGroup(uid, groupId)

        return if (result.isSuccess) {
            Result.success(user.copy(billGroups = user.billGroups.filter { it.id != groupId }))
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
        }
    }
}
