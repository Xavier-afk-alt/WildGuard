package com.example.assignment.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.assignment.data.InventoryItem

@Composable
fun InventoryItemRow(

    item: InventoryItem,

    onUse: () -> Unit

) {

    Row(

        verticalAlignment =
            Alignment.CenterVertically,

        horizontalArrangement =
            Arrangement.SpaceBetween,

        modifier =
            Modifier
                .width(280.dp)

    ) {

        Image(

            painter =
                painterResource(item.image),

            contentDescription =
                item.title,

            modifier =
                Modifier.size(45.dp)

        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text =
                "${item.title} x${item.quantity}"
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Button(
            onClick = onUse
        ) {

            Text("Use")

        }

    }
}