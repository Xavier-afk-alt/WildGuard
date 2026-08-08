package com.example.assignment.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.assignment.components.RedeemHeader
import com.example.assignment.components.RewardCard
import com.example.assignment.viewmodel.RewardViewModel
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.assignment.components.PurchaseConfirmationDialog
import com.example.assignment.data.RewardItem

@Composable
fun RedeemShopPage(

    navController: NavController,

    rewardViewModel: RewardViewModel

) {

    var selectedReward by remember {
        mutableStateOf<RewardItem?>(null)
    }

    Column {

        RedeemHeader(

            coin = rewardViewModel.currentPoint,

            onBack = {
                navController.popBackStack()
            }

        )

        LazyVerticalGrid(

            columns = GridCells.Fixed(2),

            modifier = Modifier.fillMaxSize()

        ) {

            items(rewardViewModel.rewardItems) { reward ->

                RewardCard(

                    reward = reward,

                    currentPoint = rewardViewModel.currentPoint,

                    onPurchase = {

                        rewardViewModel.purchaseReward(reward)

                    }

                )

            }

        }

    }

    selectedReward?.let { reward ->

        PurchaseConfirmationDialog(

            reward = reward,

            onDismiss = {

                selectedReward = null

            },

            onConfirm = {

                rewardViewModel.purchaseReward(reward)

                selectedReward = null

            }

        )

    }

}