package com.aradsheybak.pos_simulator_iso8583.core.domain.usecase

import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.Transaction

class ValidateTransactionUseCase {
    operator fun invoke(transaction: Transaction): Boolean {
        return when {
            transaction.pan.length !in 13..19 -> false
            transaction.amount.isEmpty() -> false
            transaction.pin.isNotEmpty() && transaction.pin.length !in 4..6 -> false
            else -> true
        }
    }
}