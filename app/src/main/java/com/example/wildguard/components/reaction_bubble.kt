package com.example.wildguard.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.wildguard.R

enum class ReactionType {
    SLEEP,
    SMILE,
    HEART,

    WATER,
    GROW,
    LEAF
}

@Composable
fun ReactionBubble(
    visible: Boolean,
    reactionType: ReactionType,
    modifier: Modifier = Modifier
){
    AnimatedVisibility(
        visible = visible,

        enter =
            fadeIn(
                animationSpec = tween(180)
            ) +
                    scaleIn(
                        initialScale = 0.4f,
                        animationSpec = tween(
                            durationMillis = 280,
                            easing = FastOutSlowInEasing
                        )
                    ),

        exit =
            fadeOut(
                animationSpec = tween(180)
            ) +
                    scaleOut(
                        targetScale = 0.4f,
                        animationSpec = tween(220)
                    ),

        modifier = modifier
    ) {

        val reactionImage = when (reactionType) {

            ReactionType.SLEEP ->
                R.drawable.ic_launcher_foreground//reaction_sleep

            ReactionType.SMILE ->
                R.drawable.ic_launcher_foreground//reaction_smile

            ReactionType.HEART ->
                R.drawable.ic_launcher_foreground//reaction_heart

            ReactionType.WATER->
                R.drawable.ic_launcher_foreground//reaction_water

            ReactionType.GROW->
                R.drawable.ic_launcher_foreground//reaction_grow

            ReactionType.LEAF->
                R.drawable.ic_launcher_foreground//reaction_leaf
        }

        Box(
            modifier = Modifier
                .size(58.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape
                )
                .background(
                    color = Color.White,
                    shape = CircleShape
                ),

            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(reactionImage),

                contentDescription = null,

                modifier = Modifier.size(48.dp)
            )
        }
    }
}