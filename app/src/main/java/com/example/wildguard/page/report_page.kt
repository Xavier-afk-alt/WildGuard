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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.wildguard.R
import com.example.wildguard.data.remote.SupabaseSession
import com.example.wildguard.data.remote.WildGuardTileSource
import com.example.wildguard.data.report.IncidentReport
import com.example.wildguard.data.report.IncidentType
import com.example.wildguard.data.report.Severity
import com.example.wildguard.navigation.Page
import com.example.wildguard.navigation.navigateToTopLevel
import com.example.wildguard.viewmodel.ReportViewModel
import com.example.wildguard.localization.AppLanguage
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
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val ReportGreen = Color(0xFF2E7D32)
private val ReportBackground = Color(0xFFF1F8F1)

private fun reportText(english: String, chinese: String, malay: String): String =
    AppLanguage.text(english, chinese, malay)

private fun localizedIncidentType(type: IncidentType): String = when (type) {
    IncidentType.INJURED_ANIMAL -> reportText("Injured Animal", "受伤动物", "Haiwan Cedera")
    IncidentType.ILLEGAL_HUNTING -> reportText("Illegal Hunting", "非法狩猎", "Pemburuan Haram")
    IncidentType.HABITAT_DAMAGE -> reportText("Habitat Damage", "栖息地破坏", "Kerosakan Habitat")
    IncidentType.ANIMAL_TRAP -> reportText("Animal Trap", "动物陷阱", "Perangkap Haiwan")
    IncidentType.DEAD_ANIMAL -> reportText("Dead Animal", "死亡动物", "Haiwan Mati")
    IncidentType.OTHER -> reportText("Other", "其他", "Lain-lain")
}

private fun localizedWeather(weather: String): String = when (weather) {
    "Clear" -> reportText("Clear", "晴朗", "Cerah")
    "Rainy" -> reportText("Rainy", "下雨", "Hujan")
    "Stormy" -> reportText("Stormy", "暴风雨", "Ribut")
    else -> reportText("Cloudy", "多云", "Mendung")
}

/** Persistent draft for the period in which an external camera app is open. */
private object ReportDraftStore {
    private const val PREFS = "incident_report_draft"
    private const val STEP_TWO = "step_two"
    private const val TYPE = "incident_type"
    private const val IMAGES = "image_uris"
    private const val PENDING_CAMERA_URI = "pending_camera_uri"
    private const val SPECIES = "species"
    private const val COUNT = "count"
    private const val AGGRESSIVE = "aggressive"
    private const val DESCRIPTION = "description"
    private const val SEVERITY = "severity"
    private const val WEATHER = "weather"
    private const val DATE_TIME = "date_time"
    private const val LOCATION_NAME = "location_name"
    private const val LATITUDE = "latitude"
    private const val LONGITUDE = "longitude"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isStepTwo(context: Context) = prefs(context).getBoolean(STEP_TWO, false)
    fun setStepTwo(context: Context, value: Boolean) =
        prefs(context).edit().putBoolean(STEP_TWO, value).apply()

    fun type(context: Context): IncidentType? = prefs(context).getString(TYPE, null)
        ?.let { saved -> IncidentType.entries.firstOrNull { it.name == saved } }

    fun setType(context: Context, type: IncidentType?) {
        val editor = prefs(context).edit()
        if (type == null) editor.remove(TYPE) else editor.putString(TYPE, type.name)
        editor.apply()
    }

    fun images(context: Context): List<String> =
        prefs(context).getStringSet(IMAGES, emptySet()).orEmpty().toList()

    fun setImages(context: Context, images: List<String>) =
        prefs(context).edit().putStringSet(IMAGES, images.toSet()).apply()

