package com.example.wildguard.page.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wildguard.auth.AuthViewModel
import com.example.wildguard.components.PageTopBar
import com.example.wildguard.localization.AppLanguage
import com.example.wildguard.navigation.Page
import com.example.wildguard.navigation.navigateToTopLevel

private val ActiveSessionGreen = Color(0xFF08743F)

@Composable
fun ActiveSessionPage(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()

    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        PageTopBar(AppLanguage.text("Active Session", "当前登录设备", "Sesi Aktif")) { navController.popBackStack() }
        Column(Modifier.padding(14.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Devices, null, tint = ActiveSessionGreen)
                    Column(Modifier.padding(start = 12.dp).weight(1f)) {
                        Text(AppLanguage.text("This device", "此设备", "Peranti ini"), fontWeight = FontWeight.Bold)
                        Text(
                            AppLanguage.text("Currently signed in", "目前已登录", "Sedang log masuk"),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    authViewModel.signOut()
                    navigateToTopLevel(navController, Page.Home.route)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(AppLanguage.text("Sign out of this device", "退出此设备登录", "Log keluar dari peranti ini"))
            }
        }
    }
}