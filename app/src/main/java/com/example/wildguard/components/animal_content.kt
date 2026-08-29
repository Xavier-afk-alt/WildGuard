package com.example.wildguard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.wildguard.components.scene.AnimalScene
import com.example.wildguard.viewmodel.InventoryViewModel
import com.example.wildguard.viewmodel.RewardViewModel
import kotlinx.coroutines.delay


private const val PAT_HAND_ID =
    1


@Composable
fun AnimalContent(

    rewardViewModel: RewardViewModel,

    inventoryViewModel: InventoryViewModel,

    sceneQuickUseEnabled: Boolean,

    // Tell RewardPage whether the selector is currently open.
    onSelectorExpandedChanged: (Boolean) -> Unit = {},

    modifier: Modifier = Modifier

) {

    // ============================================================
    // ANIMAL SELECTOR
    // ============================================================

    var selectorExpanded by remember {
        mutableStateOf(false)
    }


    // ============================================================
    // PAT HAND VISUAL
    // ============================================================

    var showPatTool by remember {
        mutableStateOf(false)
    }


    // ============================================================
    // KEEP LATEST CALLBACK
    // ============================================================

    val currentOnSelectorExpandedChanged by
    rememberUpdatedState(
        onSelectorExpandedChanged
    )


    // ============================================================
    // INFORM REWARD PAGE WHEN SELECTOR OPENS / CLOSES
    //
    // This makes sure:
    //
    // "☝ Hold scene"
    //
    // does not appear while animal selector is expanded.
    // ============================================================

    LaunchedEffect(
        selectorExpanded
    ) {

        currentOnSelectorExpandedChanged(
            selectorExpanded
        )
    }


    // ============================================================
    // ANIMAL SELECTOR TRANSPARENCY
    // ============================================================

    val selectorAlpha by
    animateFloatAsState(

        targetValue =
            if (selectorExpanded) {

                1f

            } else {

                0.80f
            },

        label =
            "selectorTransparency"
    )


    // ============================================================
    // RECORD DISCOVERED ANIMAL
    // ============================================================

    LaunchedEffect(
        rewardViewModel.selectedAnimal
    ) {

        rewardViewModel
            .recordAnimalDiscovered(
                rewardViewModel
                    .selectedAnimal
                    .name
            )
    }


    // ============================================================
    // PAT HAND EFFECT TIMER
    // ============================================================

    LaunchedEffect(
        showPatTool
    ) {

        if (showPatTool) {

            delay(
                1200
            )


            showPatTool =
                false
        }
    }


    // ============================================================
    // ANIMAL QUICK-USE PERMISSION
    //
    // RewardPage checks:
    //
    // - Inventory closed
    // - Item dialog closed
    //
    // AnimalContent additionally checks:
    //
    // - Animal selector closed
    // ============================================================

    val animalQuickUseEnabled =

        sceneQuickUseEnabled &&
                !selectorExpanded


    Box(

        modifier =
            modifier.fillMaxSize()

    ) {

        // ========================================================
        // ANIMAL SCENE
        // ========================================================

        AnimalScene(

            animal =
                rewardViewModel
                    .selectedAnimal,


            quickUseEnabled =
                animalQuickUseEnabled,


            showPatTool =
                showPatTool,


            showReaction =
                rewardViewModel
                    .showReaction,


            reactionType =
                rewardViewModel
                    .currentReaction,


            reactionEventId =
                rewardViewModel
                    .reactionEventId,


            modifier =
                Modifier.fillMaxSize(),


            // ====================================================
            // NORMAL SHORT TAP
            // ====================================================

            onShortPress = {

                rewardViewModel
                    .showRandomAnimalMessage()
            },


            // ====================================================
            // LONG PRESS QUICK USE
            // ====================================================

            onLongPress = {

                val consumed =
                    inventoryViewModel
                        .useItem(
                            PAT_HAND_ID
                        )


                if (consumed) {

                    // Show Pat Pat Hand visual.
                    showPatTool =
                        true


                    // Perform animal quick interaction.
                    rewardViewModel
                        .quickPatAnimal()


                    true

                } else {

                    // No Pat Pat Hand available.
                    rewardViewModel
                        .showNoToolMessage(
                            "pat"
                        )


                    false
                }
            }
        )


        // ========================================================
        // ANIMAL SELECTOR
        // ========================================================

        Column(

            modifier = Modifier
                .align(
                    Alignment.TopCenter
                )
                .padding(
                    top = 8.dp
                )
                .graphicsLayer {

                    alpha =
                        selectorAlpha
                },

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            AnimalSelector(

                selectedAnimal =
                    rewardViewModel
                        .selectedAnimal,

                expanded =
                    selectorExpanded,


                // =================================================
                // OPEN / CLOSE SELECTOR
                // =================================================

                onToggle = {

                    selectorExpanded =
                        !selectorExpanded
                },


                // =================================================
                // SELECT ANIMAL
                // =================================================

                onAnimalSelected = { animal ->

                    rewardViewModel
                        .selectAnimal(
                            animal
                        )


                    // Close selector after choosing.
                    selectorExpanded =
                        false
                }
            )
        }
    }
}