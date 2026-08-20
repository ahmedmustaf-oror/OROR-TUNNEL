package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TunnelConfig
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportConfigScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var rawText by remember { mutableStateOf("") }
    var configName by remember { mutableStateOf("") }
    var protocol by remember { mutableStateOf("SSH") }
    var host by remember { mutableStateOf("") }
    var portText by remember { mutableStateOf("22") }
    var sni by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("استيراد Config جديد", color = TextPrimary) },
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
            if (errorMessage != null) {
                Surface(
                    color = CyberRed.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Text(errorMessage!!, color = CyberRed, fontSize = 13.sp, modifier = Modifier.padding(10.dp))
                }
            }

            Text("بيانات الـ Config الأساسية", color = CyberCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = configName,
                onValueChange = { configName = it },
                label = { Text("اسم التكوين (Config Name)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberPurple,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    label = { Text("Host / IP") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPurple,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.weight(2f)
                )

                OutlinedTextField(
                    value = portText,
                    onValueChange = { portText = it },
                    label = { Text("Port") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPurple,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = sni,
                onValueChange = { sni = it },
                label = { Text("SNI (Server Name Indication)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberPurple,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("أو لصق نص الـ Payload / Configuration مباشرة", color = CyberCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = rawText,
                onValueChange = { rawText = it },
                placeholder = { Text("GET / HTTP/1.1[crlf]Host: domain.com[crlf]...", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberPurple,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (configName.isBlank()) {
                        errorMessage = "يرجى كتابة اسم الـ Config"
                        return@Button
                    }
                    val newConfig = TunnelConfig(
                        name = configName,
                        protocol = protocol,
                        host = host,
                        port = portText.toIntOrNull() ?: 22,
                        payload = rawText,
                        sni = sni,
                        isCustom = true
                    )
                    viewModel.addCustomConfig(newConfig)
                    onBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("حفظ واستيراد الـ Config", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
