package com.aradsheybak.pos_simulator_iso8583.core.domain.entity

data class Transaction(    val pan: String,
                           val amount: String,
                           val pin: String = "",
                           val terminalId: String = "12345678",
                           val merchantId: String = "123456789012345")
