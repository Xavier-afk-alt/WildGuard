package com.example.assignment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.assignment.R

@Composable
fun HiddenInteractionButton(

    modifier: Modifier = Modifier,

    visible: Boolean,

    onClick: () -> Unit

) {

    if (visible) {

        Box(

            modifier = Modifier
                .size(40.dp)
                .background(Color.White, CircleShape)
                .clickable { onClick() },

            contentAlignment = Alignment.Center

        ) {

            Icon(

                painter = painterResource(R.drawable.ic_launcher_foreground),

                contentDescription = null

            )

        }

    }

}