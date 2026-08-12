package com.example.assignment.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assignment.components.InventoryQuickAccess
import com.example.assignment.components.InventoryButton
import androidx.navigation.NavController
import com.example.assignment.components.*
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

    var showInventoryQuickAccess by remember {
        mutableStateOf(false)
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

            Spacer(
                modifier = Modifier.weight(1f)
            )

            when (rewardViewModel.rewardType) {

                RewardType.PLANT -> {

                    PlantContent(
                        rewardViewModel.currentPoint
                    )

                }

                RewardType.ANIMAL -> {

                    AnimalContent(
                        rewardViewModel
                    )

                }

            }

        }


        // ==========================================
        // INVENTORY QUICK ACCESS
        // ==========================================

        InventoryQuickAccess(

            expanded = showInventoryQuickAccess,

            onToggle = {
                showInventoryQuickAccess =
                    !showInventoryQuickAccess
            },

            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 16.dp,
                    bottom = 16.dp
                )
        )



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

}