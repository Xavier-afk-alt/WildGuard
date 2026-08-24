package com.example.wildguard.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.wildguard.components.PurchaseConfirmationDialog
import com.example.wildguard.components.RedeemHeader
import com.example.wildguard.components.RewardCard
import com.example.wildguard.components.VerticalScrollIndicators
import com.example.wildguard.data.PurchaseResult
import com.example.wildguard.data.RewardItem
import com.example.wildguard.viewmodel.InventoryViewModel
import com.example.wildguard.viewmodel.RewardViewModel
import kotlinx.coroutines.launch

@Composable
fun RedeemShopPage(
    navController: NavController,
    rewardViewModel: RewardViewModel,
    inventoryViewModel: InventoryViewModel
) {
    var selectedReward by remember {
        mutableStateOf<RewardItem?>(null)
    }

    var selectedQuantity by remember {
        mutableStateOf(1)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val redeemGridState =
        rememberLazyGridState()

    val scope =
        rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            /*
             * =========================================
             * REDEEM HEADER
             * =========================================
             */

            RedeemHeader(
                coin = rewardViewModel.currentPoint,
                onBack = {
                    navController.popBackStack()
                }
            )

            /*
             * =========================================
             * REWARD GRID AREA
             *
             * The grid and the indicator overlay are
             * inside the same Box.
             * =========================================
             */

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                LazyVerticalGrid(
                    state = redeemGridState,

                    columns = GridCells.Fixed(2),

                    modifier =
                        Modifier.fillMaxSize()
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

                                    /*
                                     * Coming soon
                                     */

                                    reward.stock < 0 -> {
                                        scope.launch {
                                            snackbarHostState
                                                .showSnackbar(
                                                    "Stock is coming soon."
                                                )
                                        }
                                    }

                                    /*
                                     * Sold out
                                     */

                                    reward.stock == 0 -> {
                                        scope.launch {
                                            snackbarHostState
                                                .showSnackbar(
                                                    "This item is sold out."
                                                )
                                        }
                                    }

                                    /*
                                     * Insufficient points
                                     */

                                    rewardViewModel.currentPoint <
                                            reward.price -> {
                                        scope.launch {
                                            snackbarHostState
                                                .showSnackbar(
                                                    "Insufficient points to purchase."
                                                )
                                        }
                                    }

                                    /*
                                     * Purchase available
                                     */

                                    else -> {
                                        selectedReward =
                                            reward

                                        selectedQuantity =
                                            1
                                    }
                                }
                            }
                        )
                    }
                }

                /*
                 * =====================================
                 * UNIFIED ARROW POSITION
                 *
                 * Exactly the same implementation used
                 * by Achievement and Inventory.
                 * =====================================
                 */

                VerticalScrollIndicators(
                    canScrollBackward =
                        redeemGridState.canScrollBackward,

                    canScrollForward =
                        redeemGridState.canScrollForward,

                    modifier = Modifier
                        .align(
                            Alignment.CenterEnd
                        )
                        .padding(end = 8.dp)
                )
            }

            /*
             * =========================================
             * PURCHASE CONFIRMATION
             * =========================================
             */

            selectedReward?.let { reward ->

                PurchaseConfirmationDialog(
                    reward = reward,

                    currentPoint =
                        rewardViewModel.currentPoint,

                    quantity =
                        selectedQuantity,

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

                        if (
                            result == PurchaseResult.SUCCESS
                        ) {
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
                            snackbarHostState
                                .showSnackbar(message)
                        }
                    }
                )
            }
        }
    }
}