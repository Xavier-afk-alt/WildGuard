package com.example.wildguard.page.admin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.wildguard.R
import com.example.wildguard.data.repository.*
import com.example.wildguard.notification.ProximityAlertManager
import com.example.wildguard.notification.WildGuardNotifications
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.example.wildguard.data.remote.WildGuardTileSource
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.time.Instant
import java.util.Date

private val AdminGreen = Color(0xFF176B43)
private enum class AdminScreen { Dashboard, News, Reports, Announcements, Users, Rewards }

@Composable
fun AdminApp(onLogout: () -> Unit) {
    var screen by remember { mutableStateOf(AdminScreen.Dashboard) }
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AdminBottomBar(selected = screen, onSelected = { screen = it })
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (screen) {
                AdminScreen.Dashboard -> AdminDashboard(
                    onLogout = onLogout,
                    onRewards = { screen = AdminScreen.Rewards },
                    onReports = { screen = AdminScreen.Reports },
                    onNews = { screen = AdminScreen.News },
                    onUsers = { screen = AdminScreen.Users }
                )
                AdminScreen.News -> ManageNewsPage { screen = AdminScreen.Dashboard }
                AdminScreen.Reports -> ManageReportsPage { screen = AdminScreen.Dashboard }
                AdminScreen.Announcements -> ManageAnnouncementsPage { screen = AdminScreen.Dashboard }
                AdminScreen.Users -> ManageUsersPage { screen = AdminScreen.Dashboard }
                AdminScreen.Rewards -> ManageRewardsPage { screen = AdminScreen.Dashboard }
            }
        }
    }
}

@Composable
private fun AdminBottomBar(selected: AdminScreen, onSelected: (AdminScreen) -> Unit) {
    val items = listOf(
        Triple(AdminScreen.Dashboard, "Home", Icons.Outlined.Home),
        Triple(AdminScreen.News, "News", Icons.Outlined.Newspaper),
        Triple(AdminScreen.Reports, "Reports", Icons.Outlined.Assignment),
        Triple(AdminScreen.Announcements, "Announce", Icons.Outlined.Campaign),
        Triple(AdminScreen.Users, "Users", Icons.Outlined.Group)
    )
    NavigationBar(containerColor = Color(0xFFF6F0FA)) {
        items.forEach { (screen, label, icon) ->
            NavigationBarItem(
                selected = selected == screen,
                onClick = { onSelected(screen) },
                icon = { Icon(icon, label) },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFE7DDF4),
                    selectedIconColor = AdminGreen,
                    selectedTextColor = AdminGreen
                )
            )
        }
    }
}

