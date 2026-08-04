package com.quangkhai.getgo_application.presentation.ui.split

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.domain.model.BillGroup
import com.quangkhai.getgo_application.presentation.ui.shared.AppTopBar
import com.quangkhai.getgo_application.presentation.viewmodel.UserViewModel
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

@Composable
fun SplitBillScreen(
    onBack: () -> Unit,
    userViewModel: UserViewModel,
    onOpenGroup: (String) -> Unit
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val groups = currentUser?.billGroups ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AppTopBar(title = "Bill Groups", onBack = onBack)

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (groups.isEmpty()) {
                    Text(
                        "No groups yet. Tap + to create your first split group.",
                        color = GetGoTheme.colors.labelColor,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    groups.forEach { group ->
                        GroupCard(group, onOpen = onOpenGroup, onDelete = { userViewModel.deleteBillGroup(it) })
                    }
                }
                Spacer(Modifier.height(70.dp))
            }

            FloatingActionButton(
                onClick = { onOpenGroup("new") },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "New group")
            }
        }
    }
}

@Composable
private fun GroupCard(group: BillGroup, onOpen: (String) -> Unit, onDelete: (String) -> Unit) {
    val total = group.bills.sumOf { it.amount }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.5.dp, GetGoTheme.colors.outlineElements, RoundedCornerShape(16.dp))
            .clickable { group.id?.let(onOpen) }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(GetGoTheme.colors.fillElements),
            contentAlignment = Alignment.Center
        ) {
            Text("🧾", fontSize = 18.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(group.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(
                "${group.people.size} people · ${group.bills.size} bills · ${formatVnd(total)}",
                color = GetGoTheme.colors.labelColor,
                fontSize = 12.sp
            )
        }
        Text(
            "✕",
            color = GetGoTheme.colors.dangerous,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { group.id?.let(onDelete) }
        )
    }
}
