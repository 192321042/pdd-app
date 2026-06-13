package com.example.frontend.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.backend.database.Contact
import com.example.frontend.viewmodel.EmergencyViewModel

// 1. SOS HUD Screen
@Composable
fun SosScreen(viewModel: EmergencyViewModel, onDismiss: () -> Unit) {
    val countdown by viewModel.sosCountdownTimer.collectAsState()
    val activeSosType by viewModel.sosType.collectAsState()
    val isSosActive by viewModel.isSosTriggered.collectAsState()
    val locationText by viewModel.currentLocationString.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF1F0))
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.CrisisAlert,
                contentDescription = "SOS Warning",
                tint = Color(0xFFFF1744),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "EMERGENCY BEACON DISPATCHED",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFF1744),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Telemetry & location files are streaming to local police networks and response teams.",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(195.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF1744))
                .clickable {
                    viewModel.cancelActiveSos()
                    onDismiss()
                }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$countdown",
                    color = Color.White,
                    fontSize = 68.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "TAP TO CANCEL",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "(FALSE ALARM)",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Real-Time Emergency Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Trigger mechanism: $activeSosType", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF1744))
                    Text("GPS Anchor: $locationText", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            val context = androidx.compose.ui.platform.LocalContext.current
            Button(
                onClick = {
                    viewModel.broadcastSmsViaSystemApp(context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Manual Send Backup SMS",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Manual Backup SMS to Guardians", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.broadcastWhatsAppViaSystemApp(context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), // WhatsApp Brand Green
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Manual Send WhatsApp",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Manual SOS Alert on WhatsApp", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.cancelActiveSos()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Cancel,
                    contentDescription = "Cancel Alert",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Deactivate Panic Beacon (False Alert)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

// 2. Contacts Registry Screen
@Composable
fun ContactsScreen(viewModel: EmergencyViewModel) {
    val contactList by viewModel.contacts.collectAsState()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("Family") }
    var isPrimary by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
            .padding(16.dp),
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
                        text = "Add Trusted Guardian Responder",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1A1C1E)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Guardian Name") },
                        modifier = Modifier.fillMaxWidth().testTag("guardian_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number / SMS Endpoint") },
                        modifier = Modifier.fillMaxWidth().testTag("guardian_phone_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Relation: ", fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                        listOf("Family", "Police", "Friend", "Doctor").forEach { rel ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (relationship == rel) Color(0xFFE3F2FD) else Color(0xFFF1F5F9))
                                    .clickable { relationship = rel }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = rel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (relationship == rel) Color(0xFF0288D1) else Color(0xFF64748B))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isPrimary, onCheckedChange = { isPrimary = it })
                        Text("Designate as primary SMS/Vitals target", fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank() && phone.isNotBlank()) {
                                viewModel.addNewEmergencyContact(name, phone, relationship, isPrimary)
                                name = ""
                                phone = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Register Trusted Contact", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "Registered Guardians & Authorities (${contactList.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        if (contactList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No guardians registered yet. Press above to add.", color = Color(0xFF64748B))
                }
            }
        } else {
            items(contactList) { item ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (item.isPrimary) Color(0xFFFFEBEE) else Color(0xFFE3F2FD),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (item.relationship == "Police") Icons.Filled.Policy else Icons.Filled.Person,
                                        contentDescription = "Contact icon",
                                        tint = if (item.isPrimary) Color(0xFFFF1744) else Color(0xFF0288D1),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    if (item.isPrimary) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFFFEBEE))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text("PRIMARY", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF1744))
                                        }
                                    }
                                }
                                Text(text = "${item.relationship} • ${item.phone}", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                        }

                        IconButton(onClick = { viewModel.removeEmergencyContact(item.id) }) {
                            Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = "Remove contact", tint = Color(0xFFFF1744))
                        }
                    }
                }
            }
        }
    }
}

// 3. Multilingual OpenAI/Gemini Chatbot Screen
@Composable
fun ChatbotScreen(viewModel: EmergencyViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val isBotLoading by viewModel.isChatLoading.collectAsState()
    val currentLanguage by viewModel.chatbotLanguage.collectAsState()
    var inputQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
    ) {
        // Multi-Language Guidance Switcher
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "English" to "Guide Me",
                    "Telugu" to "సహాయం చేయండి",
                    "Hindi" to "सुरक्षा निर्देश",
                    "Tamil" to "உதவி பெறு"
                ).forEach { lang ->
                    val isSelected = currentLanguage == lang.first
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF6200EA) else Color(0xFFEDE7F6))
                            .clickable {
                                viewModel.setChatbotLanguage(lang.first)
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = lang.first, 
                                fontSize = 9.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = if (isSelected) Color.White else Color(0xFF6200EA)
                            )
                            Text(
                                text = lang.second, 
                                fontSize = 10.sp, 
                                fontWeight = FontWeight.Black, 
                                color = if (isSelected) Color.White else Color(0xFF6200EA)
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.sender == "User"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 2.dp,
                            bottomEnd = if (isMe) 2.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) Color(0xFFE3F2FD) else Color.White
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.sender,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMe) Color(0xFF0288D1) else Color(0xFF6200EA)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.message,
                                fontSize = 13.sp,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }

            if (isBotLoading) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF6200EA), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Guardian is consulting rescue resources...", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }
            }
        }

        // Input chat box
        Surface(
            tonalElevation = 8.dp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth().navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = { Text("Type distress question or instructions...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (inputQuery.isNotBlank()) {
                                viewModel.sendUserChatMessage(inputQuery)
                                inputQuery = ""
                            }
                        }) {
                            Icon(imageVector = Icons.Filled.Send, contentDescription = "Send", tint = Color(0xFF0288D1))
                        }
                    }
                )
            }
        }
    }
}

