package com.quangkhai.getgo_application.domain.usecase.user.billgroup

import com.quangkhai.getgo_application.domain.model.BillGroup
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

class UpdateBillGroupUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User, group: BillGroup): Result<User> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))
        val gid = group.id ?: return Result.failure(Exception("Group has no id"))

        val result = userRepository.updateBillGroup(uid, gid, group)

        return if (result.isSuccess) {
            val updated = user.billGroups.map { if (it.id == gid) group else it }
            Result.success(user.copy(billGroups = updated))
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
        }
    }
}
