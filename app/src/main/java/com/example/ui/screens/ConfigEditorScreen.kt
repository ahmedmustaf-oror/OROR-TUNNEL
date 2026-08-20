package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.SecurityManager
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigEditorScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val customPayload by viewModel.customPayload.collectAsState()
    var payloadText by remember(customPayload) { mutableStateOf(customPayload) }
    var method by remember { mutableStateOf("GET") }
    var hostText by remember { mutableStateOf("sg1.orortunnel.net") }
    var pathText by remember { mutableStateOf("/") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تخصيص HTTP Request / Headers", color = TextPrimary) },
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
            Text("مُحضر الـ Custom Payload / Header Injection", color = CyberCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("يستخدم لتخطي الحجب وتسريع الاتصال عبر ثغرات الـ Header", color = TextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = method,
                    onValueChange = { method = it },
                    label = { Text("Method") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPurple,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = hostText,
                    onValueChange = { hostText = it },
                    label = { Text("Bug Host") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPurple,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.weight(2f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = payloadText,
                onValueChange = { payloadText = it },
                label = { Text("Custom Payload Raw String") },
                placeholder = { Text("CONNECT [host_port] [protocol][crlf]Host: [host][crlf]...", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberPurple,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Generate Button Helper
            Button(
                onClick = {
                    payloadText = "$method $pathText HTTP/1.1[crlf]Host: $hostText[crlf]Upgrade: websocket[crlf]Connection: Keep-Alive[crlf][crlf]"
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("توليد Payload تلقائي بـ Header القياسي", color = CyberCyan)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val sanitized = SecurityManager.sanitizePayload(payloadText)
                    viewModel.saveCustomPayload(sanitized)
                    onBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("حفظ الـ Custom Payload", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
