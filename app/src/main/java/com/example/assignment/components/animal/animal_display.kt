package com.example.assignment.components.animal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun AnimalDisplay(

    image: Int,

    animalName: String,

    description: String

) {

    Column(

        modifier = Modifier.fillMaxWidth(),

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Image(

            painter = painterResource(image),

            contentDescription = animalName,

            modifier = Modifier.size(220.dp)

        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = animalName
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = description
        )

    }

}