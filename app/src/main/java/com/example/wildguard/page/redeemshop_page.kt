package com.example.wildguard.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

        mutableStateOf<RewardItem?>(
            null
        )
    }


    var selectedQuantity by remember {

        mutableStateOf(
            1
        )
    }


    val snackbarHostState =
        remember {

            SnackbarHostState()
        }


    val redeemListState =
        rememberLazyListState()


    val scope =
        rememberCoroutineScope()


    // ============================================================
    // PURCHASE REQUEST
    // ============================================================

    fun requestPurchase(
        reward: RewardItem
    ) {


        when {


            reward.stock < 0 -> {


                scope.launch {

                    snackbarHostState
                        .showSnackbar(

                            "Stock is coming soon."
                        )
                }
            }


            reward.stock == 0 -> {


                scope.launch {

                    snackbarHostState
                        .showSnackbar(

                            "This item is sold out."
                        )
                }
            }


            rewardViewModel
                .currentPoint <
                    reward.price -> {


                scope.launch {

                    snackbarHostState
                        .showSnackbar(

                            "Insufficient points to purchase."
                        )
                }
            }


            else -> {


                selectedReward =
                    reward


                selectedQuantity =
                    1
            }
        }
    }


    // ============================================================
    // NO NESTED SCAFFOLD
    // ============================================================

    BoxWithConstraints(

        modifier =
            Modifier.fillMaxSize()

    ) {


        val columnCount =

            if (
                maxWidth <
                340.dp
            ) {

                1

            } else {

                2
            }


        val horizontalPagePadding =
            16.dp


        val pairSpacing =
            10.dp


        // ========================================================
        // CALCULATE APPROXIMATE CARD WIDTH
        // ========================================================

        val estimatedCardWidth =

            if (
                columnCount ==
                2
            ) {

                (
                        maxWidth -
                                (
                                        horizontalPagePadding *
                                                2f
                                        ) -
                                pairSpacing
                        ) /
                        2f

            } else {

                maxWidth -
                        (
                                horizontalPagePadding *
                                        2f
                                )
            }


        val compactCard =

            estimatedCardWidth <
                    170.dp


        // ========================================================
        // GROUP ONLY ADJACENT ITEMS
        // ========================================================

        val rewardRows =

            rewardViewModel
                .rewardItems
                .chunked(
                    columnCount
                )


        Column(

            modifier =
                Modifier.fillMaxSize()

        ) {


            RedeemHeader(

                coin =
                    rewardViewModel
                        .currentPoint,

                onBack = {

                    navController
                        .popBackStack()
                }
            )


            Box(

                modifier = Modifier

                    .fillMaxWidth()

                    .weight(
                        1f
                    )

            ) {


                // =================================================
                // LAZY COLUMN OF PAIRS
                // =================================================

                LazyColumn(

                    state =
                        redeemListState,

                    modifier =
                        Modifier.fillMaxSize(),

                    contentPadding =
                        PaddingValues(

                            start =
                                horizontalPagePadding,

                            end =
                                horizontalPagePadding,

                            top =
                                8.dp,

                            bottom =
                                12.dp
                        ),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )

                ) {


                    items(

                        items =
                            rewardRows,

                        key = {
                                row ->


                            row.joinToString(
                                "_"
                            ) {

                                it.id
                                    .toString()
                            }
                        }

                    ) {
                            rowItems ->


                        // =========================================
                        // VERY NARROW PHONE
                        // =========================================

                        if (
                            columnCount ==
                            1
                        ) {


                            RewardCard(

                                reward =
                                    rowItems.first(),

                                currentPoint =
                                    rewardViewModel
                                        .currentPoint,

                                onPurchase = {

                                    requestPurchase(
                                        rowItems.first()
                                    )
                                },

                                compact =
                                    false,

                                modifier =
                                    Modifier.fillMaxWidth()
                            )


                        } else {


                            // =====================================
                            // TWO ITEMS SHARE ONLY THIS ROW HEIGHT
                            // =====================================

                            Row(

                                modifier = Modifier

                                    .fillMaxWidth()

                                    .height(
                                        IntrinsicSize.Max
                                    ),

                                horizontalArrangement =
                                    Arrangement.spacedBy(
                                        pairSpacing
                                    )

                            ) {


                                rowItems.forEach {
                                        reward ->


                                    RewardCard(

                                        reward =
                                            reward,

                                        currentPoint =
                                            rewardViewModel
                                                .currentPoint,

                                        onPurchase = {

                                            requestPurchase(
                                                reward
                                            )
                                        },

                                        compact =
                                            compactCard,

                                        modifier = Modifier

                                            .weight(
                                                1f
                                            )

                                            .fillMaxHeight()
                                    )
                                }


                                // If final row contains only one item,
                                // keep it half-width.
                                if (
                                    rowItems.size ==
                                    1
                                ) {


                                    Spacer(

                                        modifier =
                                            Modifier.weight(
                                                1f
                                            )
                                    )
                                }
                            }
                        }
                    }
                }


                VerticalScrollIndicators(

                    canScrollBackward =
                        redeemListState
                            .canScrollBackward,

                    canScrollForward =
                        redeemListState
                            .canScrollForward,

                    modifier = Modifier

                        .align(
                            Alignment.CenterEnd
                        )

                        .padding(
                            end = 8.dp
                        )
                )
            }
        }


        // ========================================================
        // SNACKBAR
        // ========================================================

        SnackbarHost(

            hostState =
                snackbarHostState,

            modifier = Modifier

                .align(
                    Alignment.BottomCenter
                )

                .padding(

                    start =
                        12.dp,

                    end =
                        12.dp,

                    bottom =
                        10.dp
                )
        )


        // ========================================================
        // CONFIRMATION
        // ========================================================

        selectedReward?.let {
                reward ->


            PurchaseConfirmationDialog(

                reward =
                    reward,

                currentPoint =
                    rewardViewModel
                        .currentPoint,

                quantity =
                    selectedQuantity,


                onQuantityChange = {
                        quantity ->

                    selectedQuantity =
                        quantity
                },


                onDismiss = {

                    selectedReward =
                        null

                    selectedQuantity =
                        1
                },


                onConfirm = {


                    val result =
                        rewardViewModel
                            .purchaseReward(

                                reward,

                                selectedQuantity
                            )


                    if (
                        result ==
                        PurchaseResult.SUCCESS
                    ) {


                        inventoryViewModel
                            .addItem(

                                reward,

                                selectedQuantity
                            )
                    }


                    selectedReward =
                        null


                    selectedQuantity =
                        1


                    val message =

                        when (result) {


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
                            .showSnackbar(
                                message
                            )
                    }
                }
            )
        }
    }
}