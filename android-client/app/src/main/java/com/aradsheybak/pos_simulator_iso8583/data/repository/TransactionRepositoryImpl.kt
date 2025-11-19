package com.aradsheybak.pos_simulator_iso8583.data.repository

import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.ISO8583Config
import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.Transaction
import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.TransactionResult
import com.aradsheybak.pos_simulator_iso8583.core.domain.repository.TransactionRepository
import com.aradsheybak.pos_simulator_iso8583.data.datasource.remote.ISOSocketDataSource
import com.aradsheybak.pos_simulator_iso8583.data.mapper.TransactionMapper

class TransactionRepositoryImpl(
    private val socketDataSource: ISOSocketDataSource,
    private val transactionMapper: TransactionMapper,
    private val config: ISO8583Config
) : TransactionRepository {

    override suspend fun sendTransaction(transaction: Transaction): TransactionResult {
        return try {
            // 1. Map transaction to ISO8583 message
            val isoMessage = transactionMapper.mapToISO8583Message(transaction)

            // 2. Send to server via socket
            val response = socketDataSource.sendMessage(
                config.serverHost,
                config.serverPort,
                isoMessage
            )

            // 3. Map response to domain result
            transactionMapper.mapToTransactionResult(response, transaction)

        } catch (e: Exception) {
            TransactionResult(
                mti = "ERROR",
                responseCode = "99",
                responseMessage = "Network error: ${e.message}"
            )
        }
    }

    override suspend fun validateTransaction(transaction: Transaction): Boolean {
        return when {
            transaction.pan.length !in 13..19 -> false
            transaction.amount.isEmpty() -> false
            transaction.pin.isNotEmpty() && transaction.pin.length !in 4..6 -> false
            else -> true
        }
    }
}