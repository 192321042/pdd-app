package com.example.frontend.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import java.util.Locale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.viewmodel.EmergencyViewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.view.PreviewView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager

@Composable
fun DetectionViews(
    viewModel: EmergencyViewModel,
    initialTab: String = "Monitor"
) {
    var activeTab by remember { mutableStateOf(initialTab) }
    val tabs = listOf("Monitor", "Voice Analyzer", "Behavioral", "Vision ML", "GPS Map")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
            .statusBarsPadding()
    ) {
        // High Contrast Sliding Tab Row (Accessibility Support)
        ScrollableTabRow(
            selectedTabIndex = tabs.indexOf(activeTab).coerceAtLeast(0),
            edgePadding = 12.dp,
            containerColor = Color.White,
            contentColor = Color(0xFF0288D1),
            modifier = Modifier.fillMaxWidth().shadow(1.dp)
        ) {
            tabs.forEach { tab ->
                Tab(
                    selected = activeTab == tab,
                    onClick = { activeTab = tab },
                    text = {
                        Text(
                            text = tab,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == tab) Color(0xFF0288D1) else Color(0xFF64748B)
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                "Monitor" -> MonitorSubScreen(viewModel)
                "Voice Analyzer" -> VoiceSubScreen(viewModel)
                "Behavioral" -> BehavioralSubScreen(viewModel)
                "Vision ML" -> VisionSubScreen(viewModel)
                "GPS Map" -> GpsSubScreen(viewModel)
            }
        }
    }
}

// 1. AI Central Monitor & Anomaly Charts Screen
@Composable
fun MonitorSubScreen(viewModel: EmergencyViewModel) {
    val score by viewModel.currentSafetyScore.collectAsState()
    val stress by viewModel.stressMetric.collectAsState()
    val isMonitoring by viewModel.isMonitoringActive.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().shadow(0.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AI Threat Probability Assessment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1A1C1E)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Anomaly Probability Graph (Canvas custom draw)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(Color(0xFFFAFBFD), RoundedCornerShape(12.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            val w = size.width
                            val h = size.height
                            // Draw grid lines
                            drawLine(Color(0xFFE2E8F0), Offset(0f, h * 0.25f), Offset(w, h * 0.25f), strokeWidth = 1f)
                            drawLine(Color(0xFFE2E8F0), Offset(0f, h * 0.5f), Offset(w, h * 0.5f), strokeWidth = 1f)
                            drawLine(Color(0xFFE2E8F0), Offset(0f, h * 0.75f), Offset(w, h * 0.75f), strokeWidth = 1f)

                            // Stress curve points
                            val peakY = if (score < 50) h * 0.85f else h * 0.25f
                            val points = listOf(
                                Offset(0f, h * 0.15f),
                                Offset(w * 0.2f, h * 0.12f),
                                Offset(w * 0.4f, h * 0.22f),
                                Offset(w * 0.6f, peakY),
                                Offset(w * 0.8f, h * 0.18f),
                                Offset(w, h * 0.1f)
                            )

                            for (i in 0 until points.size - 1) {
                                drawLine(
                                    color = if (score < 50) Color(0xFFFF1744) else Color(0xFF0288D1),
                                    start = points[i],
                                    end = points[i + 1],
                                    strokeWidth = 6f
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Risk: ${100 - score}%", fontSize = 11.sp, color = Color(0xFFFF1744), fontWeight = FontWeight.Bold)
                        Text("Time Window: 60s Moving", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().shadow(0.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Vitals Assessment Metrics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    VitalsBar(name = "Speech Acoustic Stress", percent = stress, color = Color(0xFFFF9100))
                    Spacer(modifier = Modifier.height(12.dp))
                    VitalsBar(name = "Posture Instability Analytics", percent = if (score < 50) 84 else 18, color = Color(0xFF6200EA))
                    Spacer(modifier = Modifier.height(12.dp))
                    VitalsBar(name = "Cardiac Heart-Rate Spike", percent = if (score < 50) 90 else 64, color = Color(0xFFFF1744))
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive Local Simulations",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1A1C1E)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "For demonstration validation, you can trigger individual anomalies below.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { viewModel.simulateScreamDetected("Help me! Save me immediately!") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Simulate Scream", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.simulateFallDetected() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Simulate Fall", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.simulateCameraFearDetected() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9100)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simulate Fear/Dilation Face ML Trigger", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun VitalsBar(name: String, percent: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
            Text(text = "$percent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Color(0xFFECEFF1)
        )
    }
}


// 2. Voice Stress analysis HUD Subscreen
@Composable
fun VoiceSubScreen(viewModel: EmergencyViewModel) {
    val waveformColors by viewModel.micWaveformHistory.collectAsState()
    val textClassification by viewModel.voiceClassificationText.collectAsState()
    val isVoiceActive by viewModel.isVoiceAnalysisActive.collectAsState()
    val isMicListening by viewModel.isMicrophoneListening.collectAsState()
    var inputPhrase by remember { mutableStateOf("") }
 
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().shadow(0.5.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Real-Time Microphone Waveform Decibel Oscilloscope",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
 
                    // Audio Waveform Visualization Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val spacing = size.width / (waveformColors.size + 1)
                            val midY = size.height / 2f
                            waveformColors.forEachIndexed { i, amp ->
                                val barHeight = size.height * amp
                                drawLine(
                                    color = if (amp > 0.4f) Color(0xFFFF1744) else Color(0xFF0288D1),
                                    start = Offset(spacing * (i + 1), midY - (barHeight / 2)),
                                    end = Offset(spacing * (i + 1), midY + (barHeight / 2)),
                                    strokeWidth = 10f
                                )
                            }
                        }
                    }
 
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Frequency Assessment Threshold: ${if (waveformColors.any { it > 0.5f }) "CRITICAL STRESS" else "No Screaming/Safe"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (waveformColors.any { it > 0.5f }) Color(0xFFFF1744) else Color(0xFF2E7D32)
                    )
                }
            }
        }
 
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Speech Classifier Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        
                        // Interactive Toggle Button
                        Button(
                            onClick = {
                                if (isMicListening) {
                                    viewModel.stopContinuousVoiceTriggerListener()
                                } else {
                                    viewModel.startContinuousVoiceTriggerListener()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMicListening) Color(0xFFFF1744) else Color(0xFF2E7D32)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isMicListening) Icons.Filled.MicOff else Icons.Filled.Mic,
                                contentDescription = "Mic toggle",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isMicListening) "Stop Mic listening" else "Live Scan Mic",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = textClassification,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Trigger Phrase Simulation",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = inputPhrase,
                        onValueChange = { inputPhrase = it },
                        placeholder = { Text("E.g., help me, emergency, save me") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (inputPhrase.isNotBlank()) {
                                viewModel.simulateScreamDetected(inputPhrase)
                                inputPhrase = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Record & Dispatch AI Transcription", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.generateVoiceRecordingSimulation() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Simulate Ambient Voice Snapshot Encryption", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// 3. Fall, Shaking, & Kinetic Behavioral Subscreen
@Composable
fun BehavioralSubScreen(viewModel: EmergencyViewModel) {
    val alertText by viewModel.behavioralAlertText.collectAsState()
    val score by viewModel.currentSafetyScore.collectAsState()
    val accelValues by viewModel.accelerometerValues.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Kinetic Telemetry Assessment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Accelerometer (X,Y,Z)", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                text = "Axis Vector: %.2f, %.2f, %.2f m/s²".format(accelValues.first, accelValues.second, accelValues.third),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Gyroscope Spin", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(
                                text = if (score < 50) "Angular: 218 rad/s" else "Angular: 2 rad/s",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (score < 50) Color(0xFFFF1744) else Color(0xFF1A1C1E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Algorithmic Assessment Verdict:",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = alertText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (score < 50) Color(0xFFFF1744) else Color(0xFF2E7D32)
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().shadow(0.5.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsWalk,
                        contentDescription = "Movement patterns",
                        tint = Color(0xFF6200EA),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Behavioral Learning Tracker",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Learns regular walking patterns and predicts panic rushes, running speeds, or unprompted immobility vectors.",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    Button(
                        onClick = { viewModel.simulateShakeDetected() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simulate High-G Mobile Shake Gesture", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.simulateFallDetected() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simulate Sudden Impact Fall Anomaly", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// 4. Camera Expression Recognizer Vision ML Subscreen
@Composable
fun VisionSubScreen(viewModel: EmergencyViewModel) {
    val faceText by viewModel.faceExpressionState.collectAsState()
    val faceAnalysisAlert by viewModel.faceExpressionAlert.collectAsState()
    val score by viewModel.currentSafetyScore.collectAsState()

    // Live optical scanning telemetry variables
    val framesAnalyzed by viewModel.cameraFramesCount.collectAsState()
    val cameraLuminance by viewModel.cameraLuminance.collectAsState()
    val cameraVariance by viewModel.cameraVariance.collectAsState()
    val isAutoDetectEnabled by viewModel.isAutoExpressionDetectionEnabled.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "A.I. Optical Face Scanner",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1A1C1E)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isAutoDetectEnabled) Color(0xFFE8F5E9) else Color(0xFFECEFF1))
                                .clickable { viewModel.setAutoExpressionDetectionEnabled(!isAutoDetectEnabled) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isAutoDetectEnabled) "SCANNING ACTIVE" else "PAUSED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAutoDetectEnabled) Color(0xFF2E7D32) else Color(0xFF455A64)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Real Camera Preview Frame holding direct CameraX integration
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE2E2E6)),
                        contentAlignment = Alignment.Center
                    ) {
                        CameraSection(viewModel = viewModel, modifier = Modifier.fillMaxSize())

                        // High Tech scanning laser line overlay over live camera
                        var laserYOffset by remember { mutableStateOf(0.1f) }
                        LaunchedEffect(Unit) {
                            while (true) {
                                delay(30)
                                laserYOffset += 0.015f
                                if (laserYOffset > 0.95f) laserYOffset = 0.05f
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .align(Alignment.TopCenter)
                                .offset(y = (220.dp * laserYOffset))
                                .background(Color(0xFF00FF88)) // glowing active emerald scanner bar
                        )

                        if (score < 50) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(4.dp, Color(0xFFFF1744), RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFF1744).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Filled.Face,
                                        contentDescription = "Threat facial detection",
                                        tint = Color(0xFFFF1744),
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "DISTRESS EXPRESSION MATCHED",
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFF1744),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Expression: $faceText",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (score < 50) Color(0xFFFF1744) else Color(0xFF0288D1)
                        )
                        Text(
                            text = "Sentinel Tracking: ${if (isAutoDetectEnabled) "Active" else "Disabled"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isAutoDetectEnabled) Color(0xFF2E7D32) else Color(0xFFFF1744)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sentinel Optical Diagnostic Telemetry",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text("Frames Scanned", fontSize = 9.sp, color = Color(0xFF64748B))
                            Text("$framesAnalyzed", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text("Eye/Landmark Lux", fontSize = 9.sp, color = Color(0xFF64748B))
                            Text("${cameraLuminance.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text("Motion Variance", fontSize = 9.sp, color = Color(0xFF64748B))
                            Text("${String.format("%.1f", cameraVariance)}%", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Facial Emotion & Expression Simulator (Force Test)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("😊 Calm", "😭 Sad", "😟 Hesitated").forEach { item ->
                            val emotionName = item.substring(3)
                            val isSelected = faceText == emotionName
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) Color(0xFFD32F2F) else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .background(if (isSelected) Color(0xFFFFEBEE) else Color.White)
                                    .clickable {
                                        viewModel.updateDetectedExpression(emotionName)
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFFD32F2F) else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Consciousness Analytics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = faceAnalysisAlert,
                        fontSize = 12.sp,
                        color = Color(0xFF42474E)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.simulateCameraFearDetected() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9100)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Trigger Camera Anomaly Detection Simulation", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// 5. Google Maps Location Tracker & Finder HUD
@Composable
fun GpsSubScreen(viewModel: EmergencyViewModel) {
    val locationText by viewModel.currentLocationString.collectAsState()
    val lat by viewModel.currentLatitude.collectAsState()
    val lng by viewModel.currentLongitude.collectAsState()
    var zoomLevel by remember { mutableStateOf(1.0f) }
    var searchResultText by remember { mutableStateOf("") }

    // Base coordinates to offset our scrolling satellite terrain features
    val baseLat = 17.4065
    val baseLng = 78.4772
    val scale = 14000f * zoomLevel
    val dx = ((lng - baseLng) * scale).toFloat()
    val dy = ((lat - baseLat) * scale).toFloat()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "A.I. Tactical Satellite Map (Lock Active)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF1E293B)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE0F2FE))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Satellite,
                                    contentDescription = "Satellite Active",
                                    tint = Color(0xFF0288D1),
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SATELLITE VIEW",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Satellite Tactical Canvas Box with zooming & movement tracking
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0B132B)), // Dark Cosmic Void
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw ocean/lake body on satellite (deep dark blue water body)
                            drawCircle(
                                color = Color(0xFF0E1E38),
                                radius = 220f * zoomLevel,
                                center = Offset(w * 0.15f - dx, h * 0.85f + dy)
                            )

                            // Draw dynamic vegetation/forest sectors (satellite organic green-shades)
                            drawRect(
                                color = Color(0xFF1A352B),
                                topLeft = Offset(50f - dx, h * 0.15f + dy),
                                size = Size(180f * zoomLevel, 130f * zoomLevel)
                            )
                            drawCircle(
                                color = Color(0xFF142D23),
                                radius = 170f * zoomLevel,
                                center = Offset(w * 0.85f - dx, h * 0.2f + dy)
                            )

                            // Draw satellite concrete/urban grids (roads)
                            val roadColor = Color(0xFF2E3B4E)
                            drawLine(roadColor, Offset(w * 0.2f - dx, 0f), Offset(w * 0.2f - dx, h), strokeWidth = 8f * zoomLevel)
                            drawLine(roadColor, Offset(w * 0.55f - dx, 0f), Offset(w * 0.55f - dx, h), strokeWidth = 14f * zoomLevel)
                            drawLine(roadColor, Offset(w * 0.82f - dx, 0f), Offset(w * 0.82f - dx, h), strokeWidth = 8f * zoomLevel)
                            drawLine(roadColor, Offset(0f, h * 0.35f + dy), Offset(w, h * 0.35f + dy), strokeWidth = 9f * zoomLevel)
                            drawLine(roadColor, Offset(0f, h * 0.72f + dy), Offset(w, h * 0.72f + dy), strokeWidth = 15f * zoomLevel)

                            // Draw Latitude/Longitude coordinate overlay grid lines
                            val gridSpacing = 90f * zoomLevel
                            var gridX = (w * 0.5f - dx) % gridSpacing
                            if (gridX < 0) gridX += gridSpacing
                            while (gridX < w) {
                                drawLine(Color(0x1338BDF8), Offset(gridX, 0f), Offset(gridX, h), strokeWidth = 2f)
                                gridX += gridSpacing
                            }

                            var gridY = (h * 0.5f + dy) % gridSpacing
                            if (gridY < 0) gridY += gridSpacing
                            while (gridY < h) {
                                drawLine(Color(0x1338BDF8), Offset(0f, gridY), Offset(w, gridY), strokeWidth = 2f)
                                gridY += gridSpacing
                            }

                            // Dynamic target scope radar sweep animation line
                            val time = System.currentTimeMillis()
                            val sweepAngle = (time / 10) % 360
                            val radius = Math.min(w, h) * 0.4f
                            val sweepX = w * 0.5f + radius * Math.cos(Math.toRadians(sweepAngle.toDouble())).toFloat()
                            val sweepY = h * 0.5f + radius * Math.sin(Math.toRadians(sweepAngle.toDouble())).toFloat()
                            drawLine(
                                color = Color(0x3338BDF8),
                                start = Offset(w * 0.5f, h * 0.5f),
                                end = Offset(sweepX, sweepY),
                                strokeWidth = 3f
                            )

                            // Draw central user lock point reticle
                            // Pulse circle ring
                            drawCircle(
                                color = Color(0xFFFF1744).copy(alpha = 0.2f),
                                radius = 25f + (time % 800) / 12f,
                                center = Offset(w * 0.5f, h * 0.5f)
                            )
                            // Core tracker point
                            drawCircle(
                                color = Color(0xFFFF1744),
                                radius = 7f,
                                center = Offset(w * 0.5f, h * 0.5f)
                            )
                            // Outer tracking square
                            drawRect(
                                color = Color(0xFF38BDF8),
                                topLeft = Offset(w * 0.5f - 18f, h * 0.5f - 18f),
                                size = Size(36f, 36f),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                            )
                        }

                        // Overlay Floating UI displays inside map View
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xE60F172A))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = String.format(Locale.getDefault(), "SAT LINK STABLE: (%.5f°, %.5f°)", lat, lng),
                                fontSize = 8.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                        }

                        // Floating zoom & controller overlays on right section of map
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconButton(
                                onClick = { zoomLevel = (zoomLevel + 0.25f).coerceAtMost(3.0f) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xE61E293B), RoundedCornerShape(6.dp))
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Zoom In", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { zoomLevel = (zoomLevel - 0.25f).coerceAtLeast(0.5f) },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xE61E293B), RoundedCornerShape(6.dp))
                            ) {
                                Icon(Icons.Filled.Remove, contentDescription = "Zoom Out", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Address lock on Satellite: $locationText",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated walking trigger on the map card
                    Button(
                        onClick = { viewModel.simulateWalkingMovement() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Filled.DirectionsWalk, contentDescription = "Simulate Moving", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Trigger Simulated Walking Trail (Live GPS Move)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().shadow(0.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Instant Response Search",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { searchResultText = "Nearest Hosp: Apollo Emergency Division (1.2 km away) - Phone: 108" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.LocalHospital, contentDescription = "Hospitals")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Find Hospitals", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { searchResultText = "Nearest Police: Central Security Enclosure station (0.6 km away) - Phone: 100" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.LocalActivity, contentDescription = "Police")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Find Police", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (searchResultText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = searchResultText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CameraXPreview(viewModel: EmergencyViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    val analysisExecutor = remember { java.util.concurrent.Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose {
            try {
                analysisExecutor.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().apply {
                        setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA // Default front lens for expressions

                    val analysis = androidx.camera.core.ImageAnalysis.Builder()
                        .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    analysis.setAnalyzer(analysisExecutor) { imageProxy ->
                        try {
                            val buffer = imageProxy.planes[0].buffer
                            val remaining = buffer.remaining()
                            if (remaining > 0) {
                                val data = ByteArray(remaining)
                                buffer.get(data)
                                var sum = 0L
                                for (i in 0 until data.size step 8) {
                                    sum += (data[i].toInt() and 0xFF)
                                }
                                val stepSize = data.size / 8f
                                val avgLuminance = if (stepSize > 0) sum / stepSize else 128f

                                var varianceSum = 0.0
                                var sampleCount = 0
                                for (i in 0 until data.size step 16) {
                                    val diff = (data[i].toInt() and 0xFF) - avgLuminance
                                    varianceSum += diff * diff
                                    sampleCount++
                                }
                                val variance = if (sampleCount > 0) Math.sqrt(varianceSum / sampleCount).toFloat() else 30f

                                viewModel.onCameraFrameAnalyzed(avgLuminance, variance)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            imageProxy.close()
                        }
                    }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        analysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
            previewView
        },
        modifier = modifier
    )
}

@Composable
fun CameraSection(viewModel: EmergencyViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    if (hasCameraPermission) {
        CameraXPreview(viewModel = viewModel, modifier = modifier)
    } else {
        Box(
            modifier = modifier.background(Color(0xFFE2E2E6)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = "No camera permission",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Front Camera Preview Blocked",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Grant Camera Access", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
