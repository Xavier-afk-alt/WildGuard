package com.example.assignment.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.assignment.data.InventoryItem

enum class InventoryQuickAction {
    PAT_ANIMAL,
    WATER_PLANT
}

@Composable
fun InventoryItemDialog(
    item: InventoryItem,
    onDismiss: () -> Unit,
    onQuickAccess: (() -> Unit)? = null
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            shape = RoundedCornerShape(24.dp),

            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ======================================
                // ITEM IMAGE
                // ======================================

                Image(
                    painter = painterResource(
                        id = item.image
                    ),

                    contentDescription = item.title,

                    modifier = Modifier
                        .size(120.dp),

                    contentScale = ContentScale.Fit
                )

                Spacer(
                    modifier = Modifier.size(12.dp)
                )

                // ======================================
                // ITEM NAME
                // ======================================

                Text(
                    text = item.title,
                    fontSize = 22.sp
                )

                Spacer(
                    modifier = Modifier.size(6.dp)
                )

                // ======================================
                // QUANTITY
                // ======================================

                Text(
                    text = "Owned: ×${item.quantity}",
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.size(16.dp)
                )

                // ======================================
                // DESCRIPTION
                // ======================================

                Text(
                    text = item.description,
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.size(20.dp)
                )

                // ======================================
                // QUICK ACCESS
                // ======================================

                if (onQuickAccess != null) {

                    Button(
                        onClick = onQuickAccess,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Quick Access"
                        )
                    }

                    Spacer(
                        modifier = Modifier.size(4.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.size(4.dp)
                )

                // ======================================
                // CLOSE
                // ======================================

                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = "Close"
                    )
                }
            }
        }
    }
}