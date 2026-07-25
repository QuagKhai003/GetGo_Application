package com.quangkhai.getgo_application.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quangkhai.getgo_application.domain.model.Friend
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.presentation.viewmodel.UserViewModel

/**
 * A plain screen for testing the user layer by hand.
 * Not the real UI - it just gives every use case a button.
 */
@Composable
fun UserTestScreen(viewModel: UserViewModel = viewModel()) {

    val user by viewModel.currentUser.collectAsState()
    val friendResults by viewModel.friendResults.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.message.collect { text ->
            snackbarHostState.showSnackbar(text)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text("GetGo - user test screen", style = MaterialTheme.typography.titleLarge)

            LoadUserSection(viewModel)

            HorizontalDivider()

            if (user == null) {
                Text("No user loaded yet. Type an id (try u1) and press Load.")
            } else {
                val loadedUser = user!!

                Text(
                    "${loadedUser.name}  (@${loadedUser.username})",
                    fontWeight = FontWeight.Bold
                )
                Text("id: ${loadedUser.id}")

                HorizontalDivider()
                LocationSection(viewModel, loadedUser.myLocations)

                HorizontalDivider()
                FavoriteSection(viewModel, loadedUser.favorites)

                HorizontalDivider()
                FriendSection(viewModel, friendResults)
            }
        }
    }
}

@Composable
private fun LoadUserSection(viewModel: UserViewModel) {
    var userId by remember { mutableStateOf("9039db26-e8a4-49b0-a6d0-2a876956210f") }
    var newName by remember { mutableStateOf("") }
    var newUsername by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Text("User", style = MaterialTheme.typography.titleMedium)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("user id") },
            modifier = Modifier.weight(1f)
        )
        Button(onClick = { viewModel.loadUser(userId) }) { Text("Load") }
    }

    Text("Register a new user", style = MaterialTheme.typography.bodyMedium)
    OutlinedTextField(
        value = newName,
        onValueChange = { newName = it },
        label = { Text("name") },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = newUsername,
        onValueChange = { newUsername = it },
        label = { Text("username") },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = newPassword,
        onValueChange = { newPassword = it },
        label = { Text("password (6+)") },
        modifier = Modifier.fillMaxWidth()
    )
    Button(
        onClick = {
            viewModel.register(newName, newUsername, newPassword)
            newName = ""
            newUsername = ""
            newPassword = ""
        }
    ) { Text("Register") }
}

@Composable
private fun LocationSection(viewModel: UserViewModel, locations: List<Location>) {
    Text("My locations (${locations.size})", style = MaterialTheme.typography.titleMedium)

    locations.forEach { location ->
        LocationRow(
            location = location,
            onDelete = { location.id?.let { viewModel.deleteLocation(it) } },
            onRename = { newName ->
                viewModel.updateLocation(location.copy(name = newName))
            }
        )
    }

    LocationForm(buttonText = "Add location") { name, lat, long, address ->
        viewModel.addLocation(name, lat, long, address)
    }
}

@Composable
private fun FavoriteSection(viewModel: UserViewModel, favorites: List<Location>) {
    Text("Favorites (${favorites.size})", style = MaterialTheme.typography.titleMedium)

    favorites.forEach { favorite ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(favorite.name, fontWeight = FontWeight.Bold)
                    Text("${favorite.lat}, ${favorite.long}")
                }
                OutlinedButton(
                    onClick = { favorite.id?.let { viewModel.deleteFavorite(it) } }
                ) { Text("Remove") }
            }
        }
    }

    LocationForm(buttonText = "Add favorite") { name, lat, long, address ->
        viewModel.addFavorite(Location(id = null, name = name, lat = lat, long = long, address = address))
    }
}

