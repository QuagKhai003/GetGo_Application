package com.quangkhai.getgo_application.presentation.ui.split

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.quangkhai.getgo_application.domain.model.Bill
import com.quangkhai.getgo_application.domain.usecase.bill.SplitCalculator
import com.quangkhai.getgo_application.presentation.ui.shared.PillButton
import com.quangkhai.getgo_application.ui.theme.GetGoTheme

@Composable
fun BillDetailDialog(bill: Bill, onDismiss: () -> Unit) {
    val shares = SplitCalculator.sharesForBill(bill)
    val locationText = bill.location?.let { location ->
        val label = location.address.ifBlank { location.name }
        "$label - ${location.lat}, ${location.long}"
    } ?: "—"

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(bill.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            InfoLine("Amount", formatVnd(bill.amount))
            InfoLine("Date", bill.date.ifBlank { "—" })
            InfoLine("Location", locationText)
            InfoLine("Split", if (bill.splitEqually) "Equal" else "Unequal")

            Divider()
            Text("Paid by", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            if (bill.paidBy.isEmpty()) {
                Text("— no payer entered (this bill won't settle)", color = GetGoTheme.colors.dangerous, fontSize = 12.sp)
            } else {
                bill.paidBy.forEach { (name, amount) -> InfoLine(name, formatVnd(amount)) }
            }

            Divider()
            Text("Each owes", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            bill.participants.forEach { person -> InfoLine(person, formatVnd(shares[person] ?: 0.0)) }

            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                PillButton("Close", onClick = onDismiss)
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = GetGoTheme.colors.labelColor, fontSize = 13.sp)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Divider() {
    HorizontalDivider(thickness = 1.dp, color = GetGoTheme.colors.outlineElements, modifier = Modifier.padding(vertical = 4.dp))
}
