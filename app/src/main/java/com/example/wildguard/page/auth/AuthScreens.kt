package com.example.wildguard.page.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wildguard.R
import com.example.wildguard.auth.AuthViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage

private val DarkGreen = Color(0xFF154E35)
private val ButtonGreen = Color(0xFF1F7A4D)
private val Mint = Color(0xFFEAF6EF)

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onOpenSignUp: () -> Unit,
    onForgotPassword: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Outlined.ArrowBack, "Back", tint = DarkGreen)
                }
            }
            BrandHeader()
            Spacer(Modifier.height(42.dp))
            Text("Welcome Back", fontSize = 31.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
            // Keep the login form closer to the visual center of the screen.
            Spacer(Modifier.height(56.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = .93f), RoundedCornerShape(26.dp))
                    .padding(18.dp)
            ) {
                AuthField(email, { email = it }, "Email Address", "Enter your email", Icons.Outlined.Email, KeyboardType.Email)
                Spacer(Modifier.height(10.dp))
                AuthField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Enter your password",
                    leadingIcon = Icons.Outlined.Lock,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )
                Text(
                    "Forgot Password?",
                    modifier = Modifier.align(Alignment.End).padding(top = 7.dp).clickable(onClick = onForgotPassword),
                    color = ButtonGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(14.dp))
            PrimaryAuthButton("Log In", authViewModel.isBusy) {
                authViewModel.login(email, password)
            }
            AuthMessage(authViewModel.message)
            SocialSection(
                onGoogle = authViewModel::loginWithGoogle,
                enabled = !authViewModel.isBusy
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account? ", color = Color(0xFF789388), fontSize = 13.sp)
                Text("Sign Up", modifier = Modifier.clickable(onClick = onOpenSignUp), color = ButtonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
fun RoleSelectionScreen(
    onUserSelected: () -> Unit,
    onAdminSelected: () -> Unit
) {
    AuthBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 26.dp, end = 26.dp, top = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BrandHeader()
            Spacer(Modifier.height(34.dp))
            Text("Welcome to WildGuard", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
            Text("Choose how you want to continue", color = Color.White, fontSize = 14.sp)
            Spacer(Modifier.height(34.dp))
            Button(
                onClick = onUserSelected,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen)
            ) {
                Icon(Icons.Outlined.Person, null)
                Spacer(Modifier.size(10.dp))
                Text("Continue as User", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = onAdminSelected,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFA8D5A2),
                    contentColor = Color(0xFF123D2A)
                )
            ) {
                Icon(Icons.Outlined.AdminPanelSettings, null)
                Spacer(Modifier.size(10.dp))
                Text("Continue as Admin", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun AdminLoginScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AuthBackground {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.Outlined.ArrowBack, "Back", tint = DarkGreen)
                }
            }
            BrandHeader()
            Spacer(Modifier.height(34.dp))
            Text("Admin Login", fontSize = 31.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
            Spacer(Modifier.height(44.dp))
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(Color.White.copy(alpha = .93f), RoundedCornerShape(26.dp)).padding(18.dp)
            ) {
                AuthField(username, { username = it; errorMessage = null }, "Username", "Enter admin username", Icons.Outlined.Person)
                Spacer(Modifier.height(10.dp))
                AuthField(
                    password, { password = it; errorMessage = null }, "Password", "Enter admin password",
                    Icons.Outlined.Lock, isPassword = true, passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )
            }
            Spacer(Modifier.height(16.dp))
            PrimaryAuthButton("Log In as Admin", busy = authViewModel.isBusy) {
                errorMessage = null
                authViewModel.loginAdmin(username, password, onLoginSuccess)
            }
            AuthMessage(errorMessage ?: authViewModel.message)
        }
    }
}

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onBackToLogin: () -> Unit,
    onVerificationSent: (email: String, password: String) -> Unit
) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreed by remember { mutableStateOf(false) }
    var avatarUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showTerms by remember { mutableStateOf(false) }
    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        avatarUri = it
    }

    if (showTerms) {
        SignUpTermsConditionsPage(onBack = { showTerms = false })
        return
    }

    AuthBackground {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))
            BrandHeader()
            Text("Create Account", fontSize = 29.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth().background(Color.White.copy(alpha = .94f), RoundedCornerShape(26.dp)).padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(82.dp).background(Color(0xFFE7F2EA), CircleShape)
                            .border(2.dp, Color.Black, CircleShape)
                            .clickable { avatarPicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatarUri != null) {
                            AsyncImage(
                                model = avatarUri,
                                contentDescription = "Selected profile photo",
                                modifier = Modifier.fillMaxSize().padding(3.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Outlined.CameraAlt, null, tint = ButtonGreen, modifier = Modifier.size(31.dp))
                        }
                    }
                    Text(
                        if (avatarUri == null) "Add Profile Photo" else "Change Profile Photo",
                        color = ButtonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { avatarPicker.launch("image/*") }.padding(vertical = 6.dp)
                    )
                }
                AuthField(fullName, { fullName = it }, "Full Name", "Enter your full name", Icons.Outlined.Person)
                Spacer(Modifier.height(7.dp))
                AuthField(email, { email = it }, "Email Address", "Enter your email", Icons.Outlined.Email, KeyboardType.Email)
                Spacer(Modifier.height(7.dp))
                AuthField(phone, { phone = it }, "Phone Number", "+60 12-3456789", Icons.Outlined.Phone, KeyboardType.Phone)
                Spacer(Modifier.height(7.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(Modifier.weight(1f)) { AuthField(password, { password = it }, "Password", "Password", Icons.Outlined.Lock, isPassword = true) }
                    Box(Modifier.weight(1f)) { AuthField(confirmPassword, { confirmPassword = it }, "Confirm", "Confirm", Icons.Outlined.Lock, isPassword = true) }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = agreed,
                        onCheckedChange = { agreed = it },
                        colors = CheckboxDefaults.colors(checkedColor = ButtonGreen),
                        modifier = Modifier.size(30.dp)
                    )
                    Text("I agree to the ", color = Color(0xFF60766B), fontSize = 11.sp)
                    Text(
                        "Terms & Conditions",
                        color = ButtonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showTerms = true }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            PrimaryAuthButton("Create Account", authViewModel.isBusy, enabled = agreed) {
                authViewModel.signUp(
                    fullName, email, phone, password, confirmPassword,
                    avatarUri, context, { onVerificationSent(email.trim(), password) }
                )
            }
            AuthMessage(authViewModel.message)
            SocialSection(authViewModel::loginWithGoogle, !authViewModel.isBusy)
            Row {
                Text("Already have an account? ", color = Color(0xFF789388), fontSize = 13.sp)
                Text("Log In", modifier = Modifier.clickable(onClick = onBackToLogin), color = ButtonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AuthBackground(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize().background(Mint)) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(Color(0xFFD8F2E3), w * .10f, Offset(w * .13f, h * .08f))
            drawCircle(Color(0xFFD6EFDF), w * .07f, Offset(w * .88f, h * .15f))
            val back = Path().apply { moveTo(0f, h * .33f); lineTo(w * .23f, h * .19f); lineTo(w * .47f, h * .36f); lineTo(w * .67f, h * .15f); lineTo(w, h * .34f); lineTo(w, h); lineTo(0f, h); close() }
            drawPath(back, Color(0xFF91CDA8))
            val front = Path().apply { moveTo(0f, h * .46f); lineTo(w * .35f, h * .25f); lineTo(w * .57f, h * .42f); lineTo(w * .78f, h * .23f); lineTo(w, h * .38f); lineTo(w, h); lineTo(0f, h); close() }
            drawPath(front, Color(0xFF3D8B61))
            drawRect(DarkGreen, Offset(0f, h * .57f), androidx.compose.ui.geometry.Size(w, h * .43f))
        }
        content()
    }
}