@Composable
private fun FriendSection(viewModel: UserViewModel, friends: List<Friend>) {
    var query by remember { mutableStateOf("") }
    var friendName by remember { mutableStateOf("") }

    Text("Friends (${friends.size})", style = MaterialTheme.typography.titleMedium)

    OutlinedTextField(
        value = query,
        onValueChange = {
            query = it
            viewModel.searchFriend(it)          // filters in memory, no network
        },
        label = { Text("search saved friends") },
        modifier = Modifier.fillMaxWidth()
    )

    friends.forEach { friend ->
        FriendRow(
            friend = friend,
            onDelete = { friend.id?.let { viewModel.deleteFriend(it) } },
            onSaveLocation = { newLocation ->
                viewModel.updateFriendLocation(friend, newLocation)
            }
        )
    }

    OutlinedTextField(
        value = friendName,
        onValueChange = { friendName = it },
        label = { Text("friend name") },
        modifier = Modifier.fillMaxWidth()
    )
    LocationForm(buttonText = "Add friend") { name, lat, long, address ->
        viewModel.addFriend(friendName, name, lat, long, address)
        friendName = ""
    }
}

@Composable
private fun FriendRow(
    friend: Friend,
    onDelete: () -> Unit,
    onSaveLocation: (Location) -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var lat by remember(friend) { mutableStateOf(friend.location.lat.toString()) }
    var long by remember(friend) { mutableStateOf(friend.location.long.toString()) }
    var address by remember(friend) { mutableStateOf(friend.location.address) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(friend.name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                OutlinedButton(onClick = onDelete) { Text("Remove") }
            }

            if (editing) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = lat,
                        onValueChange = { lat = it },
                        label = { Text("lat") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = long,
                        onValueChange = { long = it },
                        label = { Text("long") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        onSaveLocation(
                            friend.location.copy(
                                lat = lat.toDoubleOrNull() ?: friend.location.lat,
                                long = long.toDoubleOrNull() ?: friend.location.long,
                                address = address
                            )
                        )
                        editing = false
                    }) { Text("Save") }
                    OutlinedButton(onClick = { editing = false }) { Text("Cancel") }
                }
            } else {
                Text("${friend.location.name} (${friend.location.lat}, ${friend.location.long})")
                if (friend.location.address.isNotBlank()) Text(friend.location.address)
                OutlinedButton(onClick = { editing = true }) { Text("Edit location") }
            }
        }
    }
}

@Composable
private fun LocationRow(
    location: Location,
    onDelete: () -> Unit,
    onRename: (String) -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(location.name) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (editing) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("new name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        onRename(editedName)
                        editing = false
                    }) { Text("Save") }
                    OutlinedButton(onClick = { editing = false }) { Text("Cancel") }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(location.name, fontWeight = FontWeight.Bold)
                        Text("${location.lat}, ${location.long}")
                        if (location.address.isNotBlank()) Text(location.address)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        OutlinedButton(onClick = { editing = true }) { Text("Edit") }
                        OutlinedButton(onClick = onDelete) { Text("Delete") }
                    }
                }
            }
        }
    }
}

/**
 * Shared little form: name / lat / long / address plus one button.
 */
@Composable
private fun LocationForm(
    buttonText: String,
    onSubmit: (name: String, lat: Double, long: Double, address: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("10.7769") }
    var long by remember { mutableStateOf("106.7009") }
    var address by remember { mutableStateOf("") }

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("location name") },
        modifier = Modifier.fillMaxWidth()
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = lat,
            onValueChange = { lat = it },
            label = { Text("lat") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = long,
            onValueChange = { long = it },
            label = { Text("long") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
    }
    OutlinedTextField(
        value = address,
        onValueChange = { address = it },
        label = { Text("address") },
        modifier = Modifier.fillMaxWidth()
    )
    Button(
        onClick = {
            val latValue = lat.toDoubleOrNull() ?: 0.0
            val longValue = long.toDoubleOrNull() ?: 0.0
            onSubmit(name, latValue, longValue, address)
            name = ""
            address = ""
        }
    ) { Text(buttonText) }
}
