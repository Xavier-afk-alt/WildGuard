package com.example.wildguard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeTopBar() {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),

        horizontalArrangement = Arrangement.SpaceBetween

    ) {

        Text("LOGO")

        Row {

            Button(
                onClick = {}
            ) {

                Text("Notification")

            }

            Spacer(Modifier.width(8.dp))

            Button(
                onClick = {}
            ) {

                Text("Profile")

            }

        }

    }

}