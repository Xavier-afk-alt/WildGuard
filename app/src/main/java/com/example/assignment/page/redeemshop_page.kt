package com.example.assignment.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.assignment.components.RedeemHeader
import com.example.assignment.components.RewardCard
import com.example.assignment.viewmodel.RewardViewModel
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.assignment.components.PurchaseConfirmationDialog
import com.example.assignment.data.RewardItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import com.example.assignment.data.PurchaseResult
import com.example.assignment.viewmodel.InventoryViewModel
import kotlinx.coroutines.launch

@Composable
fun RedeemShopPage(

    navController: NavController,

    rewardViewModel: RewardViewModel,

    inventoryViewModel: InventoryViewModel

) {
    // Currently selected reward
    var selectedReward by remember {
        mutableStateOf<RewardItem?>(null)
    }

    // Quantity selected in confirmation dialog
    var selectedQuantity by remember {
        mutableStateOf(1)
    }

    // Snackbar
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {

            // =========================
            // REDEEM HEADER
            // =========================

            RedeemHeader(

                coin = rewardViewModel.currentPoint,

                onBack = {
                    navController.popBackStack()
                }

            )


            // =========================
            // REWARD GRID
            // =========================

            LazyVerticalGrid(

                columns = GridCells.Fixed(2),

                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)

            ) {

                items(
                    rewardViewModel.rewardItems
                ) { reward ->

                    RewardCard(

                        reward = reward,

                        currentPoint =
                            rewardViewModel.currentPoint,

                        onPurchase = {

                            when {

                                // =========================
                                // STOCK COMING SOON
                                // =========================

                                reward.stock < 0 -> {

                                    scope.launch {

                                        snackbarHostState.showSnackbar(
                                            "Stock is coming soon."
                                        )

                                    }

                                }


                                // =========================
                                // SOLD OUT
                                // =========================

                                reward.stock == 0 -> {

                                    scope.launch {

                                        snackbarHostState.showSnackbar(
                                            "This item is sold out."
                                        )

                                    }

                                }


                                // =========================
                                // NOT ENOUGH POINTS
                                // =========================

                                rewardViewModel.currentPoint
                                        < reward.price -> {

                                    scope.launch {

                                        snackbarHostState.showSnackbar(
                                            "Insufficient points to purchase."
                                        )

                                    }

                                }


                                // =========================
                                // CAN PURCHASE
                                // =========================

                                else -> {

                                    selectedReward = reward

                                    selectedQuantity = 1

                                }

                            }

                        }

                    )

                }

            }

        }

        // =========================
        // PURCHASE CONFIRMATION
        // =========================

        selectedReward?.let { reward ->

            PurchaseConfirmationDialog(

                reward = reward,

                quantity = selectedQuantity,

                onQuantityChange = { quantity ->

                    selectedQuantity = quantity

                },

                onDismiss = {

                    selectedReward = null

                    selectedQuantity = 1

                },

                onConfirm = {

                    val result =
                        rewardViewModel.purchaseReward(
                            reward,
                            selectedQuantity
                        )

                    if (result == PurchaseResult.SUCCESS) {
                        inventoryViewModel.addItem(
                            reward,
                            selectedQuantity
                        )
                    }

                    selectedReward = null
                    selectedQuantity = 1

                    val message = when (result) {
                        PurchaseResult.SUCCESS ->
                            "✓ Successfully redeemed ${reward.title}"

                        PurchaseResult.COMING_SOON ->
                            "${reward.title} is coming soon."

                        PurchaseResult.SOLD_OUT ->
                            "${reward.title} is sold out."

                        PurchaseResult.EXCEEDS_STOCK ->
                            "Not enough ${reward.title} in stock."

                        PurchaseResult.INSUFFICIENT_POINTS ->
                            "Insufficient points to purchase ${reward.title}."
                    }

                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }

                }

            )

        }

    }

}