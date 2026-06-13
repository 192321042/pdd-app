package com.example.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.frontend.screens.*
import com.example.frontend.theme.MyApplicationTheme
import com.example.frontend.viewmodel.EmergencyViewModel
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId

class MainActivity : ComponentActivity() {
    private var rootNavController: androidx.navigation.NavHostController? = null

    private fun parseAccessToken(intent: android.content.Intent?): String? {
        val uri = intent?.data ?: return null
        android.util.Log.d("MainActivity", "Parsing deep link: $uri")
        
        // 1. Check fragment first (Supabase uses #access_token=...)
        val fragment = uri.fragment
        if (!fragment.isNullOrEmpty()) {
            val params = fragment.split("&")
            for (param in params) {
                val parts = param.split("=")
                if (parts.size == 2 && parts[0] == "access_token") {
                    return parts[1]
                }
            }
        }
        
        // 2. Check query parameter fallback
        return uri.getQueryParameter("access_token")
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val token = parseAccessToken(intent)
        if (token != null) {
            com.example.backend.api.SupabaseClient.accessToken = token
            rootNavController?.navigate("reset_password") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    @OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request essential systems permissions for direct SMS alert packages, live GPS tracking, and microphone voice scanning
        val permissions = arrayOf(
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )
        val missing = permissions.filter {
            checkSelfPermission(it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            requestPermissions(missing.toTypedArray(), 101)
        }

        val token = parseAccessToken(intent)
        val initialRoute = if (token != null) {
            com.example.backend.api.SupabaseClient.accessToken = token
            "reset_password"
        } else {
            "splash"
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.semantics {
                        testTagsAsResourceId = true
                    }
                ) {
                    val viewModel: EmergencyViewModel = viewModel()
                    val navController = rememberNavController()
                    rootNavController = navController

                    NavHost(
                        navController = navController,
                        startDestination = initialRoute
                    ) {
                        composable("splash") {
                            SplashScreen {
                                navController.navigate("onboarding") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }

                        composable("onboarding") {
                            OnboardingScreen {
                                navController.navigate("login") {
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        }

                        composable("login") {
                            LoginScreen(
                                viewModel = viewModel,
                                onLoginSuccess = {
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToReset = {
                                    navController.navigate("reset_password") {
                                        popUpTo("login") { inclusive = false }
                                    }
                                }
                            )
                        }

                        composable("main") {
                            MainContainer(viewModel = viewModel) {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }

                        composable("reset_password") {
                            ResetPasswordScreen(
                                viewModel = viewModel,
                                onResetSuccess = {
                                    viewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onCancel = {
                                    viewModel.logout()
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(viewModel: EmergencyViewModel, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val isSosTriggered by viewModel.isSosTriggered.collectAsState()
    val userRole by viewModel.currentUserRole.collectAsState()

    // Determine standard bottom navigation bar destinations
    val navItems = listOf(
        NavigationItem("dashboard", "Home", Icons.Filled.Home),
        NavigationItem("ai_status", "AI Status", Icons.Filled.AutoAwesome),
        NavigationItem("contacts", "Guardians", Icons.Filled.People),
        NavigationItem("chatbot", "AI Chat", Icons.Filled.Message),
        NavigationItem("profile", "Settings", Icons.Filled.Settings)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if (currentRoute != "dashboard") {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Shield,
                                contentDescription = "Shield Logo",
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (currentRoute) {
                                    "ai_status" -> "Active ML Sensors"
                                    "contacts" -> "Guardian Registry"
                                    "chatbot" -> "AI Safety Advisor"
                                    "profile" -> "Safety Settings"
                                    "reports" -> "Incident Logs"
                                    "notifications" -> "Alert Center"
                                    "admin" -> "Admin Control Center"
                                    "rescue_team" -> "Rescue Dispatch HQ"
                                    else -> "OmniGuard AI"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "System Hub Logs",
                                tint = Color(0xFF0288D1)
                            )
                            val notifyList by viewModel.notifications.collectAsState()
                            if (notifyList.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFFFF1744), CircleShape)
                                        .align(androidx.compose.ui.Alignment.TopEnd)
                                )
                            }
                        }
                    }

                    // Special Roles top shortcuts for demonstration purposes
                    if (userRole == "Admin") {
                        IconButton(onClick = { navController.navigate("admin") }) {
                            Icon(imageVector = Icons.Filled.AdminPanelSettings, contentDescription = "Admin Space", tint = Color(0xFF6200EA))
                        }
                    } else if (userRole == "Rescue Team") {
                        IconButton(onClick = { navController.navigate("rescue_team") }) {
                            Icon(imageVector = Icons.Filled.LocalActivity, contentDescription = "Rescue Desk", tint = Color(0xFF0288D1))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (currentRoute == "dashboard") Color.Transparent else Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { item ->
                    val selected = currentRoute == item.route || 
                                   (item.route == "ai_status" && currentRoute in listOf("ai_status", "voice", "behavioral", "camera", "gps_map"))
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = { Text(text = item.label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0288D1),
                            selectedTextColor = Color(0xFF0288D1),
                            indicatorColor = Color(0xFFE3F2FD)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == "dashboard" && !isSosTriggered) {
                ExtendedFloatingActionButton(
                    text = { Text("Panic SOS", color = Color.White, fontWeight = FontWeight.Black) },
                    icon = { Icon(imageVector = Icons.Filled.CrisisAlert, contentDescription = "SOS Alert Signal", tint = Color.White) },
                    onClick = { viewModel.triggerManualSos("FAB Easy Trigger") },
                    containerColor = Color(0xFFFF1744),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = if (currentRoute == "dashboard") 0.dp else innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .background(Color(0xFFF4F6FA))
        ) {
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("dashboard") {
                    DashboardScreen(viewModel) { route ->
                        navController.navigate(route)
                    }
                }

                composable("ai_status") {
                    DetectionViews(viewModel, "Monitor")
                }

                composable("voice") {
                    DetectionViews(viewModel, "Voice Analyzer")
                }

                composable("behavioral") {
                    DetectionViews(viewModel, "Behavioral")
                }

                composable("camera") {
                    DetectionViews(viewModel, "Vision ML")
                }

                composable("gps_map") {
                    DetectionViews(viewModel, "GPS Map")
                }

                composable("contacts") {
                    ContactsScreen(viewModel)
                }

                composable("chatbot") {
                    ChatbotScreen(viewModel)
                }

                composable("profile") {
                    ProfileScreen(viewModel, onLogout)
                }

                composable("reports") {
                    IncidentReportsScreen(viewModel)
                }

                composable("notifications") {
                    NotificationsScreen(viewModel)
                }

                composable("admin") {
                    AdminDashboardScreen(viewModel)
                }

                composable("rescue_team") {
                    RescueTeamDashboardScreen(viewModel)
                }

                composable("sos") {
                    SosScreen(viewModel) {
                        navController.navigate("dashboard") {
                            popUpTo("dashboard") { inclusive = false }
                        }
                    }
                }
            }

            // High priority full screen SOS alarm transition
            if (isSosTriggered && currentRoute != "sos") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    SosScreen(viewModel) {
                        viewModel.cancelActiveSos()
                    }
                }
            }


        }
    }
}

data class NavigationItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
