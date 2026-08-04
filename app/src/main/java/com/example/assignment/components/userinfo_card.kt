package com.example.assignment.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserInfoCard(){

    Card(

        modifier=Modifier
            .fillMaxWidth()
            .padding(12.dp)

    ){

        Column(

            modifier=Modifier.padding(16.dp)

        ){

            Text("Username")

            Text("Guardian Alex")

            Spacer(Modifier.height(8.dp))

            Text("Email")

            Text("example@gmail.com")

        }

    }

}