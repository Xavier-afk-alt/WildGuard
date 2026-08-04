package com.example.assignment.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.assignment.data.animalList
import com.example.assignment.viewmodel.AnimalType

@Composable
fun AnimalSelector(

    selectedAnimal:AnimalType,

    onAnimalSelected:(AnimalType)->Unit

){

    LazyRow(

        horizontalArrangement = Arrangement.spacedBy(8.dp),

        modifier = Modifier.fillMaxWidth()

    ){

        items(animalList){

                animal->

            Card(

                colors = CardDefaults.cardColors(

                    if(animal.type==selectedAnimal)

                        Color(0xFFFFF176)

                    else

                        Color.White

                ),

                modifier = Modifier
                    .size(55.dp)
                    .clickable{

                        onAnimalSelected(animal.type)

                    }

            ){

                Image(

                    painter = painterResource(animal.icon),

                    contentDescription = animal.name,

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)

                )

            }

        }

    }

}