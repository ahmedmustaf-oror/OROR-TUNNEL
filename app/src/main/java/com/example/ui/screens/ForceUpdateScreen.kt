package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SystemUpdate
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
import com.example.data.model.AppUpdateInfo
import com.example.ui.theme.*

@Composable
fun ForceUpdateScreen(updateInfo: AppUpdateInfo) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("يتوفر تحديث إجباري جديد! 🚀", color = CyberCyan, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "الإصدار الجديد (${updateInfo.latestVersionName}) متوفر الآن مع تحسينات في السرعة واستقرار الاتصال.",
            color = TextPrimary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        if (updateInfo.changelog.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = CyberSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(updateInfo.changelog, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(12.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                val url = if (updateInfo.downloadUrl.isNotEmpty()) updateInfo.downloadUrl else "https://elias555.serv00.net/orortunnel/"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("تحديث التطبيق الآن", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
