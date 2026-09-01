package com.example.wildguard.page

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wildguard.data.repository.AdminContentRepository
import com.example.wildguard.data.repository.ManagedNews
import java.text.DateFormat
import java.util.Date

private val ContentGreen = Color(0xFF176B43)

@Composable
fun UserNewsSection() {
    var selected by remember { mutableStateOf<ManagedNews?>(null) }
    LaunchedEffect(Unit) { AdminContentRepository.refreshNews() }
    if (AdminContentRepository.news.isEmpty()) {
        Box(
            modifier = Modifier.padding(horizontal = 34.dp).fillMaxWidth().height(150.dp)
                .background(Color(0xFFF3F5F3), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) { Text("No news published yet", color = Color.Gray, fontSize = 12.sp) }
    } else {
        val item = AdminContentRepository.news.first()
        Card(
            onClick = { selected = item },
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column {
                item.imageBase64?.let { decodeNewsBitmap(it) }?.let {
                    Image(it.asImageBitmap(), item.title, Modifier.fillMaxWidth().height(160.dp), contentScale = ContentScale.Crop)
                }
                Column(Modifier.padding(12.dp)) {
                    Text(item.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text(item.content, maxLines = 2, fontSize = 11.sp)
                    Text("Tap to read details", color = ContentGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
                }
            }
        }
    }
    selected?.let { NewsDetailsDialog(it) { selected = null } }
}

@Composable
private fun NewsDetailsDialog(item: ManagedNews, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(item.title, fontWeight = FontWeight.ExtraBold) }, text = { Column {
        item.imageBase64?.let { decodeNewsBitmap(it) }?.let { Image(it.asImageBitmap(), item.title, Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop) }
        Text(item.content, fontSize = 13.sp, modifier = Modifier.padding(top = 10.dp))
    } }, confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } })
}

@Composable
fun AnnouncementPage(navController: NavController) {
    LaunchedEffect(Unit) { AdminContentRepository.refreshAnnouncements() }
    Column(Modifier.fillMaxSize().background(Color(0xFFF6F8F6))) {
        Row(
            Modifier.fillMaxWidth().background(ContentGreen).statusBarsPadding().height(62.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = Color.White) }
            Text("Announcements", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
        }
        if (AdminContentRepository.announcements.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No announcements yet", color = Color.Gray) }
        } else LazyColumn(contentPadding = PaddingValues(13.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(AdminContentRepository.announcements, key = { it.id }) { item ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Box(Modifier.size(44.dp).background(Color(0xFFE3F1E7), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Campaign, null, tint = ContentGreen) }
                        Column(Modifier.padding(start = 11.dp).weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                            Text(item.content, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                            Text(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(item.createdAt)), color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 7.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun decodeNewsBitmap(value: String) = runCatching {
    val bytes = Base64.decode(value, Base64.DEFAULT)
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}.getOrNull()
