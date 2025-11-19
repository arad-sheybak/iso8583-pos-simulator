package com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction.layout.TransactionLayout
import org.koin.androidx.compose.koinViewModel

@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Handle side effects
    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TransactionEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is TransactionEffect.TransactionSuccess -> {
                    // Handle success if needed
                }
            }
        }
    }

    // Main layout composition
    TransactionLayout(
        state = state,
        onPanChanged = { viewModel.processIntent(TransactionIntent.PanChanged(it)) },
        onAmountChanged = { viewModel.processIntent(TransactionIntent.AmountChanged(it)) },
        onPinChanged = { viewModel.processIntent(TransactionIntent.PinChanged(it)) },
        onSendTransaction = { viewModel.processIntent(TransactionIntent.SendTransaction) }
    )
}