package com.quangkhai.getgo_application.presentation.ui.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quangkhai.getgo_application.domain.model.Friend
import com.quangkhai.getgo_application.presentation.viewmodel.UserViewModel
import com.quangkhai.getgo_application.presentation.ui.friend.components.PickLocationSheet
import com.quangkhai.getgo_application.presentation.ui.shared.AppCard
import com.quangkhai.getgo_application.presentation.ui.shared.AppTopBar
import com.quangkhai.getgo_application.presentation.ui.shared.Avatar
import com.quangkhai.getgo_application.presentation.ui.shared.SectionLabel
import com.quangkhai.getgo_application.ui.theme.GetGoTheme
import com.quangkhai.getgo_application.ui.theme.GetGo_ApplicationTheme

@Composable
fun FriendListScreen(
    onBack: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    val friends by userViewModel.friendResults.collectAsState()
    var query by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var newFriendName by remember { mutableStateOf("") }
    var showPicker by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AppTopBar(title = "Your Friend List", onBack = onBack)

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SearchField(
                    value = query,
                    onValueChange = { query = it; userViewModel.searchFriend(it) },
                    hint = "Search friends by name or email…"
                )

                Column {
                    SectionLabel("${friends.size} friends")
                    AppCard(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)) {
                        friends.forEachIndexed { index, friend ->
                            FriendRow(
                                friend = friend,
                                onEditAddress = {
                                    userViewModel.startEditFriend(friend)
                                    showPicker = true
                                },
                                onRemove = { friend.id?.let { userViewModel.deleteFriend(it) } }
                            )
                            if (index < friends.lastIndex) {
                                HorizontalDivider(thickness = 1.dp, color = GetGoTheme.colors.outlineElements)
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add friend")
            }

            if (showAddDialog) {
                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    title = { Text("Add friend") },
                    text = {
                        OutlinedTextField(
                            value = newFriendName,
                            onValueChange = { newFriendName = it },
                            label = { Text("Friend name") },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (newFriendName.isNotBlank()) {
                                userViewModel.startAddFriend(newFriendName)
                                newFriendName = ""
                                showAddDialog = false
                                showPicker = true
                            }
                        }) { Text("Next") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                    }
                )
            }
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
}

@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit, hint: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(23.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, GetGoTheme.colors.outlineElements, RoundedCornerShape(23.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Filled.Search,
            contentDescription = null,
            tint = GetGoTheme.colors.labelColor,
            modifier = Modifier.size(18.dp)
        )
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(hint, color = GetGoTheme.colors.labelColor, fontSize = 13.sp)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FriendRow(
    friend: Friend,
    onEditAddress: () -> Unit,
    onRemove: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    val address = friend.location.address.ifBlank { friend.location.name }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Avatar(initial = friend.name.take(1))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = friend.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text("📍 $address", color = GetGoTheme.colors.labelColor, fontSize = 12.sp)
        }
        Box {
            Icon(
                Icons.Filled.MoreVert,
                contentDescription = "More",
                tint = GetGoTheme.colors.labelColor,
                modifier = Modifier.clickable { menuOpen = true }
            )
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text("Edit address") },
                    onClick = { menuOpen = false; onEditAddress() }
                )
                DropdownMenuItem(
                    text = { Text("Remove friend", color = GetGoTheme.colors.dangerous) },
                    onClick = { menuOpen = false; onRemove() }
                )
            }
        }
    }
}

@Preview
@Composable
private fun FriendListScreenPreview() {
    GetGo_ApplicationTheme {
        FriendListScreen(onBack = { })
    }
}
