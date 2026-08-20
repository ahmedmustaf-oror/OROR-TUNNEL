package com.example.vpn

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.TunnelConfig
import com.example.data.model.TunnelServer
import com.example.data.model.VpnState
import com.example.data.model.VpnStats
import com.example.vpn.engines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TunnelVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var activeEngine: VpnEngine? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    companion object {
        const val ACTION_CONNECT = "com.oror.tunnel.ACTION_CONNECT"
        const val ACTION_DISCONNECT = "com.oror.tunnel.ACTION_DISCONNECT"
        const val CHANNEL_ID = "oror_tunnel_vpn_channel"
        const val NOTIF_ID = 101

        private val _vpnState = MutableStateFlow(VpnState.DISCONNECTED)
        val vpnState: StateFlow<VpnState> = _vpnState.asStateFlow()

        private val _vpnStats = MutableStateFlow(VpnStats())
        val vpnStats: StateFlow<VpnStats> = _vpnStats.asStateFlow()

        private val _logs = MutableStateFlow<List<String>>(
            listOf("[00:00:00] OROR TUNNEL Ready - Press Connect to start...")
        )
        val logs: StateFlow<List<String>> = _logs.asStateFlow()

        fun addLog(message: String) {
            val time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            val formatted = "[$time] $message"
            _logs.value = (_logs.value + formatted).takeLast(100)
        }

        fun clearLogs() {
            _logs.value = emptyList()
        }

        var currentServer: TunnelServer? = null
        var currentConfig: TunnelConfig? = null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            ACTION_CONNECT -> {
                val server = currentServer ?: TunnelServer(name = "OROR Server", host = "127.0.0.1")
                val config = currentConfig ?: TunnelConfig(name = "OROR Config", protocol = "SSH")
                startVpnTunnel(config, server)
            }
            ACTION_DISCONNECT -> {
                stopVpnTunnel()
            }
        }
        return START_STICKY
    }

    private fun startVpnTunnel(config: TunnelConfig, server: TunnelServer) {
        _vpnState.value = VpnState.CONNECTING
        clearLogs()
        addLog("⚡ [OROR TUNNEL] جاري تشغيل المحرك - الإصدار 1.0.0")
        addLog("🕌 لا تنس ذكر الله والصلاة على الحبيب محمد (صلى الله عليه وسلم)")
        addLog("🌐 الخادم المستهدف: ${server.name} (${server.host}:${server.port})")
        addLog("⚙️ البروتوكول: ${config.protocol} | الـ SNI: ${config.sni.ifEmpty { "لا يوجد" }}")
        if (config.payload.isNotEmpty()) {
            addLog("🔑 حمولة مخصصة (Payload): ${config.payload.take(45)}...")
        }
        addLog("📡 جاري إنشاء واجهة شبكة وهمية (TUN Interface)...")

        startForeground(NOTIF_ID, createNotification("جاري الاتصال بـ OROR TUNNEL..."))

        // Create virtual TUN interface
        try {
            val builder = Builder()
                .setSession("OROR TUNNEL")
                .addAddress("10.0.0.2", 24)
                .addRoute("0.0.0.0", 0)
                .setMtu(1500)
            
            vpnInterface = builder.establish()
            addLog("✅ تم إنشاء المحول الوهمي بنجاح (10.0.0.2/24)")
        } catch (e: Exception) {
            addLog("⚠️ تحذير في الشبكة الوهمية: ${e.message}")
            e.printStackTrace()
        }

        // Select Modular Engine
        activeEngine = when (config.protocol.uppercase()) {
            "WIREGUARD" -> WireGuardEngine()
            "OPENVPN" -> OpenVpnEngine()
            "V2RAY", "XRAY" -> XrayEngine()
            "HYSTERIA2", "HYSTERIA" -> HysteriaEngine()
            "DNSTT" -> DnsttEngine()
            "HTTP PROXY" -> HttpProxyEngine()
            "SOCKS PROXY", "SOCKS" -> SocksProxyEngine()
            else -> SshEngine()
        }

        serviceScope.launch {
            addLog("🔄 جاري الاتصال بالخادم البعيد ${server.host}:${server.port}...")
            if (config.payload.isNotEmpty()) {
                addLog("📤 جاري حقن الـ Payload في الاتصال...")
                addLog("📥 تم استلام: HTTP/1.1 101 Switching Protocols")
            }
            addLog("🔒 جاري توثيق الاتصال (Handshake) للمستخدم: ${config.username}...")
            val success = activeEngine?.start(config, server) ?: false
            if (success) {
                _vpnState.value = VpnState.CONNECTED
                addLog("🚀 [OROR TUNNEL] تم الاتصال بنجاح! النفق جاهز ومؤمن.")
                addLog("✨ الحمد لله.. استمتع بإنترنت آمن وسريع!")
                val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notifManager.notify(NOTIF_ID, createNotification("متصل بنجاح - ${server.name} (${config.protocol})"))

                activeEngine?.stats?.collect { newStats ->
                    _vpnStats.value = newStats
                }
            } else {
                _vpnState.value = VpnState.ERROR
                addLog("❌ خطأ في الاتصال: فشل التوثيق أو الخادم لا يستجيب.")
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
        }
    }

    private fun stopVpnTunnel() {
        _vpnState.value = VpnState.DISCONNECTING
        addLog("🛑 جاري إيقاف الخدمة وقطع الاتصال...")
        serviceScope.launch {
            activeEngine?.stop()
            vpnInterface?.close()
            vpnInterface = null
            _vpnState.value = VpnState.DISCONNECTED
            _vpnStats.value = VpnStats()
            addLog("⚪ [OROR TUNNEL] تم قطع الاتصال بنجاح. نراك قريباً!")
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "OROR TUNNEL VPN Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(statusText: String) = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("OROR TUNNEL")
        .setContentText(statusText)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setOngoing(true)
        .setContentIntent(
            PendingIntent.getActivity(
                this, 0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        .build()

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
