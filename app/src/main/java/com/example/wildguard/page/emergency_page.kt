package com.example.wildguard.page

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.CountDownTimer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.wildguard.R
import com.example.wildguard.components.EmergencyCard
import com.example.wildguard.navigation.Page
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.Locale

private val EmergencyRed = Color(0xFFFF5252)
private val EmergencyGreen = Color(0xFF2E7D32)
private const val PERHILITAN_HOTLINE = "1800885151"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyPage(navController: NavController) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val fusedLocationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var isSosActive by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(3) }
    var locationText by remember { mutableStateOf("Checking GPS permission…") }
    var countDownTimer by remember { mutableStateOf<CountDownTimer?>(null) }

    fun refreshLocation() {
        fetchEmergencyLocation(
            fusedLocationClient = fusedLocationClient,
            context = context,
            onStatus = { locationText = it }
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            refreshLocation()
        } else {
            locationText = "Location permission not granted. SOS calling still works."
        }
    }

    LaunchedEffect(Unit) {
        val fineGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            refreshLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            countDownTimer?.cancel()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Emergency Response") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(R.drawable.back),
                        contentDescription = "Back"
                    )
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF9F9F9))
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 92.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmergencyRed),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                "Need Immediate Assistance?",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Press and hold SOS for 3 seconds to open the wildlife emergency hotline.",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .size(164.dp)
                        .background(EmergencyRed, CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isSosActive = true
                                    countdown = 3
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)

                                    countDownTimer?.cancel()
                                    countDownTimer = object : CountDownTimer(3000L, 1000L) {
                                        override fun onTick(millisUntilFinished: Long) {
                                            countdown = ((millisUntilFinished + 999L) / 1000L)
                                                .toInt()
                                                .coerceAtLeast(1)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        }

                                        override fun onFinish() {
                                            countdown = 0
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            openEmergencyDialer(context)
                                        }
                                    }.start()

                                    tryAwaitRelease()
                                    countDownTimer?.cancel()
                                    countDownTimer = null
                                    isSosActive = false
                                    countdown = 3
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSosActive) countdown.toString() else "SOS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp
                    )
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    "Press & hold for 3 seconds",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = { openEmergencyDialer(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = EmergencyRed
                    ),
                    border = BorderStroke(1.dp, EmergencyRed),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Call PERHILITAN Hotline", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(28.dp))

                Text(
                    "Emergency Guide",
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.height(12.dp))

                EmergencyCard(
                    title = "Injured Wildlife",
                    description = "Animal requires medical assistance.",
                    icon = Icons.Default.Favorite,
                    color = Color(0xFFD32F2F),
                    onClick = { navController.navigate(Page.Report.route) }
                )

                Spacer(Modifier.height(12.dp))

                EmergencyCard(
                    title = "Forest Fire",
                    description = "Move to safety and report smoke or fire.",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color.Red,
                    onClick = { navController.navigate(Page.Report.route) }
                )

                Spacer(Modifier.height(12.dp))

                EmergencyCard(
                    title = "Illegal Logging",
                    description = "Protect forests and report illegal activities.",
                    icon = Icons.Default.Forest,
                    color = EmergencyGreen,
                    onClick = { navController.navigate(Page.Report.route) }
                )

                Spacer(Modifier.height(12.dp))

                EmergencyCard(
                    title = "Poaching",
                    description = "Report illegal hunting or wildlife trapping.",
                    icon = Icons.Default.Warning,
                    color = Color(0xFFFF9800),
                    onClick = { navController.navigate(Page.Report.route) }
                )

                Spacer(Modifier.height(24.dp))
            }

            Surface(
                color = Color(0xFFE8F5E9),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = EmergencyGreen
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Emergency GPS",
                            fontWeight = FontWeight.Bold,
                            color = EmergencyGreen,
                            fontSize = 12.sp
                        )
                        Text(
                            locationText,
                            fontSize = 10.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

private fun openEmergencyDialer(context: Context) {
    // ACTION_DIAL is deliberate: it works without CALL_PHONE permission and prevents an
    // accidental immediate phone call while still preparing the official hotline number.
    val intent = Intent(
        Intent.ACTION_DIAL,
        Uri.parse("tel:$PERHILITAN_HOTLINE")
    )
    runCatching { context.startActivity(intent) }
}

@SuppressLint("MissingPermission")
private fun fetchEmergencyLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onStatus: (String) -> Unit
) {
    val fineGranted = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarseGranted = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!fineGranted && !coarseGranted) {
        onStatus("Location permission is required for GPS coordinates.")
        return
    }

    onStatus("Getting current location…")
    val cancellationToken = CancellationTokenSource()

    fusedLocationClient
        .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
        .addOnSuccessListener { currentLocation ->
            if (currentLocation != null) {
                onStatus(
                    String.format(
                        Locale.getDefault(),
                        "GPS ready: %.5f, %.5f",
                        currentLocation.latitude,
                        currentLocation.longitude
                    )
                )
            } else {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { lastLocation ->
                        if (lastLocation != null) {
                            onStatus(
                                String.format(
                                    Locale.getDefault(),
                                    "GPS ready: %.5f, %.5f",
                                    lastLocation.latitude,
                                    lastLocation.longitude
                                )
                            )
                        } else {
                            onStatus("No GPS fix yet. Set an emulator location or enable device GPS.")
                        }
                    }
                    .addOnFailureListener {
                        onStatus("GPS unavailable. SOS calling still works.")
                    }
            }
        }
        .addOnFailureListener {
            onStatus("GPS unavailable. SOS calling still works.")
        }
}
