package com.example.assignment.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment.components.animal.AnimalDisplay
import com.example.assignment.viewmodel.AnimalType
import com.example.assignment.viewmodel.RewardViewModel
import com.example.assignment.R
@Composable
fun AnimalContent(

    rewardViewModel: RewardViewModel

) {
    Box(

        modifier = Modifier.fillMaxSize()

    ) {

        Column(

            modifier = Modifier.fillMaxSize(),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            if (rewardViewModel.showInteraction) {

                InteractionBubble(
                    text = rewardViewModel.interactionMessage
                )

            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            AnimalSelector(

                selectedAnimal =
                    rewardViewModel.selectedAnimal

            ) { animal ->

                rewardViewModel.selectAnimal(animal)

            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = rewardViewModel.selectedAnimal.name,
                modifier = Modifier.padding(20.dp)
            )

            LaunchedEffect(rewardViewModel.selectedAnimal) {

                rewardViewModel.unlockInteraction(

                    "Hello ${rewardViewModel.selectedAnimal.name}!"

                )

                rewardViewModel.recordAnimalDiscovered(
                    rewardViewModel.selectedAnimal.name
                )

            }

            when (rewardViewModel.selectedAnimal) {

                AnimalType.TIGER -> {

                    AnimalDisplay(

                        image = R.drawable.tiger_animal,

                        animalName = "Tiger",

                        description = "Sleeping peacefully."

                    )

                }

                AnimalType.FOX -> {

                    AnimalDisplay(

                        image = R.drawable.fox_animal,

                        animalName = "Fox",

                        description = "Sleeping peacefully."

                    )

                }

                AnimalType.BEAR -> {

                    AnimalDisplay(

                        image = R.drawable.bear_animal,

                        animalName = "Bear",

                        description = "Sleeping peacefully."

                    )

                }

                AnimalType.PANDA -> {

                    AnimalDisplay(

                        image = R.drawable.panda_animal,

                        animalName = "Panda",

                        description = "Sleeping peacefully."

                    )

                }

                AnimalType.KOALA -> {

                    AnimalDisplay(

                        image = R.drawable.koala_animal,

                        animalName = "Koala",

                        description = "Sleeping peacefully."

                    )

                }

                AnimalType.LION -> {

                    AnimalDisplay(

                        image = R.drawable.lion_animal,

                        animalName = "Lion",

                        description = "Sleeping peacefully."

                    )

                }

                AnimalType.CAT -> {

                    AnimalDisplay(

                        image = R.drawable.cat_animal,

                        animalName = "Cat",

                        description = "Sleeping peacefully."

                    )

                }

                AnimalType.WOLF -> {

                    AnimalDisplay(

                        image = R.drawable.wolf_animal,

                        animalName = "Wolf",

                        description = "Sleeping peacefully."

                    )

                }

                AnimalType.DOG -> {

                    AnimalDisplay(

                        image = R.drawable.dog_animal,

                        animalName = "Dog",

                        description = "Sleeping peacefully."

                    )
                }

            }

        }

        /*
        HiddenInteractionButton(

            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),

            visible = rewardViewModel.showInteractionButton

        ) {

            rewardViewModel.nextInteraction()

        }*/
    }
}