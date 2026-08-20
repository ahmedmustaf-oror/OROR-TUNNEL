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

class WireGuardEngine : VpnEngine {
    private val _state = MutableStateFlow(VpnState.DISCONNECTED)
    override val state: StateFlow<VpnState> = _state.asStateFlow()
    private val _stats = MutableStateFlow(VpnStats())
    override val stats: StateFlow<VpnStats> = _stats.asStateFlow()
    private var job: Job? = null

    override suspend fun start(config: TunnelConfig, server: TunnelServer): Boolean {
        _state.value = VpnState.CONNECTING
        delay(800)
        _state.value = VpnState.CONNECTED
        var durationSecs = 0L
        var bytesIn = 0L
        var bytesOut = 0L
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive && _state.value == VpnState.CONNECTED) {
                delay(1000)
                durationSecs++
                val down = Random.nextDouble(500.0, 3500.0)
                val up = Random.nextDouble(100.0, 800.0)
                bytesIn += (down * 1024).toLong()
                bytesOut += (up * 1024).toLong()
                _stats.value = VpnStats(
                    bytesIn = bytesIn,
                    bytesOut = bytesOut,
                    downloadSpeedKbps = down,
                    uploadSpeedKbps = up,
                    durationSeconds = durationSecs,
                    pingMs = Random.nextInt(15, 35),
                    serverIp = server.host
                )
            }
        }
        return true
    }

    override suspend fun stop(): Boolean {
        _state.value = VpnState.DISCONNECTING
        job?.cancel()
        delay(400)
        _state.value = VpnState.DISCONNECTED
        _stats.value = VpnStats()
        return true
    }
}

class OpenVpnEngine : VpnEngine {
    private val _state = MutableStateFlow(VpnState.DISCONNECTED)
    override val state: StateFlow<VpnState> = _state.asStateFlow()
    private val _stats = MutableStateFlow(VpnStats())
    override val stats: StateFlow<VpnStats> = _stats.asStateFlow()
    private var job: Job? = null

    override suspend fun start(config: TunnelConfig, server: TunnelServer): Boolean {
        _state.value = VpnState.CONNECTING
        delay(1500)
        _state.value = VpnState.CONNECTED
        var durationSecs = 0L
        var bytesIn = 0L
        var bytesOut = 0L
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive && _state.value == VpnState.CONNECTED) {
                delay(1000)
                durationSecs++
                val down = Random.nextDouble(200.0, 1800.0)
                val up = Random.nextDouble(50.0, 400.0)
                bytesIn += (down * 1024).toLong()
                bytesOut += (up * 1024).toLong()
                _stats.value = VpnStats(
                    bytesIn = bytesIn,
                    bytesOut = bytesOut,
                    downloadSpeedKbps = down,
                    uploadSpeedKbps = up,
                    durationSeconds = durationSecs,
                    pingMs = Random.nextInt(40, 75),
                    serverIp = server.host
                )
            }
        }
        return true
    }

    override suspend fun stop(): Boolean {
        _state.value = VpnState.DISCONNECTING
        job?.cancel()
        delay(400)
        _state.value = VpnState.DISCONNECTED
        _stats.value = VpnStats()
        return true
    }
}

class XrayEngine : VpnEngine {
    private val _state = MutableStateFlow(VpnState.DISCONNECTED)
    override val state: StateFlow<VpnState> = _state.asStateFlow()
    private val _stats = MutableStateFlow(VpnStats())
    override val stats: StateFlow<VpnStats> = _stats.asStateFlow()
    private var job: Job? = null

    override suspend fun start(config: TunnelConfig, server: TunnelServer): Boolean {
        _state.value = VpnState.CONNECTING
        delay(900)
        _state.value = VpnState.CONNECTED
        var durationSecs = 0L
        var bytesIn = 0L
        var bytesOut = 0L
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive && _state.value == VpnState.CONNECTED) {
                delay(1000)
                durationSecs++
                val down = Random.nextDouble(400.0, 2800.0)
                val up = Random.nextDouble(80.0, 600.0)
                bytesIn += (down * 1024).toLong()
                bytesOut += (up * 1024).toLong()
                _stats.value = VpnStats(
                    bytesIn = bytesIn,
                    bytesOut = bytesOut,
                    downloadSpeedKbps = down,
                    uploadSpeedKbps = up,
                    durationSeconds = durationSecs,
                    pingMs = Random.nextInt(25, 50),
                    serverIp = server.host
                )
            }
        }
        return true
    }

    override suspend fun stop(): Boolean {
        _state.value = VpnState.DISCONNECTING
        job?.cancel()
        delay(300)
        _state.value = VpnState.DISCONNECTED
        _stats.value = VpnStats()
        return true
    }
}

class HysteriaEngine : VpnEngine {
    private val _state = MutableStateFlow(VpnState.DISCONNECTED)
    override val state: StateFlow<VpnState> = _state.asStateFlow()
    private val _stats = MutableStateFlow(VpnStats())
    override val stats: StateFlow<VpnStats> = _stats.asStateFlow()
    private var job: Job? = null

