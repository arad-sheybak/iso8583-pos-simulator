package com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.Transaction
import com.aradsheybak.pos_simulator_iso8583.core.domain.usecase.SendTransactionUseCase
import com.aradsheybak.pos_simulator_iso8583.core.domain.usecase.ValidateTransactionUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class TransactionViewModel(
    private val sendTransactionUseCase: SendTransactionUseCase,
    private val validateTransactionUseCase: ValidateTransactionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionState())
    val state: StateFlow<TransactionState> = _state

    private val _effects = MutableSharedFlow<TransactionEffect>()
    val effects: SharedFlow<TransactionEffect> = _effects

    fun processIntent(intent: TransactionIntent) {
        when (intent) {
            is TransactionIntent.PanChanged -> {
                _state.update { it.copy(pan = intent.pan.filter { char -> char.isDigit() }) }
            }
            is TransactionIntent.AmountChanged -> {
                _state.update { it.copy(amount = intent.amount.filter { char -> char.isDigit() }) }
            }
            is TransactionIntent.PinChanged -> {
                _state.update { it.copy(pin = intent.pin.filter { char -> char.isDigit() }.take(6)) }
            }
            TransactionIntent.SendTransaction -> sendTransaction()
            TransactionIntent.ClearError -> _state.update { it.copy(error = null) }
        }
    }

    private fun sendTransaction() {
        val currentState = _state.value
        val transaction = Transaction(
            pan = currentState.pan,
            amount = currentState.amount,
            pin = currentState.pin
        )

        if (!validateTransactionUseCase(transaction)) {
            viewModelScope.launch {
                _effects.emit(TransactionEffect.ShowError("Invalid transaction data"))
            }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val result = sendTransactionUseCase(transaction)
                _state.update {
                    it.copy(
                        isLoading = false,
                        transactionResult = result
                    )
                }
                _effects.emit(TransactionEffect.TransactionSuccess(result))
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Transaction failed: ${e.message}"
                    )
                }
                _effects.emit(TransactionEffect.ShowError("Transaction failed: ${e.message}"))
            }
        }
    }
}

// Koin Module for ViewModel
val viewModelModule = module {
    viewModel { TransactionViewModel(get(), get()) }
}