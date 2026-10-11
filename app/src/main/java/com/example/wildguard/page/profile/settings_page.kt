package com.example.wildguard.page.profile

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.wildguard.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth

private val SettingsGreen = Color(0xFF08743F)

object AppPreferenceStore {
    private const val PREFS = "wildguard_app_preferences"
    const val locationAlertsKey = "location_alerts"
    private fun userKey(key: String): String {
        val userId = if (SupabaseProvider.isConfigured) SupabaseProvider.client.auth.currentUserOrNull()?.id else null
        return "${userId ?: "guest"}_$key"
    }
    fun language(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(userKey("language"), "English") ?: "English"
    fun setLanguage(context: Context, value: String) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(userKey("language"), value).apply()
    fun boolean(context: Context, key: String, default: Boolean) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(userKey(key), default)
    fun setBoolean(context: Context, key: String, value: Boolean) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(userKey(key), value).apply()
}

@Composable
fun SettingsPage(navController: NavController) {
    val context = LocalContext.current
    var language by remember { mutableStateOf(AppLanguage.current) }
    var showLanguages by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        PageTopBar(settingText(language, "Settings", "设置", "Tetapan")) { navController.popBackStack() }
        LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { SectionLabel(settingText(language, "APP PREFERENCES", "应用偏好", "PILIHAN APLIKASI")) }
            item { SettingsRow(Icons.Outlined.Language, settingText(language, "Language", "语言", "Bahasa"), language) { showLanguages = true } }
            item { SectionLabel(settingText(language, "ABOUT", "关于", "TENTANG")) }
            item { SettingsRow(Icons.Outlined.Info, settingText(language, "About WildGuard", "关于 WildGuard", "Tentang WildGuard"), settingText(language, "Version 1.0", "版本 1.0", "Versi 1.0")) { navController.navigate(Page.AboutWildGuard.route) } }
            item { SettingsRow(Icons.Outlined.Description, settingText(language, "Terms & Conditions", "条款与条件", "Terma & Syarat"), settingText(language, "View", "查看", "Lihat")) { navController.navigate(Page.TermsOfService.route) } }
        }
    }
    if (showLanguages) AlertDialog(
        onDismissRequest = { showLanguages = false }, title = { Text(settingText(language, "Choose language", "选择语言", "Pilih bahasa")) },
        text = { Column { listOf("English", "中文", "Bahasa Melayu").forEach { option ->
            Row(Modifier.fillMaxWidth().clickable { language = option; AppLanguage.set(context, option); showLanguages = false }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(language == option, null); Text(option, Modifier.padding(start = 8.dp))
            }
        } } },
        confirmButton = { TextButton(onClick = { showLanguages = false }) { Text(settingText(language, "Close", "关闭", "Tutup")) } }
    )
}

private fun settingText(language: String, english: String, chinese: String, malay: String) = when (language) { "中文" -> chinese; "Bahasa Melayu" -> malay; else -> english }

@Composable private fun SectionLabel(text: String) = Text(text, color = SettingsGreen, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, start = 4.dp))

@Composable private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(onClick, Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White)) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = SettingsGreen); Column(Modifier.padding(start = 13.dp).weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall) }; Icon(Icons.Outlined.ChevronRight, null, tint = Color.Gray) }
    }
}

@Composable private fun SwitchRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White)) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = SettingsGreen); Column(Modifier.padding(start = 13.dp).weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall) }; Switch(checked, onChecked, colors = SwitchDefaults.colors(checkedTrackColor = SettingsGreen)) }
    }
}
