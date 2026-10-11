package com.example.wildguard.page

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.core.app.ActivityCompat
import com.example.wildguard.R
import com.example.wildguard.data.repository.SharedIncidentRepository
import com.example.wildguard.data.report.IncidentReport
import com.example.wildguard.data.report.ReportStatus
import com.example.wildguard.navigation.Page
import com.example.wildguard.navigation.navigateToTopLevel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.delay
import com.example.wildguard.localization.AppLanguage
import com.example.wildguard.data.profile.UserProfileStore
import com.example.wildguard.data.repository.AdminContentRepository

private val HomeGreen = Color(0xFF154E35)

@Composable
fun HomePage(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val reports = SharedIncidentRepository.reports
    var userLocation by remember { mutableStateOf<Location?>(null) }
    val profileRevision = UserProfileStore.revision
    val profile = remember(profileRevision) { UserProfileStore.loadCurrentProfile(context) }

    fun findLocation() {
        fetchHomeLocation(context) { userLocation = it }
    }

    LaunchedEffect(Unit) {
        SharedIncidentRepository.refresh()
        val granted = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (granted) findLocation()
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(15_000)
            SharedIncidentRepository.refresh()
        }
    }

    val validReports = reports.filter {
        it.status != ReportStatus.RESOLVED && it.status != ReportStatus.REJECTED &&
            (it.latitude != 0.0 || it.longitude != 0.0)
    }
    val nearestReport = if (userLocation == null) validReports.firstOrNull() else validReports.minByOrNull {
        distanceMetres(userLocation!!, it)
    }
    val nearestDistance = nearestReport?.let { report -> userLocation?.let { distanceMetres(it, report) } }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        HomeHeader(navController)
        HeroBanner(profile.fullName) { navigateToTopLevel(navController, Page.Explore.route) }
        WildlifeAlert(nearestReport, nearestDistance) {
            navController.navigate(Page.WildlifeMap.route)
        }
        Text(
            AppLanguage.text("Quick Access", "快速功能"), fontSize = 21.sp, fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF111111), modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
        QuickAccess(navController)
        Text(
            AppLanguage.text("News", "新闻"), fontSize = 21.sp, fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF111111), modifier = Modifier.padding(start = 14.dp, top = 12.dp, bottom = 7.dp)
        )
        UserNewsSection(navController)
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun HomeHeader(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) { AdminContentRepository.refreshAnnouncements() }
    val latestAnnouncementId = AdminContentRepository.announcements.firstOrNull()?.id
    val notificationPrefs = remember(context) {
        context.getSharedPreferences("wildguard_notification_state", android.content.Context.MODE_PRIVATE)
    }
    var lastReadAnnouncementId by remember { mutableStateOf(notificationPrefs.getString("last_read_announcement_id", null)) }
    val hasUnreadAnnouncement = latestAnnouncementId != null && latestAnnouncementId != lastReadAnnouncementId
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFF0F8F3)).statusBarsPadding().height(58.dp).padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ------------------------
        // home page-logo
        // ------------------------
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(R.drawable.wildguard_logo), "WildGuard logo",
                modifier = Modifier.size(42.dp).clip(CircleShape), contentScale = ContentScale.Fit
            )
            Text("WILDGUARD", color = HomeGreen, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
        // ------------------------
        // home page-notification button
        // ------------------------
        Row {
            IconButton(onClick = {
                latestAnnouncementId?.let {
                    notificationPrefs.edit().putString("last_read_announcement_id", it).apply()
                    lastReadAnnouncementId = it
                }
                navController.navigate(Page.Announcements.route)
            }) {
                Box {
                    Icon(Icons.Outlined.Notifications, "Notifications", tint = Color.Black, modifier = Modifier.size(27.dp))
                    if (hasUnreadAnnouncement) {
                        Box(
                            Modifier.align(Alignment.TopEnd).size(9.dp)
                                .background(Color.Red, CircleShape)
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }
                }
            }
            IconButton(onClick = { navController.navigate(Page.Profile.route) }) {
                Icon(Icons.Outlined.AccountCircle, "Profile", tint = Color.Black, modifier = Modifier.size(29.dp))
            }
        }
    }
}

