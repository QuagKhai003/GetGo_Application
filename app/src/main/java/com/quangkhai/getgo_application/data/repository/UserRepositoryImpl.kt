package com.quangkhai.getgo_application.data.repository

import android.util.Log
import com.quangkhai.getgo_application.data.network.UserApi
import com.quangkhai.getgo_application.domain.model.Friend
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException

class UserRepositoryImpl(private val userApi: UserApi = ApiClient.userApi) : UserRepository {

    // User CRUD Function--------------------------

    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            val user = userApi.getUserById(userId)
            Result.success(user)
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            Result.failure(Exception("Server error, code: ${e.code()}"))
        } catch (e: Exception) {
            Log.e("UserRepo", "getUserById($userId) failed", e)
            Result.failure(Exception("Something went wrong"))
        }
    }

    override suspend fun createUser(user: User): Result<User> {
        return try {
            val createdUser = userApi.createUser(user)
            Result.success(createdUser)
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            Result.failure(Exception("Server error, code: ${e.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))

        val body = user.copy(myLocations = emptyList(), friends = emptyList(), favorites = emptyList())

        return try {
            val response = userApi.updateUser(uid, body)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server rejected code: ${response.code()}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            val response = userApi.deleteUser(userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server rejected code: ${response.code()}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    // Saved Friend CRUD Functions-----------------

    override suspend fun getFriends(userId: String): Result<List<Friend>> {
        return try {
            val friends = userApi.getFriends(userId)
            Result.success(friends)
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            Result.failure(Exception("Server error, code: ${e.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    override suspend fun addFriend(userId: String, friend: Friend): Result<Unit> {
        return try {
            val response = userApi.addFriend(userId, friend)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server rejected code: ${response.code()}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    override suspend fun updateFriendLocation(userId: String, friendId: String, friend: Friend): Result<Unit> {
        return try {
            val response = userApi.updateFriendLocation(userId, friendId, friend)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server rejected code: ${response.code()}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    override suspend fun deleteFriend(userId: String, friendId: String): Result<Unit> {
        return try {
            val response = userApi.deleteFriend(userId, friendId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server rejected code: ${response.code()}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    // Saved Location CRUD Functions---------------

    override suspend fun getLocations(userId: String): Result<List<Location>> {
        return try {
            val locations = userApi.getLocations(userId)
            Result.success(locations)
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            Result.failure(Exception("Server error, code: ${e.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    override suspend fun addLocation(userId: String, location: Location): Result<Unit> {
        return try {
            val response = userApi.addLocation(userId, location)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server rejected code: ${response.code()}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    override suspend fun updateLocation(userId: String, locationId: String, location: Location): Result<Unit> {
        return try {
            val response = userApi.updateLocation(userId, locationId, location)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server rejected code: ${response.code()}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    override suspend fun deleteLocation(userId: String, locationId: String): Result<Unit> {
        return try {
            val response = userApi.deleteLocation(userId, locationId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server rejected code: ${response.code()}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    // Favorite Functions--------------------------

    override suspend fun getFavorites(userId: String): Result<List<Location>> {
        return try {
            val favorites = userApi.getFavorites(userId)
            Result.success(favorites)
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            Result.failure(Exception("Server error, code: ${e.code()}"))
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    override suspend fun addFavorite(userId: String, location: Location): Result<Unit> {
        return try {
            val response = userApi.addFavorite(userId, location)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server rejected code: ${response.code()}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }

    override suspend fun deleteFavorite(userId: String, locationId: String): Result<Unit> {
        return try {
            val response = userApi.deleteFavorite(userId, locationId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Server rejected code: ${response.code()}"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong"))
        }
    }
}
