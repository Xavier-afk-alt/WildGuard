package com.example.assignment.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressCard(

    currentPoint:Int,

    maxPoint:Int,

    status:String

){

    Card(

        modifier=Modifier
            .padding(horizontal=14.dp)
            .fillMaxWidth(),

        shape=RoundedCornerShape(30.dp),

        colors=CardDefaults.cardColors(
            containerColor=Color.White
        )

    ){

        Row(

            modifier=Modifier.padding(14.dp),

            verticalAlignment=Alignment.CenterVertically

        ){

            Icon(

                Icons.Default.EmojiEvents,

                null,

                tint=Color(0xFFFFB300),

                modifier=Modifier.size(45.dp)

            )

            Spacer(modifier=Modifier.width(14.dp))

            Column {

                Text(

                    "$currentPoint/$maxPoint",

                    style=MaterialTheme.typography.headlineSmall,

                    color=Color(0xFF1B8D2F)

                )

                Text(

                    "Current status: $status"

                )

            }

        }

        LinearProgressIndicator(

            progress={

                currentPoint.toFloat()/maxPoint

            },

            modifier=Modifier
                .fillMaxWidth()
                .padding(12.dp)

        )

    }

}