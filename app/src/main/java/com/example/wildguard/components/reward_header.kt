package com.example.wildguard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.wildguard.R


@Composable
fun RewardHeader() {

    Box(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 2.dp,
                bottom = 1.dp
            ),

        contentAlignment =
            Alignment.Center

    ) {


        Image(

            painter =
                painterResource(
                    R.drawable.terranest_logo
                ),

            contentDescription =
                "TerraNest",

            // Original = 48.dp
            //
            // Small reduction only.
            modifier =
                Modifier.height(
                    40.dp
                )
        )
    }
}