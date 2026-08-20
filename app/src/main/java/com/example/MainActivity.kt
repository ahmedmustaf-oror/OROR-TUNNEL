package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppStructure(viewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "الرئيسية", Icons.Default.Home)
    object Servers : Screen("servers", "السيرفرات", Icons.Default.Dns)
    object Configs : Screen("configs", "الـ Configs", Icons.Default.VpnKey)
    object Settings : Screen("settings", "الإعدادات", Icons.Default.Settings)
    object About : Screen("about", "عن التطبيق", Icons.Default.Info)

    // Non-bottom bar screens
    object ImportConfig : Screen("import_config", "استيراد", Icons.Default.Add)
    object ConfigEditor : Screen("config_editor", "مُحضر HTTP", Icons.Default.Code)
    object Details : Screen("details", "تفاصيل الاتصال", Icons.Default.BarChart)
    object Notifications : Screen("notifications", "الإشعارات", Icons.Default.Notifications)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppStructure(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val maintenanceInfo by viewModel.maintenanceInfo.collectAsState()
    val updateInfo by viewModel.updateInfo.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    // Handle Maintenance Mode
    if (maintenanceInfo?.maintenanceMode == true) {
        MaintenanceScreen(info = maintenanceInfo!!)
        return
    }

    // Handle Force Update
    if (updateInfo?.forceUpdate == true) {
        ForceUpdateScreen(updateInfo = updateInfo!!)
        return
    }

    val bottomBarScreens = listOf(
        Screen.Home,
        Screen.Servers,
        Screen.Configs,
        Screen.Settings,
        Screen.About
    )

    val showBottomBar = bottomBarScreens.any { it.route == currentRoute }

    Scaffold(
        topBar = {
            if (showBottomBar) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(CyberPurple, CyberCyan)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.VpnLock,
                                    contentDescription = "Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Row {
                                Text("OROR ", color = CyberPurpleGlow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("TUNNEL", color = CyberCyanGlow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                            BadgedBox(
                                badge = {
                                    if (notifications.isNotEmpty()) {
                                        Badge(containerColor = CyberRed) {
                                            Text("${notifications.size}", color = Color.White)
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = TextPrimary)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDarkBg)
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = CyberSurface,
                    contentColor = CyberCyan,
                    tonalElevation = 8.dp
                ) {
                    bottomBarScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title,
                                    tint = if (isSelected) CyberCyan else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    screen.title,
                                    color = if (isSelected) CyberCyan else TextSecondary,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = CyberSurfaceLight
                            )
                        )
                    }
                }
            }
        },
        containerColor = CyberDarkBg
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateServers = { navController.navigate(Screen.Servers.route) },
                    onNavigateConfigs = { navController.navigate(Screen.Configs.route) },
                    onNavigateEditor = { navController.navigate(Screen.ConfigEditor.route) },
                    onNavigateDetails = { navController.navigate(Screen.Details.route) }
                )
            }
            composable(Screen.Servers.route) {
                ServersScreen(
                    viewModel = viewModel,
                    onServerSelected = { navController.navigate(Screen.Home.route) }
                )
            }
            composable(Screen.Configs.route) {
                ConfigsScreen(
                    viewModel = viewModel,
                    onNavigateImport = { navController.navigate(Screen.ImportConfig.route) },
                    onNavigateEditor = { navController.navigate(Screen.ConfigEditor.route) },
                    onConfigSelected = { navController.navigate(Screen.Home.route) }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.About.route) {
                AboutScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ImportConfig.route) {
                ImportConfigScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ConfigEditor.route) {
                ConfigEditorScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Details.route) {
                ConnectionDetailsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
