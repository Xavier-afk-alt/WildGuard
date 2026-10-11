package com.example.wildguard.page.explore

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wildguard.R
import com.example.wildguard.data.repository.SharedIncidentRepository
import com.example.wildguard.data.remote.WildGuardTileSource
import com.example.wildguard.data.report.IncidentReport
import com.example.wildguard.data.report.ReportStatus
import com.example.wildguard.localization.AppLanguage
import com.example.wildguard.navigation.Page
import com.example.wildguard.notification.WildGuardNotifications
import com.example.wildguard.notification.ProximityAlertManager
import kotlinx.coroutines.delay
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.DateFormat
import java.util.Date

private val MapGreen = Color(0xFF2B7147)
private const val MAX_NEARBY_REPORT_DISTANCE_METRES = 50_000.0
private val ProtectedForestPoint = GeoPoint(5.4240, 100.2690)

@Composable
fun WildlifeMapPage(navController: NavController) {
    val context = LocalContext.current
    val reports = SharedIncidentRepository.reports
    val snackbarHostState = remember { SnackbarHostState() }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var currentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var selectedReport by remember { mutableStateOf<IncidentReport?>(null) }
    var detailReport by remember { mutableStateOf<IncidentReport?>(null) }
    var showCurrentLocationDetails by remember { mutableStateOf(false) }
    var showProtectedForestDetails by remember { mutableStateOf(false) }
    var previousMapCenter by remember { mutableStateOf(GeoPoint(4.2105, 101.9758)) }
    var previousMapZoom by remember { mutableDoubleStateOf(8.0) }
    fun clearSelectedLocator() {
        selectedReport = null
        showCurrentLocationDetails = false
        showProtectedForestDetails = false
        mapView?.controller?.setZoom(previousMapZoom)
        mapView?.controller?.animateTo(previousMapCenter)
    }
    var isLocating by remember { mutableStateOf(true) }
    val activeReports = reports.filter {
        it.status != ReportStatus.RESOLVED && it.status != ReportStatus.REJECTED &&
            (it.latitude != 0.0 || it.longitude != 0.0)
    }
    // ------------------------
    // wildlife map-all nearby reports
    // ------------------------
    val nearbyReports = currentLocation?.let { userPoint ->
        activeReports
            .map { report -> report to userPoint.distanceToAsDouble(GeoPoint(report.latitude, report.longitude)) }
            .filter { (_, distance) -> distance <= MAX_NEARBY_REPORT_DISTANCE_METRES }
            .sortedBy { (_, distance) -> distance }
            .map { (report, _) -> report }
    }.orEmpty()

    fun selectNearbyReport(report: IncidentReport) {
        val map = mapView
        if (selectedReport == null && !showCurrentLocationDetails && !showProtectedForestDetails && map != null) {
            previousMapZoom = map.zoomLevelDouble
            previousMapCenter = GeoPoint(map.mapCenter.latitude, map.mapCenter.longitude)
        }
        selectedReport = report
        showCurrentLocationDetails = false
        showProtectedForestDetails = false
        map?.controller?.setZoom(16.5)
        map?.controller?.animateTo(GeoPoint(report.latitude, report.longitude))
        detailReport = report
    }
    fun locateUser() {
        fetchWildlifeMapLocation(
            context = context,
            onLocationFound = { latitude, longitude ->
                val point = GeoPoint(latitude, longitude)
                currentLocation = point
                context.getSharedPreferences("wildguard_proximity_runtime", android.content.Context.MODE_PRIVATE)
                    .edit().putString("user_last_lat", latitude.toString())
                    .putString("user_last_lon", longitude.toString()).apply()
                mapView?.controller?.setZoom(15.0)
                mapView?.controller?.animateTo(point)
            },
            onUnavailable = { isLocating = false }
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            locateUser()
        } else {
            isLocating = false
        }
    }

    LaunchedEffect(currentLocation) {
        if (currentLocation != null) {
            delay(800)
            isLocating = false
        }
    }

    LaunchedEffect(isLocating) {
        if (isLocating) {
            delay(8_000)
            isLocating = false
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            locateUser()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            SharedIncidentRepository.refresh()
            delay(15_000)
        }
    }

    LaunchedEffect(currentLocation, activeReports.map { "${it.reportId}:${it.status}:${it.severity}:${it.isAggressive}" }) {
        val point = currentLocation ?: return@LaunchedEffect
        ProximityAlertManager.checkUser(context, point.latitude, point.longitude, activeReports)?.let { alert ->
            WildGuardNotifications.proximity(context, alert)
            snackbarHostState.showSnackbar(alert.message)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            locateUser()
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
                        onResume()
                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                        controller.setZoom(8.0)
                        controller.setCenter(GeoPoint(4.2105, 101.9758))
                        mapView = this
                    }
                },
                update = { map ->
                    map.overlays.clear()
                    currentLocation?.let { point ->
                        map.overlays.add(
                            Marker(map).apply {
                                position = point
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Your current location"
                                icon = ContextCompat.getDrawable(
                                    context,
                                    if (showCurrentLocationDetails) R.drawable.map_marker_current_selected
                                    else R.drawable.map_marker_current
                                )
                                setOnMarkerClickListener { marker, _ ->
                                    if (showCurrentLocationDetails) {
                                        clearSelectedLocator()
                                    } else {
                                        if (selectedReport == null && !showProtectedForestDetails) {
                                            previousMapZoom = map.zoomLevelDouble
                                            previousMapCenter = GeoPoint(map.mapCenter.latitude, map.mapCenter.longitude)
                                        }
                                        showCurrentLocationDetails = true
                                        showProtectedForestDetails = false
                                        selectedReport = null
                                        map.controller.setZoom(16.5)
                                        map.controller.animateTo(marker.position)
                                    }
                                    true
                                }
                            }
                        )
                    }
                    activeReports.forEach { report ->
                        map.overlays.add(
                            Marker(map).apply {
                                position = GeoPoint(report.latitude, report.longitude)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = report.animalSpecies.ifBlank { report.incidentType.displayName }
                                snippet = "${report.locationName}\n${report.description}"
                                icon = ContextCompat.getDrawable(
                                    context,
                                    if (selectedReport?.reportId == report.reportId) R.drawable.map_marker_report_selected
                                    else R.drawable.map_marker_report
                                )
                                setOnMarkerClickListener { marker, _ ->
                                    if (selectedReport?.reportId == report.reportId) {
                                        clearSelectedLocator()
                                    } else {
                                        if (selectedReport == null && !showCurrentLocationDetails && !showProtectedForestDetails) {
                                            previousMapZoom = map.zoomLevelDouble
                                            previousMapCenter = GeoPoint(map.mapCenter.latitude, map.mapCenter.longitude)
                                        }
                                        selectedReport = report
                                        showCurrentLocationDetails = false
                                        showProtectedForestDetails = false
                                        map.controller.setZoom(16.5)
                                        map.controller.animateTo(marker.position)
                                    }
                                    true
                                }
                            }
                        )
                    }
                    map.overlays.add(
                        Marker(map).apply {
                            position = ProtectedForestPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "Penang Hill Protected Forest"
                            icon = ContextCompat.getDrawable(
                                context,
                                if (showProtectedForestDetails) R.drawable.map_marker_forest_selected
                                else R.drawable.map_marker_forest
                            )
                            setOnMarkerClickListener { marker, _ ->
                                if (showProtectedForestDetails) {
                                    clearSelectedLocator()
                                } else {
                                    if (selectedReport == null && !showCurrentLocationDetails && !showProtectedForestDetails) {
                                        previousMapZoom = map.zoomLevelDouble
                                        previousMapCenter = GeoPoint(map.mapCenter.latitude, map.mapCenter.longitude)
                                    }
                                    selectedReport = null
                                    showCurrentLocationDetails = false
                                    showProtectedForestDetails = true
                                    map.controller.setZoom(15.5)
                                    map.controller.animateTo(marker.position)
                                }
                                true
                            }
                        }
                    )
                    activeReports.firstOrNull()?.let {
                        if (activeReports.size == 1 && currentLocation == null) {
                            map.controller.setZoom(13.0)
                            map.controller.animateTo(GeoPoint(it.latitude, it.longitude))
                        }
                    }
                    map.invalidate()
                }
            )

            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(14.dp),
                shape = CircleShape,
                shadowElevation = 4.dp,
                color = Color.White
            ) {
                IconButton(onClick = { isLocating = true; locateUser() }) {
                    Icon(
                        painter = painterResource(R.drawable.map_marker_current),
                        contentDescription = AppLanguage.text("Center on your location", "定位到您的位置"),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            if (isLocating) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White.copy(alpha = 0.94f)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MapGreen)
                        Text(
                            "Finding your location…",
                            color = MapGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 14.dp)
                        )
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp)
            )
        }

        // Keep locator details outside the map viewport so the text cannot be hidden
        // by marker popups, map controls, or the application's bottom navigation.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF7FAF7))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MapLegend()
            if (showProtectedForestDetails) {
                ProtectedForestCard(onDeselect = ::clearSelectedLocator)
            } else if (showCurrentLocationDetails && currentLocation != null) {
                CurrentLocationCard(currentLocation!!, onDeselect = ::clearSelectedLocator)
            } else {
                NearbyReportsSection(
                    reports = nearbyReports,
                    selectedReportId = selectedReport?.reportId,
                    onReportClick = ::selectNearbyReport,
                    onDeselect = ::clearSelectedLocator
                )
            }
        }
    }

    detailReport?.let { report ->
        WildlifeReportDetailsDialog(report = report, onDismiss = { detailReport = null })
    }
}

