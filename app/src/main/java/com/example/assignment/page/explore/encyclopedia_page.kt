package com.example.assignment.page.explore

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.assignment.components.PageTopBar

@Composable
fun EncyclopediaPage(navController: NavController) {

    Column(

        modifier = Modifier.fillMaxSize()

    ) {
        PageTopBar(

            title = "Wildlife Encyclopedia",

            onBackClick = {

                navController.popBackStack()

            }

        )

        Text("Wildlife Encyclopedia")

    }

}