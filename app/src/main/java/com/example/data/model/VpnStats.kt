package com.example.data.model

data class VpnStats(
    val bytesIn: Long = 0L,
    val bytesOut: Long = 0L,
    val downloadSpeedKbps: Double = 0.0,
    val uploadSpeedKbps: Double = 0.0,
    val durationSeconds: Long = 0L,
    val pingMs: Int = 0,
    val serverIp: String = "127.0.0.1",
    val errorMessage: String? = null
)