@Composable
private fun HeroBanner(userName: String, onExplore: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(228.dp)) {
        Image(
            painterResource(R.drawable.home_elephant), "Elephant in rainforest",
            modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.horizontalGradient(listOf(Color.Black.copy(alpha = .63f), Color.Transparent))
            )
        )
        Column(Modifier.align(Alignment.CenterStart).padding(start = 14.dp)) {
            Text(AppLanguage.text("Welcome back", "欢迎回来"), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(
                "GUARDIAN ${userName.ifBlank { "WILDGUARD USER" }.uppercase()}",
                color = Color(0xFFC8FF75),
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onExplore, shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF17704C)),
                modifier = Modifier.height(42.dp)
            ) { Text(AppLanguage.text("Explore Now  >", "立即探索  >"), fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun WildlifeAlert(report: IncidentReport?, distanceMetres: Float?, onDetails: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 13.dp).fillMaxWidth().height(76.dp)
            .background(Color.White, RoundedCornerShape(18.dp))
            .border(2.dp, Color(0xFF171717), RoundedCornerShape(18.dp)).padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.WarningAmber,
            null,
            tint = if (report == null) Color.Gray else Color.Red,
            modifier = Modifier.size(50.dp)
        )
        Column(Modifier.padding(start = 14.dp)) {
            Text(
                if (report == null) AppLanguage.text("No Wildlife Alert", "暂无野生动物警报") else AppLanguage.text("Wildlife Alert", "野生动物警报"),
                color = if (report == null) Color.Gray else Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            if (report == null) {
                Text(AppLanguage.text("No wildlife incident has been reported yet.", "目前没有野生动物事件报告。"), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            } else {
                val animal = report.animalSpecies.ifBlank { report.incidentType.displayName }
                val distanceText = distanceMetres?.let { formatDistance(it) } ?: AppLanguage.text("distance unavailable", "距离不可用")
                Text(AppLanguage.text("$animal reported $distanceText from your location", "$animal 的报告地点距离您 $distanceText"), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    AppLanguage.text("View Details >", "查看详情 >"), color = Color(0xFF244BE0), fontSize = 10.sp,
                    fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onDetails)
                )
            }
        }
    }
}

private fun distanceMetres(location: Location, report: IncidentReport): Float {
    val result = FloatArray(1)
    Location.distanceBetween(location.latitude, location.longitude, report.latitude, report.longitude, result)
    return result[0]
}

private fun formatDistance(metres: Float): String =
    if (metres < 1000f) "${metres.toInt()} m" else String.format(java.util.Locale.US, "%.1f km", metres / 1000f)

@SuppressLint("MissingPermission")
private fun fetchHomeLocation(context: android.content.Context, onFound: (Location) -> Unit) {
    val fine = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val coarse = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    if (!fine && !coarse) return
    val client = LocationServices.getFusedLocationProviderClient(context)
    runCatching {
        client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { current ->
                if (current != null) onFound(current)
                else client.lastLocation.addOnSuccessListener { last -> last?.let(onFound) }
            }
            .addOnFailureListener {
                client.lastLocation.addOnSuccessListener { last -> last?.let(onFound) }
            }
    }
}

@Composable
private fun QuickAccess(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        QuickCard(AppLanguage.text("Wildlife\nMap", "野生动物\n地图"), R.drawable.quick_map, Color(0xFF9BFFA9), Modifier.weight(1f)) {
            navController.navigate(Page.WildlifeMap.route)
        }
        QuickCard(AppLanguage.text("Wildlife\nEncyclopedia", "野生动物\n百科"), R.drawable.quick_encyclopedia, Color(0xFFAAD6A8), Modifier.weight(1f)) {
            navController.navigate(Page.Encyclopedia.route)
        }
        QuickCard(AppLanguage.text("Wildlife\nSafety Guide", "野生动物\n安全指南"), R.drawable.quick_safety, Color(0xFF9CEAF4), Modifier.weight(1f)) {
            navController.navigate(Page.SafetyGuide.route)
        }
    }
}

@Composable
private fun QuickCard(title: String, image: Int, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.height(112.dp).clip(RoundedCornerShape(18.dp)).background(color)
            .clickable(onClick = onClick).padding(7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painterResource(image), title, modifier = Modifier.size(55.dp), contentScale = ContentScale.Fit)
        Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 12.sp)
    }
}
