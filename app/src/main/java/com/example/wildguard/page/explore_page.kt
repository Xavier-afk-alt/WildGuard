package com.example.wildguard.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wildguard.components.ExploreCard
import com.example.wildguard.navigation.Page

@Composable
fun ExplorePage(

    navController: NavController

) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)

    ) {

        Text("Explore")

        Spacer(modifier = Modifier.height(20.dp))

        ExploreCard(

            title = "Wildlife Map",

            description = "Track wildlife locations"

        ) {

            navController.navigate(Page.WildlifeMap.route)

        }

        ExploreCard(

            title = "Wildlife Encyclopedia",

            description = "Browse wildlife information"

        ) {

            navController.navigate(Page.Encyclopedia.route)

        }

        ExploreCard(

            title = "Safety Guide",

            description = "Emergency instructions"

        ) {

            navController.navigate(Page.SafetyGuide.route)

        }

    }

}