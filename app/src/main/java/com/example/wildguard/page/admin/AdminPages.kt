package com.example.wildguard.page.admin

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.wildguard.R
import com.example.wildguard.data.repository.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.util.Date

private val AdminGreen = Color(0xFF176B43)
private enum class AdminScreen { Dashboard, News, Reports, Announcements, Users }

@Composable
fun AdminApp(onLogout: () -> Unit) {
    var screen by remember { mutableStateOf(AdminScreen.Dashboard) }
    when (screen) {
        AdminScreen.Dashboard -> AdminDashboard(
            onNews = { screen = AdminScreen.News },
            onReports = { screen = AdminScreen.Reports },
            onAnnouncements = { screen = AdminScreen.Announcements },
            onUsers = { screen = AdminScreen.Users },
            onLogout = onLogout
        )
        AdminScreen.News -> ManageNewsPage { screen = AdminScreen.Dashboard }
        AdminScreen.Reports -> ManageReportsPage { screen = AdminScreen.Dashboard }
        AdminScreen.Announcements -> ManageAnnouncementsPage { screen = AdminScreen.Dashboard }
        AdminScreen.Users -> ManageUsersPage { screen = AdminScreen.Dashboard }
    }
}

@Composable
private fun AdminDashboard(
    onNews: () -> Unit,
    onReports: () -> Unit,
    onAnnouncements: () -> Unit,
    onUsers: () -> Unit,
    onLogout: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(Color(0xFFF4F8F5))) {
        Row(
            Modifier.fillMaxWidth().background(AdminGreen).statusBarsPadding().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painterResource(R.drawable.wildguard_logo), null, Modifier.size(56.dp).clip(CircleShape))
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text("Admin Dashboard", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text("Manage WildGuard content and users", color = Color.White.copy(alpha = .85f), fontSize = 12.sp)
            }
            IconButton(onClick = onLogout) { Icon(Icons.Outlined.Logout, "Log out", tint = Color.White) }
        }
        Text("Management", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, modifier = Modifier.padding(18.dp))
        Column(Modifier.padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminModuleCard("Manage News", "Add, edit or delete wildlife news", Icons.Outlined.Newspaper, Color(0xFFCDEFD2), onNews)
            AdminModuleCard("Manage Reports", "Review cases submitted by users", Icons.Outlined.Assignment, Color(0xFFFFE4BF), onReports)
            AdminModuleCard("Announcement Management", "Share announcements with all users", Icons.Outlined.Campaign, Color(0xFFCDE8F6), onAnnouncements)
            AdminModuleCard("Manage Users", "View and manage user details", Icons.Outlined.Group, Color(0xFFE2D9F5), onUsers)
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
    Row(
        Modifier.fillMaxWidth().background(AdminGreen).statusBarsPadding().height(62.dp).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = Color.White) }
        Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp, modifier = Modifier.weight(1f))
        addAction?.let { IconButton(onClick = it) { Icon(Icons.Outlined.Add, "Add", tint = Color.White) } }
    }
}

@Composable
private fun ManageNewsPage(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var editor by remember { mutableStateOf<ManagedNews?>(null) }
    var showNewEditor by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<ManagedNews?>(null) }
    LaunchedEffect(Unit) { AdminContentRepository.refreshNews() }
    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        AdminTopBar("News Details", onBack) { showNewEditor = true }
        if (AdminContentRepository.news.isEmpty()) EmptyAdminState("No news yet", "Tap + to publish the first news.")
        else LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(AdminContentRepository.news, key = { it.id }) { item ->
                AdminNewsCard(item, { editor = item }, { deleteTarget = item })
            }
        }
    }
    if (showNewEditor || editor != null) NewsEditorDialog(editor, {
        showNewEditor = false; editor = null
    }) { scope.launch { AdminContentRepository.saveNews(it) }; showNewEditor = false; editor = null }
    deleteTarget?.let { item ->
        ConfirmDelete(
            message = "Delete this news?",
            onConfirm = { scope.launch { AdminContentRepository.deleteNews(item.id) }; deleteTarget = null },
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
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
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
            else onSave(ManagedNews(existing?.id ?: java.util.UUID.randomUUID().toString(), title.trim(), content.trim(), image, existing?.createdAt ?: System.currentTimeMillis()))
        }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun ManageAnnouncementsPage(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var editor by remember { mutableStateOf<ManagedAnnouncement?>(null) }
    var adding by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<ManagedAnnouncement?>(null) }
    LaunchedEffect(Unit) { AdminContentRepository.refreshAnnouncements() }
    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        AdminTopBar("Announcement Details", onBack) { adding = true }
        if (AdminContentRepository.announcements.isEmpty()) EmptyAdminState("No announcement yet", "Tap + to share an announcement.")
        else LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(AdminContentRepository.announcements, key = { it.id }) { item ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(item.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text(item.content, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                        Text(formatTime(item.createdAt), fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                        Row(Modifier.align(Alignment.End)) {
                            TextButton(onClick = { editor = item }) { Text("Edit") }
                            TextButton(onClick = { deleteTarget = item }) { Text("Delete", color = Color.Red) }
                        }
                    }
                }
            }
        }
    }
    if (adding || editor != null) AnnouncementEditorDialog(editor, { adding = false; editor = null }) {
        scope.launch { AdminContentRepository.saveAnnouncement(it) }; adding = false; editor = null
    }
    deleteTarget?.let { item ->
        ConfirmDelete(
            message = "Delete this announcement?",
            onConfirm = { scope.launch { AdminContentRepository.deleteAnnouncement(item.id) }; deleteTarget = null },
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
        confirmButton = { Button(onClick = { if (title.isBlank() || content.isBlank()) error = "Title and content are required." else onSave(ManagedAnnouncement(existing?.id ?: java.util.UUID.randomUUID().toString(), title.trim(), content.trim(), existing?.createdAt ?: System.currentTimeMillis())) }) { Text("Share") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
private fun ManageReportsPage(onBack: () -> Unit) {
    var selected by remember { mutableStateOf<com.example.wildguard.data.report.IncidentReport?>(null) }
    LaunchedEffect(Unit) { SharedIncidentRepository.refresh() }
    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        AdminTopBar("Reports Details", onBack)
        if (SharedIncidentRepository.reports.isEmpty()) EmptyAdminState("No reports", "User incident reports will appear here.")
        else LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            items(SharedIncidentRepository.reports, key = { it.reportId }) { report ->
                Card(onClick = { selected = report }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp)) {
                    Column(Modifier.padding(13.dp)) {
                        Row { Text(report.incidentType.displayName, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f)); Text(report.severity.name, color = Color(0xFFD56A00), fontSize = 11.sp) }
                        Text(report.animalSpecies.ifBlank { "Animal not specified" }, fontSize = 12.sp)
                        Text(report.locationName, color = AdminGreen, fontSize = 11.sp)
                        Text(formatTime(report.timestamp), color = Color.Gray, fontSize = 10.sp)
                    }
                }
            }
        }
    }
    selected?.let { ReportDetailsDialog(it) { selected = null } }
}

