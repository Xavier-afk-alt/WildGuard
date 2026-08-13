package com.example.assignment.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment.components.animal.AnimalDisplay
import com.example.assignment.viewmodel.AnimalType
import com.example.assignment.viewmodel.RewardViewModel
import com.example.assignment.R
import com.example.assignment.components.scene.AnimalScene
import com.example.assignment.viewmodel.InventoryViewModel
import kotlinx.coroutines.delay
private const val PAT_HAND_ID = 1

@Composable
fun AnimalContent(
    rewardViewModel: RewardViewModel,
    inventoryViewModel: InventoryViewModel,
    modifier: Modifier = Modifier
) {
    var selectorExpanded by remember { mutableStateOf(false) }
    var showPatTool by remember { mutableStateOf(false) }

    LaunchedEffect(rewardViewModel.selectedAnimal) {
        rewardViewModel.recordAnimalDiscovered(
            rewardViewModel.selectedAnimal.name
        )
    }

    LaunchedEffect(showPatTool) {
        if (showPatTool) {
            delay(1200)
            showPatTool = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AnimalScene(
            animal = rewardViewModel.selectedAnimal,
            showPatTool = showPatTool,
            modifier = Modifier.fillMaxSize(),
            onShortPress = {
                rewardViewModel.showRandomAnimalMessage()
            },
            onLongPress = {
                if (inventoryViewModel.useItem(PAT_HAND_ID)) {
                    showPatTool = true
                    rewardViewModel.quickPatAnimal()
                } else {
                    rewardViewModel.showNoToolMessage("pat")
                }
            }
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimalSelector(
                selectedAnimal = rewardViewModel.selectedAnimal,

                expanded = selectorExpanded,

                onToggle = {
                    selectorExpanded = !selectorExpanded
                },

                onAnimalSelected = { animal ->

                    rewardViewModel.selectAnimal(animal)

                    // Automatically close after selection
                    selectorExpanded = false
                }
            )

            AnimatedVisibility(
                visible = rewardViewModel.showInteraction,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                InteractionBubble(
                    text = rewardViewModel.interactionMessage
                )
            }
        }

        Text(
            text = rewardViewModel.selectedAnimal.name,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )
    }
}
