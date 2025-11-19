package com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction.TransactionState
import com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction.component.LoadingIndicator
import com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction.component.TransactionForm
import com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction.component.TransactionResultCard

@Composable
fun TransactionLayout(
    state: TransactionState,
    onPanChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onPinChanged: (String) -> Unit,
    onSendTransaction: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val (
            title,
            form,
            loadingIndicator,
            resultCard
        ) = createRefs()

        val guidelineTop = createGuidelineFromTop(0.08f)
        val guidelineBottom = createGuidelineFromBottom(0.05f)

        // Title
        Text(
            text = "🏦 POS Simulator",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(guidelineTop)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Transaction Form
        TransactionForm(
            state = state,
            onPanChanged = onPanChanged,
            onAmountChanged = onAmountChanged,
            onPinChanged = onPinChanged,
            onSendTransaction = onSendTransaction,
            modifier = Modifier.constrainAs(form) {
                top.linkTo(title.bottom, 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(guidelineBottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        )

        // Loading Indicator
        if (state.isLoading) {
            LoadingIndicator(
                modifier = Modifier.constrainAs(loadingIndicator) {
                    top.linkTo(form.top)
                    bottom.linkTo(form.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }

        // Result Card
        state.transactionResult?.let { result ->
            TransactionResultCard(
                result = result,
                modifier = Modifier.constrainAs(resultCard) {
                    top.linkTo(form.top)
                    bottom.linkTo(form.bottom)
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(parent.end, 16.dp)
                    width = Dimension.fillToConstraints
                }
            )
        }
    }
}