package com.example.wildguard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wildguard.data.PurchaseMode
import com.example.wildguard.data.RewardItem

@Composable
fun PurchaseConfirmationDialog(

    reward: RewardItem,

    currentPoint: Int,

    quantity: Int,

    onQuantityChange: (Int) -> Unit,

    onDismiss: () -> Unit,

    onConfirm: () -> Unit
) {

    val maxAffordableQuantity =
        if (reward.price > 0) {
            currentPoint / reward.price
        } else {
            reward.stock
        }

    val maxQuantity =
        minOf(
            reward.stock,
            maxAffordableQuantity
        ).coerceAtLeast(1)

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Confirm Purchase")
        },

        text = {
            Column {

                Text(
                    text = "Purchase ${reward.title}?"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // =========================
                // MULTIPLE PURCHASE
                // =========================

                if (reward.purchaseMode == PurchaseMode.MULTIPLE) {

                    Text(
                        text = "Quantity: $quantity"
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    if (maxQuantity > 1) {

                        Slider(

                            value = quantity.toFloat(),

                            onValueChange = { value ->

                                val newQuantity =
                                    value
                                        .toInt()
                                        .coerceIn(
                                            1,
                                            maxQuantity
                                        )

                                onQuantityChange(
                                    newQuantity
                                )
                            },

                            valueRange =
                                1f..maxQuantity.toFloat(),

                            steps =
                                (maxQuantity - 2)
                                    .coerceAtLeast(0)
                        )

                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Min: 1"
                        )

                        Text(
                            text = "Max: $maxQuantity"
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Available stock: ${reward.stock}"
                    )

                } else {

                    // =========================
                    // SINGLE PURCHASE
                    // =========================

                    Text(
                        text = "Quantity: 1"
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text =
                        "Total: ${
                            reward.price * quantity
                        } points"
                )
            }
        },

        confirmButton = {

            Button(
                onClick = onConfirm
            ) {
                Text("Confirm")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}