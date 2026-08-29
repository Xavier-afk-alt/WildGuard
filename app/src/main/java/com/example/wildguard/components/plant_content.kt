package com.example.wildguard.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wildguard.R
import com.example.wildguard.components.scene.PlantScene
import com.example.wildguard.viewmodel.InventoryViewModel
import com.example.wildguard.viewmodel.RewardViewModel
import kotlinx.coroutines.delay


private const val WATERING_TOOL_ID =
    2


@Composable
fun PlantContent(

    rewardViewModel: RewardViewModel,

    inventoryViewModel: InventoryViewModel,

    sceneQuickUseEnabled: Boolean,

    modifier: Modifier = Modifier

) {

    var showWateringTool by remember {
        mutableStateOf(false)
    }


    // Permanent plant progress.
    val growthPoint =
        rewardViewModel
            .plantGrowthPoint


    val plantImage =
        when {

            growthPoint >= 7500 ->
                R.drawable.tree_finalphase

            growthPoint >= 5000 ->
                R.drawable.tree_thirdphase

            growthPoint >= 1000 ->
                R.drawable.tree_secondphase

            growthPoint >= 500 ->
                R.drawable.tree_firstphase

            growthPoint >= 250 ->
                R.drawable.seed_thirdphase

            growthPoint >= 50 ->
                R.drawable.seed_secondphase

            else ->
                R.drawable.seed_firstphase
        }


    val plantSize =
        when {

            growthPoint >= 7500 ->
                270.dp

            growthPoint >= 5000 ->
                230.dp

            growthPoint >= 1000 ->
                200.dp

            growthPoint >= 500 ->
                170.dp

            growthPoint >= 250 ->
                160.dp

            growthPoint >= 50 ->
                150.dp

            else ->
                130.dp
        }


    val plantOffsetY =
        when {

            growthPoint >= 7500 ->
                5.dp

            growthPoint >= 5000 ->
                10.dp

            growthPoint >= 1000 ->
                15.dp

            growthPoint >= 500 ->
                20.dp

            growthPoint >= 250 ->
                25.dp

            growthPoint >= 50 ->
                30.dp

            else ->
                35.dp
        }


    val reactionSize =
        when {

            growthPoint >= 5000 ->
                64.dp

            growthPoint >= 500 ->
                60.dp

            else ->
                54.dp
        }


    LaunchedEffect(
        showWateringTool
    ) {

        if (showWateringTool) {

            delay(1200)

            showWateringTool =
                false
        }
    }


    PlantScene(

        plantImage =
            plantImage,


        plantSize =
            plantSize,


        plantOffsetY =
            plantOffsetY,


        quickUseEnabled =
            sceneQuickUseEnabled,


        showWateringTool =
            showWateringTool,


        showReaction =
            rewardViewModel
                .showReaction,


        reactionType =
            rewardViewModel
                .currentReaction,


        reactionEventId =
            rewardViewModel
                .reactionEventId,


        reactionSize =
            reactionSize,


        modifier =
            modifier,


        onShortPress = {

            rewardViewModel
                .showRandomPlantMessage()
        },


        onLongPress = {


            val consumed =
                inventoryViewModel
                    .useItem(
                        WATERING_TOOL_ID
                    )


            if (consumed) {


                showWateringTool =
                    true


                rewardViewModel
                    .quickWaterPlant()


                true

            } else {


                rewardViewModel
                    .showNoToolMessage(
                        "watering"
                    )


                false
            }
        }
    )
}