@Composable
private fun AdminDashboard(
    onLogout: () -> Unit,
    onRewards: () -> Unit,
    onReports: () -> Unit,
    onNews: () -> Unit,
    onUsers: () -> Unit
) {
    LaunchedEffect(Unit) {
        AdminContentRepository.refreshUsers()
        AdminContentRepository.refreshNews()
        AdminContentRepository.refreshAnnouncements()
        SharedIncidentRepository.refresh()
    }
    val activeReports = SharedIncidentRepository.reports.count {
        it.status != com.example.wildguard.data.report.ReportStatus.RESOLVED &&
            it.status != com.example.wildguard.data.report.ReportStatus.REJECTED
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF4F8F5)),
        contentPadding = PaddingValues(bottom = 22.dp)
    ) {
        item {
            Box(
                Modifier.fillMaxWidth().background(AdminGreen).statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Column(Modifier.padding(end = 48.dp)) {
                    Text("Good day, Admin", color = Color.White.copy(alpha = .8f), fontSize = 13.sp)
                    Text("WildGuard Overview", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Monitor the community and keep wildlife safe.", color = Color.White.copy(alpha = .88f), fontSize = 12.sp)
                }
                IconButton(onClick = onLogout, modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(Icons.Outlined.Logout, "Log out", tint = Color.White)
                }
            }
        }
        item {
            Text("Today at a glance", fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, modifier = Modifier.padding(18.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminStatCard("${AdminContentRepository.users.size}", "Users", Icons.Outlined.Group, Color(0xFFDDF3E2), Modifier.weight(1f), onUsers)
                AdminStatCard("$activeReports", "Active alerts", Icons.Outlined.WarningAmber, Color(0xFFFFE4BF), Modifier.weight(1f), onReports)
                AdminStatCard("${AdminContentRepository.news.size}", "News", Icons.Outlined.Newspaper, Color(0xFFDDECF7), Modifier.weight(1f), onNews)
            }
        }
        item {
            // ------------------------
            // admin home-recent report view all
            // ------------------------
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 18.dp, end = 10.dp, top = 18.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent report",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onReports) {
                    Text("View All", color = AdminGreen, fontWeight = FontWeight.Bold)
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = AdminGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            val latest = SharedIncidentRepository.reports.firstOrNull()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .clickable(enabled = latest != null, onClick = onReports),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                if (latest == null) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CheckCircle, null, tint = AdminGreen, modifier = Modifier.size(38.dp))
                        Column(Modifier.padding(start = 13.dp)) {
                            Text("No reports submitted", fontWeight = FontWeight.Bold)
                            Text("New community reports will appear here.", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                } else {
                    Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(38.dp).background(Color(0xFFFFE4BF), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.WarningAmber,
                                contentDescription = null,
                                tint = AdminGreen,
                                modifier = Modifier.size(21.dp)
                            )
                        }
                        Column(Modifier.padding(start = 13.dp).weight(1f)) {
                            Text(latest.incidentType.displayName, fontWeight = FontWeight.ExtraBold)
                            Text(latest.animalSpecies.ifBlank { latest.locationName }, fontSize = 12.sp)
                            Text(formatTime(latest.timestamp), color = Color.Gray, fontSize = 10.sp)
                        }
                        Text(latest.status.name, color = AdminGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }
        item {
            Text("Reward management", fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, modifier = Modifier.padding(start = 18.dp, top = 22.dp, bottom = 10.dp))
            Column(Modifier.padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminModuleCard("Manage Rewards", "Add vouchers, change stock or delete rewards", Icons.Outlined.CardGiftcard, Color(0xFFECE0FF), onRewards)
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE4F2E8))
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.VerifiedUser, null, tint = AdminGreen, modifier = Modifier.size(36.dp))
                    Column(Modifier.padding(start = 13.dp)) {
                        Text("System status", fontWeight = FontWeight.ExtraBold, color = AdminGreen)
                        Text("WildGuard services are ready and monitoring reports.", fontSize = 12.sp, color = Color(0xFF4F6A5B))
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(onClick = onClick, modifier = modifier.height(120.dp), shape = RoundedCornerShape(19.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(Modifier.size(38.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = AdminGreen, modifier = Modifier.size(21.dp))
            }
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = AdminGreen)
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun AdminModuleCard(title: String, description: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().height(92.dp), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.fillMaxSize().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(58.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = AdminGreen, modifier = Modifier.size(31.dp))
            }
            Column(Modifier.padding(start = 14.dp).weight(1f)) {
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text(description, color = Color.Gray, fontSize = 11.sp)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = AdminGreen)
        }
    }
}

@Composable
private fun AdminTopBar(title: String, onBack: () -> Unit, addAction: (() -> Unit)? = null) {
    Column(Modifier.fillMaxWidth().background(AdminGreen).statusBarsPadding().zIndex(20f)) {
        Row(
            Modifier.fillMaxWidth().height(62.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = Color.White) }
            Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp, modifier = Modifier.weight(1f))
            addAction?.let { IconButton(onClick = it) { Icon(Icons.Outlined.Add, "Add", tint = Color.White) } }
        }
    }
}

