package com.example.wildguard.components.scene

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wildguard.R
import com.example.wildguard.components.ReactionType
import com.example.wildguard.components.SceneReactionBubble


private const val REFERENCE_PLANT_SCENE_HEIGHT =
    600f


private fun clampPlantDp(
    value: Dp,
    minimum: Dp,
    maximum: Dp
): Dp {

    val safeMaximum =
        if (maximum < minimum) {
            minimum
        } else {
            maximum
        }


    return when {

        value < minimum ->
            minimum

        value > safeMaximum ->
            safeMaximum

        else ->
            value
    }
}


@Composable
fun PlantScene(

    plantImage: Int,

    plantSize: Dp = 180.dp,

    plantOffsetY: Dp = 35.dp,

    onShortPress: () -> Unit,

    onLongPress: () -> Boolean,

    quickUseEnabled: Boolean,

    showWateringTool: Boolean,

    showReaction: Boolean,

    reactionType: ReactionType,

    reactionEventId: Int,

    reactionSize: Dp = 58.dp,

    modifier: Modifier = Modifier

) {

    val density =
        LocalDensity.current


    BoxWithConstraints(

        modifier = modifier
            .fillMaxSize()
            .background(
                Color(0xFFFFF8D9)
            )

    ) {


        val maximumPlantWidth =
            maxWidth *
                    0.74f


        val maximumPlantHeight =
            maxHeight *
                    0.62f


        val responsivePlantSize =
            minOf(
                plantSize,
                maximumPlantWidth,
                maximumPlantHeight
            )


        val plantScale =
            if (
                plantSize.value >
                0f
            ) {

                (
                        responsivePlantSize.value /
                                plantSize.value
                        )
                    .coerceIn(
                        0.65f,
                        1f
                    )

            } else {

                1f
            }


        val heightScale =
            (
                    maxHeight.value /
                            REFERENCE_PLANT_SCENE_HEIGHT
                    )
                .coerceIn(
                    0.65f,
                    1.10f
                )


        val rawOffsetY =
            plantOffsetY *
                    heightScale


        val rawMaximumOffsetY =
            (
                    (
                            maxHeight -
                                    responsivePlantSize
                            ) / 2f
                    ) -
                    8.dp


        val maximumOffsetY =
            if (
                rawMaximumOffsetY >
                0.dp
            ) {
                rawMaximumOffsetY
            } else {
                0.dp
            }


        val responsiveOffsetY =
            clampPlantDp(
                rawOffsetY,
                0.dp,
                maximumOffsetY
            )


        val responsiveReactionSize =
            clampPlantDp(
                reactionSize *
                        plantScale,
                44.dp,
                64.dp
            )


        val responsiveWaterToolSize =
            clampPlantDp(
                80.dp *
                        plantScale,
                56.dp,
                80.dp
            )


        val plantLeft =
            (
                    maxWidth -
                            responsivePlantSize
                    ) / 2f


        val plantTop =
            (
                    maxHeight -
                            responsivePlantSize
                    ) / 2f +
                    responsiveOffsetY


        val plantRight =
            plantLeft +
                    responsivePlantSize


        val plantBottom =
            plantTop +
                    responsivePlantSize


        SceneLongPressQuickUse(

            holdIconRes =
                R.drawable.watering_tool,


            onTapAt = {
                    position: Offset ->


                val tapX =
                    with(density) {
                        position.x.toDp()
                    }


                val tapY =
                    with(density) {
                        position.y.toDp()
                    }


                if (
                    tapX >= plantLeft &&
                    tapX <= plantRight &&
                    tapY >= plantTop &&
                    tapY <= plantBottom
                ) {

                    onShortPress()
                }
            },


            onRepeatLongPress =
                onLongPress,


            quickUseEnabled =
                quickUseEnabled,


            standbyDurationMillis =
                700L,


            countdownDurationMillis =
                2000,


            modifier =
                Modifier.fillMaxSize()

        ) {


            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(
                        0.38f
                    )
                    .align(
                        Alignment.BottomCenter
                    )
                    .background(
                        Color(
                            0xFF67A900
                        )
                    )
            )


            Box(

                modifier = Modifier
                    .align(
                        Alignment.Center
                    )
                    .size(
                        responsivePlantSize
                    )
                    .offset(
                        y =
                            responsiveOffsetY
                    )

            ) {


                Image(

                    painter =
                        painterResource(
                            plantImage
                        ),

                    contentDescription =
                        "Plant",

                    modifier =
                        Modifier.fillMaxSize()
                )


                SceneReactionBubble(

                    activeVisible =
                        showReaction,

                    activeReaction =
                        reactionType,

                    activeEventId =
                        reactionEventId,

                    idleReaction =
                        ReactionType.LEAF,

                    modifier = Modifier
                        .align(
                            Alignment.TopEnd
                        )
                        .offset(
                            x =
                                (-6).dp *
                                        plantScale,
                            y =
                                (-16).dp *
                                        plantScale
                        ),

                    bubbleSize =
                        responsiveReactionSize,

                    iconSize =
                        responsiveReactionSize -
                                10.dp
                )


                AnimatedVisibility(

                    visible =
                        showWateringTool,

                    modifier = Modifier
                        .align(
                            Alignment.CenterEnd
                        )
                        .offset(
                            x =
                                12.dp *
                                        plantScale,
                            y =
                                10.dp *
                                        plantScale
                        ),

                    enter =
                        fadeIn(),

                    exit =
                        fadeOut()

                ) {


                    Image(

                        painter =
                            painterResource(
                                R.drawable.watering_tool
                            ),

                        contentDescription =
                            "Watering tool",

                        modifier =
                            Modifier.size(
                                responsiveWaterToolSize
                            )
                    )
                }
            }
        }
    }
}