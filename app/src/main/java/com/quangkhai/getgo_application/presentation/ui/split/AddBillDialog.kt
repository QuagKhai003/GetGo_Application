package com.quangkhai.getgo_application.presentation.ui.split

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.quangkhai.getgo_application.domain.model.Bill
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.presentation.ui.shared.PillButton
import com.quangkhai.getgo_application.presentation.ui.shared.PillStyle
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

@Composable
fun AddBillDialog(
    people: List<String>,
    onAdd: (Bill) -> Unit,
    onDismiss: () -> Unit,
    location: Location? = null
) {
    val today = remember { java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.US).format(java.util.Date()) }
    var name by remember { mutableStateOf(location?.name ?: "") }
    var amountText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(today) }
    var equal by remember { mutableStateOf(true) }
    // everyone in the group is included by default; uncheck anyone not in this bill
    val participants = remember { mutableStateListOf<String>().apply { addAll(people) } }
    val paid = remember { mutableStateMapOf<String, String>() }    // name -> paid amount text
    val shares = remember { mutableStateMapOf<String, String>() }  // name -> owed amount text (unequal)

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val paidSum = people.sumOf { paid[it]?.toDoubleOrNull() ?: 0.0 }
    val shareSum = participants.sumOf { shares[it]?.toDoubleOrNull() ?: 0.0 }
    // paid must cover the whole bill; for unequal, shares must too - else it can't settle
    val paidOk = kotlin.math.abs(paidSum - amount) < 1.0
    val sharesOk = equal || kotlin.math.abs(shareSum - amount) < 1.0
    val canAdd = name.isNotBlank() && amount > 0 && participants.isNotEmpty() && paidOk && sharesOk

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Add bill", color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Bill name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Total amount") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it },
                label = { Text("Date (dd-MM-yyyy)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (location != null) {
                OutlinedTextField(
                    value = "${location.address.ifBlank { location.name }} - ${location.lat}, ${location.long}",
                    onValueChange = {},
                    label = { Text("Location") },
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PillButton("Equal", onClick = { equal = true }, style = if (equal) PillStyle.Primary else PillStyle.Outline)
                PillButton("Unequal", onClick = { equal = false }, style = if (!equal) PillStyle.Primary else PillStyle.Outline)
            }

            Text("Who's in + who paid", color = GetGoTheme.colors.labelColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)

            people.forEach { person ->
                val isPart = person in participants
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isPart,
                            onCheckedChange = { checked ->
                                if (checked) participants.add(person) else participants.remove(person)
                            }
                        )
                        Text(person, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    }
                    if (isPart) {
                        Row(
                            modifier = Modifier.padding(start = 12.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = paid[person] ?: "",
                                onValueChange = { paid[person] = it },
                                label = { Text("paid") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            if (!equal) {
                                OutlinedTextField(
                                    value = shares[person] ?: "",
                                    onValueChange = { shares[person] = it },
                                    label = { Text("owes") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            val paidColor = if (paidSum == amount) GetGoTheme.colors.labelColor else GetGoTheme.colors.dangerous
            Text("Paid ${paidSum.toLong()} of ${amount.toLong()}", color = paidColor, fontSize = 12.sp)
            if (!equal) {
                val shareColor = if (shareSum == amount) GetGoTheme.colors.labelColor else GetGoTheme.colors.dangerous
                Text("Shares ${shareSum.toLong()} of ${amount.toLong()}", color = shareColor, fontSize = 12.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                PillButton("Cancel", onClick = onDismiss, style = PillStyle.Outline)
                Spacer(Modifier.width(8.dp))
                if (canAdd) {
                    PillButton("Add bill", onClick = {
                        val paidBy = people.mapNotNull { p ->
                            val v = paid[p]?.toDoubleOrNull() ?: 0.0
                            if (v > 0) p to v else null
                        }.toMap()
                        val custom = if (equal) emptyMap()
                        else participants.associateWith { shares[it]?.toDoubleOrNull() ?: 0.0 }
                        onAdd(
                            Bill(
                                name = name.trim(),
                                amount = amount,
                                paidBy = paidBy,
                                splitEqually = equal,
                                participants = participants.toList(),
                                customShares = custom,
                                location = location,
                                date = dateText.trim()
                            )
                        )
                    }, style = PillStyle.Primary)
                }
            }
        }
    }
}