@Composable
private fun BrandHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painterResource(R.drawable.wildguard_logo),
            "WildGuard logo",
            tint = Color.Unspecified,
            modifier = Modifier.size(62.dp).clip(CircleShape)
        )
        Text("WildGuard", color = DarkGreen, fontWeight = FontWeight.Bold, fontSize = 21.sp)
    }
}

@Composable
fun VerificationEmailScreen(
    email: String,
    authViewModel: AuthViewModel,
    verified: Boolean,
    onCheckVerification: () -> Unit,
    onContinue: () -> Unit,
    onBackToLogin: () -> Unit
) {
    AuthBackground {
        Column(
            Modifier.fillMaxSize().padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                if (verified) Icons.Outlined.CheckCircle else Icons.Outlined.MarkEmailRead,
                null,
                tint = if (verified) ButtonGreen else DarkGreen,
                modifier = Modifier.size(86.dp)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                if (verified) "You're verified" else "Check Your Email",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                if (verified) "Your email has been verified successfully."
                else "We sent a verification link to\n$email",
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(top = 10.dp, bottom = 24.dp)
            )
            if (verified) {
                PrimaryAuthButton("Continue to WildGuard", false, onClick = onContinue)
            } else {
                PrimaryAuthButton("I've verified", authViewModel.isBusy, onClick = onCheckVerification)
                Spacer(Modifier.height(10.dp))
                PrimaryAuthButton("Resend Verification Email", authViewModel.isBusy) {
                    authViewModel.resendVerification(email)
                }
                AuthMessage(authViewModel.message)
                Text(
                    "Back to Login",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(18.dp).clickable(onClick = onBackToLogin)
                )
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }
    var otpVerified by remember { mutableStateOf(false) }

    if (otpVerified) {
        NewPasswordScreen(
            authViewModel = authViewModel,
            onPasswordUpdated = onBack
        )
        return
    }
    AuthBackground {
        Box(Modifier.fillMaxSize().imePadding()) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Lock, null, tint = DarkGreen, modifier = Modifier.size(72.dp))
                Text("Reset Password", fontSize = 29.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
                Text(
                    if (emailSent) "Enter the 8-digit OTP sent to your email."
                    else "Enter the email address registered with WildGuard.",
                    color = Color.White, textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 18.dp)
                )
                if (!emailSent) {
                    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(22.dp)).padding(18.dp)) {
                        AuthField(email, { email = it }, "Email Address", "Enter your email", Icons.Outlined.Email, KeyboardType.Email)
                    }
                    Spacer(Modifier.height(16.dp))
                    PrimaryAuthButton("Send OTP", authViewModel.isBusy) {
                        authViewModel.sendPasswordResetOtp(email) { emailSent = true }
                    }
                    AuthMessage(authViewModel.message)
                } else {
                    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(22.dp)).padding(18.dp)) {
                        AuthField(
                            otp,
                            { value -> otp = value.filter(Char::isDigit).take(8) },
                            "OTP Code",
                            "Enter 8-digit OTP",
                            Icons.Outlined.Lock,
                            KeyboardType.Number
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    PrimaryAuthButton("I've verified", authViewModel.isBusy) {
                        authViewModel.verifyPasswordResetOtp(email, otp) { otpVerified = true }
                    }
                    Spacer(Modifier.height(10.dp))
                    TextButton(
                        onClick = { authViewModel.sendPasswordResetOtp(email) {} },
                        enabled = !authViewModel.isBusy
                    ) { Text("Resend OTP", color = Color.White, fontWeight = FontWeight.Bold) }
                    AuthMessage(authViewModel.message)
                }
            }
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 12.dp, top = 24.dp)
            ) { Icon(Icons.Outlined.ArrowBack, "Back", tint = DarkGreen) }
        }
    }
}

