package com.example.wildguard.page.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.wildguard.data.repository.SharedIncidentRepository
import com.example.wildguard.data.remote.WildGuardTileSource
import com.example.wildguard.data.report.IncidentReport
import com.example.wildguard.navigation.Page
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private val MapGreen = Color(0xFF2B7147)

@Composable
fun WildlifeMapPage(navController: NavController) {
    val context = LocalContext.current
    val reports = SharedIncidentRepository.reports
    var mapView by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            SharedIncidentRepository.refresh()
            delay(15_000)
        }
    }

    DisposableEffect(Unit) {
        onDispose { mapView?.onDetach() }
    }

    Column(Modifier.fillMaxSize().background(Color.White)) {
        MapHeader(navController)
        Box(Modifier.fillMaxWidth().weight(1f)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    Configuration.getInstance().apply {
                        load(context, context.getSharedPreferences("osmdroid", android.content.Context.MODE_PRIVATE))
                        userAgentValue = "WildGuard/1.0 (${context.packageName})"
                    }
                    MapView(context).apply {
                        setTileSource(WildGuardTileSource)
                        setMultiTouchControls(true)
                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                        controller.setZoom(8.0)
                        controller.setCenter(GeoPoint(4.2105, 101.9758))
                        mapView = this
                    }
                },
                update = { map ->
                    map.overlays.clear()
                    reports.filter { it.latitude != 0.0 || it.longitude != 0.0 }.forEach { report ->
                        map.overlays.add(
                            Marker(map).apply {
                                position = GeoPoint(report.latitude, report.longitude)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = report.animalSpecies.ifBlank { report.incidentType.displayName }
                                snippet = "${report.locationName}\n${report.description}"
                            }
                        )
                    }
                    reports.firstOrNull { it.latitude != 0.0 || it.longitude != 0.0 }?.let {
                        if (reports.size == 1) {
                            map.controller.setZoom(13.0)
                            map.controller.animateTo(GeoPoint(it.latitude, it.longitude))
                        }
                    }
                    map.invalidate()
                }
            )

            NearbyReportCard(
                report = reports.firstOrNull(),
                modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 18.dp, vertical = 66.dp)
            )
            MapLegend(Modifier.align(Alignment.BottomCenter).padding(horizontal = 12.dp, vertical = 12.dp))
        }
    }
}

@Composable
private fun MapHeader(navController: NavController) {
    Column(Modifier.fillMaxWidth().background(MapGreen).statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().height(58.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(40.dp).background(Color.White, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = MapGreen)
            }
            Text(
                "Wildlife Map", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 10.dp).weight(1f)
            )
            /*IconButton(onClick = { navController.navigate(Page.Announcements.route) }) {
                Icon(Icons.Outlined.Notifications, "Notifications", tint = Color.White)
            }*/
            IconButton(onClick = { navController.navigate(Page.Profile.route) }) {
                Icon(Icons.Outlined.AccountCircle, "Profile", tint = Color.White)
            }
        }
    }
}

@Composable
private fun NearbyReportCard(report: IncidentReport?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(22.dp))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(22.dp)).padding(12.dp)
    ) {
        Text("Nearby Wildlife", color = MapGreen, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
        if (report == null) {
            Text("No community incident has been reported yet.", fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        } else {
            Row(Modifier.padding(top = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).background(Color(0xFFDDF3E2), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.LocationOn, null, tint = MapGreen)
                }
                Column(Modifier.padding(start = 9.dp).weight(1f)) {
                    Text(
                        report.animalSpecies.ifBlank { report.incidentType.displayName },
                        fontWeight = FontWeight.ExtraBold, fontSize = 15.sp
                    )
                    Text(report.locationName, fontSize = 10.sp, maxLines = 1)
                    Text(report.severity.name.lowercase().replaceFirstChar { it.uppercase() }, color = MapGreen, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun MapLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().height(42.dp).background(Color.White, RoundedCornerShape(22.dp))
            .border(1.dp, Color(0xFF333333), RoundedCornerShape(22.dp)).padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        LegendItem(Icons.Outlined.LocationOn, "Report Location", MapGreen)
        LegendItem(Icons.Outlined.Warning, "Community Alert", Color(0xFFFFA3A8))
    }
}

@Composable
private fun LegendItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        Text(text, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp))
    }
}
