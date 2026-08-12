package com.example.assignment.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.assignment.viewmodel.InventoryViewModel

@Composable
fun InventoryPanel(

    inventoryViewModel: InventoryViewModel,

    onUseItem: (com.example.assignment.data.InventoryItem) -> Unit,

    modifier: Modifier = Modifier

) {

    Card(
        modifier = modifier
            .padding(8.dp)
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text("Inventory")

            inventoryViewModel.items.forEach { item ->

                InventoryItemRow(

                    item = item,

                    onUse = {
                        onUseItem(item)
                    }

                )

            }

        }

    }
}