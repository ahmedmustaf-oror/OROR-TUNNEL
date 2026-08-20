package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("عن OROR TUNNEL", color = TextPrimary) },
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Security, contentDescription = null, tint = CyberPurpleGlow, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("OROR TUNNEL", color = CyberPurpleGlow, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("الإصدار 1.0.0 (Build 100)", color = CyberCyan, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                color = CyberSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "تطبيق OROR TUNNEL هو حل نفق شبكي عالي الأمان والأداء يدعم بروتوكولات متعددة مثل SSH, WireGuard, OpenVPN, V2Ray/Xray, Hysteria2, و SlowDNS.",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Right
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/201021520331"))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تواصل عبر واتساب: 01021520331", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
