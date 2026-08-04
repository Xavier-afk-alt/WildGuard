package com.example.assignment.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileMenuCard(

    title:String,

    description:String,

    onClick:()->Unit

){

    Card(

        modifier=Modifier
            .fillMaxWidth()
            .padding(vertical=6.dp)
            .clickable{

                onClick()

            }

    ){

        Column(

            modifier=Modifier.padding(16.dp)

        ){

            Text(title)

            Spacer(Modifier.height(4.dp))

            Text(description)

        }

    }

}