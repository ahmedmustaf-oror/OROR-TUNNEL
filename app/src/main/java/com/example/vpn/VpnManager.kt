package com.example.vpn

import android.content.Context
import android.content.Intent
import com.example.data.model.TunnelConfig
import com.example.data.model.TunnelServer
import com.example.data.model.VpnState
import com.example.data.model.VpnStats
import kotlinx.coroutines.flow.StateFlow

object VpnManager {

    val vpnState: StateFlow<VpnState> = TunnelVpnService.vpnState
    val vpnStats: StateFlow<VpnStats> = TunnelVpnService.vpnStats
    val logs: StateFlow<List<String>> = TunnelVpnService.logs

    fun clearLogs() {
        TunnelVpnService.clearLogs()
    }

    fun connect(context: Context, server: TunnelServer, config: TunnelConfig) {
        TunnelVpnService.currentServer = server
        TunnelVpnService.currentConfig = config
        val intent = Intent(context, TunnelVpnService::class.java).apply {
            action = TunnelVpnService.ACTION_CONNECT
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun disconnect(context: Context) {
        val intent = Intent(context, TunnelVpnService::class.java).apply {
            action = TunnelVpnService.ACTION_DISCONNECT
        }
        context.startService(intent)
    }
}
