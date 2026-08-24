package com.example.wildguard.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wildguard.components.AlertSection
import com.example.wildguard.components.HomeTopBar
import com.example.wildguard.components.NewsSection
import com.example.wildguard.components.QuickAccessSection
import com.example.wildguard.components.WelcomeSection
import androidx.navigation.NavController

@Composable
fun HomePage(

    navController: NavController

) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())

    ) {

        HomeTopBar()

        Spacer(modifier = Modifier.height(16.dp))

        WelcomeSection(

            onExploreClick = {

                navController.navigate("explore")

            }

        )

        Spacer(modifier = Modifier.height(16.dp))

        AlertSection(

            onViewDetail = {

                //later

            }

        )

        Spacer(modifier = Modifier.height(16.dp))

        QuickAccessSection(

            navController = navController

        )

        Spacer(modifier = Modifier.height(16.dp))

        NewsSection()

    }

}