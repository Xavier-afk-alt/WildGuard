package com.example.wildguard.components

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun QuickAccessSection(

    navController: NavController

) {

    val quickList = listOf(

        "Wildlife Map",

        "Encyclopedia",

        "Safety Guide"

    )

    LazyRow {

        items(quickList) {

            QuickAccessCard(

                title = it,

                navController = navController

            )

        }

    }

}