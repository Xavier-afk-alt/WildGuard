package com.example.wildguard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeSection(

    onExploreClick: () -> Unit

) {

    Card(

        modifier = Modifier.fillMaxWidth()

    ) {

        Column(

            modifier = Modifier.padding(16.dp)

        ) {

            Text("Welcome Back")

            Text("Guardian Alex")

            Spacer(modifier = Modifier.height(8.dp))

            Button(

                onClick = onExploreClick

            ) {

                Text("Explore Now")

            }

        }

    }

}