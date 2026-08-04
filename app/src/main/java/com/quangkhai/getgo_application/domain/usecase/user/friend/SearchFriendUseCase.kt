package com.quangkhai.getgo_application.domain.usecase.user.friend

import com.quangkhai.getgo_application.domain.model.Friend
import com.quangkhai.getgo_application.domain.model.User

/**
 * Filters the user's own saved friends by name.
 */
class SearchFriendUseCase {
    operator fun invoke(user: User, query: String): List<Friend> {
        val trimmed = query.trim()

        if (trimmed.isBlank()) {
            return user.friends
        }

        return user.friends.filter { friend ->
            friend.name.contains(trimmed, ignoreCase = true)
        }
    }
}
