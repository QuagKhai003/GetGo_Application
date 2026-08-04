package com.quangkhai.getgo_application.domain.usecase.bill

import com.quangkhai.getgo_application.domain.model.Bill
import com.quangkhai.getgo_application.domain.model.BillGroup

// each person's position in the group after every bill
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
        var d = 0
        var c = 0
        while (d < debtors.size && c < creditors.size) {
            val pay = minOf(oweLeft[d], backLeft[c])
            result.add(Settlement(from = debtors[d].name, to = creditors[c].name, amount = pay))
            oweLeft[d] -= pay
            backLeft[c] -= pay
            if (oweLeft[d] < 0.01) d++
            if (backLeft[c] < 0.01) c++
        }
        return result
    }
}
