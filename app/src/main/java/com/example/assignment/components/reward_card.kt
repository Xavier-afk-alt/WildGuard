package com.example.assignment.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.assignment.R
import com.example.assignment.data.RewardItem
import androidx.compose.ui.graphics.Color

@Composable
fun RewardCard(

    reward: RewardItem,

    currentPoint: Int,

    onPurchase: () -> Unit

) {

    val isLowStockAlert = reward.stock < 0

    val isSoldOut = reward.stock == 0

    val insufficientPoint =
        !isLowStockAlert &&
                !isSoldOut &&
                currentPoint < reward.price

    val canPurchase =
        !isLowStockAlert &&
                !isSoldOut &&
                !insufficientPoint

    val displayImage =
        if (isLowStockAlert)
            R.drawable.low_stock
        else
            reward.image

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),

        shape = RoundedCornerShape(8.dp)

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            // Type + stock
            Text(
                text = "${reward.type}  |  Stock: ${
                    if (isLowStockAlert) "-" else reward.stock
                }"
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // Reward image
            Image(

                painter = painterResource(displayImage),

                contentDescription = reward.title,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)

            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // Title
            Text(
                text = reward.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            // Description
            Text(
                text = reward.description,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            // Price + purchase button
            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.SpaceBetween

            ) {

                Text(
                    text = "🪙 ${reward.price}"
                )

                Button(

                    onClick = {
                        if (canPurchase) {
                            onPurchase()
                        }
                    },

                    enabled = true,

                    colors = ButtonDefaults.buttonColors(

                        containerColor = when {

                            isSoldOut ->
                                Color(0xFFFF5252)       // Red

                            isLowStockAlert ->
                                Color(0xFFD0D0D0)       // Grey

                            insufficientPoint ->
                                Color(0xFFD0D0D0)       // Grey

                            else ->
                                Color(0xFF00C853)       // Green
                        },

                        contentColor = when {

                            isSoldOut ->
                                Color.White

                            else ->
                                Color.Black
                        }
                    )

                ) {

                    Text(

                        when {

                            isSoldOut ->
                                "Sold Out"

                            else ->
                                "Purchase"

                        }

                    )

                }

            }

        }

    }

}