@Composable
private fun ManageNewsPage(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var editor by remember { mutableStateOf<ManagedNews?>(null) }
    var showNewEditor by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<ManagedNews?>(null) }
    LaunchedEffect(Unit) { AdminContentRepository.refreshNews() }
    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0), snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F7F5))) {
            AdminTopBar("News Details", onBack) { showNewEditor = true }
            if (AdminContentRepository.news.isEmpty()) EmptyAdminState("No news yet", "Tap + to publish the first news.")
            else LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(AdminContentRepository.news, key = { it.id }) { item ->
                    AdminNewsCard(item, { editor = item }, { deleteTarget = item })
                }
            }
        }
    }
    if (showNewEditor || editor != null) NewsEditorDialog(editor, {
        showNewEditor = false; editor = null
    }) { item ->
        val wasEditing = editor != null
        showNewEditor = false; editor = null
        scope.launch {
            AdminContentRepository.saveNews(item)
                .onSuccess { snackbarHostState.showSnackbar(if (wasEditing) "News updated" else "News added") }
                .onFailure { snackbarHostState.showSnackbar("Unable to sync news. Please try again.") }
        }
    }
    deleteTarget?.let { item ->
        ConfirmDelete(
            message = "Delete this news?",
            onConfirm = {
                deleteTarget = null
                scope.launch {
                    AdminContentRepository.deleteNews(item.id)
                        .onSuccess { snackbarHostState.showSnackbar("News deleted") }
                        .onFailure { snackbarHostState.showSnackbar("Delete failed: ${it.message ?: "Check the Supabase delete policy."}") }
                }
            },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun AdminNewsCard(item: ManagedNews, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column {
            item.imageBase64?.let { base64ToBitmap(it) }?.let {
                Image(it.asImageBitmap(), item.title, Modifier.fillMaxWidth().height(150.dp), contentScale = ContentScale.Crop)
            }
            Column(Modifier.padding(13.dp)) {
                Text(item.title, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                Text(item.content, fontSize = 12.sp, maxLines = 3)
                Text(formatTime(item.createdAt), fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    TextButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, null); Text(" Edit") }
                    TextButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, null, tint = Color.Red); Text(" Delete", color = Color.Red) }
                }
            }
        }
    }
}

