package com.example.wildguard.page.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wildguard.R
import com.example.wildguard.auth.AuthViewModel

private val DarkGreen = Color(0xFF154E35)
private val ButtonGreen = Color(0xFF1F7A4D)
private val Mint = Color(0xFFEAF6EF)

@Composable
fun LoginScreen(authViewModel: AuthViewModel, onOpenSignUp: () -> Unit) {
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
            BrandHeader()
            Spacer(Modifier.height(42.dp))
            Text("Welcome Back", fontSize = 31.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
            Text("Protect wildlife together", color = Color(0xFF6C8A7A), fontSize = 13.sp)
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
                    modifier = Modifier.align(Alignment.End).padding(top = 7.dp),
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
                onFacebook = authViewModel::loginWithFacebook,
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
fun SignUpScreen(authViewModel: AuthViewModel, onBackToLogin: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreed by remember { mutableStateOf(false) }

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
                    Text("I agree to the Terms & Privacy Policy", color = Color(0xFF60766B), fontSize = 11.sp)
                }
            }

            Spacer(Modifier.height(10.dp))
            PrimaryAuthButton("Create Account", authViewModel.isBusy, enabled = agreed) {
                authViewModel.signUp(fullName, email, phone, password, confirmPassword, onBackToLogin)
            }
            AuthMessage(authViewModel.message)
            SocialSection(authViewModel::loginWithGoogle, authViewModel::loginWithFacebook, !authViewModel.isBusy)
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
        // Login, Sign Up and Home share drawable/wildguard_logo.png.
        // Login and Sign Up both use this exact same 62dp container and 50dp image size.
        Box(Modifier.size(62.dp).background(DarkGreen, CircleShape), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.wildguard_logo), "WildGuard logo", tint = Color.Unspecified, modifier = Modifier.size(50.dp))
        }
        Text("WildGuard", color = DarkGreen, fontWeight = FontWeight.Bold, fontSize = 21.sp)
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
                focusedBorderColor = ButtonGreen, unfocusedBorderColor = Color.Transparent
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
private fun SocialSection(onGoogle: () -> Unit, onFacebook: () -> Unit, enabled: Boolean) {
    Row(Modifier.fillMaxWidth().padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), color = Color.White.copy(alpha = .55f))
        Text("  or continue with  ", color = Color.White.copy(alpha = .72f), fontSize = 11.sp)
        HorizontalDivider(Modifier.weight(1f), color = Color.White.copy(alpha = .55f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(26.dp)) {
        SocialButton("G", Color(0xFF4285F4), enabled, onGoogle)
        SocialButton("f", Color(0xFF1877F2), enabled, onFacebook)
    }
    Spacer(Modifier.height(14.dp))
}

@Composable
private fun SocialButton(letter: String, color: Color, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.size(54.dp).background(Color.White, RoundedCornerShape(15.dp)).clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(letter, color = color, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
    }
}

@Composable
private fun AuthMessage(message: String?) {
    if (!message.isNullOrBlank()) {
        Text(message, color = Color(0xFFFFE3A0), fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
    }
}
