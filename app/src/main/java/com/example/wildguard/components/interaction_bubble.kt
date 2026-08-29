package com.example.wildguard.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
fun InteractionBubble(

    text: String,

    @DrawableRes
    iconRes: Int,

    modifier: Modifier = Modifier

) {

    Card(

        modifier =
            modifier,

        shape =
            RoundedCornerShape(
                20.dp
            ),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    Color.White.copy(
                        alpha = 0.97f
                    )
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )

    ) {


        Row(

            modifier = Modifier.padding(
                start = 10.dp,
                top = 9.dp,
                end = 14.dp,
                bottom = 9.dp
            ),

            verticalAlignment =
                Alignment.CenterVertically

        ) {


            // ====================================================
            // CURRENT REWARD ICON
            //
            // Animal:
            // selected AnimalSelector icon
            //
            // Plant:
            // Reward Toggle plant icon
            // ====================================================

            Surface(

                modifier =
                    Modifier.size(
                        40.dp
                    ),

                shape =
                    CircleShape,

                color =
                    Color(0xFFF1F6EE)

            ) {


                Box(

                    contentAlignment =
                        Alignment.Center

                ) {


                    Image(

                        painter =
                            painterResource(
                                iconRes
                            ),

                        contentDescription =
                            null,

                        modifier =
                            Modifier.size(
                                32.dp
                            )
                    )
                }
            }


            Spacer(

                modifier =
                    Modifier.width(
                        9.dp
                    )
            )


            Text(
                text = text
            )
        }
    }
}