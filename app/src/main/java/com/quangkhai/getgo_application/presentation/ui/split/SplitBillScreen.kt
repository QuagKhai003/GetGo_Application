package com.quangkhai.getgo_application.presentation.ui.split

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quangkhai.getgo_application.presentation.ui.shared.AppCard
import com.quangkhai.getgo_application.presentation.ui.shared.AppTopBar
import com.quangkhai.getgo_application.presentation.ui.shared.Avatar
import com.quangkhai.getgo_application.presentation.ui.shared.PillButton
import com.quangkhai.getgo_application.presentation.ui.shared.PillStyle
import com.quangkhai.getgo_application.presentation.ui.shared.SectionLabel
import com.quangkhai.getgo_application.presentation.ui.shared.StatusChip
import com.quangkhai.getgo_application.ui.theme.GetGoTheme
import com.quangkhai.getgo_application.ui.theme.GetGo_ApplicationTheme

@Composable
fun SplitBillScreen(onBack: () -> Unit) {
    var addOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AppTopBar(title = "Split Bill · Lunch @ Phở 24", onBack = onBack)

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TotalBanner(total = "₫ 480,000", people = "4 people")

                Column {
                    SectionLabel("Paid by")
                    AppCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Avatar(initial = "K")
                            Column(modifier = Modifier.weight(1f)) {
                                Text("You paid", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("₫ 480,000 upfront", color = GetGoTheme.colors.labelColor, fontSize = 12.sp)
                            }
                            Text("change ›", color = GetGoTheme.colors.labelColor, fontSize = 12.sp)
                        }
                    }
                }

                Column {
                    SectionLabel("List bills · items chosen to split")
                    AppCard(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)) {
                        BillRow(name = "Lunch @ Phở 24", sub = "Equal · 4 people", amount = "₫480,000")
                        RowDivider()
                        BillRow(name = "Coffee @ Highlands", sub = "Unequal · 3 people", amount = "₫165,000")
                        RowDivider()
                        BillRow(name = "Taxi home", sub = "Equal · 2 people", amount = "₫90,000")
                    }
                }

                Column {
                    SectionLabel("Split detail (equal)")
                    AppCard(contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp)) {
                        SplitRow("An Tran", "₫120,000", "owes you", GetGoTheme.colors.dangerous)
                        RowDivider()
                        SplitRow("Binh Le", "₫120,000", "owes you", GetGoTheme.colors.dangerous)
                        RowDivider()
                        SplitRow("Chi Pham", "₫120,000", "paid ✓", MaterialTheme.colorScheme.primary)
                        RowDivider()
                        SplitRow("You", "₫120,000", "your share", GetGoTheme.colors.labelColor)
                    }
                }

                Spacer(modifier = Modifier.height(70.dp))
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (addOpen) {
                    PillButton(text = "⚖️  Equally split", onClick = { }, style = PillStyle.Outline)
                    PillButton(text = "🔀  Unequally split", onClick = { }, style = PillStyle.Outline)
                }
                FloatingActionButton(
                    onClick = { addOpen = !addOpen },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add bill")
                }
            }
        }
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
private fun BillRow(name: String, sub: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
    }
}

@Composable
private fun SplitRow(name: String, amount: String, chip: String, chipColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Avatar(initial = name.take(1), size = 34.dp, fontSize = 13.sp)
        Text(name, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(amount, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        StatusChip(text = chip, background = chipColor)
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(thickness = 1.dp, color = GetGoTheme.colors.outlineElements)
}

@Preview
@Composable
private fun SplitBillScreenPreview() {
    GetGo_ApplicationTheme {
        SplitBillScreen(onBack = { })
    }
}