// 4. Incident Reports Logs Database
@Composable
fun IncidentReportsScreen(viewModel: EmergencyViewModel) {
    val incidentLogs by viewModel.incidentLogs.collectAsState()
    var exportProgressText by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Incident Compliance Logs Engine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        text = "Encapsulates sensor parameters, audio transcripts, vital telemetry, and location routes legally for medical & judicial review.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                exportProgressText = viewModel.exportIncidentLogsAsPdf(context)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.PictureAsPdf, contentDescription = "PDF")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export PDF Report", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.clearHistory() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Purge Database Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (exportProgressText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (exportProgressText.startsWith("Error")) Color(0xFFFFEBEE) else Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = exportProgressText,
                                fontSize = 11.sp,
                                color = if (exportProgressText.startsWith("Error")) Color(0xFFFF1744) else Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (incidentLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No historic telemetry logged offline. Clean secure state.", color = Color(0xFF64748B))
                }
            }
        } else {
            items(incidentLogs) { log ->
                val dateStr = java.text.SimpleDateFormat("MMM dd, yyyy • hh:mm a 'IST'", java.util.Locale.getDefault()).apply {
                    timeZone = java.util.TimeZone.getTimeZone("Asia/Kolkata")
                }.format(java.util.Date(log.timestamp))
                val isCancelled = log.alertStatus.equals("Cancelled", ignoreCase = true)
                val statusColor = if (isCancelled) Color(0xFF64748B) else Color(0xFFFF1744)
                
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                    ) {
                        // Left-side status indicator bar
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight()
                                .background(statusColor)
                        )
                        
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Top Row: Type and Status Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (log.alert_trigger_source) {
                                            "Voice" -> Icons.Filled.Mic
                                            "Face" -> Icons.Filled.Face
                                            "Motion" -> Icons.Filled.DirectionsRun
                                            else -> Icons.Filled.CrisisAlert
                                        },
                                        contentDescription = "Trigger source",
                                        tint = statusColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = log.type,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                                
                                // Status Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isCancelled) Color(0xFFF1F5F9) else Color(0xFFFFEBEE))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = log.alertStatus.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isCancelled) Color(0xFF475569) else Color(0xFFFF1744)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Timestamp
                            Text(
                                text = dateStr,
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Description
                            Text(
                                text = log.description,
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                lineHeight = 18.sp
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Bottom Telemetry Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // GPS Location Row
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = "Location",
                                        tint = Color(0xFF6200EA),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (log.liveLocation.contains("(")) log.liveLocation.substringBefore("(").trim() else log.liveLocation,
                                        fontSize = 11.sp,
                                        color = Color(0xFF475569),
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                                
                                // AI Confidence Badge
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Analytics,
                                        contentDescription = "Analysis",
                                        tint = Color(0xFF0288D1),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "AI: ${log.aiConfidenceScore}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0288D1)
                                    )
                                }
                            }

                            // Alerted Contacts if present
                            if (!log.alerted_contact_names.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.People,
                                        contentDescription = "Contacts Alerted",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Alerted: ${log.alerted_contact_names}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF475569)
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

