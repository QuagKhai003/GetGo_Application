package com.quangkhai.getgo_application.presentation.ui.split

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.quangkhai.getgo_application.domain.model.BillGroup
import com.quangkhai.getgo_application.presentation.ui.shared.PillButton
import com.quangkhai.getgo_application.presentation.ui.shared.PillStyle
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

// pick which bill group to add this place's bill to
@Composable
fun GroupPickerDialog(
    groups: List<BillGroup>,
    onPick: (BillGroup) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Add bill to which group?", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            if (groups.isEmpty()) {
                Text(
                    "No groups yet. Create one in Split Bill first.",
                    color = GetGoTheme.colors.labelColor,
                    fontSize = 13.sp
                )
            } else {
                Column(
                    modifier = Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groups.forEach { group ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.5.dp, GetGoTheme.colors.outlineElements, RoundedCornerShape(10.dp))
                                .clickable { onPick(group) }
                                .padding(horizontal = 12.dp, vertical = 12.dp)
                        ) {
                            Column {
                                Text(group.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    "${group.people.size} people · ${group.bills.size} bills",
                                    color = GetGoTheme.colors.labelColor,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                PillButton("Cancel", onClick = onDismiss, style = PillStyle.Outline)
            }
        }
    }
}