@Composable
private fun ReportDetailsDialog(report: com.example.wildguard.data.report.IncidentReport, onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(onDismissRequest = onDismiss, title = { Text(report.incidentType.displayName) }, text = { Column {
        Detail("Report ID", report.reportId); Detail("Animal", report.animalSpecies.ifBlank { "Not specified" }); Detail("Animal count", report.animalCount.toString());
        Detail("Severity", report.severity.name); Detail("Location", report.locationName); Detail("Coordinates", "${report.latitude}, ${report.longitude}"); Detail("Description", report.description); Detail("Reported", formatTime(report.timestamp))
    } }, confirmButton = { Button(onClick = {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:${report.latitude},${report.longitude}?q=${report.latitude},${report.longitude}")))
    }) { Text("Open Location") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } })
}

@Composable
private fun ManageUsersPage(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var selected by remember { mutableStateOf<ManagedUser?>(null) }
    var deleteTarget by remember { mutableStateOf<ManagedUser?>(null) }
    LaunchedEffect(Unit) { AdminContentRepository.refreshUsers() }
    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        AdminTopBar("User Details", onBack)
        if (AdminContentRepository.users.isEmpty()) EmptyAdminState("No users", "Registered user profiles will appear here.")
        else LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            items(AdminContentRepository.users, key = { it.id }) { user ->
                Card(onClick = { selected = user }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(15.dp)) {
                    Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(44.dp).background(Color(0xFFE2F1E6), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Person, null, tint = AdminGreen) }
                        Column(Modifier.padding(start = 10.dp).weight(1f)) { Text(user.fullName.ifBlank { "Unnamed User" }, fontWeight = FontWeight.ExtraBold); Text(user.email, fontSize = 11.sp); if (user.isDisabled) Text("DISABLED", color = Color.Red, fontSize = 10.sp) }
                        IconButton(onClick = { deleteTarget = user }) { Icon(Icons.Outlined.Delete, null, tint = Color.Red) }
                    }
                }
            }
        }
    }
    selected?.let { UserEditorDialog(it, { selected = null }) { scope.launch { AdminContentRepository.saveUser(it) }; selected = null } }
    deleteTarget?.let { user ->
        ConfirmDelete(
            message = "Remove ${user.email} profile?",
            onConfirm = { scope.launch { AdminContentRepository.deleteUserProfile(user.id) }; deleteTarget = null },
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

@Composable private fun EmptyAdminState(title: String, description: String) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Outlined.Inbox, null, tint = Color.Gray, modifier = Modifier.size(52.dp)); Text(title, fontWeight = FontWeight.Bold); Text(description, color = Color.Gray, fontSize = 12.sp) } } }
@Composable private fun Detail(label: String, value: String) { Text(label, color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 7.dp)); Text(value, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
@Composable private fun ConfirmDelete(message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) { AlertDialog(onDismissRequest = onDismiss, title = { Text("Confirm Delete") }, text = { Text(message) }, confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Delete") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }) }
private fun formatTime(time: Long): String = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(time))
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