@Composable
private fun NewsEditorDialog(existing: ManagedNews?, onDismiss: () -> Unit, onSave: (ManagedNews) -> Unit) {
    val context = LocalContext.current
    var title by remember(existing) { mutableStateOf(existing?.title.orEmpty()) }
    var content by remember(existing) { mutableStateOf(existing?.content.orEmpty()) }
    var image by remember(existing) { mutableStateOf(existing?.imageBase64) }
    var error by remember { mutableStateOf<String?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { image = imageUriToBase64(context, it); if (image == null) error = "Unable to use this image." }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add News" else "Edit News") },
        text = { Column(Modifier.fillMaxWidth()) {
            OutlinedTextField(title, { title = it }, label = { Text("News title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(content, { content = it }, label = { Text("News content") }, modifier = Modifier.fillMaxWidth().height(150.dp))
            OutlinedButton(onClick = { picker.launch("image/*") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Icon(Icons.Outlined.Image, null); Text(if (image == null) " Choose Picture" else " Change Picture")
            }
            error?.let { Text(it, color = Color.Red, fontSize = 11.sp) }
        } },
        confirmButton = { Button(onClick = {
            if (title.isBlank() || content.isBlank()) error = "Title and content are required."
            else onSave(ManagedNews(existing?.id ?: java.util.UUID.randomUUID().toString(), title.trim(), content.trim(), image, existing?.createdAt ?: Instant.now().toString()))
        }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun ManageAnnouncementsPage(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var editor by remember { mutableStateOf<ManagedAnnouncement?>(null) }
    var adding by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<ManagedAnnouncement?>(null) }
    LaunchedEffect(Unit) { AdminContentRepository.refreshAnnouncements() }
    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0), snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F7F5))) {
            AdminTopBar("Announcement Details", onBack) { adding = true }
            if (AdminContentRepository.announcements.isEmpty()) EmptyAdminState("No announcement yet", "Tap + to share an announcement.")
            else LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(AdminContentRepository.announcements, key = { it.id }) { item ->
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(14.dp)) {
                            Text(item.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text(item.content, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                            Text(formatTime(item.createdAt), fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                                TextButton(onClick = { editor = item }) { Text("Edit") }
                                TextButton(onClick = { deleteTarget = item }) { Text("Delete", color = Color.Red) }
                            }
                        }
                    }
                }
            }
        }
    }
    if (adding || editor != null) AnnouncementEditorDialog(editor, { adding = false; editor = null }) { item ->
        val wasEditing = editor != null
        adding = false; editor = null
        scope.launch {
            AdminContentRepository.saveAnnouncement(item)
                .onSuccess { snackbarHostState.showSnackbar(if (wasEditing) "Announcement updated" else "Announcement added") }
                .onFailure { snackbarHostState.showSnackbar("Unable to sync announcement. Please try again.") }
        }
    }
    deleteTarget?.let { item ->
        ConfirmDelete(
            message = "Delete this announcement?",
            onConfirm = {
                deleteTarget = null
                scope.launch {
                    AdminContentRepository.deleteAnnouncement(item.id)
                        .onSuccess { snackbarHostState.showSnackbar("Announcement deleted") }
                        .onFailure { snackbarHostState.showSnackbar("Delete failed: ${it.message ?: "Check the Supabase delete policy."}") }
                }
            },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun AnnouncementEditorDialog(existing: ManagedAnnouncement?, onDismiss: () -> Unit, onSave: (ManagedAnnouncement) -> Unit) {
    var title by remember(existing) { mutableStateOf(existing?.title.orEmpty()) }
    var content by remember(existing) { mutableStateOf(existing?.content.orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (existing == null) "New Announcement" else "Edit Announcement") },
        text = { Column { OutlinedTextField(title, { title = it }, label = { Text("Title") }); OutlinedTextField(content, { content = it }, label = { Text("Content") }, modifier = Modifier.height(150.dp)); error?.let { Text(it, color = Color.Red) } } },
        confirmButton = { Button(onClick = { if (title.isBlank() || content.isBlank()) error = "Title and content are required." else onSave(ManagedAnnouncement(existing?.id ?: java.util.UUID.randomUUID().toString(), title.trim(), content.trim(), existing?.createdAt ?: Instant.now().toString())) }) { Text("Share") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
private fun ManageReportsPage(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var selected by remember { mutableStateOf<com.example.wildguard.data.report.IncidentReport?>(null) }
    var mapReport by remember { mutableStateOf<com.example.wildguard.data.report.IncidentReport?>(null) }
    var deleteTarget by remember { mutableStateOf<com.example.wildguard.data.report.IncidentReport?>(null) }
    LaunchedEffect(Unit) { SharedIncidentRepository.refresh() }
    mapReport?.let { report ->
        AdminReportMap(report = report, onBack = { mapReport = null })
        return
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F7F5))) {
            AdminTopBar("Reports Details", onBack)
            if (SharedIncidentRepository.reports.isEmpty()) EmptyAdminState("No reports", "User incident reports will appear here.")
            else LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                items(SharedIncidentRepository.reports, key = { it.reportId }) { report ->
                    Card(onClick = { selected = report }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp)) {
                        Column(Modifier.padding(13.dp)) {
                            Row {
                                Text(report.incidentType.displayName, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                                Text(
                                    report.status.name,
                                    color = if (report.status == com.example.wildguard.data.report.ReportStatus.RESOLVED) Color.Gray else Color(0xFFD56A00),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(report.animalSpecies.ifBlank { "Animal not specified" }, fontSize = 12.sp)
                            Text(report.locationName, color = AdminGreen, fontSize = 11.sp)
                            Text(formatTime(report.timestamp), color = Color.Gray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
    selected?.let { report ->
        ReportDetailsDialog(
            report = report,
            onDismiss = { selected = null },
            onResolve = {
                scope.launch { SharedIncidentRepository.markResolved(report.reportId) }
                selected = null
            },
            onOpenMap = { selected = null; mapReport = report },
            onDelete = { selected = null; deleteTarget = report }
        )
    }
    deleteTarget?.let { report ->
        ConfirmDelete(
            message = "Delete this incident report? This action cannot be undone.",
            onConfirm = {
                deleteTarget = null
                scope.launch {
                    SharedIncidentRepository.delete(report.reportId)
                        .onSuccess { snackbarHostState.showSnackbar("Incident report deleted") }
                        .onFailure { snackbarHostState.showSnackbar("Delete failed: ${it.message ?: "Check the Supabase delete policy."}") }
                }
            },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
// ------------------------
// admin report-photo and details
// ------------------------
private fun ReportDetailsDialog(
    report: com.example.wildguard.data.report.IncidentReport,
    onDismiss: () -> Unit,
    onResolve: () -> Unit,
    onOpenMap: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(report.incidentType.displayName) }, text = { Column(Modifier.verticalScroll(rememberScrollState())) {
        report.imageUris.forEach { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = "Incident report photo",
                modifier = Modifier.fillMaxWidth().height(190.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE8EEE9)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp))
        }
        Detail("Report ID", report.reportId); Detail("Animal", report.animalSpecies.ifBlank { "Not specified" }); Detail("Animal count", report.animalCount.toString());
        Detail("Severity", report.severity.name); Detail("Status", report.status.name); Detail("Location", report.locationName); Detail("Coordinates", "${report.latitude}, ${report.longitude}"); Detail("Weather", report.weather); Detail("Description", report.description); Detail("Reported", formatTime(report.timestamp))
    } }, confirmButton = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (report.status != com.example.wildguard.data.report.ReportStatus.RESOLVED) {
                    Button(
                        onClick = onResolve,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AdminGreen)
                    ) {
                        Text("Resolved", maxLines = 1, fontSize = 12.sp)
                    }
                }
                OutlinedButton(
                    onClick = onOpenMap,
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                ) {
                    Text("Open Map", maxLines = 1, fontSize = 12.sp)
                }
            }
            Button(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) {
                Icon(Icons.Outlined.Delete, null)
                Text(" Delete Incident Report")
            }
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp)
            ) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        }
    }, dismissButton = {})
}

@Composable
private fun AdminReportMap(
    report: com.example.wildguard.data.report.IncidentReport,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(report.reportId) {
        context.getSharedPreferences("wildguard_proximity_runtime", Context.MODE_PRIVATE)
            .edit().putString("admin_report_id", report.reportId).apply()
    }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var responseTeamLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var fittedBounds by remember(report.reportId) { mutableStateOf(false) }

    fun locateNgo() {
        fetchAdminNgoLocation(context) { latitude, longitude ->
            responseTeamLocation = GeoPoint(latitude, longitude)
            context.getSharedPreferences("wildguard_proximity_runtime", Context.MODE_PRIVATE)
                .edit().putString("admin_last_lat", latitude.toString())
                .putString("admin_last_lon", longitude.toString()).apply()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) locateNgo()
    }

    LaunchedEffect(Unit) {
        val allowed = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (allowed) locateNgo() else permissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            locateNgo()
        }
    }

    LaunchedEffect(responseTeamLocation, report.reportId, report.status) {
        val point = responseTeamLocation ?: return@LaunchedEffect
        ProximityAlertManager.checkAdmin(context, point.latitude, point.longitude, report)?.let { alert ->
            WildGuardNotifications.proximity(context, alert)
            snackbarHostState.showSnackbar(alert.message)
        }
    }

    DisposableEffect(Unit) {
        mapView?.onResume()
        onDispose {
            mapView?.onPause()
            mapView?.onDetach()
        }
    }

    Column(Modifier.fillMaxSize().background(Color.White)) {
        AdminTopBar("Report Map", onBack)
        Box(Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    Configuration.getInstance().apply {
                        load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
                        userAgentValue = "WildGuard/1.0 (${context.packageName})"
                    }
                    MapView(context).apply {
                        setTileSource(WildGuardTileSource)
                        setMultiTouchControls(true)
                        onResume()
                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                        controller.setZoom(14.0)
                        controller.setCenter(GeoPoint(report.latitude, report.longitude))
                        mapView = this
                    }
                },
                update = { map ->
                    map.overlays.clear()
                    map.overlays.add(Marker(map).apply {
                        position = GeoPoint(report.latitude, report.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = report.animalSpecies.ifBlank { report.incidentType.displayName }
                        snippet = report.locationName
                        icon = ContextCompat.getDrawable(context, R.drawable.map_marker_report)
                    })
                    responseTeamLocation?.let { point ->
                        map.overlays.add(Marker(map).apply {
                            position = point
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "Your Location (Admin)"
                            snippet = "Current location of this admin device"
                            icon = ContextCompat.getDrawable(context, R.drawable.map_marker_current)
                        })
                        if (!fittedBounds) {
                            map.zoomToBoundingBox(
                                BoundingBox.fromGeoPoints(
                                    listOf(point, GeoPoint(report.latitude, report.longitude))
                                ),
                                true,
                                100
                            )
                            fittedBounds = true
                        }
                    }
                    map.invalidate()
                }
            )

            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(14.dp),
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                shadowElevation = 5.dp
            ) {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 11.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.map_marker_report), null, tint = Color.Unspecified, modifier = Modifier.size(22.dp))
                        Text(" Report location", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.map_marker_current), null, tint = Color.Unspecified, modifier = Modifier.size(22.dp))
                        Text(" Your Location (Admin)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.TopCenter).padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

@SuppressLint("MissingPermission")
private fun fetchAdminNgoLocation(context: Context, onFound: (Double, Double) -> Unit) {
    val fine = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val coarse = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    if (!fine && !coarse) return
    val client = LocationServices.getFusedLocationProviderClient(context)
    fun useLast() {
        client.lastLocation.addOnSuccessListener { it?.let { location -> onFound(location.latitude, location.longitude) } }
    }
    runCatching {
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location -> if (location != null) onFound(location.latitude, location.longitude) else useLast() }
            .addOnFailureListener { useLast() }
    }.onFailure { useLast() }
}

@Composable
private fun ManageUsersPage(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var selected by remember { mutableStateOf<ManagedUser?>(null) }
    var deleteTarget by remember { mutableStateOf<ManagedUser?>(null) }
    LaunchedEffect(Unit) {
        while (true) {
            AdminContentRepository.refreshUsers()
            delay(3_000)
        }
    }
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F7F5))) {
            AdminTopBar("User Details", onBack)
            if (AdminContentRepository.users.isEmpty()) EmptyAdminState("No users", "Registered user profiles will appear here.")
            else LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                items(AdminContentRepository.users, key = { it.id }) { user ->
                    Card(onClick = { selected = user }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp)) {
                        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                            val profileBitmap = user.profilePictureBase64?.let { base64ToBitmap(it) }
                            Box(Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFE2F1E6)), contentAlignment = Alignment.Center) {
                                if (profileBitmap != null) {
                                    Image(profileBitmap.asImageBitmap(), "${user.fullName} profile picture", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                } else {
                                    Icon(Icons.Outlined.Person, null, tint = AdminGreen)
                                }
                            }
                            Column(Modifier.padding(start = 10.dp).weight(1f)) { Text(user.fullName.ifBlank { "Unnamed User" }, fontWeight = FontWeight.ExtraBold); Text(user.email, fontSize = 11.sp); if (user.isDisabled) Text("DISABLED", color = Color.Red, fontSize = 10.sp) }
                            IconButton(onClick = { deleteTarget = user }) { Icon(Icons.Outlined.Delete, null, tint = Color.Red) }
                        }
                    }
                }
            }
        }
    }
    selected?.let { UserEditorDialog(it, { selected = null }) { scope.launch { AdminContentRepository.saveUser(it) }; selected = null } }
    deleteTarget?.let { user ->
        ConfirmDelete(
            message = "Permantly \"${user.fullName.ifBlank { user.email }}\" ? This user account will gone forever!",
            onConfirm = {
                deleteTarget = null
                scope.launch {
                    AdminContentRepository.deleteUserProfile(user.id)
                        .onSuccess { snackbarHostState.showSnackbar("User permanently deleted") }
                        .onFailure { error ->
                            snackbarHostState.showSnackbar(
                                "Delete failed: ${error.message ?: "Run SUPABASE_DELETE_USER.sql first."}"
                            )
                        }
                }
            },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun UserEditorDialog(user: ManagedUser, onDismiss: () -> Unit, onSave: (ManagedUser) -> Unit) {
    var name by remember { mutableStateOf(user.fullName) }; var phone by remember { mutableStateOf(user.phone) }; var disabled by remember { mutableStateOf(user.isDisabled) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Manage User") }, text = { Column {
        Text(user.email, color = Color.Gray, fontSize = 12.sp); OutlinedTextField(name, { name = it }, label = { Text("Full name") }); OutlinedTextField(phone, { phone = it }, label = { Text("Phone") });
        Row(verticalAlignment = Alignment.CenterVertically) { Switch(disabled, { disabled = it }); Text(" Disable account", color = if (disabled) Color.Red else Color.Black) }
    } }, confirmButton = { Button(onClick = { onSave(user.copy(fullName = name.trim(), phone = phone.trim(), isDisabled = disabled)) }) { Text("Save") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
private fun ManageRewardsPage(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var editing by remember { mutableStateOf<RewardCatalogRow?>(null) }
    var adding by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<RewardCatalogRow?>(null) }
    LaunchedEffect(Unit) { RewardCatalogRepository.refresh() }
    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0), snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
      Column(Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F7F5))) {
        AdminTopBar("Manage Rewards", onBack) { adding = true }
        LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            items(RewardCatalogRepository.rewards, key = { it.id }) { reward ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(reward.toRewardItem().image),
                            contentDescription = reward.title,
                            modifier = Modifier.size(54.dp).padding(end = 10.dp),
                            contentScale = ContentScale.Fit
                        )
                        Column(Modifier.weight(1f)) {
                            Text(reward.title, fontWeight = FontWeight.ExtraBold)
                            Text("${reward.type} • ${reward.price} pts • Stock: ${reward.stock}", color = if (reward.stock <= 0) Color.Red else AdminGreen, fontSize = 12.sp)
                            Text(
                                if (reward.purchaseMode.equals("PENDING", true)) "Pending" else "Purchase",
                                color = if (reward.purchaseMode.equals("PENDING", true)) Color(0xFFEF6C00) else AdminGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton({ editing = reward }) { Icon(Icons.Outlined.Edit, null) }
                        IconButton({ deleteTarget = reward }) { Icon(Icons.Outlined.Delete, null, tint = Color.Red) }
                    }
                }
            }
        }
      }
    }
    if (adding || editing != null) RewardEditorDialog(editing, { adding = false; editing = null }) { reward ->
        val action = if (editing == null) "added" else "updated"
        adding = false
        editing = null
        scope.launch {
            RewardCatalogRepository.saveReward(reward)
                .onSuccess { snackbarHostState.showSnackbar("Reward $action successfully") }
                .onFailure { snackbarHostState.showSnackbar("Unable to save reward: ${it.message ?: "Unknown error"}") }
        }
    }
    deleteTarget?.let { reward ->
        ConfirmDelete(
            message = "Permanently delete \"${reward.title}\"? This reward will be removed from the database.",
            onConfirm = {
                deleteTarget = null
                scope.launch {
                    RewardCatalogRepository.deleteReward(reward.id)
                        .onSuccess { snackbarHostState.showSnackbar("Reward deleted") }
                        .onFailure { snackbarHostState.showSnackbar("Delete failed: ${it.message ?: "Unknown error"}") }
                }
            },
            onDismiss = { deleteTarget = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RewardEditorDialog(existing: RewardCatalogRow?, onDismiss: () -> Unit, onSave: (RewardCatalogRow) -> Unit) {
    val itemTypes = listOf("Interaction", "Voucher", "Eco Gift", "Collectible")
    val drawableOptions = listOf(
        "Default Reward Image" to R.drawable.low_stock,
        "Pat Pat Hand" to R.drawable.pat_hand,
        "Watering Tool" to R.drawable.watering_tool,
        "Grab Voucher" to R.drawable.grab_voucher,
        "FoodPanda Voucher" to R.drawable.foodpanda_voucher,
        "TerraNest Logo" to R.drawable.terranest_logo,
        "WildGuard Logo" to R.drawable.wildguard_logo,
        "Plant" to R.drawable.plant,
        "Animal" to R.drawable.animal
    )
    val buttonStatuses = listOf("Purchase", "Pending")
    var type by remember(existing) { mutableStateOf(existing?.type?.takeIf { it in itemTypes } ?: itemTypes.first()) }
    var typeMenuExpanded by remember { mutableStateOf(false) }
    var selectedImageRes by remember(existing) {
        mutableIntStateOf(existing?.imageRes?.takeIf { it != 0 } ?: when (existing?.id) {
            1 -> R.drawable.pat_hand; 2 -> R.drawable.watering_tool; 3 -> R.drawable.grab_voucher; 4 -> R.drawable.foodpanda_voucher
            else -> R.drawable.low_stock
        })
    }
    var imageMenuExpanded by remember { mutableStateOf(false) }
    var buttonStatus by remember(existing) {
        mutableStateOf(if (existing?.purchaseMode.equals("PENDING", true)) "Pending" else "Purchase")
    }
    var statusMenuExpanded by remember { mutableStateOf(false) }
    var title by remember(existing) { mutableStateOf(existing?.title.orEmpty()) }
    var description by remember(existing) { mutableStateOf(existing?.description.orEmpty()) }
    var price by remember(existing) { mutableStateOf(existing?.price?.toString().orEmpty()) }
    var stock by remember(existing) { mutableStateOf(existing?.stock?.toString().orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (existing == null) "Add Reward" else "Edit Reward") }, text = { Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = typeMenuExpanded,
            onExpandedChange = { typeMenuExpanded = !typeMenuExpanded }
        ) {
            OutlinedTextField(
                value = type,
                onValueChange = {},
                readOnly = true,
                label = { Text("Item type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = typeMenuExpanded,
                onDismissRequest = { typeMenuExpanded = false }
            ) {
                itemTypes.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { type = option; typeMenuExpanded = false }
                    )
                }
            }
        }
        ExposedDropdownMenuBox(
            expanded = imageMenuExpanded,
            onExpandedChange = { imageMenuExpanded = !imageMenuExpanded }
        ) {
            OutlinedTextField(
                value = drawableOptions.firstOrNull { it.second == selectedImageRes }?.first ?: "Default Reward Image",
                onValueChange = {}, readOnly = true,
                label = { Text("Drawable image") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = imageMenuExpanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = imageMenuExpanded, onDismissRequest = { imageMenuExpanded = false }) {
                drawableOptions.forEach { (label, drawableRes) ->
                    DropdownMenuItem(text = { Text(label) }, onClick = { selectedImageRes = drawableRes; imageMenuExpanded = false })
                }
            }
        }
        OutlinedTextField(title, { title = it }, label = { Text("Item name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(description, { description = it }, label = { Text("Item description") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(price, { price = it.filter(Char::isDigit) }, label = { Text("Price (points)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(stock, { stock = it.filter(Char::isDigit) }, label = { Text("Stock number") }, modifier = Modifier.fillMaxWidth())
        ExposedDropdownMenuBox(
            expanded = statusMenuExpanded,
            onExpandedChange = { statusMenuExpanded = !statusMenuExpanded }
        ) {
            OutlinedTextField(
                value = buttonStatus, onValueChange = {}, readOnly = true,
                label = { Text("Button status") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusMenuExpanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = statusMenuExpanded, onDismissRequest = { statusMenuExpanded = false }) {
                buttonStatuses.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = { buttonStatus = option; statusMenuExpanded = false })
                }
            }
        }
        error?.let { Text(it, color = Color.Red) }
    } }, confirmButton = { Button({
        val p = price.toIntOrNull(); val s = stock.toIntOrNull()
        if (title.isBlank() || description.isBlank() || p == null || s == null) error = "Complete all required fields."
        else onSave(RewardCatalogRow(
            id = existing?.id ?: ((RewardCatalogRepository.rewards.maxOfOrNull { it.id } ?: 0) + 1),
            type = type,
            title = title.trim(),
            description = description.trim(),
            price = p,
            stock = s,
            purchaseMode = if (buttonStatus == "Pending") "PENDING" else if (type == "Interaction") "MULTIPLE" else "SINGLE",
            imageRes = selectedImageRes
        ))
    }) { Text("Save") } }, dismissButton = { TextButton(onDismiss) { Text("Cancel") } })
}


@Composable private fun EmptyAdminState(title: String, description: String) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Outlined.Inbox, null, tint = Color.Gray, modifier = Modifier.size(52.dp)); Text(title, fontWeight = FontWeight.Bold); Text(description, color = Color.Gray, fontSize = 12.sp) } } }
@Composable private fun Detail(label: String, value: String) { Text(label, color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 7.dp)); Text(value, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
@Composable private fun ConfirmDelete(message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) { AlertDialog(onDismissRequest = onDismiss, title = { Text("Confirm Delete") }, text = { Text(message) }, confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Delete") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }) }
private fun formatTime(time: Long): String = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(time))
private fun formatTime(time: String): String = runCatching {
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date.from(Instant.parse(time)))
}.getOrDefault(time)
private fun base64ToBitmap(value: String) = runCatching {
    val bytes = Base64.decode(value, Base64.DEFAULT)
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}.getOrNull()
private fun imageUriToBase64(context: Context, uri: Uri): String? = runCatching {
    val bitmap = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) } ?: return null
    val ratio = minOf(1f, 1200f / maxOf(bitmap.width, bitmap.height).toFloat())
    val resized = if (ratio < 1f) Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true) else bitmap
    val output = ByteArrayOutputStream(); resized.compress(Bitmap.CompressFormat.JPEG, 75, output)
    Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
}.getOrNull()
