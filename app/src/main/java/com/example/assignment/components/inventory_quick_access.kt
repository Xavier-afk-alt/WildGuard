package com.example.assignment.components

import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.data.InventoryItem
import com.example.assignment.viewmodel.InventoryViewModel

@Composable
fun InventoryQuickAccess(
    inventoryViewModel: InventoryViewModel,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onItemClick: (InventoryItem) -> Unit = {}
) {

    Box(
        modifier = modifier
    ) {

        // ==========================================
        // EXPANDED INVENTORY
        // ==========================================

        if (expanded) {

            Card(
                modifier = Modifier
                    .width(320.dp)
                    .padding(bottom = 60.dp),

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

                    // ==================================
                    // INVENTORY HEADER
                    // ==================================

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Inventory",
                            fontSize = 20.sp
                        )

                        IconButton(
                            onClick = onToggle
                        ) {

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close inventory"
                            )
                        }
                    }

                    // ==================================
                    // INVENTORY ITEMS
                    // ==================================

                    if (inventoryViewModel.items.isEmpty()) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),

                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "Inventory is empty",
                                fontSize = 14.sp
                            )
                        }

                    } else {

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(230.dp),

                            contentPadding = PaddingValues(4.dp),

                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp),

                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            items(
                                items = inventoryViewModel.items,
                                key = { it.rewardId }
                            ) { item ->

                                InventoryItemCard(
                                    item = item,
                                    onClick = {
                                        onItemClick(item)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // BACKPACK BUTTON
        // ==========================================

        FloatingActionButton(
            onClick = onToggle,

            modifier = Modifier
                .align(Alignment.BottomStart)
        ) {

            Icon(
                imageVector = Icons.Default.Backpack,
                contentDescription = "Inventory"
            )
        }
    }
}

@Composable
private fun InventoryItemCard(
    item: InventoryItem,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp),

        shape = RoundedCornerShape(14.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center
            ) {

                androidx.compose.foundation.Image(
                    painter = painterResource(
                        id = item.image
                    ),

                    contentDescription = item.title,

                    modifier = Modifier
                        .size(50.dp)
                        .clip(
                            RoundedCornerShape(10.dp)
                        ),

                    contentScale = ContentScale.Fit
                )

                Text(
                    text = item.title,

                    fontSize = 12.sp,

                    maxLines = 1
                )
            }

            // ======================================
            // QUANTITY BADGE
            // ======================================

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(
                        color = Color.Black
                            .copy(alpha = 0.75f),

                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(
                        horizontal = 6.dp,
                        vertical = 2.dp
                    )
            ) {

                Text(
                    text = "×${item.quantity}",

                    color = Color.White,

                    fontSize = 11.sp
                )
            }
        }
    }
}