@Composable
private fun ProtectedForestCard(onDeselect: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(22.dp))
            .border(1.dp, Color(0xFF2E7D32), RoundedCornerShape(22.dp)).padding(14.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                AppLanguage.text("Protected Forest Area", "受保护森林区", "Kawasan Hutan Dilindungi"),
                color = Color(0xFF2E7D32), fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDeselect, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Outlined.Close, AppLanguage.text("Deselect forest", "取消选择森林", "Nyahpilih hutan"), tint = Color(0xFF616161))
            }
        }
        Text("Penang Hill Biosphere Reserve", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("5.42400, 100.26900", color = Color(0xFF616161), fontSize = 11.sp)
        Text(
            AppLanguage.text(
                "A protected forest landscape supporting native wildlife and important hill ecosystems.",
                "受保护的森林景观，为本地野生动物和重要山地生态系统提供栖息地。",
                "Landskap hutan dilindungi yang menyokong hidupan liar tempatan dan ekosistem bukit penting."
            ),
            color = Color(0xFF424242), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun CurrentLocationCard(point: GeoPoint, onDeselect: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(22.dp))
            .border(1.dp, Color(0xFF167D66), RoundedCornerShape(22.dp)).padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(AppLanguage.text("Your Location", "您的位置", "Lokasi Anda"), color = Color(0xFF167D66), fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
            IconButton(onClick = onDeselect, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Outlined.Close, AppLanguage.text("Deselect location", "取消选择位置", "Nyahpilih lokasi"), tint = Color(0xFF616161))
            }
        }
        Text(AppLanguage.text("Current device position", "当前设备位置", "Kedudukan peranti semasa"), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 5.dp))
        Text("${"%.5f".format(point.latitude)}, ${"%.5f".format(point.longitude)}", color = Color(0xFF424242), fontSize = 12.sp)
    }
}

