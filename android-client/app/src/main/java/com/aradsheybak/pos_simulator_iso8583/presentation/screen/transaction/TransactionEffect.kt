package com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction

import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.TransactionResult

sealed class TransactionEffect {
    data class ShowError(val message: String) : TransactionEffect()
    data class TransactionSuccess(val result: TransactionResult) : TransactionEffect()

}