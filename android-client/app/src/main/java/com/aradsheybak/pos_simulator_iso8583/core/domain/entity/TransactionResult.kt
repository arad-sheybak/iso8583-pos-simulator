package com.aradsheybak.pos_simulator_iso8583.core.domain.entity

data class TransactionResult(val mti: String,
                             val responseCode: String,
                             val responseMessage: String,
                             val rawRequest: String = "",
                             val rawResponse: String = ""
)
