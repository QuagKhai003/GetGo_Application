package com.quangkhai.getgo_application.domain.usecase.user.billgroup

import com.quangkhai.getgo_application.domain.model.BillGroup
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository

class GetBillGroupsUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User): Result<List<BillGroup>> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))
        return userRepository.getBillGroups(uid)
    }
}
