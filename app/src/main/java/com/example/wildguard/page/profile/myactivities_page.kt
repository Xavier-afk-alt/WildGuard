package com.example.wildguard.page.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wildguard.components.PageTopBar
import com.example.wildguard.data.remote.SupabaseProvider
import com.example.wildguard.data.repository.SharedIncidentRepository
import com.example.wildguard.localization.AppLanguage
import com.example.wildguard.data.report.IncidentReport
import io.github.jan.supabase.auth.auth
import java.text.DateFormat
import java.util.Date

@Composable
fun MyActivitiesPage(navController: NavController) {
    var selectedReport by remember { mutableStateOf<IncidentReport?>(null) }
    val userId = if (SupabaseProvider.isConfigured) SupabaseProvider.client.auth.currentUserOrNull()?.id.orEmpty() else ""
    LaunchedEffect(userId) { SharedIncidentRepository.refresh() }
    val reports = SharedIncidentRepository.reports.filter { it.userId == userId }
    Column(Modifier.fillMaxSize().background(Color(0xFFF5F7F5))) {
        PageTopBar(AppLanguage.text("My Report History", "我的举报记录", "Sejarah Laporan Saya")) { navController.popBackStack() }
        if (reports.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Assignment, null, tint = Color.Gray, modifier = Modifier.size(58.dp))
                    Text(AppLanguage.text("No reports submitted", "还没有提交举报", "Tiada laporan dihantar"), fontWeight = FontWeight.Bold)
                }
            }
        } else LazyColumn(contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(reports, key = { it.reportId }) { report ->
                Card(onClick = { selectedReport = report }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(Color.White)) {
                    Column(Modifier.padding(15.dp)) {
                        report.imageUris.firstOrNull()?.let { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Report photo",
                                modifier = Modifier.fillMaxWidth().height(150.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(9.dp))
                        }
                        Row(Modifier.fillMaxWidth()) {
                            Text(report.incidentType.displayName, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                            Text(report.status.name, color = Color(0xFF08743F), fontWeight = FontWeight.Bold)
                        }
                        Text(report.animalSpecies.ifBlank { AppLanguage.text("Animal not specified", "未指定动物", "Haiwan tidak dinyatakan") })
                        Text(report.locationName, color = Color.Gray)
                        Text(DateFormat.getDateTimeInstance().format(Date(report.timestamp)), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
    selectedReport?.let { report ->
        AlertDialog(
            onDismissRequest = { selectedReport = null },
            title = { Text(report.incidentType.displayName, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    report.imageUris.forEach { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Report photo",
                            modifier = Modifier.fillMaxWidth().height(190.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text("${AppLanguage.text("Status", "状态", "Status")}: ${report.status.name}", color = Color(0xFF08743F), fontWeight = FontWeight.Bold)
                    Text("${AppLanguage.text("Species", "物种", "Spesies")}: ${report.animalSpecies.ifBlank { AppLanguage.text("Animal not specified", "未指定动物", "Haiwan tidak dinyatakan") }}")
                    Text("${AppLanguage.text("Animals affected", "受影响动物数量", "Haiwan terjejas")}: ${report.animalCount}")
                    Text("${AppLanguage.text("Location", "位置", "Lokasi")}: ${report.locationName}")
                    Text("${AppLanguage.text("Description", "描述", "Penerangan")}: ${report.description}")
                    Text(DateFormat.getDateTimeInstance().format(Date(report.timestamp)), color = Color.Gray)
                }
            },
            confirmButton = { TextButton(onClick = { selectedReport = null }) { Text(AppLanguage.text("Close", "关闭", "Tutup")) } }
        )
    }
}
