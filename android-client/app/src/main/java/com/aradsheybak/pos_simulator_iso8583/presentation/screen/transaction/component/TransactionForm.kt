package com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction.TransactionState

@Composable
fun TransactionForm(
    state: TransactionState,
    onPanChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onPinChanged: (String) -> Unit,
    onSendTransaction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
    ) {
        // Card Number Field
        OutlinedTextField(
            value = state.pan,
            onValueChange = onPanChanged,
            label = { Text("Card Number") },
            placeholder = { Text("6037991234567890") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Amount Field
        OutlinedTextField(
            value = state.amount,
            onValueChange = onAmountChanged,
            label = { Text("Amount (Rial)") },
            placeholder = { Text("100000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PIN Field
        OutlinedTextField(
            value = state.pin,
            onValueChange = onPinChanged,
            label = { Text("PIN (Optional)") },
            placeholder = { Text("1234") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Send Button
        Button(
            onClick = onSendTransaction,
            enabled = !state.isLoading && state.pan.length in 13..19 && state.amount.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Secure",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Secure Transaction")
            }
        }
    }
}