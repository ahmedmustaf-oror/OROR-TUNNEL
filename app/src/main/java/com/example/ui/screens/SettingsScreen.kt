package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var autoReconnect by remember { mutableStateOf(true) }
    var enableDnsForwarding by remember { mutableStateOf(true) }
    var enableKeepAlive by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إعدادات OROR TUNNEL", color = TextPrimary) },
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
            Text("خيارات الشبكة والأنفاق المتقدمة", color = CyberCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = CyberSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Auto Reconnect
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("إعادة الاتصال تلقائياً", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("إعادة المحاولة فوراً عند انقطاع الشبكة", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = autoReconnect,
                            onCheckedChange = { autoReconnect = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = CyberPurple)
                        )
                    }

                    Divider(color = CyberBorder, modifier = Modifier.padding(vertical = 12.dp))

                    // DNS Forwarding
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("توجيه DNS الآمن (1.1.1.1 / 8.8.8.8)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("تجاوز حجب المواقع وحماية استعلامات DNS", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = enableDnsForwarding,
                            onCheckedChange = { enableDnsForwarding = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = CyberPurple)
                        )
                    }

                    Divider(color = CyberBorder, modifier = Modifier.padding(vertical = 12.dp))

                    // KeepAlive Ping
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("فحص الاتصال الدوري (KeepAlive Ping)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("منع فصل الـ Tunnel عند خمول الشاشة", color = TextSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = enableKeepAlive,
                            onCheckedChange = { enableKeepAlive = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = CyberPurple)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("حالة الربط بالسيرفر السحابي", color = CyberCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = CyberSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberPurple.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = CyberGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text("ONLINE", color = CyberGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("OROR Remote Cloud Engine", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("مربوط بالسيرفر لجلب التحديثات والكونفجات تلقائياً", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("الدعم والاتصال والتحديث", color = CyberCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/201021520331"))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceLight),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = CyberGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تواصل عبر واتساب: 01021520331", color = TextPrimary, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    viewModel.syncRemoteData()
                    android.widget.Toast.makeText(context, "تم مزامنة البيانات من سيرفر OROR بنجاح", android.widget.Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تحديث السيرفرات والكونفجات الآن", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
