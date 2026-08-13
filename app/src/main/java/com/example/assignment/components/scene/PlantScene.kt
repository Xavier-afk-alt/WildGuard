package com.example.assignment.components.scene

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.assignment.R

@Composable
fun PlantScene(
    plantImage: Int,
    plantSize: Dp = 180.dp,
    plantOffsetY: Dp = 35.dp,
    onShortPress: () -> Unit,
    onLongPress: () -> Unit,
    showWateringTool: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8D9))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.38f)
                .align(Alignment.BottomCenter)
                .background(Color(0xFF67A900))
        )

        Image(
            painter = painterResource(plantImage),
            contentDescription = "Plant",
            modifier = Modifier
                .align(Alignment.Center)
                .size(plantSize)
                .offset(y = plantOffsetY)
                .twoSecondQuickUse(
                    onTap = onShortPress,
                    onLongPress = onLongPress
                )
        )

        AnimatedVisibility(
            visible = showWateringTool,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-24).dp, y = 45.dp),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Image(
                painter = painterResource(R.drawable.watering_tool),
                contentDescription = "Watering tool",
                modifier = Modifier.size(80.dp)
            )
        }
    }
}
