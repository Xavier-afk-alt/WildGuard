package com.example.assignment.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment.viewmodel.AnimalType
import com.example.assignment.viewmodel.RewardViewModel

@Composable
fun AnimalContent(

    rewardViewModel: RewardViewModel

){

    Column(

        horizontalAlignment= Alignment.CenterHorizontally

    ){

        if (rewardViewModel.showInteraction) {

            InteractionBubble(
                text = rewardViewModel.interactionMessage
            )

        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        AnimalSelector(

            selectedAnimal=
                rewardViewModel.selectedAnimal

        ){ animal->

            rewardViewModel.selectAnimal(animal)

        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = rewardViewModel.selectedAnimal.name,
            modifier = Modifier.padding(20.dp)
        )

        //temporary
        LaunchedEffect(rewardViewModel.selectedAnimal) {

            rewardViewModel.updateInteraction(
                "Hello ${rewardViewModel.selectedAnimal.name}!"
            )

        }

        /*when(rewardViewModel.selectedAnimal){

            AnimalType.FOX->{

                FoxPage()

            }

            AnimalType.TIGER->{

                TigerPage()

            }

            AnimalType.PANDA->{

                PandaPage()

            }

            AnimalType.ELEPHANT->{

                ElephantPage()

            }

        }*/

    }

}