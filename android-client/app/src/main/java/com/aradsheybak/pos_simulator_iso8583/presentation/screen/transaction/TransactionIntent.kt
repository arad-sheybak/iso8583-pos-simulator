package com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction

sealed class TransactionIntent {
    data class PanChanged(val pan: String) : TransactionIntent()
    data class AmountChanged(val amount: String) : TransactionIntent()
    data class PinChanged(val pin: String) : TransactionIntent()
    object SendTransaction : TransactionIntent()
    object ClearError : TransactionIntent()

}