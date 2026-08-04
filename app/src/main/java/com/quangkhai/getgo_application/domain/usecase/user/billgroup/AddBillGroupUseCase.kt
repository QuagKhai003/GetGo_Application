package com.quangkhai.getgo_application.domain.usecase.user.billgroup

import com.quangkhai.getgo_application.domain.model.BillGroup
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

class AddBillGroupUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User, group: BillGroup): Result<User> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))

        val result = userRepository.addBillGroup(uid, group)

        return if (result.isSuccess) {
            Result.success(user.copy(billGroups = user.billGroups + group))
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
        }
    }
}
