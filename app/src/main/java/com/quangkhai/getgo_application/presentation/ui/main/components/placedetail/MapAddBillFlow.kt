package com.quangkhai.getgo_application.presentation.ui.main.components.placedetail

import androidx.compose.runtime.Composable
import com.quangkhai.getgo_application.domain.model.Bill
import com.quangkhai.getgo_application.domain.model.BillGroup
import com.quangkhai.getgo_application.domain.model.Location
import com.quangkhai.getgo_application.presentation.ui.split.AddBillDialog
import com.quangkhai.getgo_application.presentation.ui.split.GroupPickerDialog

// "Add bill" from a place sheet: pick a group, then fill the bill (location prefilled).
// Nothing shows until `place` is set.
@Composable
fun MapAddBillFlow(
    place: Location?,
    group: BillGroup?,
    groups: List<BillGroup>,
    onPickGroup: (BillGroup) -> Unit,
    onAddBill: (BillGroup, Bill) -> Unit,
    onDismiss: () -> Unit
) {
    if (place == null) return

    if (group == null) {
        GroupPickerDialog(groups = groups, onPick = onPickGroup, onDismiss = onDismiss)
    } else {
        AddBillDialog(
            people = group.people,
            location = place,
            onAdd = { bill -> onAddBill(group, bill) },
            onDismiss = onDismiss
        )
    }
}
