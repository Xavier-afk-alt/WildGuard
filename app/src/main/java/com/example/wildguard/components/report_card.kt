package com.example.wildguard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ReportTypeCard(

    title:String,

    icon: ImageVector,

    onClick:()->Unit

){

    Card(

        modifier=Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable{

                onClick()

            }

    ){

        Column(

            modifier= Modifier.fillMaxSize(),

            horizontalAlignment=Alignment.CenterHorizontally,

            verticalArrangement= Arrangement.Center

        ){

            Icon(

                icon,

                null,

                modifier=Modifier.size(55.dp),

                tint=Color(0xFF2E7D32)

            )

            Spacer(
                Modifier.height(15.dp)
            )

            Text(title)

        }

    }

}