@SuppressLint("MissingPermission")
private fun fetchWildlifeMapLocation(
    context: android.content.Context,
    onLocationFound: (Double, Double) -> Unit,
    onUnavailable: () -> Unit
) {
    val hasFine = ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    if (!hasFine && !hasCoarse) return

    val client = LocationServices.getFusedLocationProviderClient(context)

    fun unavailable() {
        onUnavailable()
        Toast.makeText(
            context,
            "Current location is unavailable. Set a location in the emulator controls and try again.",
            Toast.LENGTH_LONG
        ).show()
    }

    fun useLastLocation() {
        try {
            client.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) onLocationFound(location.latitude, location.longitude)
                    else unavailable()
                }
                .addOnFailureListener { unavailable() }
        } catch (_: Exception) {
            unavailable()
        }
    }

    try {
        client.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            if (location != null) onLocationFound(location.latitude, location.longitude)
            else useLastLocation()
        }.addOnFailureListener { useLastLocation() }
    } catch (_: Exception) {
        useLastLocation()
    }
}

@Composable
private fun MapHeader(navController: NavController) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MapGreen)
            .zIndex(2f)
            .statusBarsPadding()
            .padding(bottom = 10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = Color.White)
            }
            Text(
                AppLanguage.text("Wildlife Map", "野生动物地图"), color = Color.White, fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            AppLanguage.text("View nearby wildlife reports", "查看附近的野生动物报告"),
            color = Color.White.copy(alpha = .85f),
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 52.dp, end = 16.dp, top = 1.dp)
        )
    }
}

