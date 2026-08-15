package com.example.assignment.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.assignment.components.InventoryQuickAccess
import androidx.navigation.NavController
import com.example.assignment.components.*
import com.example.assignment.data.InventoryItem
import com.example.assignment.viewmodel.InventoryViewModel
import com.example.assignment.viewmodel.RewardType
import com.example.assignment.viewmodel.RewardViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun RewardPage(
    navController: NavController,
    rewardViewModel: RewardViewModel,
    inventoryViewModel: InventoryViewModel
) {
    val firstPendingEvent = rewardViewModel.pendingPointEvents.firstOrNull()

    var visibleEventId by remember {
        mutableStateOf<String?>(null)
    }

    var showInventory by remember {
        mutableStateOf(false)
    }

    var selectedInventoryItem by remember {
        mutableStateOf<InventoryItem?>(null)
    }

    // Display queued point rewards one by one whenever RewardPage is visible.
    LaunchedEffect(firstPendingEvent?.id) {
        if (firstPendingEvent == null) {
            visibleEventId = null
            return@LaunchedEffect
        }

        visibleEventId = firstPendingEvent.id

        delay(1800.milliseconds)
        visibleEventId = null

        delay(350.milliseconds)
        rewardViewModel.removePointEvent(firstPendingEvent.id)
    }

    LaunchedEffect(
        rewardViewModel.showInteraction,
        rewardViewModel.interactionMessage
    ) {
        if (rewardViewModel.showInteraction) {

            delay(3000.milliseconds)

            rewardViewModel.hideInteraction()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F3D6))
    ) {

        // ==========================================
        // MAIN REWARD CONTENT
        // ==========================================

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            RewardHeader()

            AchievementBanner(
                earnedPoints = rewardViewModel.totalEarnedPoints,
                maxPoint = rewardViewModel.maxPoint,
                status = rewardViewModel.status,
                borderColor = rewardViewModel.statusColor,
                onClick = {
                    navController.navigate("achievements")
                }
            )

            RedeemButton(

                coin =
                    rewardViewModel.currentPoint

            ) {

                navController.navigate("redeem")

            }

            when (rewardViewModel.rewardType) {

                RewardType.PLANT -> {
                    PlantContent(
                        currentPoint = rewardViewModel.currentPoint,
                        rewardViewModel = rewardViewModel,
                        inventoryViewModel = inventoryViewModel,
                        modifier = Modifier.weight(1f)
                    )
                }

                RewardType.ANIMAL -> {
                    AnimalContent(
                        rewardViewModel = rewardViewModel,
                        inventoryViewModel = inventoryViewModel,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }


        // ==========================================
        // INVENTORY QUICK ACCESS
        // ==========================================

        InventoryQuickAccess(
            inventoryViewModel = inventoryViewModel,

            expanded = showInventory,

            onToggle = {
                showInventory = !showInventory
            },

            onItemClick = { item ->
                selectedInventoryItem = item
            },

            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 16.dp,
                    bottom = 16.dp
                )
        )

        selectedInventoryItem?.let { item ->

            InventoryItemDialog(
                item = item,

                onDismiss = {
                    selectedInventoryItem = null
                },

                onQuickAccess = {

                    when (item.rewardId) {

                        1 -> {

                            if (rewardViewModel.quickPatAnimal()) {

                                inventoryViewModel.useItem(
                                    item.rewardId
                                )

                                selectedInventoryItem = null
                            }
                        }

                        2 -> {

                            if (rewardViewModel.quickWaterPlant()) {

                                inventoryViewModel.useItem(
                                    item.rewardId
                                )

                                selectedInventoryItem = null
                            }
                        }
                    }
                }
            )
        }

        // ==========================================
        // ANIMAL / PLANT SWITCH
        // ==========================================

        RewardToggleButton(

            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),

            currentType =
                rewardViewModel.rewardType

        ) {

            rewardViewModel.switchRewardType()

        }

        PointEarningPopup(
            event = firstPendingEvent,
            visible = visibleEventId == firstPendingEvent?.id,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 105.dp)
        )

    }

    if (
        rewardViewModel.showInteraction &&
        rewardViewModel.interactionMessage.isNotEmpty()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            InteractionBubble(
                text = rewardViewModel.interactionMessage,
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = 4.dp
                    )
                    .offset(y = 195.dp)
            )
        }
    }

}

/*
fun getQuickAccessAction(
    item: InventoryItem
): (() -> Unit)? {

    return when (item.rewardId) {

        // Seed
        1 -> {
            {
                // Plant action
            }
        }

        // Water
        2 -> {
            {
                // Water action
            }
        }

        // Animal food
        3 -> {
            {
                // Feed action
            }
        }

        // Medicine
        4 -> {
            {
                // Treat action
            }
        }

        else -> null
    }
}*/