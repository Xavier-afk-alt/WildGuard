package com.example.wildguard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onItemClick: (InventoryItem) -> Unit = {}
) {

    val inventoryGridState = rememberLazyGridState()

    Box(
        modifier = modifier
    ) {

        // ==========================================
        // INVENTORY EXPANDED PANEL
        // Only display when expanded = true
        // ==========================================

        if (expanded) {

            Card(
                modifier = Modifier
                    .width(320.dp)
                    .padding(
                        bottom = 56.dp
                    ),

                shape = RoundedCornerShape(20.dp),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {

                    // ==========================================
                    // INVENTORY HEADER
                    // ==========================================

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),

                        verticalAlignment =
                            Alignment.CenterVertically,

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Inventory",
                            fontSize = 20.sp
                        )

                        IconButton(
                            onClick = onToggle
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Close,

                                contentDescription =
                                    "Close inventory"
                            )
                        }
                    }

                    // ==========================================
                    // INVENTORY CONTENT
                    // ==========================================

                    if (
                        inventoryViewModel.items.isEmpty()
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text = "Inventory is empty",
                                fontSize = 14.sp
                            )
                        }

                    } else {

                        // ==========================================
                        // GRID + SCROLL INDICATORS
                        // ==========================================

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp)
                        ) {

                            LazyVerticalGrid(

                                state =
                                    inventoryGridState,

                                columns =
                                    GridCells.Fixed(3),

                                modifier =
                                    Modifier.fillMaxWidth(),

                                contentPadding =
                                    PaddingValues(
                                        start = 4.dp,
                                        end = 40.dp,
                                        top = 4.dp,
                                        bottom = 4.dp
                                    ),

                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp),

                                verticalArrangement =
                                    Arrangement.spacedBy(8.dp)

                            ) {

                                items(
                                    items =
                                        inventoryViewModel.items,

                                    key = {
                                        it.rewardId
                                    }

                                ) { item ->

                                    val isAvailable =
                                        isItemAvailableForRewardType(
                                            item = item,
                                            rewardType = currentRewardType
                                        )

                                    InventoryItemCard(

                                        item = item,

                                        enabled = isAvailable,

                                        onClick = {

                                            if (isAvailable) {
                                                onItemClick(item)
                                            }

                                        }

                                    )
                                }
                            }

                            // ==========================================
                            // UNIFIED VERTICAL ARROWS
                            // ==========================================

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
                                    .padding(end = 8.dp)

                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // INVENTORY BUTTON
        //
        // IMPORTANT:
        // This must be OUTSIDE if(expanded)
        // so it is always visible.
        // ==========================================

        FloatingActionButton(

            onClick = onToggle,

            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(52.dp)

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
    enabled: Boolean,
    onClick: () -> Unit
) {

    Card(

        onClick = onClick,

        enabled = enabled,

        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp),

        shape = RoundedCornerShape(14.dp),

        colors = CardDefaults.cardColors(
            containerColor =
                if (enabled) {
                    Color.White
                } else {
                    Color(0xFFE0E0E0)
                }
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation =
                if (enabled) 3.dp else 0.dp
        )

    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally

            ) {

                Icon(

                    painter =
                        painterResource(item.image),

                    contentDescription =
                        item.title,

                    modifier =
                        Modifier.size(46.dp),

                    tint =
                        if (enabled) {
                            Color.Unspecified
                        } else {
                            Color.Gray
                        }

                )

                Text(

                    text = item.title,

                    fontSize = 11.sp,

                    maxLines = 1,

                    color =
                        if (enabled) {
                            Color.Unspecified
                        } else {
                            Color.Gray
                        }

                )
            }

            Box(

                modifier = Modifier
                    .align(
                        Alignment.BottomEnd
                    )
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .background(
                        Color.Black.copy(
                            if (enabled) 0.75f else 0.4f
                        )
                    )
                    .padding(
                        horizontal = 6.dp,
                        vertical = 2.dp
                    )

            ) {

                Text(

                    text = "×${item.quantity}",

                    color =
                        Color.White,

                    fontSize =
                        11.sp

                )
            }
        }
    }
}

@Composable
fun isItemAvailableForRewardType(
    item: InventoryItem,
    rewardType: RewardType
): Boolean {

    return when (item.rewardId) {

        // Pat Animal
        1 -> {
            rewardType == RewardType.ANIMAL
        }

        // Water Plant
        2 -> {
            rewardType == RewardType.PLANT
        }

        // Future/general items
        else -> {
            true
        }
    }
}