// ------------------------
// wildlife map-nearby report list
// ------------------------
@Composable
private fun NearbyReportsSection(
    reports: List<IncidentReport>,
    selectedReportId: String?,
    onReportClick: (IncidentReport) -> Unit,
    onDeselect: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                AppLanguage.text("Nearby Reports", "附近报告", "Laporan Berdekatan"),
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            if (selectedReportId != null) {
                IconButton(onClick = onDeselect, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Outlined.Close, "Deselect report", tint = Color(0xFF616161))
                }
            }
        }
        if (reports.isEmpty()) {
            NearbyReportCard(report = null)
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                items(reports, key = { it.reportId }) { report ->
                    NearbyReportCard(
                        report = report,
                        modifier = Modifier.width(280.dp),
                        isSelected = report.reportId == selectedReportId,
                        onOpenDetails = onReportClick
                    )
                }
            }
        }
    }
}

@Composable
private fun NearbyReportCard(
    report: IncidentReport?,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onOpenDetails: (IncidentReport) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .background(Color.White, RoundedCornerShape(22.dp))
            .border(
                if (isSelected) 2.dp else 1.dp,
                if (isSelected) Color(0xFFD32F2F) else Color(0xFF222222),
                RoundedCornerShape(22.dp)
            )
            .then(if (report != null) Modifier.clickable { onOpenDetails(report) } else Modifier)
            .padding(16.dp)
    ) {
        if (report == null) {
            Text(AppLanguage.text("No active report within 50 km of your location.", "您的位置附近 50 公里内没有有效报告。", "Tiada laporan aktif dalam lingkungan 50 km dari lokasi anda."), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        } else {
            Row(Modifier.padding(top = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).background(Color(0xFFFFE1E1), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.LocationOn, null, tint = Color(0xFFD32F2F))
                }
                Column(Modifier.padding(start = 9.dp).weight(1f)) {
                    Text(
                        report.animalSpecies.ifBlank { report.incidentType.displayName },
                        fontWeight = FontWeight.ExtraBold, fontSize = 15.sp
                    )
                    Text(
                        report.locationName.ifBlank { "Location details unavailable" },
                        fontSize = 12.sp,
                        color = Color(0xFF424242),
                        maxLines = 2
                    )
                    Text(
                        "${"%.5f".format(report.latitude)}, ${"%.5f".format(report.longitude)}",
                        fontSize = 10.sp,
                        color = Color(0xFF616161)
                    )
                    Text(
                        "Severity: " + report.severity.name.lowercase().replaceFirstChar { it.uppercase() },
                        color = Color(0xFFD32F2F), fontSize = 12.sp, fontWeight = FontWeight.Bold
                    )
                    if (report.description.isNotBlank()) {
                        Text(report.description, fontSize = 11.sp, color = Color(0xFF424242), maxLines = 2)
                    }
                }
            }
        }
    }
}

@Composable
// ------------------------
// wildlife map-report details
// ------------------------
private fun WildlifeReportDetailsDialog(report: IncidentReport, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(report.incidentType.displayName, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                report.imageUris.forEach { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Reported wildlife photo",
                        modifier = Modifier.fillMaxWidth().height(190.dp)
                            .background(Color(0xFFE8EEE9), RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                MapReportDetail("Report ID", report.reportId)
                MapReportDetail("Animal", report.animalSpecies.ifBlank { "Not specified" })
                MapReportDetail("Animal count", report.animalCount.toString())
                MapReportDetail("Severity", report.severity.name)
                MapReportDetail("Status", report.status.name)
                MapReportDetail("Coordinates", "${report.latitude}, ${report.longitude}")
                MapReportDetail("Weather", report.weather)
                MapReportDetail("Description", report.description.ifBlank { "No description" })
                MapReportDetail("Reported", DateFormat.getDateTimeInstance().format(Date(report.timestamp)))
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

@Composable
private fun MapReportDetail(label: String, value: String) {
    Text(label, color = Color.Gray, fontSize = 11.sp)
    Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
}

@Composable
private fun MapLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().height(44.dp).background(Color.White, RoundedCornerShape(22.dp))
            .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(22.dp)).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        LegendMarkerItem(R.drawable.map_marker_report, AppLanguage.text("Report Location", "报告地点"))
        LegendMarkerItem(R.drawable.map_marker_current, AppLanguage.text("Your Location", "您的位置"))
        LegendMarkerItem(R.drawable.map_marker_forest, AppLanguage.text("Protected Forest", "保护森林", "Hutan Dilindungi"))
    }
}

@Composable
private fun LegendMarkerItem(drawableRes: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(drawableRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(22.dp)
        )
        Text(text, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 3.dp), maxLines = 2)
    }
}
