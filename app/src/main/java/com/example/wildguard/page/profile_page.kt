package com.example.wildguard.page

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wildguard.data.profile.UserProfile
import com.example.wildguard.data.profile.UserProfileStore
import com.example.wildguard.navigation.Page
import com.example.wildguard.page.profile.AppPreferenceStore
import java.io.File
import com.example.wildguard.localization.AppLanguage
import com.example.wildguard.data.repository.AdminContentRepository

private val ProfileGreen = Color(0xFF08743F)

private data class ProfileOption(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String? = null,
    val showViewAll: Boolean = false,
    val isLogout: Boolean = false
)

@Composable
fun ProfilePage(
    navController: NavController,
    isAdmin: Boolean = false,
    onSignOut: () -> Unit = {}
) {
    val context = LocalContext.current
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { AdminContentRepository.refreshAnnouncements() }
    val latestAnnouncementId = AdminContentRepository.announcements.firstOrNull()?.id
    val notificationPrefs = remember(context) {
        context.getSharedPreferences("wildguard_notification_state", android.content.Context.MODE_PRIVATE)
    }
    var lastReadAnnouncementId by remember {
        mutableStateOf(notificationPrefs.getString("last_read_announcement_id", null))
    }
    val hasUnreadAnnouncement = latestAnnouncementId != null && latestAnnouncementId != lastReadAnnouncementId
    val language = AppLanguage.current
    val profileRevision = UserProfileStore.revision
    val profile = remember(isAdmin, profileRevision) {
        if (isAdmin) UserProfile("Administrator", "admin@wildguard.app", null)
        else UserProfileStore.loadCurrentProfile(context)
    }
    val options = listOf(
        ProfileOption(profileText(language, "My Activities", "我的活动", "Aktiviti Saya"), profileText(language, "View and track your recent activities", "查看和追踪最近活动", "Lihat aktiviti terkini"), Icons.Outlined.CalendarMonth, Page.Activities.route, true),
        ProfileOption(profileText(language, "My Rewards", "我的奖励", "Ganjaran Saya"), profileText(language, "Check your rewards and benefits", "查看奖励和福利", "Semak ganjaran anda"), Icons.Outlined.CardGiftcard, Page.MyRewards.route, true),
        ProfileOption(profileText(language, "My Achievement", "我的成就", "Pencapaian Saya"), profileText(language, "See your badges and accomplishments", "查看徽章与成就", "Lihat lencana anda"), Icons.Outlined.WorkspacePremium, Page.Achievements.route, true),
        ProfileOption(profileText(language, "Profile Information", "个人资料", "Maklumat Profil"), profileText(language, "Manage your personal information", "管理个人资料", "Urus maklumat peribadi"), Icons.Outlined.Person, Page.EditProfile.route),
        ProfileOption(profileText(language, "Privacy & Security", "隐私与安全", "Privasi & Keselamatan"), profileText(language, "Manage your privacy and security settings", "管理隐私和安全设置", "Urus tetapan privasi"), Icons.Outlined.Security, Page.PrivacySecurity.route),
        ProfileOption(profileText(language, "Settings", "设置", "Tetapan"), profileText(language, "Customize your app preferences", "自定义应用偏好", "Sesuaikan pilihan aplikasi"), Icons.Outlined.Settings, Page.Settings.route),
        ProfileOption(profileText(language, "Help & Support", "帮助与支持", "Bantuan & Sokongan"), profileText(language, "Get help and support", "获取帮助与支持", "Dapatkan bantuan"), Icons.Outlined.HelpOutline, Page.Help.route),
        ProfileOption(profileText(language, "Log Out", "退出登录", "Log Keluar"), profileText(language, "Sign out of your account", "退出当前账号", "Log keluar daripada akaun"), Icons.Outlined.Logout, isLogout = true)
    )

    Column(Modifier.fillMaxSize().background(Color(0xFFF7F7F7))) {
        ProfileHero(profile, hasUnreadAnnouncement) {
            latestAnnouncementId?.let {
                notificationPrefs.edit().putString("last_read_announcement_id", it).apply()
                lastReadAnnouncementId = it
            }
            navController.navigate(Page.Announcements.route)
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(options) { option ->
                ProfileOptionCard(option) {
                    if (option.isLogout) showLogoutConfirmation = true
                    else option.route?.let { navController.navigate(it) }
                }
            }
        }
    }

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = { Text(AppLanguage.text("Log out?", "确认退出？", "Log keluar?"), fontWeight = FontWeight.Bold) },
            text = { Text(AppLanguage.text("Are you sure you want to log out of your account?", "确定要退出当前账号吗？", "Adakah anda pasti mahu log keluar?")) },
            confirmButton = {
                Button(
                    onClick = { showLogoutConfirmation = false; onSignOut() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text(AppLanguage.text("Log Out", "退出登录", "Log Keluar")) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text(AppLanguage.text("Cancel", "取消", "Batal"))
                }
            }
        )
    }
}

private fun profileText(language: String, english: String, chinese: String, malay: String) = when (language) {
    "中文" -> chinese
    "Bahasa Melayu" -> malay
    else -> english
}

@Composable
private fun ProfileHero(profile: UserProfile, hasUnreadAnnouncement: Boolean, onNotifications: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(ProfileGreen)
            .padding(start = 16.dp, end = 10.dp, top = 18.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (profile.avatarPath != null) {
            AsyncImage(
                model = File(profile.avatarPath),
                contentDescription = "Profile photo",
                modifier = Modifier.size(68.dp).clip(CircleShape).background(Color(0xFFE8B174)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier.size(68.dp).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "Default profile icon",
                    tint = Color(0xFF252122),
                    modifier = Modifier.size(62.dp)
                )
            }
        }
        Column(Modifier.padding(start = 10.dp).weight(1f)) {
            Text(profile.fullName, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
            Text(profile.email, color = Color.White.copy(alpha = .9f), fontSize = 12.sp)
        }
        IconButton(
            onClick = onNotifications,
            modifier = Modifier.padding(end = 8.dp).size(36.dp).background(Color.White, CircleShape)
        ) {
            Box {
                Icon(Icons.Outlined.Notifications, "Notifications", tint = Color.Black, modifier = Modifier.size(24.dp))
                if (hasUnreadAnnouncement) {
                    Box(Modifier.align(Alignment.TopEnd).size(9.dp).background(Color.Red, CircleShape).border(1.dp, Color.White, CircleShape))
                }
            }
        }
    }
}

@Composable
private fun ProfileOptionCard(option: ProfileOption, onClick: () -> Unit) {
    val accentColor = if (option.isLogout) Color(0xFFD32F2F) else ProfileGreen
    val cardColor = if (option.isLogout) Color(0xFFFFEBEE) else Color.White
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 68.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(42.dp).background(
                    if (option.isLogout) Color(0xFFFFCDD2) else Color(0xFFEAF3EC),
                    CircleShape
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(option.icon, null, tint = accentColor, modifier = Modifier.size(25.dp))
            }
            Column(Modifier.padding(start = 10.dp).weight(1f)) {
                Text(option.title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = if (option.isLogout) accentColor else Color(0xFF151515))
                Text(option.description, fontSize = 10.sp, color = if (option.isLogout) accentColor.copy(alpha = .8f) else Color(0xFF686868))
            }
            if (option.showViewAll) {
                Text(AppLanguage.text("View All", "查看全部", "Lihat Semua"), color = ProfileGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = accentColor, modifier = Modifier.size(20.dp))
        }
    }
}
