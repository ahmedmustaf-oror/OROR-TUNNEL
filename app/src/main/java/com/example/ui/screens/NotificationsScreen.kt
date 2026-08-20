package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
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
fun NotificationsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("التنبيهات والإشعارات", color = TextPrimary) },
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
            if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد إشعارات جديدة حالياً", color = TextSecondary, fontSize = 14.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(notifications) { item ->
                        Surface(
                            color = CyberSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = CyberCyan)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(item.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(item.message, color = TextSecondary, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(item.createdAt, color = CyberPurpleGlow, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
