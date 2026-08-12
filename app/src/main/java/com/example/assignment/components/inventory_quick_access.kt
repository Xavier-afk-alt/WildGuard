package com.example.assignment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InventoryQuickAccess(
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (expanded) {

            InventoryItemButton(
                name = "Pat Hand"
            )

            InventoryItemButton(
                name = "Watering"
            )

            InventoryItemButton(
                name = "Voucher"
            )
        }

        InventoryButton(
            onClick = onToggle
        )
    }
}

@Composable
fun InventoryButton(
    onClick: () -> Unit
) {

    Card(

        modifier = Modifier
            .size(64.dp),

        shape = CircleShape,

        onClick = onClick

    ) {

        Text(
            text = "🎒",
            modifier = Modifier
                .padding(20.dp)
        )

    }

}

@Composable
fun InventoryItemButton(
    name: String
) {

    Card(

        modifier = Modifier
            .size(64.dp),

        shape = CircleShape

    ) {

        Text(
            text = name,
            modifier = Modifier
                .padding(8.dp)
        )

    }

}