    fun species(context: Context) = prefs(context).getString(SPECIES, "").orEmpty()
    fun count(context: Context) = prefs(context).getString(COUNT, "1").orEmpty()
    fun aggressive(context: Context) = prefs(context).getBoolean(AGGRESSIVE, false)
    fun description(context: Context) = prefs(context).getString(DESCRIPTION, "").orEmpty()
    fun severity(context: Context) = prefs(context).getString(SEVERITY, Severity.MEDIUM.name)
        ?.let { runCatching { Severity.valueOf(it) }.getOrDefault(Severity.MEDIUM) }
        ?: Severity.MEDIUM
    fun weather(context: Context) = prefs(context).getString(WEATHER, "Cloudy").orEmpty()
    fun dateTime(context: Context) = prefs(context).getLong(DATE_TIME, System.currentTimeMillis())
    fun locationName(context: Context) = prefs(context).getString(LOCATION_NAME, "Choose Location").orEmpty()
    fun latitude(context: Context): Double? = prefs(context).takeIf { it.contains(LATITUDE) }
        ?.getLong(LATITUDE, 0L)?.let { Double.fromBits(it) }
    fun longitude(context: Context): Double? = prefs(context).takeIf { it.contains(LONGITUDE) }
        ?.getLong(LONGITUDE, 0L)?.let { Double.fromBits(it) }

    fun saveForm(
        context: Context,
        species: String,
        count: String,
        aggressive: Boolean,
        description: String,
        severity: Severity,
        weather: String,
        dateTime: Long,
        locationName: String,
        location: Pair<Double, Double>?
    ) {
        val editor = prefs(context).edit()
            .putString(SPECIES, species)
            .putString(COUNT, count)
            .putBoolean(AGGRESSIVE, aggressive)
            .putString(DESCRIPTION, description)
            .putString(SEVERITY, severity.name)
            .putString(WEATHER, weather)
            .putLong(DATE_TIME, dateTime)
            .putString(LOCATION_NAME, locationName)
        if (location == null) editor.remove(LATITUDE).remove(LONGITUDE)
        else editor.putLong(LATITUDE, location.first.toBits())
            .putLong(LONGITUDE, location.second.toBits())
        // commit is intentional: the external camera can stop this process immediately.
        editor.commit()
    }

    fun pendingCameraUri(context: Context): String? =
        prefs(context).getString(PENDING_CAMERA_URI, null)

    fun setPendingCameraUri(context: Context, uri: String?) {
        val editor = prefs(context).edit()
        if (uri == null) editor.remove(PENDING_CAMERA_URI)
        else editor.putString(PENDING_CAMERA_URI, uri)
        editor.apply()
    }

