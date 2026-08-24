package com.example.wildguard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppLoadingScreen(
    message: String = "Loading WildGuard..."
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F3D6)),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        CircularProgressIndicator()

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier
                .height(20.dp)
        )

        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier
                .height(6.dp)
        )

        Text(
            text = "Please wait...",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}