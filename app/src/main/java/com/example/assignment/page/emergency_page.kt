package com.example.assignment.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignment.components.EmergencyCard
import com.example.assignment.components.SOSButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyPage(navController: NavController) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        TopAppBar(
            title = {
                Text("Emergency Response")
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            item {

                Text(
                    "Emergency Assistance",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Choose the emergency situation below for immediate guidance."
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            item {

                EmergencyCard(
                    title = "Forest Fire",
                    description = "Learn immediate response steps.",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color.Red
                )
            }

            item {

                Spacer(modifier = Modifier.height(16.dp))

                EmergencyCard(
                    title = "Illegal Logging",
                    description = "Protect forests and report illegal activities.",
                    icon = Icons.Default.Forest,
                    color = Color(0xFF2E7D32)
                )

            }

            item {

                Spacer(modifier = Modifier.height(16.dp))

                EmergencyCard(
                    title = "Poaching",
                    description = "Help protect endangered wildlife.",
                    icon = Icons.Default.Warning,
                    color = Color(0xFFFF9800)
                )

            }

            item {

                Spacer(modifier = Modifier.height(40.dp))

                SOSButton()

                Spacer(modifier = Modifier.height(30.dp))

            }

        }

    }

}