package com.quangkhai.getgo_application.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quangkhai.getgo_application.data.repository.UserSupabaseRepositoryImpl
import com.quangkhai.getgo_application.domain.model.Friend
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.domain.model.User
import com.quangkhai.getgo_application.domain.usecase.user.favorite.AddFavoriteUseCase
import com.quangkhai.getgo_application.domain.usecase.user.favorite.DeleteFavoriteUseCase
import com.quangkhai.getgo_application.domain.usecase.user.favorite.GetFavoritesUseCase
import com.quangkhai.getgo_application.domain.usecase.user.friend.AddFriendUseCase
import com.quangkhai.getgo_application.domain.usecase.user.friend.DeleteFriendUseCase
import com.quangkhai.getgo_application.domain.usecase.user.friend.GetFriendsUseCase
import com.quangkhai.getgo_application.domain.usecase.user.friend.SearchFriendUseCase
import com.quangkhai.getgo_application.domain.usecase.user.friend.UpdateFriendLocationUseCase
import com.quangkhai.getgo_application.domain.usecase.user.location.AddLocationUseCase
import com.quangkhai.getgo_application.domain.usecase.user.location.DeleteLocationUseCase
import com.quangkhai.getgo_application.domain.usecase.user.location.GetLocationsUseCase
import com.quangkhai.getgo_application.domain.usecase.user.location.UpdateLocationUseCase
import com.quangkhai.getgo_application.domain.usecase.user.CreateUserUseCase
import com.quangkhai.getgo_application.domain.usecase.user.DeleteUserUseCase
import com.quangkhai.getgo_application.domain.usecase.user.GetUserByIdUseCase
import com.quangkhai.getgo_application.domain.usecase.user.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val userRepository = UserSupabaseRepositoryImpl()

    private val getUserByIdUseCase = GetUserByIdUseCase(userRepository)
    private val createUserUseCase = CreateUserUseCase(userRepository)
    private val updateUserUseCase = UpdateUserUseCase(userRepository)
    private val searchFriendUseCase = SearchFriendUseCase()

    private val addLocationUseCase = AddLocationUseCase(userRepository)
    private val updateLocationUseCase = UpdateLocationUseCase(userRepository)
    private val deleteLocationUseCase = DeleteLocationUseCase(userRepository)

    private val addFavoriteUseCase = AddFavoriteUseCase(userRepository)
    private val deleteFavoriteUseCase = DeleteFavoriteUseCase(userRepository)

    private val addFriendUseCase = AddFriendUseCase(userRepository)
    private val updateFriendLocationUseCase = UpdateFriendLocationUseCase(userRepository)
    private val deleteFriendUseCase = DeleteFriendUseCase(userRepository)

    private val getLocationsUseCase = GetLocationsUseCase(userRepository)
    private val getFavoritesUseCase = GetFavoritesUseCase(userRepository)
    private val getFriendsUseCase = GetFriendsUseCase(userRepository)

    // the one logged in user - every screen reads this
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // what the friend search box is currently showing
    private val _friendResults = MutableStateFlow<List<Friend>>(emptyList())
    val friendResults: StateFlow<List<Friend>> = _friendResults.asStateFlow()

    // one off messages for a toast or snackbar
    private val _message = MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message.asSharedFlow()

    // User--------------------------------------

    fun loadUser(userId: String) {
        viewModelScope.launch {
            val userResult = getUserByIdUseCase(userId)

            if (userResult.isFailure) {
                _message.emit(userResult.exceptionOrNull()?.message ?: "Could not load user")
                return@launch
            }

            val loadedUser = userResult.getOrNull() ?: return@launch

            val locations = getLocationsUseCase(loadedUser).getOrNull() ?: emptyList()
            val favorites = getFavoritesUseCase(loadedUser).getOrNull() ?: emptyList()
            val friends = getFriendsUseCase(loadedUser).getOrNull() ?: emptyList()

            val fullUser = loadedUser.copy(
                myLocations = locations,
                favorites = favorites,
                friends = friends
            )

            _currentUser.value = fullUser
            _friendResults.value = friends
            _message.emit("Loaded ${fullUser.name}")
        }
    }

    fun register(name: String, username: String, password: String) {
        viewModelScope.launch {
            val result = createUserUseCase(name, username, password)
            handleUserResult(result)
        }
    }

    fun updateProfile(name: String, username: String) {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            val edited = user.copy(name = name, username = username)
            val result = updateUserUseCase(edited)
            handleUserResult(result)
        }
    }

    fun searchFriend(query: String) {
        val user = _currentUser.value ?: return
        _friendResults.value = searchFriendUseCase(user, query)
    }

    // Saved locations---------------------------

    fun addLocation(name: String, lat: Double, long: Double, address: String) {
        val user = _currentUser.value ?: return
        // no id yet - the server assigns one when it saves
        val newLocation = Location(id = null, name = name, lat = lat, long = long, address = address)

        viewModelScope.launch {
            val result = addLocationUseCase(user, newLocation)
            handleUserResult(result)
        }
    }

    fun updateLocation(location: Location) {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            val result = updateLocationUseCase(user, location)
            handleUserResult(result)
        }
    }

    fun deleteLocation(locationId: String) {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            val result = deleteLocationUseCase(user, locationId)
            handleUserResult(result)
        }
    }

    // Favorites---------------------------------

    fun addFavorite(location: Location) {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            val result = addFavoriteUseCase(user, location)
            handleUserResult(result)
        }
    }

    fun deleteFavorite(locationId: String) {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            val result = deleteFavoriteUseCase(user, locationId)
            handleUserResult(result)
        }
    }

    // Friends-----------------------------------

    fun addFriend(friendName: String, locationName: String, lat: Double, long: Double, address: String) {
        val user = _currentUser.value ?: return
        val friendLocation = Location(id = null, name = locationName, lat = lat, long = long, address = address)

        viewModelScope.launch {
            val result = addFriendUseCase(user, friendName, friendLocation)
            handleUserResult(result)
        }
    }

    fun updateFriendLocation(friend: Friend, location: Location) {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            val result = updateFriendLocationUseCase(user, friend, location)
            handleUserResult(result)
        }
    }

    fun deleteFriend(friendId: String) {
        val user = _currentUser.value ?: return

        viewModelScope.launch {
            val result = deleteFriendUseCase(user, friendId)
            handleUserResult(result)
        }
    }

    // ------------------------------------------

    private suspend fun handleUserResult(result: Result<User>) {
        if (result.isSuccess) {
            val user = result.getOrNull()
            _currentUser.value = user
            _friendResults.value = user?.friends ?: emptyList()
        } else {
            val error = result.exceptionOrNull()
            _message.emit(error?.message ?: "Something went wrong")
        }
    }
}