    fun clear(context: Context) = prefs(context).edit().clear().apply()
}

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
    val context = LocalContext.current
    var showDetailForm by rememberSaveable {
        mutableStateOf(ReportDraftStore.isStepTwo(context))
    }
    var showMapPicker by rememberSaveable { mutableStateOf(false) }
    var selectedType by rememberSaveable {
        mutableStateOf(ReportDraftStore.type(context))
    }
    var locationName by rememberSaveable { mutableStateOf(ReportDraftStore.locationName(context)) }
    var selectedLatitude by rememberSaveable { mutableStateOf(ReportDraftStore.latitude(context)) }
    var selectedLongitude by rememberSaveable { mutableStateOf(ReportDraftStore.longitude(context)) }
    val selectedLocation = if (selectedLatitude != null && selectedLongitude != null) {
        selectedLatitude!! to selectedLongitude!!
    } else {
        null
    }
    var successReportId by rememberSaveable { mutableStateOf<String?>(null) }

    val fusedLocationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun resetForm() {
        showDetailForm = false
        showMapPicker = false
        selectedType = null
        locationName = "Choose Location"
        selectedLatitude = null
        selectedLongitude = null
        ReportDraftStore.clear(context)
        reportViewModel.clearError()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showDetailForm) {
            ReportForm(
                modifier = Modifier.fillMaxSize(),
                initialType = selectedType ?: IncidentType.OTHER,
                initialLocation = selectedLocation,
                initialLocationName = locationName,
                onBack = {
                    showDetailForm = false
                    ReportDraftStore.setStepTwo(context, false)
                },
                onEditLocation = { showMapPicker = true },
                errorMessage = reportViewModel.errorMessage,
                isSubmitting = reportViewModel.isSubmitting,
                onClearError = reportViewModel::clearError,
                onSubmit = { report ->
                    val reportWithUser = report.copy(
                        userId = SupabaseSession.currentUserIdOrNull().orEmpty()
                    )
                    reportViewModel.submitReport(reportWithUser, context) { savedReport ->
                        rewardViewModel.recordReportSubmitted()
                        successReportId = savedReport.reportId
                    }
                }
            )
        } else {
            IncidentSelectionScreen(
                modifier = Modifier.fillMaxSize(),
                selectedType = selectedType,
                onTypeSelected = {
                    selectedType = it
                    ReportDraftStore.setType(context, it)
                },
                locationName = locationName,
                locationCoordinates = selectedLocation,
                onLocationClick = { showMapPicker = true },
                onContinue = {
                    if (selectedType != null) {
                        reportViewModel.clearError()
                        showDetailForm = true
                        ReportDraftStore.setType(context, selectedType)
                        ReportDraftStore.setStepTwo(context, true)
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
                    selectedLatitude = lat
                    selectedLongitude = lon
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
            title = { Text(AppLanguage.text("Report Submitted", "报告已提交")) },
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
                        Text(AppLanguage.text("View Reward", "查看奖励"))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        successReportId = null
                        resetForm()
                    }
                ) {
                        Text(AppLanguage.text("Done", "完成"))
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
    locationCoordinates: Pair<Double, Double>?,
    onLocationClick: () -> Unit,
    onContinue: () -> Unit,
    onSosClick: () -> Unit
) {
    val types = listOf(
        IncidentTypeUi(IncidentType.INJURED_ANIMAL, "🐘", reportText("Injured Animal", "受伤动物", "Haiwan Cedera")),
        IncidentTypeUi(IncidentType.ILLEGAL_HUNTING, "🚫", reportText("Illegal Hunting", "非法狩猎", "Pemburuan Haram")),
        IncidentTypeUi(IncidentType.HABITAT_DAMAGE, "🌳", reportText("Habitat Damage", "栖息地破坏", "Kerosakan Habitat")),
        IncidentTypeUi(IncidentType.ANIMAL_TRAP, "📦", reportText("Animal Trap", "动物陷阱", "Perangkap Haiwan")),
        IncidentTypeUi(IncidentType.DEAD_ANIMAL, "💀", reportText("Dead Animal", "死亡动物", "Haiwan Mati")),
        IncidentTypeUi(IncidentType.OTHER, "❓", reportText("Other", "其他", "Lain-lain"))
    )

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
                    reportText("Report Wildlife Incident", "举报野生动物事件", "Laporkan Insiden Hidupan Liar"),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    reportText("Step 1: choose incident type", "步骤 1：选择事件类型", "Langkah 1: pilih jenis insiden"),
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
                    reportText("Choose Incident Type", "选择事件类型", "Pilih Jenis Insiden"),
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
                            locationCoordinates?.let { (latitude, longitude) ->
                                String.format(Locale.US, "%.6f, %.6f", latitude, longitude)
                            } ?: reportText("Choose Location", "选择地点", "Pilih Lokasi"),
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
                    Text(reportText("Continue", "继续", "Teruskan"), fontSize = 17.sp, fontWeight = FontWeight.Bold)
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
    val mapView = remember(context) {
        Configuration.getInstance().apply {
            load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            userAgentValue = "WildGuard/1.0 (${context.packageName})"
        }
        MapView(context).apply {
            setTileSource(WildGuardTileSource)
            setUseDataConnection(true)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(15.0)
            controller.setCenter(initialPos)
        }
    }
    var searchQuery by remember { mutableStateOf("") }
    var isLocating by remember { mutableStateOf(initialLocation == null) }
    var automaticLocationRequest by remember { mutableStateOf(true) }
    var automaticCenter by remember { mutableStateOf<GeoPoint?>(null) }

    fun placeMarker(point: GeoPoint, title: String) {
        markerPos = point
        markerName = title
        updateMapMarker(mapView, point, title)
        mapView.controller.animateTo(point)
        mapView.controller.setZoom(17.0)
    }

    fun handleCurrentLocation(lat: Double, lon: Double) {
        val point = GeoPoint(lat, lon)
        if (automaticLocationRequest) {
            automaticCenter = point
            mapView.controller.setZoom(16.0)
            mapView.controller.setCenter(point)
            mapView.invalidate()
        } else {
            placeMarker(point, "Current location")
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
                onLocationFound = ::handleCurrentLocation,
                onUnavailable = { isLocating = false }
            )
        } else {
            isLocating = false
            Toast.makeText(
                context,
                "Location permission is needed to use your current location.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun requestCurrentLocation() {
        automaticLocationRequest = false
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
                onLocationFound = ::handleCurrentLocation,
                onUnavailable = { isLocating = false }
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

    LaunchedEffect(Unit) {
        if (initialLocation != null) {
            isLocating = false
            return@LaunchedEffect
        }
        automaticLocationRequest = true
        val hasFine = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            fetchCurrentLocation(
                fusedLocationClient = fusedLocationClient,
                context = context,
                onLocationFound = ::handleCurrentLocation,
                onUnavailable = { isLocating = false }
            )
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        kotlinx.coroutines.delay(8_000)
        isLocating = false
    }

    LaunchedEffect(automaticCenter) {
        if (automaticCenter != null) {
            kotlinx.coroutines.delay(800)
            isLocating = false
        }
    }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AndroidView(
            factory = {
                mapView.apply {
                    if (overlays.none { it is MapEventsOverlay }) {
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
                    }
                    markerPos?.let { updateMapMarker(this, it, markerName) }
                    invalidate()
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { it.invalidate() }
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
                placeholder = { Text(reportText("Search location...", "搜索地点……", "Cari lokasi...")) },
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
                Text(reportText("Use Current Location", "使用当前位置", "Gunakan Lokasi Semasa"), color = ReportGreen)
            }

            Surface(
                color = Color.White.copy(alpha = 0.92f),
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp
            ) {
                Text(
                    "Tap the exact place where the incident happened. You can also use your current GPS location.",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xFFF7FAF7))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.map_marker_report),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            reportText("Selected Incident Location", "已选择的事发地点", "Lokasi Insiden Dipilih"),
                            fontWeight = FontWeight.Bold,
                            color = ReportGreen,
                            fontSize = 12.sp
                        )
                        Text(
                            markerName.ifBlank { AppLanguage.text("Tap the map to choose a location", "点击地图选择地点") },
                            fontSize = 12.sp,
                            color = Color(0xFF333333),
                            maxLines = 2
                        )
                        markerPos?.let { point ->
                            Text(
                                String.format(
                                    Locale.getDefault(),
                                    "GPS: %.5f, %.5f",
                                    point.latitude,
                                    point.longitude
                                ),
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF666666))
                ) {
                    Text(reportText("Cancel", "取消", "Batal"))
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
                    Text(
                        text = reportText("Confirm Incident Location", "确认事件地点", "Sahkan Lokasi Insiden"),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        if (isLocating) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(color = ReportGreen)
                    Text(
                        "Finding your location…",
                        color = ReportGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 14.dp)
                    )
                }
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
            this.title = title.ifBlank { "Selected incident location" }
            icon = ContextCompat.getDrawable(mapView.context, R.drawable.map_marker_report)
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
    onLocationFound: (Double, Double) -> Unit,
    onUnavailable: () -> Unit = {}
) {
    val hasFine = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasFine && !hasCoarse) {
        Toast.makeText(
            context,
            "Location permission is not available. You can tap the map manually.",
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    fun showLocationUnavailable() {
        onUnavailable()
        Toast.makeText(
            context,
            "No GPS fix yet. In the emulator, set a location in Extended controls > Location, or tap the map manually.",
            Toast.LENGTH_LONG
        ).show()
    }

    fun tryLastLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { lastLocation ->
                    if (lastLocation != null) {
                        onLocationFound(lastLocation.latitude, lastLocation.longitude)
                    } else {
                        showLocationUnavailable()
                    }
                }
                .addOnFailureListener { showLocationUnavailable() }
                .addOnCanceledListener { showLocationUnavailable() }
        } catch (_: SecurityException) {
            showLocationUnavailable()
        } catch (_: Exception) {
            showLocationUnavailable()
        }
    }

    try {
        val cancellationToken = CancellationTokenSource()
        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
            .addOnSuccessListener { currentLocation ->
                if (currentLocation != null) {
                    onLocationFound(currentLocation.latitude, currentLocation.longitude)
                } else {
                    tryLastLocation()
                }
            }
            .addOnFailureListener { tryLastLocation() }
            .addOnCanceledListener { tryLastLocation() }
    } catch (_: SecurityException) {
        showLocationUnavailable()
    } catch (_: Exception) {
        showLocationUnavailable()
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
    isSubmitting: Boolean,
    onClearError: () -> Unit,
    onSubmit: (IncidentReport) -> Unit
) {
    val context = LocalContext.current
    var species by rememberSaveable(initialType) { mutableStateOf(ReportDraftStore.species(context)) }
    var count by rememberSaveable(initialType) { mutableStateOf(ReportDraftStore.count(context)) }
    var isAggressive by rememberSaveable(initialType) { mutableStateOf(ReportDraftStore.aggressive(context)) }
    var description by rememberSaveable(initialType) { mutableStateOf(ReportDraftStore.description(context)) }
    var severity by rememberSaveable(initialType) { mutableStateOf(ReportDraftStore.severity(context)) }
    var weather by rememberSaveable(initialType) { mutableStateOf(ReportDraftStore.weather(context)) }
    var imageUriStrings by rememberSaveable(initialType) {
        mutableStateOf(ReportDraftStore.images(context))
    }
    val imageUris = imageUriStrings.map(Uri::parse)
    var expandedWeather by rememberSaveable { mutableStateOf(false) }
    var selectedDateTimeMillis by rememberSaveable(initialType) { mutableStateOf(ReportDraftStore.dateTime(context)) }
    val selectedDateTime = Date(selectedDateTimeMillis)
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var lastUsedAction by rememberSaveable { mutableStateOf<String?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // ------------------------
    // report form-keep draft input
    // ------------------------
    val persistCurrentDraft by rememberUpdatedState(newValue = {
        ReportDraftStore.saveForm(
            context, species, count, isAggressive, description, severity,
            weather, selectedDateTimeMillis, initialLocationName, initialLocation
        )
    })
    LaunchedEffect(
        species, count, isAggressive, description, severity, weather,
        selectedDateTimeMillis, initialLocationName, initialLocation
    ) {
        persistCurrentDraft()
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) persistCurrentDraft()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            persistCurrentDraft()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val scrollState = rememberScrollState()

    if (showDatePicker) {
        LaunchedEffect(Unit) {
            val calendar = Calendar.getInstance().apply { time = selectedDateTime }
            android.app.DatePickerDialog(
                context,
                { _, year, month, day ->
                    selectedDateTimeMillis = Calendar.getInstance().apply {
                        time = selectedDateTime
                        set(year, month, day)
                    }.timeInMillis
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
                    selectedDateTimeMillis = Calendar.getInstance().apply {
                        time = selectedDateTime
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                    }.timeInMillis
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
            imageUriStrings = imageUriStrings + it.toString()
            ReportDraftStore.setImages(context, imageUriStrings)
            analyzeImageForAnimal(context, it) { label ->
                if (species.isBlank()) species = label
            }
        }
    }

    // Keep the pending file URI in saved state. Some camera applications
    // recreate WildGuard while the external camera activity is open.
    var pendingCameraUriString by rememberSaveable {
        mutableStateOf(ReportDraftStore.pendingCameraUri(context))
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { wasSaved: Boolean ->
        val savedUri = (pendingCameraUriString ?: ReportDraftStore.pendingCameraUri(context))
            ?.let(Uri::parse)
        if (!wasSaved || savedUri == null) {
            Toast.makeText(
                context,
                "The camera returned no photo. Check the emulator camera settings or use Upload Gallery.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (savedUri.toString() !in imageUriStrings) {
                imageUriStrings = imageUriStrings + savedUri.toString()
                ReportDraftStore.setImages(context, imageUriStrings)
            }
            analyzeImageForAnimal(context, savedUri) { label ->
                if (species.isBlank()) species = label
            }
        }
        // A few camera apps report false even after writing the file. Keep the
        // URI in that case so the recovery effect below can inspect it.
        if (wasSaved && savedUri != null) {
            pendingCameraUriString = null
            ReportDraftStore.setPendingCameraUri(context, null)
        }
    }

    // Some external camera apps recreate MainActivity and never redeliver the
    // ActivityResult callback to the newly composed launcher. Recover the file
    // directly from the URI saved before launching the camera.
    LaunchedEffect(pendingCameraUriString) {
        val uriString = pendingCameraUriString ?: return@LaunchedEffect
        val recoveredUri = Uri.parse(uriString)
        kotlinx.coroutines.delay(350)
        val containsPhoto = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(recoveredUri)?.use { stream ->
                    stream.read() != -1
                } == true
            }.getOrDefault(false)
        }

        if (containsPhoto && uriString !in imageUriStrings) {
            imageUriStrings = imageUriStrings + uriString
            ReportDraftStore.setImages(context, imageUriStrings)
            analyzeImageForAnimal(context, recoveredUri) { label ->
                if (species.isBlank()) species = label
            }
        }

        if (containsPhoto) {
            pendingCameraUriString = null
            ReportDraftStore.setPendingCameraUri(context, null)
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
                    localizedIncidentType(initialType),
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReportGreen
                )
                Text(
                    reportText("Step 2: incident details", "步骤 2：事件详情", "Langkah 2: butiran insiden"),
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
                title = reportText("Snap Photo", "拍照", "Ambil Foto"),
                icon = Icons.Default.CameraAlt,
                isSelected = lastUsedAction == "camera"
            ) {
                lastUsedAction = "camera"
                ReportDraftStore.saveForm(
                    context, species, count, isAggressive, description, severity,
                    weather, selectedDateTimeMillis, initialLocationName, initialLocation
                )
                runCatching {
                    val photoFile = File.createTempFile(
                        "incident_${System.currentTimeMillis()}_",
                        ".jpg",
                        File(context.cacheDir, "report_photos").apply { mkdirs() }
                    )
                    val photoUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        photoFile
                    )
                    pendingCameraUriString = photoUri.toString()
                    ReportDraftStore.setPendingCameraUri(context, photoUri.toString())
                    ReportDraftStore.setStepTwo(context, true)
                    ReportDraftStore.setType(context, initialType)
                    cameraLauncher.launch(photoUri)
                }
                    .onFailure {
                        pendingCameraUriString = null
                        ReportDraftStore.setPendingCameraUri(context, null)
                        Toast.makeText(
                            context,
                            "No camera application is available. Configure the emulator camera or use Upload Gallery.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }

            ReportActionCard(
                title = reportText("Upload Gallery", "从相册上传", "Muat Naik Galeri"),
                icon = Icons.Default.PhotoLibrary,
                isSelected = lastUsedAction == "gallery"
            ) {
                lastUsedAction = "gallery"
                ReportDraftStore.saveForm(
                    context, species, count, isAggressive, description, severity,
                    weather, selectedDateTimeMillis, initialLocationName, initialLocation
                )
                imagePickerLauncher.launch("image/*")
            }
        }

        // ------------------------
        // incident details-location coordinates
        // ------------------------
        val incidentCoordinates = initialLocation?.let { (latitude, longitude) ->
            String.format(Locale.US, "%.6f, %.6f", latitude, longitude)
        } ?: reportText("Tap to choose the exact incident location", "点击选择准确的事件地点", "Ketik untuk memilih lokasi insiden yang tepat")

        InfoCard(
            label = reportText("Incident Location", "事件地点", "Lokasi Insiden"),
            value = incidentCoordinates,
            icon = Icons.Default.LocationOn,
            onClick = onEditLocation
        )

        // ------------------------
        // incident details-phone date and time
        // ------------------------
        val formattedDateTime = remember(selectedDateTimeMillis, context) {
            val phoneDate = android.text.format.DateFormat.getMediumDateFormat(context)
                .format(selectedDateTime)
            val phoneTime = android.text.format.DateFormat.getTimeFormat(context)
                .format(selectedDateTime)
            "$phoneDate • $phoneTime"
        }

        InfoCard(
            label = reportText("Date & Time", "日期与时间", "Tarikh & Masa"),
            value = formattedDateTime,
            icon = Icons.Default.Event,
            onClick = { showDatePicker = true }
        )

        Text(
            reportText("Incident Severity", "事件严重程度", "Tahap Keterukan Insiden"),
            fontWeight = FontWeight.Bold,
            color = ReportGreen
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SeverityOption(reportText("Low", "低", "Rendah"), severity == Severity.LOW, Color.Gray) {
                severity = Severity.LOW
            }
            SeverityOption(reportText("Medium", "中等", "Sederhana"), severity == Severity.MEDIUM, Color(0xFFFFA000)) {
                severity = Severity.MEDIUM
            }
            SeverityOption(reportText("High", "高", "Tinggi"), severity == Severity.HIGH, Color.Red) {
                severity = Severity.HIGH
            }
            SeverityOption(reportText("Critical", "危急", "Kritikal"), severity == Severity.CRITICAL, Color(0xFFB71C1C)) {
                severity = Severity.CRITICAL
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    reportText("Animals Affected", "受影响动物数量", "Haiwan Terjejas"),
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
                    reportText("Weather", "天气", "Cuaca"),
                    fontWeight = FontWeight.Bold,
                    color = ReportGreen,
                    fontSize = 13.sp
                )
                ExposedDropdownMenuBox(
                    expanded = expandedWeather,
                    onExpandedChange = { expandedWeather = !expandedWeather }
                ) {
                    OutlinedTextField(
                        value = localizedWeather(weather),
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
                                text = { Text(localizedWeather(option)) },
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
                reportText("Is the animal aggressive?", "动物是否具有攻击性？", "Adakah haiwan itu agresif?"),
                fontWeight = FontWeight.Bold,
                color = ReportGreen
            )
            Switch(
                checked = isAggressive,
                onCheckedChange = { isAggressive = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFD32F2F),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFF9E9E9E)
                )
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(reportText("Detected / Observed Species", "检测／观察到的物种", "Spesies Dikesan / Diperhatikan"), color = ReportGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            OutlinedTextField(
                value = species,
                onValueChange = { species = it },
                placeholder = { Text(reportText("Enter or detect a species", "输入或检测物种", "Masukkan atau kesan spesies")) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
            Text(reportText("A selected photo may suggest a species automatically; you can edit it.", "所选照片可能会自动建议物种；您仍可编辑。", "Foto yang dipilih mungkin mencadangkan spesies secara automatik; anda boleh mengeditnya."), modifier = Modifier.padding(horizontal = 12.dp), fontSize = 11.sp, color = Color(0xFF555555))
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(reportText("Description", "说明", "Penerangan"), color = ReportGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    onClearError()
                },
                placeholder = { Text(reportText("Describe what happened...", "描述发生的情况……", "Terangkan apa yang berlaku...")) },
                modifier = Modifier.fillMaxWidth().height(130.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
        }

        if (imageUris.isNotEmpty()) {
            Text(
                reportText("Evidence Attached", "已附加证据", "Bukti Dilampirkan") + " (${imageUris.size})",
                fontWeight = FontWeight.Bold,
                color = ReportGreen
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                imageUris.forEach { uri ->
                    Box(Modifier.size(104.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Report evidence",
                            modifier = Modifier
                                .size(100.dp)
                                .align(Alignment.BottomStart)
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier.align(Alignment.TopEnd).offset(x = 2.dp, y = (-2).dp)
                                .size(20.dp).background(Color(0xFFD32F2F), CircleShape)
                                .clickable {
                                imageUriStrings = imageUriStrings - uri.toString()
                                ReportDraftStore.setImages(context, imageUriStrings)
                                if (uri.scheme == "content") {
                                    runCatching { context.contentResolver.delete(uri, null, null) }
                                }
                                lastUsedAction = null
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, "Remove attached photo", tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }
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
            colors = ButtonDefaults.buttonColors(containerColor = ReportGreen),
            enabled = !isSubmitting
        ) {
            Text(
                if (isSubmitting) reportText("Saving Report…", "正在保存举报…", "Menyimpan Laporan…")
                else reportText("Submit Report", "提交报告", "Hantar Laporan"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
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
    // Ignore very weak generic labels before applying the species rules below.
    val options = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.65f)
        .build()
    val labeler = ImageLabeling.getClient(options)
    labeler.process(image)
        .addOnSuccessListener { labels ->
            detectWildlifeName(labels)?.let(onResult)
        }
        .addOnCompleteListener {
            labeler.close()
        }
}

private fun detectWildlifeName(labels: List<com.google.mlkit.vision.label.ImageLabel>): String? {
    val wildlifeNames = linkedMapOf(
        "dog" to "Dog", "puppy" to "Dog", "canine" to "Dog",
        "cat" to "Cat", "kitten" to "Cat", "feline" to "Cat",
        "macaque" to "Monkey", "monkey" to "Monkey", "primate" to "Monkey", "ape" to "Monkey",
        "orangutan" to "Orangutan", "rhinoceros" to "Rhinoceros", "rhino" to "Rhinoceros",
        "hornbill" to "Hornbill", "eagle" to "Eagle", "owl" to "Owl", "parrot" to "Parrot",
        "bird" to "Bird", "elephant" to "Elephant", "tiger" to "Tiger", "leopard" to "Leopard",
        "deer" to "Deer", "wild boar" to "Wild Boar", "boar" to "Wild Boar", "bear" to "Bear",
        "snake" to "Snake", "crocodile" to "Crocodile", "alligator" to "Crocodile",
        "turtle" to "Turtle", "tortoise" to "Tortoise", "fox" to "Fox", "otter" to "Otter"
    )
    val candidates = labels.mapNotNull { label ->
        val raw = label.text.trim()
        val normalized = raw.lowercase()
        val species = wildlifeNames.entries
            .firstOrNull { (keyword, _) -> normalized == keyword }
            ?.value ?: return@mapNotNull null

        // Generic "Bird" is a frequent false positive from ML Kit. Only use it
        // at high confidence; named bird species and mammals use the normal limit.
        val minimumConfidence = if (species == "Bird") 0.85f else 0.68f
        if (label.confidence >= minimumConfidence) species to label.confidence else null
    }

    // Never fill the report with an arbitrary generic label. If no supported
    // species is confidently recognised, leave the editable field blank.
    return candidates.maxByOrNull { it.second }?.first
}
