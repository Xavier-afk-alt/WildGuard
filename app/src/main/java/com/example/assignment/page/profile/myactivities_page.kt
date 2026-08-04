package com.example.assignment.page.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.assignment.components.PageTopBar

@Composable
fun MyActivitiesPage(

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