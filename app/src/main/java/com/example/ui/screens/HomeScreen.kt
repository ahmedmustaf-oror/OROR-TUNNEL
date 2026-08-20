package com.example.ui.screens

import android.widget.Toast
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.draw.shadow
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VpnState
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateServers: () -> Unit,
    onNavigateConfigs: () -> Unit,
    onNavigateEditor: () -> Unit,
    onNavigateDetails: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val vpnState by viewModel.vpnState.collectAsState()
    val vpnStats by viewModel.vpnStats.collectAsState()
    val vpnLogs by viewModel.vpnLogs.collectAsState()

    val servers by viewModel.servers.collectAsState()
    val configs by viewModel.configs.collectAsState()
    val selectedServerId by viewModel.selectedServerId.collectAsState()
    val selectedConfigId by viewModel.selectedConfigId.collectAsState()
    val announcements by viewModel.announcements.collectAsState()

    val currentServer = servers.find { it.id == selectedServerId } ?: servers.firstOrNull()
    val currentConfig = configs.find { it.id == selectedConfigId } ?: configs.firstOrNull()

    var showServerPicker by remember { mutableStateOf(false) }
    var showConfigPicker by remember { mutableStateOf(false) }

    // Animation for glowing connect button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (vpnState == VpnState.CONNECTING) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header & Quick Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CyberPurple, CyberCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.VpnLock,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("OROR ", color = CyberPurpleGlow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("TUNNEL", color = CyberCyanGlow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("تأمين وتحقين الثغرات بأعلى سرعة 🚀", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Auto Sync Configs Button
                IconButton(
                    onClick = {
                        viewModel.syncRemoteData()
                        Toast.makeText(context, "جاري جلب أحدث الـ Configs بالسيرفر...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(36.dp).background(CyberSurfaceLight, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = CyberCyan, modifier = Modifier.size(20.dp))
                }

                // HTTP Payload Editor Quick Access
                Surface(
                    color = CyberSurfaceLight,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                    modifier = Modifier.clickable { onNavigateEditor() }
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = CyberPurpleGlow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Payload", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Announcement Banner
        if (announcements.isNotEmpty()) {
            Surface(
                color = CyberSurface,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberPurple.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = CyberPurpleGlow, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(announcements.first().title, color = TextPrimary, fontSize = 12.sp, maxLines = 1)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Status Badge
        val statusBg = when (vpnState) {
            VpnState.CONNECTED -> CyberGreen.copy(alpha = 0.2f)
            VpnState.CONNECTING, VpnState.DISCONNECTING -> CyberYellow.copy(alpha = 0.2f)
            VpnState.ERROR -> CyberRed.copy(alpha = 0.2f)
            else -> CyberSurfaceLight
        }
        val statusTextColor = when (vpnState) {
            VpnState.CONNECTED -> CyberGreen
            VpnState.CONNECTING, VpnState.DISCONNECTING -> CyberYellow
            VpnState.ERROR -> CyberRed
            else -> TextSecondary
        }

        Surface(
            color = statusBg,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, statusTextColor)
        ) {
            Text(
                text = when (vpnState) {
                    VpnState.CONNECTED -> "🟢 متصل ومحمي بنجاح"
                    VpnState.CONNECTING -> "🟡 جاري تحقين الثغرة والاتصال..."
                    VpnState.DISCONNECTING -> "🟡 جاري إيقاف الاتصال..."
                    VpnState.ERROR -> "🔴 تعذر الاتصال بالسيرفر"
                    else -> "⚪ جاهز للاتصال"
                },
                color = statusTextColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Big Glowing Power Button with Radar Pulse Effect
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(180.dp)
                .clickable { viewModel.toggleVpnConnection(context) }
        ) {
            // Radar pulse effect
            if (vpnState == VpnState.CONNECTING || vpnState == VpnState.CONNECTED) {
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .border(1.5.dp, if (vpnState == VpnState.CONNECTED) CyberGreen.copy(alpha=0.5f) else CyberCyan.copy(alpha=0.5f), CircleShape)
                        .background(if (vpnState == VpnState.CONNECTED) CyberGreen.copy(alpha=0.1f) else CyberCyan.copy(alpha=0.1f))
                )
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale * 1.1f)
                        .clip(CircleShape)
                        .border(2.dp, if (vpnState == VpnState.CONNECTED) CyberGreen.copy(alpha=0.6f) else CyberCyan.copy(alpha=0.6f), CircleShape)
                        .background(if (vpnState == VpnState.CONNECTED) CyberGreen.copy(alpha=0.2f) else CyberCyan.copy(alpha=0.2f))
                )
            }
            
            // Core Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (vpnState == VpnState.CONNECTED) listOf(CyberGreen, Color(0xFF00BFA5))
                            else listOf(CyberCyan, Color(0xFF1565C0))
                        )
                    )
                    .border(3.dp, if (vpnState == VpnState.CONNECTED) CyberGreenGlow else CyberCyanGlow, CircleShape)
                    .shadow(if (vpnState == VpnState.CONNECTED) 20.dp else 0.dp, CircleShape, ambientColor = CyberGreen, spotColor = CyberGreenGlow)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Connect",
                        tint = Color.White,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (vpnState == VpnState.CONNECTED) "إيقاف" else "اتصال",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Professional Network Speed Stats
        if (vpnState == VpnState.CONNECTED || vpnState == VpnState.CONNECTING) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Download Speed
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = "Down", tint = CyberGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download", color = TextSecondary, fontSize = 11.sp)
                    }
                    Text(
                        text = "${String.format("%.1f", vpnStats.downloadSpeedKbps)} KB/s",
                        color = CyberGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                // Upload Speed
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Up", tint = CyberCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Upload", color = TextSecondary, fontSize = 11.sp)
                    }
                    Text(
                        text = "${String.format("%.1f", vpnStats.uploadSpeedKbps)} KB/s",
                        color = CyberCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Interactive Server & Config Selector Rows
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Selected Server Selector Bar
            Surface(
                color = CyberSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberPurple.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth().clickable { showServerPicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(currentServer?.flag ?: "🌐", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("السيرفر / الراوتر النشط", color = TextSecondary, fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${currentServer?.ping ?: 20}ms", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(currentServer?.name ?: "اختر سيرفر الاتصال", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                    Surface(
                        color = CyberPurple.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("تغيير", color = CyberPurpleGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = CyberPurpleGlow, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            // 2. Selected Config Selector Bar
            Surface(
                color = CyberSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth().clickable { showConfigPicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Surface(
                            color = CyberCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = currentConfig?.protocol ?: "SSH",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(currentConfig?.category ?: "تكوين الثغرة", color = TextSecondary, fontSize = 10.sp)
                                if (currentConfig?.premium == true) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("VIP", color = CyberYellow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(currentConfig?.name ?: "اختر الـ Config المطلوب", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                    Surface(
                        color = CyberCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("اختر Config", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // OROR TUNNEL Terminal Log Console (Logcat View)
        Surface(
            color = Color(0xFF090D16),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberPurple.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                // Console Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("سجل الاتصال (OROR Logs)", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Copy Logs
                        IconButton(
                            onClick = {
                                val text = vpnLogs.joinToString("\n")
                                clipboardManager.setText(AnnotatedString(text))
                                Toast.makeText(context, "تم نسخ السجل إلى الحافظة", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                        // Clear Logs
                        IconButton(
                            onClick = { viewModel.clearVpnLogs() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Divider(color = CyberBorder, modifier = Modifier.padding(vertical = 6.dp))

                // Scrollable Terminal Text Area
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = false
                ) {
                    items(vpnLogs) { logLine ->
                        val (prefixColor, textColor) = when {
                            logLine.contains("✅") || logLine.contains("🚀") || logLine.contains("✨") -> CyberGreen to CyberGreen.copy(alpha=0.8f)
                            logLine.contains("❌") || logLine.contains("⚠️") -> CyberRed to CyberRed.copy(alpha=0.8f)
                            logLine.contains("📥") || logLine.contains("📤") || logLine.contains("🔄") || logLine.contains("📡") -> CyberCyan to CyberCyan.copy(alpha=0.8f)
                            logLine.contains("🔒") -> CyberYellow to CyberYellow.copy(alpha=0.8f)
                            logLine.contains("🕌") -> Color(0xFF10B981) to Color.White
                            else -> CyberPurpleGlow to Color(0xFFCBD5E1)
                        }
                        
                        Text(
                            text = buildAnnotatedString {
                                val split = logLine.split(" ", limit = 2)
                                if (split.isNotEmpty()) {
                                    withStyle(SpanStyle(color = prefixColor, fontWeight = FontWeight.Bold)) {
                                        append(split[0] + " ")
                                    }
                                    if (split.size > 1) {
                                        withStyle(SpanStyle(color = textColor)) {
                                            append(split[1])
                                        }
                                    }
                                }
                            },
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }

    // Server Selection Modal Dialog
    if (showServerPicker) {
        AlertDialog(
            onDismissRequest = { showServerPicker = false },
            containerColor = CyberSurface,
            title = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("اختر السيرفر / الراوتر", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showServerPicker = false; onNavigateServers() }) {
                        Text("عرض الكل 🚀", color = CyberCyan, fontSize = 12.sp)
                    }
                }
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp)) {
                    items(servers) { server ->
                        val isSelected = server.id == selectedServerId
                        Surface(
                            color = if (isSelected) CyberPurple.copy(alpha = 0.3f) else CyberSurfaceLight,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyberPurpleGlow else CyberBorder),
                            modifier = Modifier.fillMaxWidth().clickable {
                                viewModel.selectServer(server.id)
                                showServerPicker = false
                                Toast.makeText(context, "تم تحديد ${server.name}", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(server.flag, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(server.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("${server.host} • Ping: ${server.ping}ms", color = TextSecondary, fontSize = 10.sp)
                                    }
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showServerPicker = false }) {
                    Text("إغلاق", color = TextSecondary)
                }
            }
        )
    }

    // Config Selection Modal Dialog
    if (showConfigPicker) {
        AlertDialog(
            onDismissRequest = { showConfigPicker = false },
            containerColor = CyberSurface,
            title = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("اختر الـ Config / الثغرة", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { showConfigPicker = false; onNavigateConfigs() }) {
                        Text("متجر الكونفجات 🛒", color = CyberCyan, fontSize = 12.sp)
                    }
                }
            },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)) {
                    items(configs) { cfg ->
                        val isSelected = cfg.id == selectedConfigId
                        Surface(
                            color = if (isSelected) CyberCyan.copy(alpha = 0.2f) else CyberSurfaceLight,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyberCyan else CyberBorder),
                            modifier = Modifier.fillMaxWidth().clickable {
                                viewModel.selectConfig(cfg.id)
                                showConfigPicker = false
                                Toast.makeText(context, "تم اختيار ${cfg.name}", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Surface(color = CyberPurple.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp)) {
                                        Text(cfg.protocol, color = CyberPurpleGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(cfg.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text("${cfg.category} • ${cfg.sni.ifEmpty { cfg.host }}", color = TextSecondary, fontSize = 10.sp, maxLines = 1)
                                    }
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showConfigPicker = false }) {
                    Text("إغلاق", color = TextSecondary)
                }
            }
        )
    }
}
