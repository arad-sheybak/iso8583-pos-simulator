package com.aradsheybak.pos_simulator_iso8583.core.domain.entity

data class ISO8583Config(   val serverHost: String = "192.168.1.100",
                            val serverPort: Int = 5000,
                            val timeout: Int = 10000
)
