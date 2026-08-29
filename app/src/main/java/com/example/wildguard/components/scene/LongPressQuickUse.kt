package com.example.wildguard.components.scene

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select
import kotlin.math.roundToInt


// ============================================================
// INVENTORY ITEM
//
// Tap        = details
// Long press = inventory quick-use
// ============================================================

@Composable
fun LongPressQuickUse(
    enabled: Boolean = true,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    val currentOnClick by
    rememberUpdatedState(onClick)

    val currentOnLongPress by
    rememberUpdatedState(onLongPress)


    Box(
        modifier = modifier.combinedClickable(
            enabled = enabled,
            onClickLabel = "View item details",
            onLongClickLabel = "Quick use item",
            onLongClick = {
                currentOnLongPress()
            },
            onClick = {
                currentOnClick()
            }
        )
    ) {

        content()
    }
}


// ============================================================
// REWARD SCENE
//
// QUICK TAP:
// caller receives exact tap position.
//
// LONG PRESS:
// can start anywhere in clear scene.
//
// quickUseEnabled = false:
// - no countdown
// - no item consumption
// - long hold does nothing
//
// Normal short tap still works.
// ============================================================

@Composable
fun SceneLongPressQuickUse(

    @DrawableRes
    holdIconRes: Int,

    onTapAt: (Offset) -> Unit,

    onRepeatLongPress: () -> Boolean,

    quickUseEnabled: Boolean,

    modifier: Modifier = Modifier,

    standbyDurationMillis: Long = 700L,

    countdownDurationMillis: Int = 2000,

    content: @Composable BoxScope.() -> Unit

) {

    val currentOnTapAt by
    rememberUpdatedState(onTapAt)

    val currentOnRepeatLongPress by
    rememberUpdatedState(onRepeatLongPress)


    var showProgress by remember {
        mutableStateOf(false)
    }


    var pressPosition by remember {
        mutableStateOf(Offset.Zero)
    }


    var containerSize by remember {
        mutableStateOf(IntSize.Zero)
    }


    val progress = remember {
        Animatable(0f)
    }


    val indicatorSize =
        94.dp


    val density =
        LocalDensity.current


    val indicatorSizePx =
        with(density) {
            indicatorSize.roundToPx()
        }


    Box(

        modifier = modifier
            .onSizeChanged {
                containerSize = it
            }
            .pointerInput(
                quickUseEnabled,
                standbyDurationMillis,
                countdownDurationMillis
            ) {

                detectTapGestures(

                    onPress = { position ->

                        pressPosition =
                            position


                        coroutineScope {


                            // Finger release
                            val releaseSignal =
                                async {
                                    tryAwaitRelease()
                                }


                            // Invisible standby
                            val standbySignal =
                                async {

                                    delay(
                                        standbyDurationMillis
                                    )

                                    true
                                }


                            // =====================================
                            // QUICK TAP VS LONG HOLD
                            // =====================================

                            val startLongPress =
                                select<Boolean> {


                                    // QUICK TAP
                                    releaseSignal.onAwait {
                                            releasedNormally ->


                                        standbySignal.cancel()


                                        if (releasedNormally) {

                                            currentOnTapAt(
                                                position
                                            )
                                        }


                                        false
                                    }


                                    // LONG HOLD
                                    standbySignal.onAwait {
                                        true
                                    }
                                }


                            // Quick tap finished.
                            if (!startLongPress) {

                                return@coroutineScope
                            }


                            // =====================================
                            // SCENE NOT CLEAR
                            //
                            // Inventory open / selector open /
                            // details dialog open.
                            //
                            // Do NOT show countdown.
                            // Do NOT consume item.
                            // =====================================

                            if (!quickUseEnabled) {


                                if (
                                    !releaseSignal.isCompleted
                                ) {

                                    releaseSignal.await()
                                }


                                standbySignal.cancel()

                                return@coroutineScope
                            }


                            // =====================================
                            // CLEAR SCENE:
                            // LONG PRESS STARTS
                            // =====================================

                            showProgress =
                                true


                            try {


                                while (true) {


                                    progress.snapTo(
                                        0f
                                    )


                                    val countdownSignal =
                                        async {


                                            progress.animateTo(

                                                targetValue =
                                                    1f,

                                                animationSpec =
                                                    tween(

                                                        durationMillis =
                                                            countdownDurationMillis,

                                                        easing =
                                                            LinearEasing
                                                    )
                                            )


                                            true
                                        }


                                    // Wait for release or completion.
                                    val completed =
                                        select<Boolean> {


                                            releaseSignal.onAwait {


                                                countdownSignal
                                                    .cancel()


                                                false
                                            }


                                            countdownSignal.onAwait {
                                                true
                                            }
                                        }


                                    if (!completed) {
                                        break
                                    }


                                    if (
                                        releaseSignal.isCompleted
                                    ) {
                                        break
                                    }


                                    // Use ONE item.
                                    val consumed =
                                        currentOnRepeatLongPress()


                                    if (!consumed) {


                                        showProgress =
                                            false


                                        progress.snapTo(
                                            0f
                                        )


                                        if (
                                            !releaseSignal.isCompleted
                                        ) {

                                            releaseSignal.await()
                                        }


                                        break
                                    }


                                    // Successful:
                                    //
                                    // loop again if still holding.
                                }


                            } finally {


                                showProgress =
                                    false


                                progress.snapTo(
                                    0f
                                )


                                standbySignal.cancel()
                            }
                        }
                    }
                )
            }

    ) {


        // Actual scene
        content()


        val maximumX =
            (
                    containerSize.width -
                            indicatorSizePx
                    ).coerceAtLeast(0)


        val maximumY =
            (
                    containerSize.height -
                            indicatorSizePx
                    ).coerceAtLeast(0)


        val indicatorX =
            (
                    pressPosition.x -
                            indicatorSizePx / 2f
                    )
                .roundToInt()
                .coerceIn(
                    0,
                    maximumX
                )


        val indicatorY =
            (
                    pressPosition.y -
                            indicatorSizePx / 2f
                    )
                .roundToInt()
                .coerceIn(
                    0,
                    maximumY
                )


        AnimatedVisibility(

            visible =
                showProgress,

            modifier = Modifier
                .align(
                    Alignment.TopStart
                )
                .offset {

                    IntOffset(
                        x = indicatorX,
                        y = indicatorY
                    )
                },

            enter =
                fadeIn(),

            exit =
                fadeOut()

        ) {


            Box(

                modifier =
                    Modifier.size(
                        indicatorSize
                    ),

                contentAlignment =
                    Alignment.Center

            ) {


                // White halo
                Box(

                    modifier = Modifier
                        .size(90.dp)
                        .shadow(
                            elevation = 5.dp,
                            shape = CircleShape
                        )
                        .background(
                            color =
                                Color.White.copy(
                                    alpha = 0.38f
                                ),
                            shape =
                                CircleShape
                        )
                )


                // Full white outline
                CircularProgressIndicator(

                    progress =
                        { 1f },

                    modifier =
                        Modifier.size(
                            84.dp
                        ),

                    strokeWidth =
                        10.dp,

                    color =
                        Color.White.copy(
                            alpha = 0.96f
                        )
                )


                // Green countdown
                CircularProgressIndicator(

                    progress =
                        { progress.value },

                    modifier =
                        Modifier.size(
                            84.dp
                        ),

                    strokeWidth =
                        6.dp,

                    color =
                        Color(0xFF2E7D32)
                )


                // Tool popup
                Surface(

                    modifier = Modifier
                        .size(58.dp)
                        .shadow(
                            elevation = 5.dp,
                            shape = CircleShape
                        ),

                    shape =
                        CircleShape,

                    color =
                        Color.White.copy(
                            alpha = 0.96f
                        )

                ) {


                    Box(

                        modifier =
                            Modifier.fillMaxSize(),

                        contentAlignment =
                            Alignment.Center

                    ) {


                        Image(

                            painter =
                                painterResource(
                                    holdIconRes
                                ),

                            contentDescription =
                                "Quick use item",

                            modifier =
                                Modifier.size(
                                    40.dp
                                )
                        )
                    }
                }
            }
        }
    }
}