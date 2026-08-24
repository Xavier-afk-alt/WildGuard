package com.example.wildguard.page.explore

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.wildguard.components.PageTopBar

@Composable
fun SafetyGuidePage(navController: NavController) {

    Column(

        modifier = Modifier.fillMaxSize()

    ) {
        PageTopBar(

            title = "Wildlife Encyclopedia",

            onBackClick = {

                navController.popBackStack()

            }

        )

        Text("Safety Guide")

    }

}