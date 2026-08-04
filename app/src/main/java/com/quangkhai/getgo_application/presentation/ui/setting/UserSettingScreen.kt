package com.quangkhai.getgo_application.presentation.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quangkhai.getgo_application.presentation.viewmodel.UserViewModel
import com.quangkhai.getgo_application.presentation.ui.friend.components.PickLocationSheet
import com.quangkhai.getgo_application.presentation.ui.shared.AppCard
import com.quangkhai.getgo_application.presentation.ui.shared.AppTopBar
import com.quangkhai.getgo_application.presentation.ui.shared.Avatar
import com.quangkhai.getgo_application.presentation.ui.shared.PillButton
import com.quangkhai.getgo_application.presentation.ui.shared.PillStyle
import com.quangkhai.getgo_application.presentation.ui.shared.SectionLabel
import com.quangkhai.getgo_application.ui.theme.GetGoTheme
import com.quangkhai.getgo_application.ui.theme.GetGo_ApplicationTheme

@Composable
fun UserSettingScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    val user by userViewModel.currentUser.collectAsState()

    var showProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editUsername by remember { mutableStateOf("") }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var showPicker by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AppTopBar(title = "User Setting", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AppCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Avatar(initial = user?.name?.take(1) ?: "?", size = 72.dp, fontSize = 26.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user?.name ?: "—", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(user?.username ?: "—", color = GetGoTheme.colors.labelColor, fontSize = 12.sp)
                        PillButton(
                            text = "Edit profile",
                            onClick = {
                                editName = user?.name ?: ""
                                editUsername = user?.username ?: ""
                                showProfileDialog = true
                            },
                            modifier = Modifier.padding(top = 8.dp),
                            style = PillStyle.Outline
                        )
                    }
                }
            }

            Column {
                SectionLabel("Account")
                AppCard(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)) {
                    SettingRow(
                        emoji = "👤",
                        label = "Display name",
                        trailing = "${user?.name ?: "—"} ›",
                        onClick = {
                            editName = user?.name ?: ""
                            editUsername = user?.username ?: ""
                            showProfileDialog = true
                        }
                    )
                    RowDivider()
                    SettingRow(
                        emoji = "📍",
                        label = "Your address",
                        trailing = user?.myLocations?.firstOrNull()?.address ?: "—",
                        onClick = {
                            userViewModel.startSetMyAddress()
                            showPicker = true
                        }
                    )
                    RowDivider()
                    SettingRow(
                        emoji = "🔒",
                        label = "Change password",
                        trailing = "›",
                        onClick = { newPassword = ""; showPasswordDialog = true }
                    )
                }
            }

            Column {
                SectionLabel("Favorites · saved places")
                val favorites = user?.favorites ?: emptyList()
                AppCard(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)) {
                    if (favorites.isEmpty()) {
                        Text(
                            "No saved places yet",
                            color = GetGoTheme.colors.labelColor,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        favorites.forEachIndexed { index, fav ->
                            FavoriteRow(
                                name = fav.name,
                                address = fav.address,
                                onRemove = { fav.id?.let { userViewModel.deleteFavorite(it) } }
                            )
                            if (index < favorites.lastIndex) RowDivider()
                        }
                    }
                }
            }

            PillButton(
                text = "Log out",
                onClick = onLogout,
                style = PillStyle.Danger,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

        PickLocationSheet(
            visible = showPicker,
            onPicked = { location ->
                userViewModel.onLocationPicked(location)
                showPicker = false
            },
            onDismiss = { showPicker = false }
        )
    }

    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Edit profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editUsername,
                        onValueChange = { editUsername = it },
                        label = { Text("Username") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    userViewModel.updateProfile(editName, editUsername)
                    showProfileDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Change password") },
            text = {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPassword.length >= 6) {
                        userViewModel.changePassword(newPassword)
                        newPassword = ""
                        showPasswordDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SettingRow(emoji: String, label: String, trailing: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(emoji, fontSize = 15.sp)
        Text(label, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
        Spacer(modifier = Modifier.weight(1f))
        Text(trailing, color = GetGoTheme.colors.labelColor, fontSize = 12.sp)
    }
}

@Composable
private fun FavoriteRow(name: String, address: String, onRemove: () -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .border(1.5.dp, GetGoTheme.colors.quaternary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("★", color = GetGoTheme.colors.quaternary, fontSize = 15.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(name, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(address, color = GetGoTheme.colors.labelColor, fontSize = 12.sp)
        }
        Box {
            Text("›", color = GetGoTheme.colors.labelColor, modifier = Modifier.clickable { menuOpen = true })
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text("Remove", color = GetGoTheme.colors.dangerous) },
                    onClick = { menuOpen = false; onRemove() }
                )
            }
        }
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(thickness = 1.dp, color = GetGoTheme.colors.outlineElements)
}

@Preview
@Composable
private fun UserSettingScreenPreview() {
    GetGo_ApplicationTheme {
        UserSettingScreen(onBack = { }, onLogout = { })
    }
}
