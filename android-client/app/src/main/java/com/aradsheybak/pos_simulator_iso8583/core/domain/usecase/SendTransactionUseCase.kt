package com.aradsheybak.pos_simulator_iso8583.core.domain.usecase

import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.Transaction
import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.TransactionResult
import com.aradsheybak.pos_simulator_iso8583.core.domain.repository.TransactionRepository

class SendTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): TransactionResult {
        // first validation
        if (!repository.validateTransaction(transaction)) {
            return TransactionResult(
                mti = "ERROR",
                responseCode = "98",
                responseMessage = "Invalid transaction data"
            )
        }

        return repository.sendTransaction(transaction)
    }
}