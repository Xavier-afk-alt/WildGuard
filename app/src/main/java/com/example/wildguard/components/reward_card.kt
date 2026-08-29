package com.example.wildguard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.wildguard.R
import com.example.wildguard.data.RewardItem


@Composable
fun RewardCard(

    reward: RewardItem,

    currentPoint: Int,

    onPurchase: () -> Unit,

    modifier: Modifier = Modifier,

    compact: Boolean = false

) {

    val isLowStockAlert =
        reward.stock < 0


    val isSoldOut =
        reward.stock == 0


    val insufficientPoint =

        !isLowStockAlert &&

                !isSoldOut &&

                currentPoint <
                reward.price


    val displayImage =

        if (isLowStockAlert) {

            R.drawable.low_stock

        } else {

            reward.image
        }


    val imageHeight =

        if (compact) {

            86.dp

        } else {

            98.dp
        }


    Card(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(
                12.dp
            )

    ) {


        Column(

            modifier = Modifier

                .fillMaxWidth()

                .fillMaxHeight()

                .padding(
                    8.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {


            Text(

                text =
                    "${reward.type}  |  Stock: ${
                        if (
                            isLowStockAlert
                        ) {
                            "-"
                        } else {
                            reward.stock
                        }
                    }",

                style =
                    MaterialTheme
                        .typography
                        .labelSmall
            )


            Spacer(

                modifier =
                    Modifier.height(
                        5.dp
                    )
            )


            Image(

                painter =
                    painterResource(
                        displayImage
                    ),

                contentDescription =
                    reward.title,

                modifier = Modifier

                    .fillMaxWidth()

                    .height(
                        imageHeight
                    ),

                contentScale =
                    ContentScale.Fit
            )


            Spacer(

                modifier =
                    Modifier.height(
                        5.dp
                    )
            )


            // Full title.
            Text(

                text =
                    reward.title,

                style =
                    MaterialTheme
                        .typography
                        .titleSmall
            )


            Spacer(

                modifier =
                    Modifier.height(
                        3.dp
                    )
            )


            // Full description.
            //
            // No maxLines.
            // No ellipsis.
            Text(

                text =
                    reward.description,

                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )


            // If card beside this one is taller,
            // extra height appears here instead of
            // stretching/squeezing the text.
            Spacer(

                modifier =
                    Modifier.weight(
                        1f
                    )
            )


            if (compact) {


                // Narrow card.
                Column(

                    modifier =
                        Modifier.fillMaxWidth()

                ) {


                    Text(

                        text =
                            "🪙 ${reward.price}",

                        style =
                            MaterialTheme
                                .typography
                                .labelMedium
                    )


                    Spacer(

                        modifier =
                            Modifier.height(
                                6.dp
                            )
                    )


                    PurchaseButton(

                        isSoldOut =
                            isSoldOut,

                        isLowStockAlert =
                            isLowStockAlert,

                        insufficientPoint =
                            insufficientPoint,

                        onPurchase =
                            onPurchase,

                        modifier = Modifier

                            .fillMaxWidth()

                            .heightIn(
                                min = 38.dp
                            )
                    )
                }


            } else {


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically,

                    horizontalArrangement =
                        Arrangement.SpaceBetween

                ) {


                    Text(

                        text =
                            "🪙 ${reward.price}",

                        style =
                            MaterialTheme
                                .typography
                                .labelMedium
                    )


                    Spacer(

                        modifier =
                            Modifier.width(
                                4.dp
                            )
                    )


                    PurchaseButton(

                        isSoldOut =
                            isSoldOut,

                        isLowStockAlert =
                            isLowStockAlert,

                        insufficientPoint =
                            insufficientPoint,

                        onPurchase =
                            onPurchase,

                        modifier = Modifier

                            .widthIn(
                                min = 88.dp
                            )

                            .heightIn(
                                min = 38.dp
                            )
                    )
                }
            }
        }
    }
}


@Composable
private fun PurchaseButton(

    isSoldOut: Boolean,

    isLowStockAlert: Boolean,

    insufficientPoint: Boolean,

    onPurchase: () -> Unit,

    modifier: Modifier = Modifier

) {


    Button(

        onClick =
            onPurchase,

        enabled =
            true,

        modifier =
            modifier,

        contentPadding =
            PaddingValues(

                horizontal =
                    9.dp,

                vertical =
                    5.dp
            ),

        colors =
            ButtonDefaults.buttonColors(

                containerColor =

                    when {


                        isSoldOut ->

                            Color(0xFFFF5252)


                        isLowStockAlert ->

                            Color(0xFFD0D0D0)


                        insufficientPoint ->

                            Color(0xFFD0D0D0)


                        else ->

                            Color(0xFF00C853)
                    },


                contentColor =

                    if (isSoldOut) {

                        Color.White

                    } else {

                        Color.Black
                    }
            )

    ) {


        Text(

            text =

                if (isSoldOut) {

                    "Sold Out"

                } else {

                    "Purchase"
                },

            style =
                MaterialTheme
                    .typography
                    .labelMedium,

            maxLines =
                1
        )
    }
}