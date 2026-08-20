package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

@Composable
fun ConfigsScreen(
    viewModel: MainViewModel,
    onNavigateImport: () -> Unit,
    onNavigateEditor: () -> Unit,
    onConfigSelected: () -> Unit
) {
    val configs by viewModel.configs.collectAsState()
    val selectedConfigId by viewModel.selectedConfigId.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    val filteredConfigs = remember(configs, selectedTab) {
        when (selectedTab) {
            0 -> configs // All Configs
            1 -> configs.filter { !it.isCustom } // Remote Server Configs
            else -> configs.filter { it.isCustom } // Custom Imported Configs
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateImport,
                containerColor = CyberPurple,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Import Config")
            }
        },
        containerColor = CyberDarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("إدارة ملفات التكوين (Configs)", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("تكوينات أعدها الآدمن أو مستوردة مخصصة", color = TextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CyberSurface,
                contentColor = CyberCyan
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("الكل", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("السيرفرات", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                    Text("المستوردة", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filteredConfigs) { config ->
                    ConfigCardItem(
                        config = config,
                        isSelected = config.id == selectedConfigId,
                        onSelect = {
                            viewModel.selectConfig(config.id)
                            onConfigSelected()
                        },
                        onDelete = { viewModel.deleteConfig(config.id) },
                        onFavoriteToggle = { viewModel.toggleFavoriteConfig(config.id, !config.isFavorite) }
                    )
                }
            }
        }
    }
}

@Composable
fun ConfigCardItem(
    config: TunnelConfig,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Surface(
        color = if (isSelected) CyberSurfaceLight else CyberSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) CyberCyan else CyberBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(config.icon.ifEmpty { "⚡" }, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(config.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Surface(color = CyberPurple.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                    Text(config.protocol, color = CyberPurpleGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (config.socksIp.isNotEmpty()) {
                Text("SOCKS Proxy: ${config.socksIp}:${config.socksPort}", color = CyberYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            if (config.sni.isNotEmpty()) {
                Text("SNI: ${config.sni}", color = CyberCyan, fontSize = 11.sp)
            }
            Text("Expires: ${config.expiration}", color = TextSecondary, fontSize = 11.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (isSelected) "✓ مفعل حالياً" else "انقر للتفعيل",
                    color = if (isSelected) CyberGreen else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            imageVector = if (config.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (config.isFavorite) CyberYellow else TextSecondary
                        )
                    }
                    if (config.isCustom) {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CyberRed)
                        }
                    }
                }
            }
        }
    }
}
