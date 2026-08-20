package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MaintenanceInfo
import com.example.ui.theme.*

@Composable
fun MaintenanceScreen(info: MaintenanceInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Build, contentDescription = null, tint = CyberYellow, modifier = Modifier.size(72.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(info.title, color = CyberYellow, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(12.dp))
        Text(info.message, color = TextPrimary, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}
