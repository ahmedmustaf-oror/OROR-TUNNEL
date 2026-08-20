package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.TunnelServer
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ServersScreen(
    viewModel: MainViewModel,
    onServerSelected: () -> Unit
) {
    val servers by viewModel.servers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCountry by viewModel.selectedCountryFilter.collectAsState()
    val selectedServerId by viewModel.selectedServerId.collectAsState()

    val filteredServers = remember(servers, searchQuery, selectedCountry) {
        servers.filter { s ->
            val matchesQuery = s.name.contains(searchQuery, ignoreCase = true) ||
                    s.country.contains(searchQuery, ignoreCase = true) ||
                    s.protocol.contains(searchQuery, ignoreCase = true)
            val matchesCountry = selectedCountry == "ALL" || s.country.equals(selectedCountry, ignoreCase = true)
            matchesQuery && matchesCountry
        }
    }

    val countries = remember(servers) {
        listOf("ALL") + servers.map { it.country }.distinct()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBg)
            .padding(16.dp)
    ) {
        Text("قائمة السيرفرات الخادمة", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("اختر أسرع سيرفر يناسب اتصالك والألعاب", color = TextSecondary, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("بحث باسم السيرفر أو الدولة...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberPurple,
                unfocusedBorderColor = CyberBorder,
                focusedContainerColor = CyberSurface,
                unfocusedContainerColor = CyberSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Country Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(countries) { country ->
                val isSelected = selectedCountry == country
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setCountryFilter(country) },
                    label = { Text(if (country == "ALL") "الكل" else country) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberPurple,
                        selectedLabelColor = Color.White,
                        containerColor = CyberSurfaceLight,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Server Cards List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filteredServers) { server ->
                ServerCardItem(
                    server = server,
                    isSelected = server.id == selectedServerId,
                    onSelect = {
                        viewModel.selectServer(server.id)
                        onServerSelected()
                    },
                    onFavoriteToggle = {
                        viewModel.toggleFavoriteServer(server.id, !server.isFavorite)
                    }
                )
            }
        }
    }
}

@Composable
fun ServerCardItem(
    server: TunnelServer,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Surface(
        color = if (isSelected) CyberSurfaceLight else CyberSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) CyberPurpleGlow else CyberBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(server.flag, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(server.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        if (server.premium) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = CyberYellow.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text("VIP", color = CyberYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${server.protocol} • Load: ${server.load}%", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text("${server.ping} ms", color = if (server.ping < 50) CyberGreen else CyberYellow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("${server.currentUsers}/${server.maxUsers}", color = TextSecondary, fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (server.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (server.isFavorite) CyberYellow else TextSecondary
                    )
                }
            }
        }
    }
}
