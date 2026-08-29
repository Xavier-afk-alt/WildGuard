package com.example.wildguard.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun RedeemButton(

    coin: Int,

    onClick: () -> Unit

) {


    Box(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 3.dp,
                bottom = 2.dp
            ),

        contentAlignment =
            Alignment.Center

    ) {


        Button(

            onClick =
                onClick,

            modifier = Modifier
                // Responsive width
                .fillMaxWidth(
                    0.68f
                )
                .widthIn(
                    min = 210.dp,
                    max = 300.dp
                )
                .heightIn(
                    min = 35.dp
                ),

            shape =
                RoundedCornerShape(
                    50.dp
                ),

            colors =
                ButtonDefaults.buttonColors(

                    containerColor =
                        Color(0xFFD98AF3)
                ),

            contentPadding =
                PaddingValues(

                    horizontal =
                        16.dp,

                    vertical =
                        6.dp
                )

        ) {


            Text(
                text =
                    "🪙 $coin"
            )


            Spacer(

                modifier =
                    Modifier.width(
                        10.dp
                    )
            )


            Text(
                text =
                    "REDEEM"
            )


            Spacer(

                modifier =
                    Modifier.width(
                        6.dp
                    )
            )


            Icon(

                imageVector =
                    Icons.Default.ShoppingCart,

                contentDescription =
                    "Redeem"
            )
        }
    }
}