@Composable
private fun SignUpTermsConditionsPage(onBack: () -> Unit) {
    AuthBackground {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().statusBarsPadding().height(62.dp).background(DarkGreen),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, "Back", tint = Color.White)
                }
                Text("Terms & Conditions", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
                Text("Effective date: 4 September 2026", color = ButtonGreen, fontWeight = FontWeight.Bold)
                TermsParagraph("1. Acceptance", "By creating an account or using WildGuard, you agree to these Terms & Conditions. If you do not agree, do not create an account or use the application.")
                TermsParagraph("2. Accurate reports", "You must provide truthful account information and submit genuine wildlife reports. False, illegal, abusive or misleading reports are prohibited.")
                TermsParagraph("3. Location information", "WildGuard uses location information to display maps, identify nearby reports and support incident response. Community reports may be incomplete or inaccurate.")
                TermsParagraph("4. Privacy and security", "You are responsible for protecting your login credentials. Profile, report and location information is used only for application and safety-related features.")
                TermsParagraph("5. Safety disclaimer", "WildGuard is an information and reporting tool, not an emergency service. Keep a safe distance from wildlife and contact the relevant authority during immediate danger.")
                TermsParagraph("6. Account suspension", "Accounts that misuse the service, submit harmful content or violate these terms may be restricted or permanently removed.")
                Spacer(Modifier.height(16.dp))
                PrimaryAuthButton("Back to Create Account", false, onClick = onBack)
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TermsParagraph(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = .94f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = DarkGreen, fontWeight = FontWeight.Bold)
            Text(body, color = Color(0xFF4D5E55), fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

@Composable
fun NewPasswordScreen(
    authViewModel: AuthViewModel,
    onPasswordUpdated: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    AuthBackground {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Outlined.Refresh, null, tint = DarkGreen, modifier = Modifier.size(72.dp))
            Text("Create New Password", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
            Text("Enter and confirm your new password.", color = Color(0xFF536E60), modifier = Modifier.padding(vertical = 18.dp))
            Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(22.dp)).padding(18.dp)) {
                AuthField(password, { password = it }, "New Password", "At least 6 characters", Icons.Outlined.Lock, isPassword = true, passwordVisible = visible, onTogglePassword = { visible = !visible })
                Spacer(Modifier.height(12.dp))
                AuthField(confirmPassword, { confirmPassword = it }, "Confirm Password", "Enter password again", Icons.Outlined.Lock, isPassword = true, passwordVisible = visible, onTogglePassword = { visible = !visible })
            }
            Spacer(Modifier.height(16.dp))
            PrimaryAuthButton("Update Password", authViewModel.isBusy) {
                authViewModel.updatePassword(password, confirmPassword, onPasswordUpdated)
            }
            AuthMessage(authViewModel.message)
        }
    }
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    Column {
        Text(label, color = Color(0xFF536E60), fontSize = 11.sp, modifier = Modifier.padding(start = 2.dp, bottom = 3.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(placeholder, fontSize = 12.sp, color = Color(0xFF9BA9A2)) },
            leadingIcon = { Icon(leadingIcon, null, modifier = Modifier.size(18.dp), tint = Color(0xFF719181)) },
            trailingIcon = if (isPassword && onTogglePassword != null) {{
                IconButton(onClick = onTogglePassword) {
                    Icon(if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, null, modifier = Modifier.size(18.dp), tint = Color(0xFF719181))
                }
            }} else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
            shape = RoundedCornerShape(13.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F8F6), unfocusedContainerColor = Color(0xFFF5F8F6),
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                disabledBorderColor = Color.Black.copy(alpha = .55f)
            )
        )
    }
}

@Composable
private fun PrimaryAuthButton(text: String, busy: Boolean, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled && !busy,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen)
    ) {
        if (busy) CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
        else Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SocialSection(onGoogle: () -> Unit, enabled: Boolean) {
    Row(Modifier.fillMaxWidth().padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), color = Color.White.copy(alpha = .55f))
        Text("  or continue with  ", color = Color.White.copy(alpha = .72f), fontSize = 11.sp)
        HorizontalDivider(Modifier.weight(1f), color = Color.White.copy(alpha = .55f))
    }
    GoogleSignInButton(enabled = enabled, onClick = onGoogle)
    Spacer(Modifier.height(14.dp))
}

@Composable
private fun GoogleSignInButton(enabled: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(Color.White, RoundedCornerShape(15.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text("G", color = Color(0xFF4285F4), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.width(12.dp))
        Text("Continue with Google", color = Color(0xFF202124), fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AuthMessage(message: String?) {
    if (!message.isNullOrBlank()) {
        Text(message, color = Color(0xFFFFE3A0), fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
    }
}
