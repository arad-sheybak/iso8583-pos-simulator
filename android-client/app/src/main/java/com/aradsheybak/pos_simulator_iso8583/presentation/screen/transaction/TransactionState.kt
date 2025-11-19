package com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction

import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.TransactionResult

data class TransactionState(
    val pan: String = "",
    val amount: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val transactionResult: TransactionResult? = null,
    val error: String? = null
)
