package com.example.frontend.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.testTag
import com.example.frontend.viewmodel.EmergencyViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE1F5FE),
                        Color(0xFFFFFFFF),
                        Color(0xFFEDE7F6)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Surface(
                tonalElevation = 8.dp,
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(32.dp))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = "App Shield Logo",
                        tint = Color(0xFF0288D1),
                        modifier = Modifier
                            .size(72.dp)
                            .animateContentSize()
                    )
                    Icon(
                        imageVector = Icons.Filled.RecordVoiceOver,
                        contentDescription = "Voice Overlay Logo",
                        tint = Color(0xFF6200EA),
                        modifier = Modifier
                            .size(36.dp)
                            .offset(y = 12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "OMNIGUARD AI",
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0288D1),
                lineHeight = 38.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "AI-Based Multi-Modal Emergency Detection System",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EA),
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Smart AI Protection for Real-Time Emergency Response",
                fontSize = 13.sp,
                color = Color(0xFF42474E),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(48.dp))
            CircularProgressIndicator(
                color = Color(0xFF0288D1),
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onOnboardingComplete: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        OnboardPage(
            title = "Voice Stress Diagnostics",
            desc = "Real-time AI voice classification continuously listens for screams, crying, panicking keywords or distressed sounds securely on-device.",
            icon = Icons.Filled.KeyboardVoice,
            color = Color(0xFF0288D1)
        ),
        OnboardPage(
            title = "Motion & Fall Detection",
            desc = "Advanced kinetic sensor tracking alerts contacts instantly during sudden falls, panicky running corridors, or unexpected immobilizations.",
            icon = Icons.Filled.DirectionsRun,
            color = Color(0xFF6200EA)
        ),
        OnboardPage(
            title = "Facial Emotion Recognition",
            desc = "Uses device front lenses to identify visual indicators of fear or shock expressions and scans coordinates to log physical consciousness warnings.",
            icon = Icons.Filled.Face,
            color = Color(0xFF00B0FF)
        ),
        OnboardPage(
            title = "Immediate Guardian Dispatch",
            desc = "Upon triggering, full GPS location maps, audio stress files, and AI risk confidence summaries sync automatically with rescue responders.",
            icon = Icons.Filled.ContactPhone,
            color = Color(0xFF00E676)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FA))
            .navigationBarsPadding()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OmniGuard AI",
                color = Color(0xFF0288D1),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            TextButton(onClick = onOnboardingComplete) {
                Text("Skip", color = Color(0xFF64748B))
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { index ->
            val page = pages[index]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = page.color.copy(alpha = 0.12f),
                    modifier = Modifier.size(140.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = page.icon,
                            contentDescription = page.title,
                            tint = page.color,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(36.dp))
                Text(
                    text = page.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = page.desc,
                    fontSize = 14.sp,
                    color = Color(0xFF42474E),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { idx ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == idx) 18.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == idx) Color(0xFF0288D1)
                                else Color(0xFFCFD8DC)
                            )
                    )
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < 3) {
                        coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onOnboardingComplete()
                    }
                },
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
            ) {
                Text(
                    text = if (pagerState.currentPage == 3) "Get Started" else "Next",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class OnboardPage(val title: String, val desc: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val color: Color)

@Composable
fun LoginScreen(
    viewModel: EmergencyViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToReset: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    var successText by remember { mutableStateOf("") }

    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var isForgotLoading by remember { mutableStateOf(false) }
    var forgotErrorText by remember { mutableStateOf("") }
    var forgotSuccessText by remember { mutableStateOf("") }
    var pastedLink by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE1F5FE),
                        Color(0xFFFFFFFF),
                        Color(0xFFEDE7F6)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.LocalActivity,
                contentDescription = "Log",
                tint = Color(0xFF0288D1),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Welcome to OmniGuard AI",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
            Text(
                text = if (isRegisterMode) "Register your emergency identity profile" else "Access your emergency workspace & controllers",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Premium Segmented Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEDE7F6))
                    .padding(4.dp)
            ) {
                listOf(false to "Sign In", true to "Register").forEach { (mode, title) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isRegisterMode == mode) Color(0xFF0288D1) else Color.Transparent)
                            .clickable { 
                                isRegisterMode = mode
                                errorText = ""
                                successText = ""
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = if (isRegisterMode == mode) Color.White else Color(0xFF6200EA),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Inputs based on mode
            if (isRegisterMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorText = "" },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth().testTag("name_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorText = "" },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth().testTag("email_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; errorText = "" },
                    label = { Text("Mobile Number (with country code)") },
                    modifier = Modifier.fillMaxWidth().testTag("phone_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorText = "" },
                    label = { Text("Create Password") },
                    modifier = Modifier.fillMaxWidth().testTag("password_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorText = "" },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth().testTag("email_input"),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorText = "" },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth().testTag("password_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (!isRegisterMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Forgot Password?",
                        color = Color(0xFF6200EA),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            showForgotDialog = true
                            forgotEmail = email
                            forgotErrorText = ""
                            forgotSuccessText = ""
                        }
                    )
                }
            }

            if (errorText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = errorText,
                        color = Color(0xFFFF1744),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (showForgotDialog) {
                AlertDialog(
                    onDismissRequest = { if (!isForgotLoading) showForgotDialog = false },
                    title = {
                        Text(
                            text = "Recover Password",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1A1C1E)
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "Enter your email address to receive a secure password recovery link from Supabase.",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = forgotEmail,
                                onValueChange = { forgotEmail = it; forgotErrorText = "" },
                                label = { Text("Email Address") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                enabled = !isForgotLoading
                            )
                            if (forgotErrorText.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = forgotErrorText,
                                    color = Color(0xFFFF1744),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (forgotSuccessText.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = forgotSuccessText,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Or, if clicking the email link opened a browser showing 'Site can't be reached', copy the link from the browser's address bar and paste it below:",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = pastedLink,
                                onValueChange = { pastedLink = it; forgotErrorText = "" },
                                label = { Text("Paste Recovery Link here") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                    },
                    confirmButton = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (pastedLink.isNotBlank()) {
                                val uri = try { android.net.Uri.parse(pastedLink.trim()) } catch (e: Exception) { null }
                                var token: String? = null
                                if (uri != null) {
                                    val fragment = uri.fragment
                                    if (!fragment.isNullOrEmpty()) {
                                        val params = fragment.split("&")
                                        for (param in params) {
                                            val parts = param.split("=")
                                            if (parts.size == 2 && parts[0] == "access_token") {
                                                token = parts[1]
                                            }
                                        }
                                    }
                                    if (token == null) {
                                        token = uri.getQueryParameter("access_token")
                                    }
                                }

                                if (token != null) {
                                    Button(
                                        onClick = {
                                            com.example.backend.api.SupabaseClient.accessToken = token
                                            showForgotDialog = false
                                            pastedLink = ""
                                            onNavigateToReset()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Proceed to Reset", color = Color.White)
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            forgotErrorText = "Invalid link structure. Please copy the complete URL."
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCFD8DC)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Invalid Link", color = Color(0xFF90A4AE))
                                    }
                                }
                            } else {
                                if (isForgotLoading) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF0288D1),
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Button(
                                        onClick = {
                                            if (forgotEmail.isBlank()) {
                                                forgotErrorText = "Please enter your email."
                                            } else {
                                                isForgotLoading = true
                                                forgotErrorText = ""
                                                forgotSuccessText = ""
                                                viewModel.recoverPassword(
                                                    email = forgotEmail,
                                                    onSuccess = {
                                                        isForgotLoading = false
                                                        forgotSuccessText = "Link sent! Please check your inbox/spam."
                                                    },
                                                    onFailure = { err ->
                                                        isForgotLoading = false
                                                        forgotErrorText = err
                                                    }
                                                )
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Send Link", color = Color.White)
                                    }
                                }
                            }
                        }
                    },
                    dismissButton = {
                        if (!isForgotLoading) {
                            TextButton(onClick = { showForgotDialog = false }) {
                                Text("Cancel", color = Color(0xFF6200EA))
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = Color.White
                )
            }

            if (successText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = successText,
                        color = Color(0xFF2E7D32),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF0288D1))
            } else {
                Button(
                    onClick = {
                        if (isRegisterMode) {
                            if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                                errorText = "Please fill out all fields."
                            } else {
                                isLoading = true
                                viewModel.registerUser(
                                    name = name,
                                    email = email,
                                    phone = phone,
                                    pass = password,
                                    onSuccess = { msg ->
                                        successText = "Registration successful! Signing in..."
                                        errorText = ""
                                        viewModel.loginUser(
                                            email = email,
                                            pass = password,
                                            onSuccess = {
                                                isLoading = false
                                                onLoginSuccess()
                                            },
                                            onFailure = { err ->
                                                isLoading = false
                                                errorText = err
                                                successText = ""
                                            }
                                        )
                                    },
                                    onFailure = { err ->
                                        isLoading = false
                                        errorText = err
                                        successText = ""
                                    }
                                )
                            }
                        } else {
                            if (email.isBlank() || password.isBlank()) {
                                errorText = "Please fill out credentials."
                            } else {
                                isLoading = true
                                viewModel.loginUser(
                                    email = email,
                                    pass = password,
                                    onSuccess = {
                                        isLoading = false
                                        onLoginSuccess()
                                    },
                                    onFailure = { err ->
                                        isLoading = false
                                        errorText = err
                                    }
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                ) {
                    Text(
                        text = if (isRegisterMode) "Register Emergency Profile" else "Sign In & Unlock App",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ResetPasswordScreen(
    viewModel: EmergencyViewModel,
    onResetSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    var successText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE1F5FE),
                        Color(0xFFFFFFFF),
                        Color(0xFFEDE7F6)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Reset Password Icon",
                tint = Color(0xFF0288D1),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Reset Your Password",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
            Text(
                text = "Enter your new password below to update your security credentials.",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorText = "" },
                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth().testTag("new_password_input"),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; errorText = "" },
                label = { Text("Confirm New Password") },
                modifier = Modifier.fillMaxWidth().testTag("confirm_password_input"),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Password
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (errorText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = errorText,
                        color = Color(0xFFFF1744),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (successText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = successText,
                        color = Color(0xFF2E7D32),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF0288D1))
            } else {
                Button(
                    onClick = {
                        if (password.isBlank() || confirmPassword.isBlank()) {
                            errorText = "Please fill out all fields."
                        } else if (password.length < 6) {
                            errorText = "Password must be at least 6 characters."
                        } else if (password != confirmPassword) {
                            errorText = "Passwords do not match."
                        } else {
                            isLoading = true
                            viewModel.changePassword(
                                newPassword = password,
                                onSuccess = {
                                    isLoading = false
                                    successText = "Password updated successfully!"
                                    scope.launch {
                                        delay(1500)
                                        onResetSuccess()
                                    }
                                },
                                onFailure = { err ->
                                    isLoading = false
                                    errorText = err
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("update_password_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                ) {
                    Text(
                        text = "Update Password",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth().testTag("cancel_reset_btn")
                ) {
                    Text(
                        text = "Cancel & Return to Login",
                        color = Color(0xFF6200EA),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
