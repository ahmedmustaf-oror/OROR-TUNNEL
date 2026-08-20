package com.example.vpn

import com.example.data.model.TunnelConfig
import com.example.data.model.TunnelServer
import com.example.data.model.VpnState
import com.example.data.model.VpnStats
import kotlinx.coroutines.flow.StateFlow

interface VpnEngine {
    val state: StateFlow<VpnState>
    val stats: StateFlow<VpnStats>

    suspend fun start(config: TunnelConfig, server: TunnelServer): Boolean
    suspend fun stop(): Boolean
}
