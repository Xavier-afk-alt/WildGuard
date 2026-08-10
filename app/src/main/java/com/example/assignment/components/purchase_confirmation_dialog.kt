package com.example.assignment.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.unit.dp
import com.example.assignment.data.PurchaseMode
import com.example.assignment.data.RewardItem

@Composable
fun PurchaseConfirmationDialog(

    reward: RewardItem,

    quantity: Int,

    onQuantityChange: (Int) -> Unit,

    onDismiss: () -> Unit,

    onConfirm: () -> Unit

) {

    val totalPrice = reward.price * quantity

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Confirm Purchase")
        },

        text = {
            Column {
                Text(
                    "Purchase ${reward.title}?"
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (reward.purchaseMode == PurchaseMode.MULTIPLE) {
                    Text("Quantity: $quantity")
                    Row {
                        Button(
                            onClick = {
                                if (quantity > 1) {
                                    onQuantityChange(quantity - 1)
                                }
                            }
                        ) {
                            Text("-")
                        }

                        Text(
                            text = "  $quantity  ",
                            modifier = Modifier
                                .padding(15.dp)
                                .wrapContentHeight(align = Alignment.CenterVertically)
                        )

                        Button(
                            onClick = {
                                if (quantity < reward.stock) {
                                    onQuantityChange(quantity + 1)
                                }
                            }
                        ) {
                            Text("+")
                        }
                    }

                } else {
                    Text("Quantity: 1")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Total: $totalPrice points")
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