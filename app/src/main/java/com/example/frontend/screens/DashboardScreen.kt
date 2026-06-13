package com.example.frontend.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.backend.database.Contact
import com.example.frontend.viewmodel.EmergencyViewModel

@Composable
fun DashboardScreen(
    viewModel: EmergencyViewModel,
    onNavigateToSection: (String) -> Unit
) {
    val safetyScore by viewModel.currentSafetyScore.collectAsState()
    val stressMetric by viewModel.stressMetric.collectAsState()
    val isVoiceActive by viewModel.isVoiceAnalysisActive.collectAsState()
    val isMotionActive by viewModel.isBehavioralActive.collectAsState()
    val isCameraActive by viewModel.isCameraEmotionActive.collectAsState()
    val isSosTriggered by viewModel.isSosTriggered.collectAsState()
    val activeSosType by viewModel.sosType.collectAsState()
    val timerCount by viewModel.sosCountdownTimer.collectAsState()
    val locationString by viewModel.currentLocationString.collectAsState()
    val contactsList by viewModel.contacts.collectAsState()
    val logsList by viewModel.incidentLogs.collectAsState()

    val context = LocalContext.current
    var hasSmsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasSmsPermission = perms[Manifest.permission.SEND_SMS] ?: hasSmsPermission
        hasLocationPermission = perms[Manifest.permission.ACCESS_FINE_LOCATION] ?: hasLocationPermission
        hasRecordPermission = perms[Manifest.permission.RECORD_AUDIO] ?: hasRecordPermission
    }

    LaunchedEffect(Unit) {
        hasSmsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        hasLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        hasRecordPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (hasLocationPermission) {
            viewModel.fetchLiveLocation { }
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            viewModel.fetchLiveLocation { }
        }
    }

    // Smooth subtle card gradient
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE0F7FA),
            Color(0xFFFFF9C4),
            Color(0xFFF3E5F5)
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 56.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High-level App Slogan
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OmniGuard AI",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0288D1)
                    )
                    Text(
                        text = "Smart AI Protection for Real-Time Emergency Response",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold
                    )
                }
 
                // Interactive Roles Chips
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE3F2FD))
                        .clickable { onNavigateToSection("profile") }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    val role by viewModel.currentUserRole.collectAsState()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0288D1))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Workspace: $role",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0288D1)
                        )
                    }
                }
            }
        }

        // Interactive Permissions Compliance Card
        if (!hasSmsPermission || !hasLocationPermission || !hasRecordPermission) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)), // Amber info background
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.SecurityUpdateWarning,
                                contentDescription = "Critical permission alert",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Safety Permissions Required",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Certain core hardware features are restricted. Please authorize these to enable emergency guard activities:",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (!hasSmsPermission) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Cancel,
                                        contentDescription = "Not granted",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "SMS Dispatch (required to auto-SMS emergency contacts)",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB91C1C),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            if (!hasLocationPermission) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Cancel,
                                        contentDescription = "Not granted",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "GPS Location (required to transmit geocoded coordinates)",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB91C1C),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            if (!hasRecordPermission) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Cancel,
                                        contentDescription = "Not granted",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Microphone (required to monitor screaming alerts)",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB91C1C),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        Button(
                            onClick = {
                                val requests = mutableListOf<String>()
                                if (!hasSmsPermission) requests.add(Manifest.permission.SEND_SMS)
                                if (!hasLocationPermission) requests.add(Manifest.permission.ACCESS_FINE_LOCATION)
                                if (!hasRecordPermission) requests.add(Manifest.permission.RECORD_AUDIO)
                                if (requests.isNotEmpty()) {
                                    permissionLauncher.launch(requests.toTypedArray())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Grant",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Authorize Safety Permissions Now",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Active Threat Alarm Overlay
        if (isSosTriggered) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Critical SOS",
                                tint = Color(0xFFFF1744),
                                modifier = Modifier
                                    .size(32.dp)
//                                    .animateContentSize()
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "ACTIVE EMERGENCY DETECTED",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFF1744),
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Trigger payload: $activeSosType",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF1744))
                        ) {
                            Text(
                                text = "$timerCount",
                                color = Color.White,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.cancelActiveSos() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF1744)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("False Alert (Dismiss)", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { 
                                    onNavigateToSection("sos")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Open SOS HUD", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Live Health and Safety Dynamic Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSection("ai_status") }
                    .shadow(1.dp, RoundedCornerShape(24.dp))
            ) {
                Box(modifier = Modifier.background(brush)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AI Live Security Engine",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1C1E),
                                fontSize = 16.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.6f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.GraphicEq,
                                        contentDescription = "Active pulse",
                                        tint = Color(0xFF0288D1),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Monitoring",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0288D1)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Your Ambient Safety Level",
                                    fontSize = 12.sp,
                                    color = Color(0xFF42474E),
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$safetyScore%",
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (safetyScore > 75) Color(0xFF00E676) else Color(0xFFFF1744)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (safetyScore > 75) "Optimal" else "Distressed",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (safetyScore > 75) Color(0xFF00E676) else Color(0xFFFF1744),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // AI Shield Encryption Guard Active
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.5f))
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = "Guard Active",
                                    tint = Color(0xFF0288D1),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "SECURE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0288D1)
                                )
                                Text("Guard AI-Active", fontSize = 10.sp, color = Color(0xFF64748B))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.White.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamic GPS display row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "Location",
                                tint = Color(0xFF6200EA),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = locationString,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Active Sensors Toggles Status Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SensorStatusCard(
                    title = "Voice AI",
                    isActive = isVoiceActive,
                    icon = Icons.Filled.Mic,
                    color = Color(0xFF0288D1),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToSection("voice") }
                )
                SensorStatusCard(
                    title = "Kinetic AI",
                    isActive = isMotionActive,
                    icon = Icons.Filled.DirectionsRun,
                    color = Color(0xFF6200EA),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToSection("behavioral") }
                )
                SensorStatusCard(
                    title = "Camera Vision",
                    isActive = isCameraActive,
                    icon = Icons.Filled.CameraRear,
                    color = Color(0xFF00B0FF),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToSection("camera") }
                )
            }
        }

        // Large Quick SOS Button Panel (Requires dynamic touch target 48dp+)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Touch-Panic SOS Button",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Tap or shook device to trigger immediate dispatch",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    Button(
                        onClick = { viewModel.triggerManualSos("Quick Manual SOS") },
                        modifier = Modifier
                            .size(126.dp)
                            .testTag("submit_button"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.CrisisAlert,
                                contentDescription = "Distress Trigger",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "SOS",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }


        // Smart Guidance Bot Launcher Block
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6200EA)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSection("chatbot") }
                    .shadow(1.dp, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.SupportAgent,
                                contentDescription = "Bot",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Safety Chatbot Advisor",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Ask questions or get safety steps in Telugu, Hindi, Tamil & English.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = "Forward link",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Contacts Carousel View
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trusted Contact Responders",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1A1C1E)
                )
                TextButton(onClick = { onNavigateToSection("contacts") }) {
                    Text("Add Rescue", fontSize = 12.sp, color = Color(0xFF0288D1))
                }
            }

            if (contactsList.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "No emergency contacts listed. Set up rescue links.",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(contactsList) { buddy ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .width(150.dp)
                                .shadow(0.5.dp, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (buddy.isPrimary) Icons.Filled.Star else Icons.Filled.Person,
                                        contentDescription = "Buddy icon",
                                        tint = if (buddy.isPrimary) Color(0xFFFF9100) else Color(0xFF0288D1),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = buddy.relationship,
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = buddy.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = buddy.phone,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Trigger Logs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Anomalies Log",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1A1C1E)
                )
                TextButton(onClick = { onNavigateToSection("reports") }) {
                    Text("View Logs Database", fontSize = 12.sp, color = Color(0xFF0288D1))
                }
            }

            if (logsList.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "All systems green. No anomaly data points logged recently.",
                        modifier = Modifier.padding(20.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    logsList.take(3).forEach { report ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFEBEE))
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.OfflineShare,
                                        contentDescription = "Log category",
                                        tint = Color(0xFFFF1744),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = report.type,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = report.description,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 1
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE8F5E9))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Score: ${report.aiConfidenceScore}%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SensorStatusCard(
    title: String,
    isActive: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.shadow(0.5.dp, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isActive) color.copy(alpha = 0.15f) else Color(0xFFECEFF1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isActive) color else Color(0xFF90A4AE),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isActive) "ACTIVE" else "MUTED",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isActive) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
        }
    }
}
