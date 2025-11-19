package com.aradsheybak.pos_simulator_iso8583.data.mapper

import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.Transaction
import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.TransactionResult

class TransactionMapper {

    fun mapToISO8583Message(transaction: Transaction): ByteArray {
        // This will contain our ISO8583 message building logic
        // We'll implement this later with jPOS
        return byteArrayOf()
    }

    fun mapToTransactionResult(
        response: ByteArray,
        originalTransaction: Transaction
    ): TransactionResult {
        // This will parse the ISO8583 response
        // We'll implement this later with jPOS
        return TransactionResult(
            mti = "0210",
            responseCode = "00",
            responseMessage = "Approved",
            rawResponse = response.toHexString()
        )
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }
}