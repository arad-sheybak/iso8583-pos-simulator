package com.aradsheybak.pos_simulator_iso8583

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.aradsheybak.pos_simulator_iso8583.ui.theme.POS_Simulator_iso8583Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            POS_Simulator_iso8583Theme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//
//                }
            }
        }
    }
}

