package com.aradsheybak.pos_simulator_iso8583.presentation.screen.transaction.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.aradsheybak.pos_simulator_iso8583.core.domain.entity.TransactionResult

@Composable
fun TransactionResultCard(
    result: TransactionResult,
    modifier: Modifier = Modifier
) {
    val isSuccess = result.responseCode == "00"

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSuccess) {
                Color(0xFFE8F5E8)
            } else {
                Color(0xFFFFEBEE)
            }
        )
    ) {
        TransactionResultContent(result = result)
    }
}

@Composable
private fun TransactionResultContent(result: TransactionResult) {
    val isSuccess = result.responseCode == "00"

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        TransactionResultHeader(isSuccess = isSuccess)
        Spacer(modifier = Modifier.height(12.dp))
        TransactionResultDetails(result = result)
        Spacer(modifier = Modifier.height(12.dp))
        TransactionRawResponse(rawResponse = result.rawResponse)
    }
}

@Composable
private fun TransactionResultHeader(isSuccess: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = "Result",
            tint = if (isSuccess) Color(0xFF2E7D32) else Color(0xFFD32F2F),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isSuccess) "Transaction Successful" else "Transaction Result",
            style = MaterialTheme.typography.titleMedium,
            color = if (isSuccess) Color(0xFF2E7D32) else Color(0xFFD32F2F)
        )
    }
}

@Composable
private fun TransactionResultDetails(result: TransactionResult) {
    Column {
        TransactionDetailItem(label = "MTI:", value = result.mti)
        Spacer(modifier = Modifier.height(8.dp))
        TransactionDetailItem(label = "Response Code:", value = result.responseCode)
        Spacer(modifier = Modifier.height(8.dp))
        TransactionDetailItem(label = "Message:", value = result.responseMessage)
    }
}

@Composable
private fun TransactionDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
private fun TransactionRawResponse(rawResponse: String) {
    if (rawResponse.isNotEmpty()) {
        var expanded by remember { mutableStateOf(false) }

        Card(
            onClick = { expanded = !expanded },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                RawResponseHeader(expanded = expanded)
                RawResponseContent(rawResponse = rawResponse, expanded = expanded)
            }
        }
    }
}

@Composable
private fun RawResponseHeader(expanded: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Code,
            contentDescription = "Hex",
            tint = Color.Gray,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Raw Response (Hex)",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = "Toggle",
            tint = Color.Gray,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun RawResponseContent(rawResponse: String, expanded: Boolean) {
    if (expanded) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = rawResponse,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = Color.DarkGray
        )
    } else {
        Text(
            text = rawResponse.take(40) + if (rawResponse.length > 40) "..." else "",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = Color.Gray
        )
    }
}