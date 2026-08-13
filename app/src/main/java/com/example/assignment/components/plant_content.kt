package com.example.assignment.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.assignment.R
import com.example.assignment.components.scene.PlantScene
import com.example.assignment.viewmodel.InventoryViewModel
import com.example.assignment.viewmodel.RewardViewModel
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
        rewardViewModel.currentPoint >= 5000 ->
            R.drawable.tree_finalphase

        rewardViewModel.currentPoint >= 3000 ->
            R.drawable.back//big_tree

        rewardViewModel.currentPoint >= 2000 ->
            R.drawable.back//medium_tree

        rewardViewModel.currentPoint >= 1000 ->
            R.drawable.back//small_tree

        else ->
            R.drawable.back//seed
    }

    val plantSize = when {
        rewardViewModel.currentPoint >= 5000 ->
            270.dp

        rewardViewModel.currentPoint >= 3000 ->
            230.dp

        rewardViewModel.currentPoint >= 2000 ->
            200.dp

        rewardViewModel.currentPoint >= 1000 ->
            170.dp

        else ->
            130.dp
    }

    val plantOffsetY = when {
        rewardViewModel.currentPoint >= 5000 ->
            0.dp

        rewardViewModel.currentPoint >= 3000 ->
            10.dp

        rewardViewModel.currentPoint >= 2000 ->
            20.dp

        rewardViewModel.currentPoint >= 1000 ->
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
