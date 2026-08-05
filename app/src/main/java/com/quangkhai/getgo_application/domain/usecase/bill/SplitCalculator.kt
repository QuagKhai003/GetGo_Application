package com.quangkhai.getgo_application.domain.usecase.bill

import com.quangkhai.getgo_application.domain.model.Bill
import com.quangkhai.getgo_application.domain.model.BillGroup

// This whole file is a Claude Opus 4.8 generated code
// - for calculating the money for split bill feature
data class PersonBalance(
    val name: String,
    val paid: Double,    // total they fronted across all bills
    val share: Double,   // total they should pay across all bills
    val net: Double      // share - paid; positive = owes, negative = is owed
)

// a single "pay this person this much" transfer that settles the group
data class Settlement(
    val from: String,    // the one who owes
    val to: String,      // the one who is owed
    val amount: Double
)

object SplitCalculator {

    // how much each participant owes for a single bill
    fun sharesForBill(bill: Bill): Map<String, Double> {
        if (bill.participants.isEmpty()) return emptyMap()

        if (bill.splitEqually) {
            val each = bill.amount / bill.participants.size
            return bill.participants.associateWith { each }
        }

        return bill.participants.associateWith { name -> bill.customShares[name] ?: 0.0 }
    }

    // net balance per person across every bill in the group
    fun balances(group: BillGroup): List<PersonBalance> {
        return group.people.map { person ->
            var paid = 0.0
            var share = 0.0
            for (bill in group.bills) {
                paid += bill.paidBy[person] ?: 0.0
                share += sharesForBill(bill)[person] ?: 0.0
            }
            PersonBalance(name = person, paid = paid, share = share, net = share - paid)
        }
    }

    // turn the net balances into concrete transfers (who pays whom, how much).
    // greedy: the biggest debtor pays the biggest creditor, repeat.
    fun settlements(balances: List<PersonBalance>): List<Settlement> {
        val debtors = balances.filter { it.net > 0.01 }.sortedByDescending { it.net }
        val creditors = balances.filter { it.net < -0.01 }.sortedByDescending { -it.net }

        val oweLeft = debtors.map { it.net }.toMutableList()
        val backLeft = creditors.map { -it.net }.toMutableList()

        val result = mutableListOf<Settlement>()
        var debtorIndex = 0
        var creditorIndex = 0
        while (debtorIndex < debtors.size && creditorIndex < creditors.size) {
            val pay = minOf(oweLeft[debtorIndex], backLeft[creditorIndex])
            result.add(Settlement(from = debtors[debtorIndex].name, to = creditors[creditorIndex].name, amount = pay))
            oweLeft[debtorIndex] -= pay
            backLeft[creditorIndex] -= pay
            if (oweLeft[debtorIndex] < 0.01) debtorIndex++
            if (backLeft[creditorIndex] < 0.01) creditorIndex++
        }
        return result
    }
}
