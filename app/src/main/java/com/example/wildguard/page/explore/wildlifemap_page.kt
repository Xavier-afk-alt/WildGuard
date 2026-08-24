package com.example.wildguard.page.explore

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wildguard.components.PageTopBar

@Composable
fun WildlifeMapPage(

    navController: NavController

) {

    Column(

        modifier = Modifier.fillMaxSize()

    ) {

        PageTopBar(

            title = "Wildlife Map",

            onBackClick = {

                navController.popBackStack()

            }

        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Google Map Placeholder")

    }

}