package com.example.vpn.engines

import com.example.data.model.TunnelConfig
import com.example.data.model.TunnelServer
import com.example.data.model.VpnState
import com.example.data.model.VpnStats
import com.example.vpn.VpnEngine
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class SshEngine : VpnEngine {

    private val _state = MutableStateFlow(VpnState.DISCONNECTED)
    override val state: StateFlow<VpnState> = _state.asStateFlow()

    private val _stats = MutableStateFlow(VpnStats())
    override val stats: StateFlow<VpnStats> = _stats.asStateFlow()

    private var job: Job? = null
    private var durationSecs = 0L
    private var totalBytesIn = 0L
    private var totalBytesOut = 0L

    override suspend fun start(config: TunnelConfig, server: TunnelServer): Boolean {
        _state.value = VpnState.CONNECTING
        delay(1200) // Simulate SSH handshake, payload injection & key exchange

        if (server.host.isEmpty() && config.host.isEmpty() && config.fileUrl.isEmpty()) {
            _state.value = VpnState.ERROR
            _stats.value = _stats.value.copy(errorMessage = "SSH Host is missing")
            return false
        }

        _state.value = VpnState.CONNECTED
        val targetIp = if (server.host.isNotEmpty()) server.host else if (config.host.isNotEmpty()) config.host else "10.0.0.1"

        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive && _state.value == VpnState.CONNECTED) {
                delay(1000)
                durationSecs++
                val downSpeed = Random.nextDouble(150.0, 1200.0)
                val upSpeed = Random.nextDouble(30.0, 300.0)
                totalBytesIn += (downSpeed * 1024).toLong()
                totalBytesOut += (upSpeed * 1024).toLong()

                _stats.value = VpnStats(
                    bytesIn = totalBytesIn,
                    bytesOut = totalBytesOut,
                    downloadSpeedKbps = downSpeed,
                    uploadSpeedKbps = upSpeed,
                    durationSeconds = durationSecs,
                    pingMs = Random.nextInt(20, 45),
                    serverIp = targetIp
                )
            }
        }
        return true
    }

    override suspend fun stop(): Boolean {
        _state.value = VpnState.DISCONNECTING
        job?.cancel()
        delay(500)
        _state.value = VpnState.DISCONNECTED
        durationSecs = 0L
        _stats.value = VpnStats()
        return true
    }
}
