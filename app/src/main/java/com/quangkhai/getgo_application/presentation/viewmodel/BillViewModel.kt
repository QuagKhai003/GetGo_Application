package com.quangkhai.getgo_application.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.quangkhai.getgo_application.domain.model.Bill
import com.quangkhai.getgo_application.domain.model.BillGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

// Editing state for one bill group. Persisted through UserViewModel / currentUser.
class BillViewModel : ViewModel() {

    // null until this group is saved once (then it's the backend id)
    private var groupId: String? = null

    private val _name = MutableStateFlow("My group")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _people = MutableStateFlow(listOf("You"))
    val people: StateFlow<List<String>> = _people.asStateFlow()

    private val _bills = MutableStateFlow<List<Bill>>(emptyList())
    val bills: StateFlow<List<Bill>> = _bills.asStateFlow()

    fun setName(name: String) {
        _name.value = name
    }

    // load an existing group into the editor
    fun load(group: BillGroup) {
        groupId = group.id
        _name.value = group.name
        _people.value = group.people
        _bills.value = group.bills
    }

    // snapshot the current editor state as a group (id null = never saved)
    fun toGroup(): BillGroup = BillGroup(
        id = groupId,
        name = _name.value,
        people = _people.value,
        bills = _bills.value
    )

    fun addPerson(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotEmpty() && trimmed !in _people.value) {
            _people.value = _people.value + trimmed
        }
    }

    fun removePerson(name: String) {
        _people.value = _people.value - name
    }

    fun addBill(bill: Bill) {
        _bills.value = _bills.value + bill.copy(id = UUID.randomUUID().toString())
    }

    fun removeBill(id: String?) {
        if (id != null) _bills.value = _bills.value.filter { it.id != id }
    }
}
