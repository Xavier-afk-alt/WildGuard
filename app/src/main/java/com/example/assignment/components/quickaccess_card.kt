package com.example.assignment.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun QuickAccessCard(

    title: String,

    navController: NavController

) {

    Card(

        modifier = Modifier
            .padding(8.dp)
            .size(140.dp)
            .clickable {

                when (title) {

                    "Wildlife Map" ->
                        navController.navigate("wildlife_map")

                    "Encyclopedia" ->
                        navController.navigate("encyclopedia")

                    "Safety Guide" ->
                        navController.navigate("safety_guide")

                }

            }

    ) {

        Box(

            modifier = Modifier.fillMaxSize()

        ) {

            Text(title)

        }

    }

}