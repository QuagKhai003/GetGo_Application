package com.quangkhai.getgo_application.presentation.ui.split

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quangkhai.getgo_application.domain.model.Bill
import com.quangkhai.getgo_application.domain.model.BillGroup
import com.quangkhai.getgo_application.domain.usecase.bill.PersonBalance
import com.quangkhai.getgo_application.domain.usecase.bill.SplitCalculator
import com.quangkhai.getgo_application.presentation.ui.shared.AppCard
import com.quangkhai.getgo_application.presentation.ui.shared.AppTopBar
import com.quangkhai.getgo_application.presentation.ui.shared.Avatar
import com.quangkhai.getgo_application.presentation.ui.shared.PillButton
import com.quangkhai.getgo_application.presentation.ui.shared.SectionLabel
import com.quangkhai.getgo_application.presentation.ui.shared.StatusChip
import com.quangkhai.getgo_application.presentation.viewmodel.BillViewModel
import com.quangkhai.getgo_application.presentation.viewmodel.UserViewModel
import com.quangkhai.getgo_application.ui.theme.GetGoTheme
import kotlin.math.abs

fun formatVnd(amount: Double): String = "₫ " + String.format(java.util.Locale.US, "%,d", amount.toLong())

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BillGroupScreen(
    groupId: String,
    onBack: () -> Unit,
    userViewModel: UserViewModel,
    viewModel: BillViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current
    val currentUser by userViewModel.currentUser.collectAsState()
    val name by viewModel.name.collectAsState()
    val people by viewModel.people.collectAsState()
    val bills by viewModel.bills.collectAsState()

    val total = bills.sumOf { it.amount }
    val balances = remember(people, bills) {
        SplitCalculator.balances(BillGroup(name = "", people = people, bills = bills))
    }
    val settlements = remember(balances) { SplitCalculator.settlements(balances) }

    var newPerson by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    var detailBill by remember { mutableStateOf<Bill?>(null) }

    // a brand new group is created (name + people) first; bills are added after it exists
    val isNew = groupId == "new"

    // existing groups persist every edit right away; a new group saves via "Create"
    val autosave = { if (!isNew) userViewModel.updateBillGroup(viewModel.toGroup()) }
    val topAction: (@Composable () -> Unit)? = if (isNew) {
        {
            Text(
                "Create",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    userViewModel.addBillGroup(viewModel.toGroup())
                    onBack()
                }
            )
        }
    } else null

    // load the group this screen was opened for (unless it's a brand new one)
    var loaded by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(currentUser) {
        if (!loaded) {
            if (groupId != "new") {
                currentUser?.billGroups?.firstOrNull { it.id == groupId }?.let { viewModel.load(it) }
            }
            loaded = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AppTopBar(
            title = if (isNew) "New group" else name,
            onBack = onBack,
            trailing = topAction
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (isNew) {
                    AppCard {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.setName(it) },
                            label = { Text("Group name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    TotalBanner(total = formatVnd(total), people = "${people.size} people")
                }

                Column {
                    SectionLabel("People")
                    AppCard {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            people.forEach { person ->
                                PersonChip(person, removable = person != "You", onRemove = { viewModel.removePerson(person); autosave() })
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newPerson,
                                onValueChange = { newPerson = it },
                                label = { Text("Add name") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            PillButton("Add", onClick = {
                                viewModel.addPerson(newPerson)
                                newPerson = ""
                                autosave()
                                focusManager.clearFocus()
                            })
                        }
                    }
                }

                if (!isNew) {
                Column {
                    SectionLabel("Bills")
                    if (bills.isEmpty()) {
                        AppCard { Text("No bills yet. Tap + to add one.", color = GetGoTheme.colors.labelColor, fontSize = 13.sp) }
                    } else {
                        AppCard(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)) {
                            bills.forEachIndexed { index, bill ->
                                if (index > 0) RowDivider()
                                val mode = if (bill.splitEqually) "Equal" else "Unequal"
                                val date = if (bill.date.isNotBlank()) " · ${bill.date}" else ""
                                val place = bill.location?.name?.let { " · $it" } ?: ""
                                BillRow(
                                    name = bill.name,
                                    sub = "$mode · ${bill.participants.size} people$date$place",
                                    amount = formatVnd(bill.amount),
                                    onClick = { detailBill = bill },
                                    onDelete = { viewModel.removeBill(bill.id); autosave() }
                                )
                            }
                        }
                    }
                }

                Column {
                    SectionLabel("Who owes what")
                    AppCard(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)) {
                        balances.forEachIndexed { index, balance ->
                            if (index > 0) RowDivider()
                            BalanceRow(balance)
                        }
                    }
                }

                Column {
                    SectionLabel("Settle up · who pays whom")
                    AppCard(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)) {
                        if (settlements.isEmpty()) {
                            Text(
                                "All settled.",
                                color = GetGoTheme.colors.labelColor,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            settlements.forEachIndexed { index, settlement ->
                                if (index > 0) RowDivider()
                                SettleRow(settlement.from, settlement.to, formatVnd(settlement.amount))
                            }
                        }
                    }
                }
                }

                Spacer(modifier = Modifier.height(70.dp))
            }

            if (!isNew) {
                FloatingActionButton(
                    onClick = { showAdd = true },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add bill")
                }
            }
        }
    }

    if (showAdd) {
        AddBillDialog(
            people = people,
            onAdd = {
                viewModel.addBill(it)
                showAdd = false
                autosave()
            },
            onDismiss = { showAdd = false }
        )
    }

    detailBill?.let { bill ->
        BillDetailDialog(bill = bill, onDismiss = { detailBill = null })
    }
}

@Composable
private fun TotalBanner(total: String, people: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GetGoTheme.colors.quaternary)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Total bill", color = GetGoTheme.colors.labelColor, fontSize = 12.sp)
            Text(total, color = MaterialTheme.colorScheme.onSurface, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Split between", color = GetGoTheme.colors.labelColor, fontSize = 12.sp)
            Text(people, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PersonChip(name: String, removable: Boolean, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(GetGoTheme.colors.fillElements)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        if (removable) {
            Spacer(Modifier.width(6.dp))
            Text("✕", color = GetGoTheme.colors.labelColor, fontSize = 12.sp, modifier = Modifier.clickable { onRemove() })
        }
    }
}

@Composable
private fun BillRow(name: String, sub: String, amount: String, onClick: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🧾", fontSize = 14.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(name, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(sub, color = GetGoTheme.colors.labelColor, fontSize = 12.sp)
        }
        Text(amount, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text("✕", color = GetGoTheme.colors.dangerous, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onDelete() })
    }
}

@Composable
private fun BalanceRow(balance: PersonBalance) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Avatar(initial = balance.name.take(1), size = 34.dp, fontSize = 13.sp)
        Text(balance.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, modifier = Modifier.weight(1f))
        if (abs(balance.net) < 1.0) {
            StatusChip(text = "settled", background = GetGoTheme.colors.labelColor)
        } else if (balance.net > 0) {
            Text(formatVnd(balance.net), color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            StatusChip(text = "owes", background = GetGoTheme.colors.dangerous)
        } else {
            Text(formatVnd(-balance.net), color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            StatusChip(text = "gets back", background = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun SettleRow(from: String, to: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(from, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Text("→", color = GetGoTheme.colors.labelColor, fontSize = 15.sp)
        Text(to, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Text(amount, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(thickness = 1.dp, color = GetGoTheme.colors.outlineElements)
}
