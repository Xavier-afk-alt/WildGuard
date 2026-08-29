package com.example.wildguard.page

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
//import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wildguard.data.remote.SupabaseSession
import com.example.wildguard.data.report.IncidentReport
import com.example.wildguard.data.report.IncidentType
import com.example.wildguard.data.report.Severity
import com.example.wildguard.navigation.Page
import com.example.wildguard.navigation.navigateToTopLevel
import com.example.wildguard.viewmodel.ReportViewModel
import com.example.wildguard.viewmodel.RewardViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val ReportGreen = Color(0xFF2E7D32)
private val ReportBackground = Color(0xFFF1F8F1)

private data class IncidentTypeUi(
    val type: IncidentType,
    val emoji: String,
    val label: String
)

@Composable
fun ReportPage(
    navController: NavController,
    rewardViewModel: RewardViewModel,
    reportViewModel: ReportViewModel = viewModel()
) {
    var showDetailForm by remember { mutableStateOf(false) }
    var showMapPicker by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<IncidentType?>(null) }
    var locationName by remember { mutableStateOf("Choose Location") }
    var selectedLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var successReportId by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val fusedLocationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun resetForm() {
        showDetailForm = false
        showMapPicker = false
        selectedType = null
        locationName = "Choose Location"
        selectedLocation = null
        reportViewModel.clearError()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showDetailForm) {
            ReportForm(
                modifier = Modifier.fillMaxSize(),
                initialType = selectedType ?: IncidentType.OTHER,
                initialLocation = selectedLocation,
                initialLocationName = locationName,
                onBack = { showDetailForm = false },
                onEditLocation = { showMapPicker = true },
                errorMessage = reportViewModel.errorMessage,
                onClearError = reportViewModel::clearError,
                onSubmit = { report ->
                    val reportWithUser = report.copy(
                        userId = SupabaseSession.currentUserIdOrNull().orEmpty()
                    )
                    if (reportViewModel.submitReport(reportWithUser)) {
                        rewardViewModel.recordReportSubmitted()
                        successReportId = reportViewModel.lastSubmittedReport?.reportId
                    }
                }
            )
        } else {
            IncidentSelectionScreen(
                modifier = Modifier.fillMaxSize(),
                selectedType = selectedType,
                onTypeSelected = { selectedType = it },
                locationName = locationName,
                onLocationClick = { showMapPicker = true },
                onContinue = {
                    if (selectedType != null) {
                        reportViewModel.clearError()
                        showDetailForm = true
                    } else {
                        Toast.makeText(
                            context,
                            "Please select an incident type",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onSosClick = { navController.navigate(Page.Emergency.route) }
            )
        }

        // Keep the underlying report screen in composition while the map is open.
        // This preserves typed description/evidence when the user edits the location.
        if (showMapPicker) {
            MapLocationPicker(
                initialLocation = selectedLocation,
                initialLocationName = locationName,
                onLocationPicked = { lat, lon, name ->
                    selectedLocation = lat to lon
                    locationName = name.ifBlank {
                        "Map: ${String.format(Locale.getDefault(), "%.4f", lat)}, " +
                            String.format(Locale.getDefault(), "%.4f", lon)
                    }
                    showMapPicker = false
                },
                onDismiss = { showMapPicker = false },
                fusedLocationClient = fusedLocationClient
            )
        }
    }

    successReportId?.let { reportId ->
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Report Submitted") },
            text = {
                Text(
                    "Report $reportId was submitted successfully. " +
                        "You earned 50 report points, your achievement progress was updated, " +
                        "and any unlocked achievement bonus will also appear in Reward."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        successReportId = null
                        resetForm()
                        navigateToTopLevel(
                            navController = navController,
                            route = Page.Reward.route
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ReportGreen)
                ) {
                    Text("View Reward")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        successReportId = null
                        resetForm()
                    }
                ) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun IncidentSelectionScreen(
    modifier: Modifier = Modifier,
    selectedType: IncidentType?,
    onTypeSelected: (IncidentType) -> Unit,
    locationName: String,
    onLocationClick: () -> Unit,
    onContinue: () -> Unit,
    onSosClick: () -> Unit
) {
    val types = remember {
        listOf(
            IncidentTypeUi(IncidentType.INJURED_ANIMAL, "🐘", "Injured Animal"),
            IncidentTypeUi(IncidentType.ILLEGAL_HUNTING, "🚫", "Illegal Hunting"),
            IncidentTypeUi(IncidentType.HABITAT_DAMAGE, "🌳", "Habitat Damage"),
            IncidentTypeUi(IncidentType.ANIMAL_TRAP, "📦", "Animal Trap"),
            IncidentTypeUi(IncidentType.DEAD_ANIMAL, "💀", "Dead Animal"),
            IncidentTypeUi(IncidentType.OTHER, "❓", "Other")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ReportGreen)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Report Wildlife Incident",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Step 1: choose incident type",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = onSosClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("SOS", fontWeight = FontWeight.Bold)
            }
        }

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Choose Incident Type",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(types) { item ->
                        IncidentTypeCard(
                            item = item,
                            isSelected = selectedType == item.type,
                            onClick = { onTypeSelected(item.type) }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onLocationClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    border = BorderStroke(1.dp, ReportGreen)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = ReportGreen
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            locationName,
                            color = Color.Black,
                            maxLines = 1
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ReportGreen)
                ) {
                    Text("Continue", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun IncidentTypeCard(
    item: IncidentTypeUi,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(116.dp)
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) ReportGreen else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFDDF2E0) else Color(0xFFEAF5EB)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(Color.White.copy(alpha = 0.65f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(item.emoji, fontSize = 23.sp)
            }
            Spacer(Modifier.height(7.dp))
            Text(
                item.label,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = ReportGreen,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MapLocationPicker(
    initialLocation: Pair<Double, Double>?,
    initialLocationName: String,
    onLocationPicked: (Double, Double, String) -> Unit,
    onDismiss: () -> Unit,
    fusedLocationClient: FusedLocationProviderClient
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val initialPos = remember(initialLocation) {
        initialLocation?.let { GeoPoint(it.first, it.second) } ?: GeoPoint(3.1390, 101.6869)
    }

    var markerPos by remember(initialLocation) {
        mutableStateOf(initialLocation?.let { GeoPoint(it.first, it.second) })
    }
    var markerName by remember(initialLocationName) {
        mutableStateOf(if (initialLocation != null) initialLocationName else "")
    }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    fun placeMarker(point: GeoPoint, title: String) {
        markerPos = point
        markerName = title
        mapViewRef?.let { map ->
            updateMapMarker(map, point, title)
            map.controller.animateTo(point)
            map.controller.setZoom(17.0)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            fetchCurrentLocation(
                fusedLocationClient = fusedLocationClient,
                context = context,
                onLocationFound = { lat, lon ->
                    placeMarker(
                        GeoPoint(lat, lon),
                        "Current location"
                    )
                }
            )
        } else {
            Toast.makeText(
                context,
                "Location permission is needed to use your current location.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun requestCurrentLocation() {
        val hasFine = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            fetchCurrentLocation(
                fusedLocationClient = fusedLocationClient,
                context = context,
                onLocationFound = { lat, lon ->
                    placeMarker(GeoPoint(lat, lon), "Current location")
                }
            )
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    DisposableEffect(mapViewRef) {
        val mapView = mapViewRef
        mapView?.onResume()
        onDispose {
            mapView?.onPause()
            mapView?.onDetach()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AndroidView(
            factory = { ctx ->
                Configuration.getInstance().apply {
                    load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
                    userAgentValue = ctx.packageName
                }
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setUseDataConnection(true)
                    setMultiTouchControls(true)
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                    controller.setZoom(15.0)
                    controller.setCenter(initialPos)
                    onResume()

                    overlays.add(
                        MapEventsOverlay(
                            object : MapEventsReceiver {
                                override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                    placeMarker(p, "Incident location")
                                    return true
                                }

                                override fun longPressHelper(p: GeoPoint): Boolean = false
                            }
                        )
                    )

                    mapViewRef = this
                    markerPos?.let { updateMapMarker(this, it, markerName) }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search location...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    val result = searchLocation(context, searchQuery)
                                    if (result != null) {
                                        placeMarker(
                                            GeoPoint(result.first, result.second),
                                            searchQuery.trim()
                                        )
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Location not found.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Search",
                                tint = ReportGreen
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = ReportGreen,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Button(
                onClick = ::requestCurrentLocation,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, ReportGreen)
            ) {
                Icon(
                    Icons.Default.MyLocation,
                    contentDescription = null,
                    tint = ReportGreen
                )
                Spacer(Modifier.width(6.dp))
                Text("Use Current Location", color = ReportGreen)
            }

            Surface(
                color = Color.White.copy(alpha = 0.92f),
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp
            ) {
                Text(
                    "OpenStreetMap • tap anywhere on the map to place the incident marker",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF666666))
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    markerPos?.let { point ->
                        onLocationPicked(
                            point.latitude,
                            point.longitude,
                            markerName.ifBlank {
                                "Map: ${String.format(Locale.getDefault(), "%.4f", point.latitude)}, " +
                                    String.format(Locale.getDefault(), "%.4f", point.longitude)
                            }
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = markerPos != null,
                colors = ButtonDefaults.buttonColors(containerColor = ReportGreen)
            ) {
                Text("Confirm")
            }
        }
    }
}

private fun updateMapMarker(
    mapView: MapView,
    point: GeoPoint,
    title: String
) {
    mapView.overlays
        .filterIsInstance<Marker>()
        .toList()
        .forEach { mapView.overlays.remove(it) }

    mapView.overlays.add(
        Marker(mapView).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            this.title = title.ifBlank { "Incident location" }
        }
    )
    mapView.invalidate()
}

@Suppress("DEPRECATION")
private suspend fun searchLocation(
    context: Context,
    query: String
): Pair<Double, Double>? = withContext(Dispatchers.IO) {
    try {
        val geocoder = android.location.Geocoder(context)
        val addresses = geocoder.getFromLocationName(query, 1)
        addresses?.firstOrNull()?.let { it.latitude to it.longitude }
    } catch (_: Exception) {
        null
    }
}

@SuppressLint("MissingPermission")
private fun fetchCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationFound: (Double, Double) -> Unit
) {
    val hasFine = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasFine && !hasCoarse) return

    val cancellationToken = CancellationTokenSource()
    fusedLocationClient
        .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
        .addOnSuccessListener { currentLocation ->
            if (currentLocation != null) {
                onLocationFound(currentLocation.latitude, currentLocation.longitude)
            } else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { lastLocation ->
                        if (lastLocation != null) {
                            onLocationFound(lastLocation.latitude, lastLocation.longitude)
                        } else {
                            Toast.makeText(
                                context,
                                "No GPS fix yet. In the emulator, set a location in Extended controls > Location, or tap the map manually.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Unable to get current location. You can still tap the map manually.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
        .addOnFailureListener {
            Toast.makeText(
                context,
                "Unable to get current location. You can still tap the map manually.",
                Toast.LENGTH_SHORT
            ).show()
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportForm(
    modifier: Modifier = Modifier,
    initialType: IncidentType,
    initialLocation: Pair<Double, Double>?,
    initialLocationName: String,
    onBack: () -> Unit,
    onEditLocation: () -> Unit,
    errorMessage: String?,
    onClearError: () -> Unit,
    onSubmit: (IncidentReport) -> Unit
) {
    var species by remember(initialType) { mutableStateOf("") }
    var count by remember(initialType) { mutableStateOf("1") }
    var isAggressive by remember(initialType) { mutableStateOf(false) }
    var description by remember(initialType) { mutableStateOf("") }
    var severity by remember(initialType) { mutableStateOf(Severity.MEDIUM) }
    var weather by remember(initialType) { mutableStateOf("Cloudy") }
    var imageUris by remember(initialType) { mutableStateOf(emptyList<Uri>()) }
    var expandedWeather by remember { mutableStateOf(false) }
    var selectedDateTime by remember(initialType) { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var lastUsedAction by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    if (showDatePicker) {
        LaunchedEffect(Unit) {
            val calendar = Calendar.getInstance().apply { time = selectedDateTime }
            android.app.DatePickerDialog(
                context,
                { _, year, month, day ->
                    selectedDateTime = Calendar.getInstance().apply {
                        time = selectedDateTime
                        set(year, month, day)
                    }.time
                    showDatePicker = false
                    showTimePicker = true
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                setOnCancelListener { showDatePicker = false }
                show()
            }
        }
    }

    if (showTimePicker) {
        LaunchedEffect(Unit) {
            val calendar = Calendar.getInstance().apply { time = selectedDateTime }
            android.app.TimePickerDialog(
                context,
                { _, hour, minute ->
                    selectedDateTime = Calendar.getInstance().apply {
                        time = selectedDateTime
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                    }.time
                    showTimePicker = false
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).apply {
                setOnCancelListener { showTimePicker = false }
                show()
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUris = imageUris + it
            analyzeImageForAnimal(context, it) { label ->
                if (species.isBlank()) species = label
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val savedUri = saveBitmapToCache(context, it)
            if (savedUri != null) {
                imageUris = imageUris + savedUri
            }
            analyzeImageForAnimal(it) { label ->
                if (species.isBlank()) species = label
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ReportBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    initialType.displayName,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReportGreen
                )
                Text(
                    "Step 2: incident details",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(ReportGreen, RoundedCornerShape(2.dp))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(ReportGreen, RoundedCornerShape(2.dp))
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportActionCard(
                title = "Snap Photo",
                icon = Icons.Default.CameraAlt,
                isSelected = lastUsedAction == "camera"
            ) {
                lastUsedAction = "camera"
                cameraLauncher.launch(null)
            }

            ReportActionCard(
                title = "Upload Gallery",
                icon = Icons.Default.PhotoLibrary,
                isSelected = lastUsedAction == "gallery"
            ) {
                lastUsedAction = "gallery"
                imagePickerLauncher.launch("image/*")
            }
        }

        InfoCard(
            label = "Current Location",
            value = initialLocationName,
            icon = Icons.Default.LocationOn,
            onClick = onEditLocation
        )

        val formattedDateTime = remember(selectedDateTime) {
            SimpleDateFormat(
                "dd MMM yyyy • hh:mm a",
                Locale.getDefault()
            ).format(selectedDateTime)
        }

        InfoCard(
            label = "Date & Time",
            value = formattedDateTime,
            icon = Icons.Default.Event,
            onClick = { showDatePicker = true }
        )

        Text(
            "Incident Severity",
            fontWeight = FontWeight.Bold,
            color = ReportGreen
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SeverityOption("Low", severity == Severity.LOW, Color.Gray) {
                severity = Severity.LOW
            }
            SeverityOption("Medium", severity == Severity.MEDIUM, Color(0xFFFFA000)) {
                severity = Severity.MEDIUM
            }
            SeverityOption("High", severity == Severity.HIGH, Color.Red) {
                severity = Severity.HIGH
            }
            SeverityOption("Critical", severity == Severity.CRITICAL, Color(0xFFB71C1C)) {
                severity = Severity.CRITICAL
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Animals Affected",
                    fontWeight = FontWeight.Bold,
                    color = ReportGreen,
                    fontSize = 13.sp
                )
                OutlinedTextField(
                    value = count,
                    onValueChange = { value ->
                        count = value.filter(Char::isDigit).take(4)
                        onClearError()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Weather",
                    fontWeight = FontWeight.Bold,
                    color = ReportGreen,
                    fontSize = 13.sp
                )
                ExposedDropdownMenuBox(
                    expanded = expandedWeather,
                    onExpandedChange = { expandedWeather = !expandedWeather }
                ) {
                    OutlinedTextField(
                        value = weather,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWeather) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expandedWeather,
                        onDismissRequest = { expandedWeather = false }
                    ) {
                        listOf("Clear", "Cloudy", "Rainy", "Stormy").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    weather = option
                                    expandedWeather = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Is the animal aggressive?",
                fontWeight = FontWeight.Bold,
                color = ReportGreen
            )
            Switch(
                checked = isAggressive,
                onCheckedChange = { isAggressive = it },
                colors = SwitchDefaults.colors(checkedThumbColor = ReportGreen)
            )
        }

        OutlinedTextField(
            value = species,
            onValueChange = { species = it },
            label = { Text("Detected / Observed Species") },
            supportingText = {
                Text("A selected photo may suggest a species automatically; you can edit it.")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                onClearError()
            },
            label = { Text("Description") },
            placeholder = { Text("Describe what happened...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        if (imageUris.isNotEmpty()) {
            Text(
                "Evidence Attached (${imageUris.size})",
                fontWeight = FontWeight.Bold,
                color = ReportGreen
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                imageUris.forEach { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Report evidence",
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = Color(0xFFB3261E),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                onSubmit(
                    IncidentReport(
                        incidentType = initialType,
                        animalSpecies = species.trim(),
                        animalCount = count.toIntOrNull() ?: 0,
                        isAggressive = isAggressive,
                        description = description.trim(),
                        latitude = initialLocation?.first ?: 0.0,
                        longitude = initialLocation?.second ?: 0.0,
                        locationName = initialLocationName,
                        severity = severity,
                        weather = weather,
                        imageUris = imageUris.map(Uri::toString),
                        timestamp = selectedDateTime.time
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ReportGreen)
        ) {
            Text("Submit Report", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RowScope.ReportActionCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(96.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) ReportGreen else Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = ReportGreen)
            Spacer(Modifier.height(5.dp))
            Text(title, fontSize = 12.sp, color = ReportGreen)
        }
    }
}

@Composable
private fun InfoCard(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = ReportGreen)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(label, fontSize = 11.sp, color = Color.Gray)
                Text(
                    value,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = ReportGreen
                )
            }
        }
    }
}

@Composable
private fun SeverityOption(
    text: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = color,
                unselectedColor = Color.LightGray
            )
        )
        Text(
            text,
            fontSize = 12.sp,
            color = if (isSelected) color else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val file = File(
            context.cacheDir,
            "report_${System.currentTimeMillis()}.jpg"
        )
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
        }
        Uri.fromFile(file)
    } catch (_: Exception) {
        Toast.makeText(context, "Unable to attach the captured photo.", Toast.LENGTH_SHORT).show()
        null
    }
}

private fun analyzeImageForAnimal(
    context: Context,
    uri: Uri,
    onResult: (String) -> Unit
) {
    try {
        val image = InputImage.fromFilePath(context, uri)
        runImageLabeling(image, onResult)
    } catch (_: Exception) {
        // The evidence photo remains attached even if ML labeling is unavailable.
    }
}

private fun analyzeImageForAnimal(
    bitmap: Bitmap,
    onResult: (String) -> Unit
) {
    runImageLabeling(InputImage.fromBitmap(bitmap, 0), onResult)
}

private fun runImageLabeling(
    image: InputImage,
    onResult: (String) -> Unit
) {
    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    labeler.process(image)
        .addOnSuccessListener { labels ->
            labels.firstOrNull()?.text?.takeIf(String::isNotBlank)?.let(onResult)
        }
        .addOnCompleteListener {
            labeler.close()
        }
}
