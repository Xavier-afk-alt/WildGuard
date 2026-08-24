package com.example.wildguard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ScrollIndicatorDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

@Composable
fun ScrollIndicator(
    direction: ScrollIndicatorDirection,
    modifier: Modifier = Modifier
) {
    val icon = when (direction) {
        ScrollIndicatorDirection.UP ->
            Icons.Default.KeyboardArrowUp

        ScrollIndicatorDirection.DOWN ->
            Icons.Default.KeyboardArrowDown

        ScrollIndicatorDirection.LEFT ->
            Icons.Default.KeyboardArrowLeft

        ScrollIndicatorDirection.RIGHT ->
            Icons.Default.KeyboardArrowRight
    }

    Box(
        modifier = modifier
            .size(32.dp)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape
            )
            .background(
                color = Color.White.copy(alpha = 0.92f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4F6547),
            modifier = Modifier.size(24.dp)
        )
    }
}

/*
 * =========================================================
 * UNIFIED VERTICAL SCROLL INDICATORS
 *
 * Position this composable using:
 *
 * .align(Alignment.CenterEnd)
 * .padding(end = 8.dp)
 *
 * This keeps the same position for:
 * - Redeem Shop
 * - Achievements
 * - Inventory
 * =========================================================
 */

@Composable
fun VerticalScrollIndicators(
    canScrollBackward: Boolean,
    canScrollForward: Boolean,
    modifier: Modifier = Modifier
) {
    if (!canScrollBackward && !canScrollForward) {
        return
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (canScrollBackward) {
            ScrollIndicator(
                direction = ScrollIndicatorDirection.UP
            )
        }

        if (canScrollForward) {
            ScrollIndicator(
                direction = ScrollIndicatorDirection.DOWN
            )
        }
    }
}

/*
 * =========================================================
 * UNIFIED HORIZONTAL SCROLL INDICATORS
 *
 * This composable fills the parent scroll area and places:
 *
 * LEFT  -> CenterStart
 * RIGHT -> CenterEnd
 * =========================================================
 */

@Composable
fun HorizontalScrollIndicators(
    canScrollBackward: Boolean,
    canScrollForward: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (canScrollBackward) {
            ScrollIndicator(
                direction = ScrollIndicatorDirection.LEFT,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }

        if (canScrollForward) {
            ScrollIndicator(
                direction = ScrollIndicatorDirection.RIGHT,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}