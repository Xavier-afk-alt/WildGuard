package com.example.assignment.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.assignment.data.RewardItem

@Composable
fun PurchaseConfirmationDialog(

    reward: RewardItem,

    onDismiss: () -> Unit,

    onConfirm: () -> Unit

) {

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Confirm Purchase")
        },

        text = {

            Text(
                "Purchase ${reward.title} for ${reward.price} points?"
            )

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