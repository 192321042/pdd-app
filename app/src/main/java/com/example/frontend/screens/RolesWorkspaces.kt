package com.example.frontend.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.viewmodel.EmergencyViewModel

// 1. Admin Master Panel Screen
@Composable
fun AdminDashboardScreen(viewModel: EmergencyViewModel) {
    val logsList by viewModel.incidentLogs.collectAsState()
    var systemStatusText by remember { mutableStateOf("All security microservices are green & listening.") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        item {
            Text(
                text = "Emergency AI Server Controller Hub",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "System Admin Authority Dashboard Space",
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold
            )
        }

        // Analytical Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    title = "Daily Threats Prevented",
                    stat = "1,424",
                    color = Color(0xFF0288D1),
                    icon = Icons.Filled.SafetyCheck,
                    modifier = Modifier.weight(1f)
                )

                AdminStatCard(
                    title = "Rescue Dispatch Rate",
                    stat = "99.8%",
                    color = Color(0xFF00E676),
                    icon = Icons.Filled.FlashOn,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Server microservices checklists
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().shadow(0.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "System Health Monitoring",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ServiceStatusTile(name = "Speech Emotion Transformer Model", healthy = true)
                    ServiceStatusTile(name = "Biometric Decoupled Handshake Handler", healthy = true)
                    ServiceStatusTile(name = "Room Database Telemetry Sync Service", healthy = true)
                    ServiceStatusTile(name = "Google Maps Geofence Boundary Polling", healthy = true)

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { systemStatusText = "Diagnostic complete: All standard nodes are running at 4ms ping latency." },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Trigger Global Integrity Scan", fontWeight = FontWeight.Bold)
                    }

                    if (systemStatusText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = systemStatusText,
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Direct AI Alert Audit Trail list
        item {
            Text(
                text = "Live Dispatch System Audit Trail",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (logsList.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "No diagnostic events or incidents logged.",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        } else {
            items(logsList) { log ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = log.type, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Anchor coordinates: ${log.liveLocation}", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "TRIPPED • ${log.aiConfidenceScore}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFF1744)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    stat: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.shadow(0.5.dp, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stat, fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
            Text(text = title, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ServiceStatusTile(name: String, healthy: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, fontSize = 12.sp, color = Color(0xFF1E293B))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (healthy) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (healthy) "OPERATIONAL" else "FAILURE",
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = if (healthy) Color(0xFF2E7D32) else Color(0xFFFF1744)
            )
        }
    }
}


// 2. Rescue Team / Emergency Dispatch Control Center Screen
@Composable
fun RescueTeamDashboardScreen(viewModel: EmergencyViewModel) {
    val logsList by viewModel.incidentLogs.collectAsState()
    var dispatchStatusText by remember { mutableStateOf("Ready to receive distress data.") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        item {
            Text(
                text = "Responder Dispatch Center",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Primary Mobilization Dashboard Control Room",
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.SemiBold
            )
        }

        // Active Emergency dispatch checklist
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().shadow(0.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mobilize Dispatch Unit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE3F2FD))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Active Units: 4 online", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Trigger dispatch instructions and coordinates immediately to nearby ambulances or policing blocks.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { dispatchStatusText = "POLICE TASK FORCE DISPATCHED to telemetry lock coordinates." },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Dispatch SWAT/Police", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { dispatchStatusText = "AMBULANCE CORE mobilised with sirens. Routing ETA: 4 mins." },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Mobilize Paramedics", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (dispatchStatusText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = dispatchStatusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0288D1),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // Active Emergency Distress Board
        item {
            Text(
                text = "Active Incident Distress Register",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (logsList.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "No current distress telemetry signals recorded.",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        } else {
            items(logsList) { log ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Subject: Amulya Ammu", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFFEBEE))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "THREAT LEVEL: HIGH",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFF1744)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Vitals report: ${log.description}", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "GPS Coordinate Sync: ${log.liveLocation}", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { dispatchStatusText = "Mobilized responders to trace coordinate point: ${log.liveLocation} for ${log.type}" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Sponsor Instant Intercept Rescue Force", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
