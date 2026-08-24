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

private const val WATERING_TOOL_ID = 2

@Composable
fun PlantContent(
    currentPoint: Int,
    rewardViewModel: RewardViewModel,
    inventoryViewModel: InventoryViewModel,
    modifier: Modifier = Modifier
) {
    var showWateringTool by remember { mutableStateOf(false) }

    val plantImage = when {
        rewardViewModel.currentPoint >= 7500 ->
            R.drawable.tree_finalphase

        rewardViewModel.currentPoint >= 5000 ->
            R.drawable.tree_thirdphase

        rewardViewModel.currentPoint >= 1000 ->
            R.drawable.tree_secondphase

        rewardViewModel.currentPoint >= 500 ->
            R.drawable.tree_firstphase

        rewardViewModel.currentPoint >= 250 ->
            R.drawable.seed_thirdphase

        rewardViewModel.currentPoint >= 50 ->
            R.drawable.seed_secondphase

        else ->
            R.drawable.seed_firstphase
    }

    val plantSize = when {
        rewardViewModel.currentPoint >= 7500 ->
            270.dp

        rewardViewModel.currentPoint >= 5000 ->
            230.dp

        rewardViewModel.currentPoint >= 1000 ->
            200.dp

        rewardViewModel.currentPoint >= 500 ->
            170.dp

        rewardViewModel.currentPoint >= 250 ->
            160.dp

        rewardViewModel.currentPoint >= 50 ->
            150.dp

        else ->
            130.dp
    }

    val plantOffsetY = when {
        rewardViewModel.currentPoint >= 7500 ->
            5.dp

        rewardViewModel.currentPoint >= 5000 ->
            10.dp

        rewardViewModel.currentPoint >= 1000 ->
            15.dp

        rewardViewModel.currentPoint >= 500 ->
            20.dp

        rewardViewModel.currentPoint >= 250 ->
            25.dp

        rewardViewModel.currentPoint >= 50 ->
            30.dp

        else ->
            35.dp
    }

    LaunchedEffect(showWateringTool) {
        if (showWateringTool) {
            delay(1200)
            showWateringTool = false
        }
    }

    PlantScene(
        plantImage = plantImage,
        plantSize = plantSize,
        plantOffsetY = plantOffsetY,
        showWateringTool = showWateringTool,
        modifier = modifier,
        onShortPress = {
            rewardViewModel.showRandomPlantMessage()
        },
        onLongPress = {
            if (inventoryViewModel.useItem(WATERING_TOOL_ID)) {
                showWateringTool = true
                rewardViewModel.quickWaterPlant()
            } else {
                rewardViewModel.showNoToolMessage("watering")
            }
        }
    )
}
