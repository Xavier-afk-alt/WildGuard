package com.example.wildguard.page.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.wildguard.components.PageTopBar

@Composable
fun SettingsPage(

    navController: NavController

){

    Column{

        PageTopBar(

            title="My Activities",

            onBackClick={

                navController.popBackStack()

            }

        )

        Text("Activities Page")

    }

}