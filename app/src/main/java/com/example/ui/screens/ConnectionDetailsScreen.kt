package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionDetailsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val vpnState by viewModel.vpnState.collectAsState()
    val vpnStats by viewModel.vpnStats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تفاصيل الاتصال والإحصائيات", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDarkBg)
            )
        },
        containerColor = CyberDarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Surface(
                color = CyberSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("بيانات الشبكة الحالية", color = CyberCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow("حالة الاتصال", vpnState.name)
                    DetailRow("عنوان IP السيرفر", vpnStats.serverIp)
                    DetailRow("زمن الاستجابة (Ping)", "${vpnStats.pingMs} ms")
                    DetailRow("مدة الاتصال", "${vpnStats.durationSeconds} ثانية")
                    DetailRow("إجمالي التحميل (Bytes In)", "${vpnStats.bytesIn / (1024 * 1024)} MB")
                    DetailRow("إجمالي الرفع (Bytes Out)", "${vpnStats.bytesOut / (1024 * 1024)} MB")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Text(value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
