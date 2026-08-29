package com.example.wildguard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wildguard.components.scene.LongPressQuickUse
import com.example.wildguard.data.InventoryItem
import com.example.wildguard.viewmodel.InventoryViewModel
import com.example.wildguard.viewmodel.RewardType


@Composable
fun InventoryQuickAccess(

    inventoryViewModel: InventoryViewModel,

    currentRewardType: RewardType,

    expanded: Boolean,

    onToggle: () -> Unit,

    modifier: Modifier = Modifier,

    onItemClick: (InventoryItem) -> Unit = {},

    onQuickUse: (InventoryItem) -> Unit,

    canQuickUse: (InventoryItem) -> Boolean

) {

    val inventoryGridState =
        rememberLazyGridState()


    BoxWithConstraints(

        modifier =
            modifier

    ) {


        val panelWidth =

            if (
                maxWidth <
                284.dp
            ) {

                maxWidth

            } else {

                284.dp
            }


        if (expanded) {


            Card(

                modifier = Modifier
                    .width(
                        panelWidth
                    )
                    .padding(
                        bottom = 56.dp
                    ),

                shape =
                    RoundedCornerShape(
                        18.dp
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )

            ) {


                Column(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            10.dp
                        )

                ) {


                    Row(

                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.SpaceBetween

                    ) {


                        Text(

                            text =
                                "Inventory",

                            fontSize =
                                18.sp,

                            fontWeight =
                                FontWeight.Medium
                        )
                    }


                    Spacer(

                        modifier =
                            Modifier.height(
                                4.dp
                            )
                    )


                    Text(
                        text =
                            "Tap for details • Press & hold compatible items to quick use",
                        modifier =
                            Modifier.fillMaxWidth(),
                        color =
                            Color(0xFF65705F),
                        fontSize =
                            11.sp,
                        lineHeight =
                            14.sp,
                        textAlign =
                            TextAlign.Start
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                6.dp
                            )
                    )


                    if (
                        inventoryViewModel
                            .items
                            .isEmpty()
                    ) {


                        Box(

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    130.dp
                                ),

                            contentAlignment =
                                Alignment.Center

                        ) {


                            Text(

                                text =
                                    "Inventory is empty",

                                fontSize =
                                    14.sp
                            )
                        }


                    } else {


                        Box(

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    234.dp
                                )

                        ) {


                            LazyVerticalGrid(

                                state =
                                    inventoryGridState,

                                columns =
                                    GridCells.Fixed(
                                        2
                                    ),

                                modifier =
                                    Modifier.fillMaxWidth(),

                                contentPadding =
                                    PaddingValues(
                                        2.dp
                                    ),

                                horizontalArrangement =
                                    Arrangement.spacedBy(
                                        7.dp
                                    ),

                                verticalArrangement =
                                    Arrangement.spacedBy(
                                        7.dp
                                    )

                            ) {


                                items(

                                    items =
                                        inventoryViewModel
                                            .items,

                                    key = {
                                        it.rewardId
                                    }

                                ) { item ->


                                    val isAvailable =
                                        isItemAvailableForRewardType(

                                            item =
                                                item,

                                            rewardType =
                                                currentRewardType
                                        )


                                    val canUse =
                                        canQuickUse(
                                            item
                                        )


                                    LongPressQuickUse(

                                        enabled =
                                            true,


                                        onClick = {

                                            onItemClick(
                                                item
                                            )
                                        },


                                        onLongPress = {

                                            if (canUse) {

                                                onQuickUse(
                                                    item
                                                )
                                            }
                                        }

                                    ) {


                                        InventoryItemCard(

                                            item =
                                                item,

                                            isAvailable =
                                                isAvailable
                                        )
                                    }
                                }
                            }


                            VerticalScrollIndicators(

                                canScrollBackward =
                                    inventoryGridState
                                        .canScrollBackward,

                                canScrollForward =
                                    inventoryGridState
                                        .canScrollForward,

                                modifier = Modifier
                                    .align(
                                        Alignment.CenterEnd
                                    )
                                    .padding(
                                        end = 2.dp
                                    )
                            )
                        }
                    }
                }
            }
        }


        FloatingActionButton(

            onClick =
                onToggle,

            modifier = Modifier
                .align(
                    Alignment.BottomStart
                )
                .size(
                    50.dp
                )

        ) {


            Icon(

                imageVector =

                    if (expanded) {

                        Icons.Default.Close

                    } else {

                        Icons.Default.Backpack
                    },

                contentDescription =

                    if (expanded) {

                        "Close inventory"

                    } else {

                        "Open inventory"
                    }
            )
        }
    }
}


