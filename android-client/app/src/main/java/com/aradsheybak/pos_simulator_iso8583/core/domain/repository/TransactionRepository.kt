package com.aradsheybak.pos_simulator_iso8583.core.domain.repository

import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.Transaction
import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.TransactionResult

interface TransactionRepository {
    suspend fun sendTransaction(transaction: Transaction): TransactionResult

    suspend fun validateTransaction(transaction: Transaction): Boolean
}