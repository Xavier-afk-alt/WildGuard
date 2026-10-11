package com.example.wildguard.page.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wildguard.components.PageTopBar
import com.example.wildguard.localization.AppLanguage
import com.example.wildguard.navigation.Page

private val PrivacyGreen = Color(0xFF08743F)

@Composable
fun PrivacySecurityPage(navController: NavController) {
    val context = LocalContext.current
    var locationSharing by remember { mutableStateOf(AppPreferenceStore.boolean(context, "location_sharing", true)) }
    var analytics by remember { mutableStateOf(AppPreferenceStore.boolean(context, "analytics", false)) }
    var showPasswordInfo by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        PageTopBar(AppLanguage.text("Privacy & Security", "隐私与安全", "Privasi & Keselamatan")) { navController.popBackStack() }
        LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { PrivacyCard(Icons.Outlined.Lock, AppLanguage.text("Password & account security", "密码与账号安全", "Keselamatan kata laluan & akaun"), AppLanguage.text("Password changes are protected by Supabase email verification.", "密码更改受 Supabase 电子邮件验证保护。", "Perubahan kata laluan dilindungi oleh pengesahan e-mel Supabase.")) { showPasswordInfo = true } }
            item { PrivacySwitch(Icons.Outlined.LocationOn, AppLanguage.text("Location sharing", "位置共享", "Perkongsian lokasi"), AppLanguage.text("Use your location for nearby wildlife reports", "使用您的位置显示附近野生动物报告", "Gunakan lokasi anda untuk laporan hidupan liar berdekatan"), locationSharing) { locationSharing = it; AppPreferenceStore.setBoolean(context, "location_sharing", it) } }
            item { PrivacySwitch(Icons.Outlined.Analytics, AppLanguage.text("Usage analytics", "使用情况分析", "Analitik penggunaan"), AppLanguage.text("Help improve WildGuard with anonymous usage data", "使用匿名数据协助改善 WildGuard", "Bantu tingkatkan WildGuard dengan data penggunaan tanpa nama"), analytics) { analytics = it; AppPreferenceStore.setBoolean(context, "analytics", it) } }
            item { PrivacyCard(Icons.Outlined.Devices, AppLanguage.text("Active session", "当前登录设备", "Sesi aktif"), AppLanguage.text("This device is currently signed in", "此设备目前已登录", "Peranti ini sedang log masuk")) { navController.navigate(Page.ActiveSession.route) } }
            item { PrivacyCard(Icons.Outlined.Policy, AppLanguage.text("Privacy Policy", "隐私政策", "Dasar Privasi"), AppLanguage.text("Learn how WildGuard protects your information", "了解 WildGuard 如何保护您的资料", "Ketahui cara WildGuard melindungi maklumat anda")) { navController.navigate(Page.PrivacyPolicy.route) } }
            item { Text(AppLanguage.text("Your exact location is used only when you choose it for reports or nearby alerts.", "只有当您选择用于举报或附近警报时，系统才会使用您的准确位置。", "Lokasi tepat anda hanya digunakan apabila anda memilihnya untuk laporan atau makluman berdekatan."), color = Color.Gray, modifier = Modifier.padding(8.dp)) }
        }
    }
    if (showPasswordInfo) AlertDialog(onDismissRequest = { showPasswordInfo = false }, title = { Text(AppLanguage.text("Password security", "密码安全", "Keselamatan kata laluan")) }, text = { Text(AppLanguage.text("Use the Forgot Password option on the login page. A secure verification email will be sent to your registered address.", "请在登录页面使用忘记密码功能，安全验证邮件会发送到您的注册邮箱。", "Gunakan pilihan Lupa Kata Laluan pada halaman log masuk. E-mel pengesahan selamat akan dihantar ke alamat berdaftar anda.")) }, confirmButton = { TextButton(onClick = { showPasswordInfo = false }) { Text(AppLanguage.text("OK", "确定", "OK")) } })
}

@Composable private fun PrivacyCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = PrivacyGreen); Column(Modifier.padding(start = 12.dp).weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall) }; Icon(Icons.Outlined.ChevronRight, null, tint = Color.Gray) } }
}

@Composable private fun PrivacySwitch(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = PrivacyGreen); Column(Modifier.padding(start = 12.dp).weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall) }; Switch(checked, onChecked, colors = SwitchDefaults.colors(checkedTrackColor = PrivacyGreen)) } }
}