    override suspend fun start(config: TunnelConfig, server: TunnelServer): Boolean {
        _state.value = VpnState.CONNECTING
        delay(700)
        _state.value = VpnState.CONNECTED
        var durationSecs = 0L
        var bytesIn = 0L
        var bytesOut = 0L
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive && _state.value == VpnState.CONNECTED) {
                delay(1000)
                durationSecs++
                val down = Random.nextDouble(800.0, 5000.0)
                val up = Random.nextDouble(200.0, 1200.0)
                bytesIn += (down * 1024).toLong()
                bytesOut += (up * 1024).toLong()
                _stats.value = VpnStats(
                    bytesIn = bytesIn,
                    bytesOut = bytesOut,
                    downloadSpeedKbps = down,
                    uploadSpeedKbps = up,
                    durationSeconds = durationSecs,
                    pingMs = Random.nextInt(18, 32),
                    serverIp = server.host
                )
            }
        }
        return true
    }

    override suspend fun stop(): Boolean {
        _state.value = VpnState.DISCONNECTING
        job?.cancel()
        delay(300)
        _state.value = VpnState.DISCONNECTED
        _stats.value = VpnStats()
        return true
    }
}

class DnsttEngine : VpnEngine {
    private val _state = MutableStateFlow(VpnState.DISCONNECTED)
    override val state: StateFlow<VpnState> = _state.asStateFlow()
    private val _stats = MutableStateFlow(VpnStats())
    override val stats: StateFlow<VpnStats> = _stats.asStateFlow()
    private var job: Job? = null

    override suspend fun start(config: TunnelConfig, server: TunnelServer): Boolean {
        _state.value = VpnState.CONNECTING
        delay(1800) // SlowDNS tunnel initialization
        _state.value = VpnState.CONNECTED
        var durationSecs = 0L
        var bytesIn = 0L
        var bytesOut = 0L
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive && _state.value == VpnState.CONNECTED) {
                delay(1000)
                durationSecs++
                val down = Random.nextDouble(30.0, 250.0)
                val up = Random.nextDouble(10.0, 80.0)
                bytesIn += (down * 1024).toLong()
                bytesOut += (up * 1024).toLong()
                _stats.value = VpnStats(
                    bytesIn = bytesIn,
                    bytesOut = bytesOut,
                    downloadSpeedKbps = down,
                    uploadSpeedKbps = up,
                    durationSeconds = durationSecs,
                    pingMs = Random.nextInt(70, 140),
                    serverIp = server.host
                )
            }
        }
        return true
    }

    override suspend fun stop(): Boolean {
        _state.value = VpnState.DISCONNECTING
        job?.cancel()
        delay(400)
        _state.value = VpnState.DISCONNECTED
        _stats.value = VpnStats()
        return true
    }
}

class HttpProxyEngine : VpnEngine {
    private val _state = MutableStateFlow(VpnState.DISCONNECTED)
    override val state: StateFlow<VpnState> = _state.asStateFlow()
    private val _stats = MutableStateFlow(VpnStats())
    override val stats: StateFlow<VpnStats> = _stats.asStateFlow()
    private var job: Job? = null

    override suspend fun start(config: TunnelConfig, server: TunnelServer): Boolean {
        _state.value = VpnState.CONNECTING
        delay(600)
        _state.value = VpnState.CONNECTED
        var durationSecs = 0L
        var bytesIn = 0L
        var bytesOut = 0L
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive && _state.value == VpnState.CONNECTED) {
                delay(1000)
                durationSecs++
                val down = Random.nextDouble(100.0, 800.0)
                val up = Random.nextDouble(20.0, 200.0)
                bytesIn += (down * 1024).toLong()
                bytesOut += (up * 1024).toLong()
                _stats.value = VpnStats(
                    bytesIn = bytesIn,
                    bytesOut = bytesOut,
                    downloadSpeedKbps = down,
                    uploadSpeedKbps = up,
                    durationSeconds = durationSecs,
                    pingMs = Random.nextInt(30, 60),
                    serverIp = server.host
                )
            }
        }
        return true
    }

    override suspend fun stop(): Boolean {
        _state.value = VpnState.DISCONNECTING
        job?.cancel()
        delay(300)
        _state.value = VpnState.DISCONNECTED
        _stats.value = VpnStats()
        return true
    }
}

class SocksProxyEngine : VpnEngine {
    private val _state = MutableStateFlow(VpnState.DISCONNECTED)
    override val state: StateFlow<VpnState> = _state.asStateFlow()
    private val _stats = MutableStateFlow(VpnStats())
    override val stats: StateFlow<VpnStats> = _stats.asStateFlow()
    private var job: Job? = null

    override suspend fun start(config: TunnelConfig, server: TunnelServer): Boolean {
        _state.value = VpnState.CONNECTING
        delay(600)
        _state.value = VpnState.CONNECTED
        var durationSecs = 0L
        var bytesIn = 0L
        var bytesOut = 0L
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive && _state.value == VpnState.CONNECTED) {
                delay(1000)
                durationSecs++
                val down = Random.nextDouble(120.0, 900.0)
                val up = Random.nextDouble(25.0, 250.0)
                bytesIn += (down * 1024).toLong()
                bytesOut += (up * 1024).toLong()
                _stats.value = VpnStats(
                    bytesIn = bytesIn,
                    bytesOut = bytesOut,
                    downloadSpeedKbps = down,
                    uploadSpeedKbps = up,
                    durationSeconds = durationSecs,
                    pingMs = Random.nextInt(28, 55),
                    serverIp = server.host
                )
            }
        }
        return true
    }

    override suspend fun stop(): Boolean {
        _state.value = VpnState.DISCONNECTING
        job?.cancel()
        delay(300)
        _state.value = VpnState.DISCONNECTED
        _stats.value = VpnStats()
        return true
    }
}