@Composable
private fun InventoryItemCard(

    item: InventoryItem,

    isAvailable: Boolean

) {


    val restrictionText =

        when (
            item.rewardId
        ) {

            1 ->
                "ANIMAL ONLY"

            2 ->
                "PLANT ONLY"

            else ->
                null
        }


    // ============================================================
    // LIGHT SPECIAL STATUS COLORS
    // ============================================================

    val specialBackground =
        Color(
            0xFFFFFBF2
        )


    val specialBorder =
        Color(
            0xFFF0D49B
        )


    val specialBadgeBackground =
        Color(
            0xFFFFEFCB
        )


    val specialBadgeText =
        Color(
            0xFF986400
        )


    val specialText =
        Color(
            0xFF6E6452
        )


    val specialDot =
        Color(
            0xFFE4B65C
        )


    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(
                126.dp
            ),

        shape =
            RoundedCornerShape(
                13.dp
            ),


        // =====================================================
        // MUCH LIGHTER STATUS BORDER
        // =====================================================

        border =

            if (isAvailable) {

                null

            } else {

                BorderStroke(

                    width =
                        1.5.dp,

                    color =
                        specialBorder
                )
            },


        colors =
            CardDefaults.cardColors(

                containerColor =

                    if (isAvailable) {

                        Color.White

                    } else {

                        specialBackground
                    }
            ),


        elevation =
            CardDefaults.cardElevation(

                defaultElevation =

                    if (isAvailable) {

                        3.dp

                    } else {

                        3.dp
                    }
            )

    ) {


        Box(

            modifier =
                Modifier.fillMaxSize()

        ) {


            Column(

                modifier = Modifier
                    .fillMaxSize()
                    .padding(

                        start =
                            7.dp,

                        end =
                            7.dp,

                        top =
                            5.dp,

                        bottom =
                            22.dp
                    ),

                horizontalAlignment =
                    Alignment.CenterHorizontally

            ) {


                // =================================================
                // SAME STATUS AREA FOR BOTH ITEMS
                // =================================================

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            20.dp
                        ),

                    contentAlignment =
                        Alignment.Center

                ) {


                    if (
                        !isAvailable &&
                        restrictionText != null
                    ) {


                        Box(

                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        6.dp
                                    )
                                )
                                .background(
                                    specialBadgeBackground
                                )
                                .padding(

                                    horizontal =
                                        7.dp,

                                    vertical =
                                        2.dp
                                )

                        ) {


                            Text(

                                text =
                                    restrictionText,

                                fontSize =
                                    8.sp,

                                lineHeight =
                                    9.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    specialBadgeText
                            )
                        }
                    }
                }


                // =================================================
                // IMAGE
                // =================================================

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            48.dp
                        ),

                    contentAlignment =
                        Alignment.Center

                ) {


                    Image(

                        painter =
                            painterResource(
                                item.image
                            ),

                        contentDescription =
                            item.title,

                        modifier = Modifier
                            .size(
                                45.dp
                            )
                            .alpha(

                                if (isAvailable) {

                                    1f

                                } else {

                                    // Only slightly faded.
                                    0.78f
                                }
                            )
                    )
                }


                // =================================================
                // NAME
                // =================================================

                Text(

                    text =
                        item.title,

                    fontSize =
                        11.sp,

                    lineHeight =
                        12.5.sp,

                    fontWeight =
                        FontWeight.Medium,

                    maxLines =
                        3,

                    textAlign =
                        TextAlign.Center,

                    color =

                        if (isAvailable) {

                            Color(
                                0xFF222222
                            )

                        } else {

                            specialText
                        }
                )
            }


            // =====================================================
            // QUANTITY
            // =====================================================

            Box(

                modifier = Modifier
                    .align(
                        Alignment.BottomEnd
                    )
                    .padding(
                        end = 4.dp,
                        bottom = 4.dp
                    )
                    .clip(
                        RoundedCornerShape(
                            8.dp
                        )
                    )
                    .background(

                        if (isAvailable) {

                            Color.Black.copy(
                                alpha = 0.78f
                            )

                        } else {

                            Color(
                                0xFF9B8B6D
                            ).copy(
                                alpha = 0.72f
                            )
                        }
                    )
                    .padding(

                        horizontal =
                            6.dp,

                        vertical =
                            2.dp
                    )

            ) {


                Text(

                    text =
                        "×${item.quantity}",

                    color =
                        Color.White,

                    fontSize =
                        10.sp,

                    fontWeight =
                        FontWeight.Medium
                )
            }


            // =====================================================
            // LIGHT STATUS DOT
            // =====================================================

            if (!isAvailable) {


                Box(

                    modifier = Modifier
                        .align(
                            Alignment.BottomStart
                        )
                        .padding(
                            start = 5.dp,
                            bottom = 5.dp
                        )
                        .size(
                            8.dp
                        )
                        .clip(
                            CircleShape
                        )
                        .background(
                            specialDot
                        )
                )
            }
        }
    }
}


fun isItemAvailableForRewardType(

    item: InventoryItem,

    rewardType: RewardType

): Boolean {


    return when (
        item.rewardId
    ) {

        1 ->
            rewardType ==
                    RewardType.ANIMAL

        2 ->
            rewardType ==
                    RewardType.PLANT

        else ->
            true
    }
}