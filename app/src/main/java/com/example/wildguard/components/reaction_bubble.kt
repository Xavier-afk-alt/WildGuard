package com.example.wildguard.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wildguard.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


enum class ReactionType {
    SLEEP,
    SMILE,
    HEART,
    WATER,
    GROW,
    LEAF
}


// ============================================================
// ACTIVE REACTION
//
// HEART
// SMILE
// SLEEP
// WATER
// GROW
// LEAF
//
// reactionEventId allows the animation to restart even if
// the same reaction happens twice.
// ============================================================

@Composable
fun ReactionBubble(

    visible: Boolean,

    reactionType: ReactionType,

    reactionEventId: Int,

    modifier: Modifier = Modifier,

    bubbleSize: Dp = 58.dp,

    iconSize: Dp = 48.dp

) {

    val scale = remember {
        Animatable(0.5f)
    }

    val alpha = remember {
        Animatable(0f)
    }


    // ========================================================
    // RESTART ANIMATION FOR EVERY NEW REACTION
    // ========================================================

    LaunchedEffect(
        reactionEventId,
        visible
    ) {

        if (visible) {

            scale.snapTo(0.5f)
            alpha.snapTo(0f)

            coroutineScope {

                launch {

                    alpha.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(180)
                    )
                }

                launch {

                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = 280,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        }
    }


    AnimatedVisibility(

        visible = visible,

        exit = fadeOut(
            animationSpec = tween(220)
        ),

        modifier = modifier

    ) {

        Box(

            modifier = Modifier
                .size(bubbleSize)
                .graphicsLayer {

                    scaleX = scale.value
                    scaleY = scale.value
                    this.alpha = alpha.value
                },

            contentAlignment = Alignment.Center

        ) {

            ReactionBubbleContent(
                reactionType = reactionType,
                bubbleSize = bubbleSize,
                iconSize = iconSize
            )
        }
    }
}


// ============================================================
// IDLE REACTION
//
// Animal:
// SLEEP
//
// Plant:
// LEAF
//
// Rhythm:
//
// appear
// ↓
// stay
// ↓
// disappear
// ↓
// pause
// ↓
// repeat
// ============================================================

@Composable
fun IdleReactionBubble(

    visible: Boolean,

    reactionType: ReactionType,

    modifier: Modifier = Modifier,

    bubbleSize: Dp = 54.dp,

    iconSize: Dp = 44.dp

) {

    val alpha = remember {
        Animatable(0f)
    }

    val scale = remember {
        Animatable(0.72f)
    }


    LaunchedEffect(
        visible,
        reactionType
    ) {

        // ----------------------------------------------------
        // Active reaction currently showing
        // ----------------------------------------------------

        if (!visible) {

            alpha.snapTo(0f)
            scale.snapTo(0.72f)

            return@LaunchedEffect
        }


        // ----------------------------------------------------
        // IDLE LOOP
        // ----------------------------------------------------

        while (true) {


            // ================================================
            // APPEAR
            // ================================================

            coroutineScope {

                launch {

                    alpha.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(450)
                    )
                }

                launch {

                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = 450,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }


            // Stay visible
            delay(1200)


            // ================================================
            // DISAPPEAR
            // ================================================

            coroutineScope {

                launch {

                    alpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(550)
                    )
                }

                launch {

                    scale.animateTo(
                        targetValue = 0.80f,
                        animationSpec = tween(550)
                    )
                }
            }


            // Hidden pause
            delay(1700)


            // Prepare next cycle
            scale.snapTo(0.72f)
        }
    }


    if (visible) {

        Box(

            modifier = modifier
                .size(bubbleSize)
                .graphicsLayer {

                    this.alpha = alpha.value

                    scaleX = scale.value
                    scaleY = scale.value
                },

            contentAlignment = Alignment.Center

        ) {

            ReactionBubbleContent(
                reactionType = reactionType,
                bubbleSize = bubbleSize,
                iconSize = iconSize
            )
        }
    }
}


// ============================================================
// SCENE REACTION CONTROLLER
//
// Active reaction has priority.
//
// When active reaction finishes:
// idle reaction automatically returns.
// ============================================================

@Composable
fun SceneReactionBubble(

    activeVisible: Boolean,

    activeReaction: ReactionType,

    activeEventId: Int,

    idleReaction: ReactionType,

    modifier: Modifier = Modifier,

    bubbleSize: Dp = 58.dp,

    iconSize: Dp = 48.dp

) {

    Box(

        modifier = modifier.size(
            bubbleSize
        ),

        contentAlignment = Alignment.Center

    ) {


        // ====================================================
        // IDLE
        // ====================================================

        IdleReactionBubble(

            visible = !activeVisible,

            reactionType = idleReaction,

            modifier = Modifier.fillMaxSize(),

            bubbleSize = bubbleSize,

            iconSize = iconSize
        )


        // ====================================================
        // ACTIVE
        // ====================================================

        ReactionBubble(

            visible = activeVisible,

            reactionType = activeReaction,

            reactionEventId = activeEventId,

            modifier = Modifier.fillMaxSize(),

            bubbleSize = bubbleSize,

            iconSize = iconSize
        )
    }
}


// ============================================================
// COMMON BUBBLE UI
// ============================================================

@Composable
private fun ReactionBubbleContent(

    reactionType: ReactionType,

    bubbleSize: Dp,

    iconSize: Dp

) {

    val reactionImage =
        when (reactionType) {

            ReactionType.SLEEP ->
                R.drawable.reaction_sleep

            ReactionType.SMILE ->
                R.drawable.reaction_smile

            ReactionType.HEART ->
                R.drawable.reaction_heart

            ReactionType.WATER ->
                R.drawable.reaction_water

            ReactionType.GROW ->
                R.drawable.reaction_grow

            ReactionType.LEAF ->
                R.drawable.reaction_leaf
        }


    Box(

        modifier = Modifier
            .size(bubbleSize)
            .shadow(
                elevation = 5.dp,
                shape = CircleShape
            )
            .background(
                color = Color.White.copy(
                    alpha = 0.94f
                ),
                shape = CircleShape
            ),

        contentAlignment = Alignment.Center

    ) {

        Image(
            painter = painterResource(
                reactionImage
            ),
            contentDescription = null,
            modifier = Modifier.size(
                iconSize
            )
        )
    }
}