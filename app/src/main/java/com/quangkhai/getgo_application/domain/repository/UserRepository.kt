package com.quangkhai.getgo_application.domain.repository

import com.quangkhai.getgo_application.domain.model.BillGroup
import com.quangkhai.getgo_application.domain.model.Friend
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.User

interface UserRepository {

    // User CRUD Function
    suspend fun getUserById(userId: String): Result<User>
    suspend fun login(username: String, password: String): Result<User>
    suspend fun createUser(user: User): Result<User> // Register
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun deleteUser(userId: String): Result<Unit>

    // Saved Friend CRUD Functions
    suspend fun getFriends(userId: String): Result<List<Friend>>
    suspend fun addFriend(userId: String, friend: Friend): Result<Unit>
    suspend fun updateFriendLocation(userId: String, friendId: String, friend: Friend): Result<Unit>
    suspend fun deleteFriend(userId: String, friendId: String): Result<Unit>

    // Saved Location CRUD Functions
    suspend fun getLocations(userId: String): Result<List<Location>>
    suspend fun addLocation(userId: String, location: Location): Result<Unit>
    suspend fun updateLocation(userId: String, locationId: String, location: Location): Result<Unit>
    suspend fun deleteLocation(userId: String, locationId: String): Result<Unit>

    // Favorite Functions (places the user likes to go)
    suspend fun getFavorites(userId: String): Result<List<Location>>
    suspend fun addFavorite(userId: String, location: Location): Result<Unit>
    suspend fun deleteFavorite(userId: String, locationId: String): Result<Unit>

    // Bill Group CRUD Functions (split-bill groups)
    suspend fun getBillGroups(userId: String): Result<List<BillGroup>>
    suspend fun addBillGroup(userId: String, group: BillGroup): Result<Unit>
    suspend fun updateBillGroup(userId: String, groupId: String, group: BillGroup): Result<Unit>
    suspend fun deleteBillGroup(userId: String, groupId: String): Result<Unit>
}
