package com.example.wildguard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun AchievementBanner(

    earnedPoints: Int,

    maxPoint: Int,

    status: String,

    borderColor: Color,

    onClick: () -> Unit

) {


    val progress =

        if (maxPoint > 0) {

            (
                    earnedPoints.toFloat() /
                            maxPoint
                    )
                .coerceIn(
                    0f,
                    1f
                )

        } else {

            0f
        }


    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(

                horizontal =
                    8.dp,

                // Original 4dp
                vertical =
                    2.dp
            )
            .clickable(
                onClick = onClick
            ),

        shape =
            RoundedCornerShape(
                28.dp
            ),

        border =
            BorderStroke(
                2.dp,
                borderColor
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    2.dp
            )

    ) {


        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(

                    horizontal =
                        10.dp,

                    // Original 7dp
                    vertical =
                        7.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically

        ) {


            // ====================================================
            // TROPHY
            // ====================================================

            Box(

                // Original = 42.dp
                modifier =
                    Modifier.size(
                        36.dp
                    ),

                contentAlignment =
                    Alignment.Center

            ) {


                Icon(

                    imageVector =
                        Icons.Default.EmojiEvents,

                    contentDescription =
                        "Achievement rank",

                    tint =
                        Color(0xFFFFB52E),

                    // Original = 36.dp
                    modifier =
                        Modifier.size(
                            36.dp
                        )
                )
            }


            Spacer(
                Modifier.size(
                    6.dp
                )
            )


            // ====================================================
            // ACHIEVEMENT INFORMATION
            // ====================================================

            Column(

                modifier =
                    Modifier.weight(1f)

            ) {


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {


                    Text(

                        text =
                            "$earnedPoints/$maxPoint",

                        color =
                            Color(0xFF148B2D),

                        fontWeight =
                            FontWeight.Bold,

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )


                    Text(

                        text =
                            status,

                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }


                Text(

                    text =
                        "Achievement progress • Tap to view all",

                    style =
                        MaterialTheme
                            .typography
                            .labelSmall,

                    color =
                        Color.Gray,

                    maxLines =
                        1
                )

                Spacer(

                    Modifier.height(
                        2.dp
                    )
                )


                LinearProgressIndicator(

                    progress = {
                        progress
                    },

                    modifier = Modifier
                        .fillMaxWidth()

                        .height(
                            3.dp
                        )

                        .clip(
                            RoundedCornerShape(
                                50
                            )
                        ),

                    color =
                        borderColor,

                    trackColor =
                        Color(0xFFE9E9E9)
                )
            }
        }
    }
}