// 5. Notifications Hub Screen
@Composable
fun NotificationsScreen(viewModel: EmergencyViewModel) {
    val alerts by viewModel.notifications.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Emergency Response Console Notifications",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1A1C1E)
            )
        }

        if (alerts.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No immediate system alerts generated.", color = Color(0xFF64748B))
                }
            }
        } else {
            items(alerts) { alert ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (alert.type) {
                                "Critical" -> Icons.Filled.Error
                                "Warning" -> Icons.Filled.Warning
                                "Success" -> Icons.Filled.CheckCircle
                                else -> Icons.Filled.Info
                            },
                            contentDescription = alert.type,
                            tint = when (alert.type) {
                                "Critical" -> Color(0xFFFF1744)
                                "Warning" -> Color(0xFFFF9100)
                                "Success" -> Color(0xFF00E676)
                                else -> Color(0xFF0288D1)
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = alert.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = alert.description,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 6. Settings & Profile Screen
@Composable
fun ProfileScreen(viewModel: EmergencyViewModel, onLogout: () -> Unit) {
    val currentName by viewModel.userName.collectAsState()
    val currentEmail by viewModel.userEmail.collectAsState()
    val currentMobile by viewModel.userMobile.collectAsState()

    val bloodType by viewModel.bloodType.collectAsState()
    val allergies by viewModel.allergies.collectAsState()
    val majorConditions by viewModel.majorConditions.collectAsState()
    val languages by viewModel.languages.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(currentName) }
    var editEmail by remember { mutableStateOf(currentEmail) }
    var editMobile by remember { mutableStateOf(currentMobile) }

    var isEditingHealth by remember { mutableStateOf(false) }
    var editBloodType by remember { mutableStateOf(bloodType) }
    var editAllergies by remember { mutableStateOf(allergies) }
    var editConditions by remember { mutableStateOf(majorConditions) }
    var editLanguages by remember { mutableStateOf(languages) }

    var isChangingPassword by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var isPasswordLoading by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf("") }
    var passwordSuccess by remember { mutableStateOf("") }

    LaunchedEffect(currentName, currentEmail, currentMobile) {
        editName = currentName
        editEmail = currentEmail
        editMobile = currentMobile
    }

    LaunchedEffect(bloodType, allergies, majorConditions, languages) {
        editBloodType = bloodType
        editAllergies = allergies
        editConditions = majorConditions
        editLanguages = languages
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE7F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "User Avatar",
                            tint = Color(0xFF6200EA),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isEditing) {
                        Text(
                            text = currentName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1D2939)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentEmail,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mobile: $currentMobile",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = { isEditing = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit Profile", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Contact Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { /* Read-only primary email ID */ },
                            label = { Text("Email Address (Primary Identity)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = editMobile,
                            onValueChange = { editMobile = it },
                            label = { Text("Mobile Number") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updateUserProfile(editName, editEmail, editMobile)
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { isEditing = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Cancel", fontWeight = FontWeight.Bold)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Emergency Health Summary Profile",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (!isEditingHealth) {
                            IconButton(onClick = { isEditingHealth = true }, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit Health Summary",
                                    tint = Color(0xFF0288D1),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    if (!isEditingHealth) {
                        Text("Blood Type: $bloodType", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Allergies: $allergies", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Major Conditions: $majorConditions", fontSize = 11.sp, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Language Toggles: $languages", fontSize = 11.sp, color = Color(0xFF64748B))
                    } else {
                        OutlinedTextField(
                            value = editBloodType,
                            onValueChange = { editBloodType = it },
                            label = { Text("Blood Type") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editAllergies,
                            onValueChange = { editAllergies = it },
                            label = { Text("Allergies") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editConditions,
                            onValueChange = { editConditions = it },
                            label = { Text("Major Conditions") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editLanguages,
                            onValueChange = { editLanguages = it },
                            label = { Text("Language Toggles") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updateHealthSummary(editBloodType, editAllergies, editConditions, editLanguages)
                                    isEditingHealth = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = {
                                    editBloodType = bloodType
                                    editAllergies = allergies
                                    editConditions = majorConditions
                                    editLanguages = languages
                                    isEditingHealth = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Cancel", fontWeight = FontWeight.Bold)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Security Password Icon",
                            tint = Color(0xFF0288D1),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Security & Credentials",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Update your account password securely in the Supabase cloud database.",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isChangingPassword) {
                        Button(
                            onClick = {
                                isChangingPassword = true
                                newPassword = ""
                                passwordError = ""
                                passwordSuccess = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Change Account Password", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it; passwordError = "" },
                            label = { Text("Enter New Password") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            enabled = !isPasswordLoading
                        )
                        if (passwordError.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = passwordError, color = Color(0xFFFF1744), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        if (passwordSuccess.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = passwordSuccess, color = Color(0xFF2E7D32), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isPasswordLoading) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color(0xFF0288D1), modifier = Modifier.size(24.dp))
                                }
                            } else {
                                Button(
                                    onClick = {
                                        if (newPassword.length < 6) {
                                            passwordError = "Password must be at least 6 characters."
                                        } else {
                                            isPasswordLoading = true
                                            passwordError = ""
                                            passwordSuccess = ""
                                            viewModel.changePassword(
                                                newPassword = newPassword,
                                                onSuccess = {
                                                    isPasswordLoading = false
                                                    passwordSuccess = "Password updated successfully!"
                                                    newPassword = ""
                                                },
                                                onFailure = { err ->
                                                    isPasswordLoading = false
                                                    passwordError = err
                                                }
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Save", fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { isChangingPassword = false },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Cancel", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = "Log Out", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out Session", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}



@Composable
fun CallGridItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.size(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}
