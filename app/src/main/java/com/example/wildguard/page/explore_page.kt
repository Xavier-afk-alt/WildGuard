package com.example.wildguard.page

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.wildguard.navigation.Page
import com.example.wildguard.localization.AppLanguage
import com.example.wildguard.data.repository.AdminContentRepository

private val ExploreGreen = Color(0xFF2B7147)

@Composable
fun ExplorePage(navController: NavController) {
    val context = LocalContext.current
    LaunchedEffect(Unit) { AdminContentRepository.refreshAnnouncements() }
    val latestAnnouncementId = AdminContentRepository.announcements.firstOrNull()?.id
    val notificationPrefs = remember(context) {
        context.getSharedPreferences("wildguard_notification_state", android.content.Context.MODE_PRIVATE)
    }
    var lastReadAnnouncementId by remember {
        mutableStateOf(notificationPrefs.getString("last_read_announcement_id", null))
    }
    val hasUnreadAnnouncement = latestAnnouncementId != null && latestAnnouncementId != lastReadAnnouncementId
    Column(Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        Column(
            Modifier.fillMaxWidth().background(ExploreGreen)
                .height(76.dp).padding(horizontal = 20.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(AppLanguage.text("Explore More", "探索更多"), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Text(AppLanguage.text("Discover tools to protect wildlife", "探索保护野生动物的工具"), color = Color.White.copy(alpha = .9f), fontSize = 12.sp)
                }
                Row {
                    IconButton(onClick = {
                        latestAnnouncementId?.let {
                            notificationPrefs.edit().putString("last_read_announcement_id", it).apply()
                            lastReadAnnouncementId = it
                        }
                        navController.navigate(Page.Announcements.route)
                    }) {
                        Box {
                            Icon(Icons.Outlined.Notifications, "Notifications", tint = Color.White)
                            if (hasUnreadAnnouncement) {
                                Box(Modifier.align(Alignment.TopEnd).size(9.dp).background(Color.Red, CircleShape).border(1.dp, Color.White, CircleShape))
                            }
                        }
                    }
                    IconButton(onClick = { navController.navigate(Page.Profile.route) }) {
                        Icon(Icons.Outlined.AccountCircle, "Profile", tint = Color.White)
                    }
                }
            }
        }

        Column(Modifier.padding(horizontal = 11.dp, vertical = 22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ExploreOption(
                AppLanguage.text("Wildlife Map", "野生动物地图"), AppLanguage.text("Wildlife crossing hotspots & alerts", "野生动物出没热点与警报"), Color(0xFF9BFFA8)
            ) { navController.navigate(Page.WildlifeMap.route) }
            ExploreOption(
                AppLanguage.text("Wildlife Encyclopedia", "野生动物百科"), AppLanguage.text("Learn about species & their behaviour", "了解物种及其行为"), Color(0xFFA7D4A7)
            ) { navController.navigate(Page.Encyclopedia.route) }
            ExploreOption(
                AppLanguage.text("Wildlife Safety Guide", "野生动物安全指南"), AppLanguage.text("What to do when encountering wildlife", "遇到野生动物时该怎么做"), Color(0xFF9CE7F0)
            ) { navController.navigate(Page.SafetyGuide.route) }
        }
    }
}

@Composable
private fun ExploreOption(title: String, description: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(11.dp))
            .background(color).clickable(onClick = onClick).padding(horizontal = 17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF102419))
            Text(description, fontSize = 10.sp, color = Color(0xFF243B2E))
        }
        Icon(Icons.Outlined.ArrowForward, null, tint = ExploreGreen, modifier = Modifier.size(18.dp))
    }
}
