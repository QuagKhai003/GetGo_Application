package com.quangkhai.getgo_application.data.repository

import com.quangkhai.getgo_application.data.network.client.SupabaseClientApi
import com.quangkhai.getgo_application.domain.model.BillGroup
import com.quangkhai.getgo_application.domain.model.Friend
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.repository.UserRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import kotlin.coroutines.cancellation.CancellationException

class UserSupabaseRepositoryImpl() : UserRepository {

    private val supaDB = SupabaseClientApi.supaClientApi

    // User CRUD Function--------------------------

    override suspend fun getUserById(userId: String): Result<User> {
        return try {
            val user = supaDB.from("users")
                .select { filter { eq("id", userId) } }
                .decodeSingleOrNull<User>()

            if (user == null) {
                Result.failure(Exception("No user with that id"))
            } else {
                Result.success(user)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            val user = supaDB.from("users")
                .select {
                    filter {
                        eq("username", username)
                        eq("password", password)
                    }
                }
                .decodeSingleOrNull<User>()

            if (user == null) {
                Result.failure(Exception("Wrong username or password"))
            } else {
                Result.success(user)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun createUser(user: User): Result<User> {
        return try {
            val created = supaDB.from("users")
                .insert(
                    buildJsonObject {
                        put("name", user.name)
                        put("username", user.username)
                        put("password", user.password)
                    }
                ) { select() }
                .decodeSingle<User>()

            Result.success(created)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        val uid = user.id ?: return Result.failure(Exception("User has no id"))

        return try {
            supaDB.from("users").update({
                    set("name", user.name)
                    set("username", user.username)
                    set("password", user.password)
            }) { filter { eq("id", uid) } }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            supaDB.from("users").delete { filter { eq("id", userId) } }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    // Saved Friend CRUD Functions-----------------

    override suspend fun getFriends(userId: String): Result<List<Friend>> {
        return try {
            val friends = supaDB.from("friends")
                .select { filter { eq("user_id", userId) } }
                .decodeList<Friend>()

            Result.success(friends)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun addFriend(userId: String, friend: Friend): Result<Unit> {
        return try {
            supaDB.from("friends").insert(buildJsonObject {
                put("user_id", userId)
                put("name", friend.name)
                put("location", Json.encodeToJsonElement(friend.location))
            })
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun updateFriendLocation(userId: String, friendId: String, friend: Friend): Result<Unit> {
        return try {
            // From the friend table, update location collumn with new location object from friend.location
            supaDB.from("friends").update({ set("location", friend.location) }) {
                filter {
                    eq("id", friendId)
                    eq("user_id", userId)
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun deleteFriend(userId: String, friendId: String): Result<Unit> {
        return try {
            supaDB.from("friends").delete {
                filter {
                    eq("id", friendId)
                    eq("user_id", userId)
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    // Saved Location CRUD Functions---------------

    override suspend fun getLocations(userId: String): Result<List<Location>> {
        return try {
            val locations = supaDB.from("locations")
                .select { filter { eq("user_id", userId) } }
                .decodeList<Location>()

            Result.success(locations)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun addLocation(userId: String, location: Location): Result<Unit> {
        return try {
            supaDB.from("locations").insert(locationBody(userId, location))
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun updateLocation(userId: String, locationId: String, location: Location): Result<Unit> {
        return try {
            supaDB.from("locations").update({
                    set("name", location.name)
                    set("lat", location.lat)
                    set("long", location.long)
                    set("address", location.address)
            }) {
                filter {
                    eq("id", locationId)
                    eq("user_id", userId)
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun deleteLocation(userId: String, locationId: String): Result<Unit> {
        return try {
            supaDB.from("locations").delete {
                filter {
                    eq("id", locationId)
                    eq("user_id", userId)
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    // Favorite Functions--------------------------

    override suspend fun getFavorites(userId: String): Result<List<Location>> {
        return try {
            val favorites = supaDB.from("favorites")
                .select { filter { eq("user_id", userId) } }
                .decodeList<Location>()

            Result.success(favorites)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun addFavorite(userId: String, location: Location): Result<Unit> {
        return try {
            supaDB.from("favorites").insert(locationBody(userId, location))
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun deleteFavorite(userId: String, locationId: String): Result<Unit> {
        return try {
            supaDB.from("favorites").delete {
                filter {
                    eq("id", locationId)
                    eq("user_id", userId)
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    private fun locationBody(userId: String, location: Location) = buildJsonObject {
        put("user_id", userId)
        put("name", location.name)
        put("lat", location.lat)
        put("long", location.long)
        put("address", location.address)
    }

    // Bill Group CRUD Functions-------------------
    // one "bill_groups" row per group; people + bills stored as JSON columns

    override suspend fun getBillGroups(userId: String): Result<List<BillGroup>> {
        return try {
            val groups = supaDB.from("bill_groups")
                .select { filter { eq("user_id", userId) } }
                .decodeList<BillGroup>()

            Result.success(groups)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun addBillGroup(userId: String, group: BillGroup): Result<Unit> {
        return try {
            supaDB.from("bill_groups").insert(billGroupBody(userId, group))
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun updateBillGroup(userId: String, groupId: String, group: BillGroup): Result<Unit> {
        return try {
            supaDB.from("bill_groups").update({
                set("name", group.name)
                set("people", Json.encodeToJsonElement(group.people))
                set("bills", Json.encodeToJsonElement(group.bills))
            }) {
                filter {
                    eq("id", groupId)
                    eq("user_id", userId)
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    override suspend fun deleteBillGroup(userId: String, groupId: String): Result<Unit> {
        return try {
            supaDB.from("bill_groups").delete {
                filter {
                    eq("id", groupId)
                    eq("user_id", userId)
                }
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: \n ${e.message}"))
        }
    }

    private fun billGroupBody(userId: String, group: BillGroup) = buildJsonObject {
        put("user_id", userId)
        put("name", group.name)
        put("people", Json.encodeToJsonElement(group.people))
        put("bills", Json.encodeToJsonElement(group.bills))
    }
}
