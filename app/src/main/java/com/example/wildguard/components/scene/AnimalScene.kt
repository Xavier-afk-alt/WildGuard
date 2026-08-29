package com.example.wildguard.components.scene

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wildguard.R
import com.example.wildguard.components.ReactionType
import com.example.wildguard.components.SceneReactionBubble
import com.example.wildguard.viewmodel.AnimalType


private const val REFERENCE_SCENE_WIDTH =
    393f

private const val REFERENCE_SCENE_HEIGHT =
    600f


private data class AnimalSceneConfig(

    @DrawableRes
    val animalImage: Int,

    @DrawableRes
    val habitatImage: Int,

    val size: Dp,

    val offsetX: Dp,

    val offsetY: Dp,

    val reactionSize: Dp,

    val reactionOffsetX: Dp,

    val reactionOffsetY: Dp,

    val headAnchorX: Float,

    val headAnchorY: Float
)


private fun configFor(
    animal: AnimalType
): AnimalSceneConfig {

    return when (animal) {

        AnimalType.TIGER ->
            AnimalSceneConfig(
                R.drawable.tiger_animal,
                R.drawable.tiger_habitat,
                230.dp,
                40.dp,
                300.dp,
                62.dp,
                (-10).dp,
                (-18).dp,
                0.22f,
                0.47f
            )

        AnimalType.FOX ->
            AnimalSceneConfig(
                R.drawable.fox_animal,
                R.drawable.fox_habitat,
                160.dp,
                (-65).dp,
                230.dp,
                54.dp,
                (-6).dp,
                (-14).dp,
                0.25f,
                0.45f
            )

        AnimalType.BEAR ->
            AnimalSceneConfig(
                R.drawable.bear_animal,
                R.drawable.bear_habitat,
                210.dp,
                30.dp,
                375.dp,
                60.dp,
                (-10).dp,
                (-18).dp,
                0.18f,
                0.46f
            )

        AnimalType.PANDA ->
            AnimalSceneConfig(
                R.drawable.panda_animal,
                R.drawable.panda_habitat,
                205.dp,
                (-150).dp,
                300.dp,
                58.dp,
                (-8).dp,
                (-16).dp,
                0.50f,
                0.37f
            )

        AnimalType.KOALA ->
            AnimalSceneConfig(
                R.drawable.koala_animal,
                R.drawable.koala_habitat,
                130.dp,
                135.dp,
                335.dp,
                52.dp,
                (-18).dp,
                (-12).dp,
                0.55f,
                0.34f
            )

        AnimalType.LION ->
            AnimalSceneConfig(
                R.drawable.lion_animal,
                R.drawable.lion_habitat,
                240.dp,
                (-90).dp,
                280.dp,
                62.dp,
                (-10).dp,
                (-18).dp,
                0.80f,
                0.46f
            )

        AnimalType.CAT ->
            AnimalSceneConfig(
                R.drawable.cat_animal,
                R.drawable.cat_habitat,
                175.dp,
                (-45).dp,
                250.dp,
                54.dp,
                (-8).dp,
                (-14).dp,
                0.27f,
                0.40f
            )

        AnimalType.WOLF ->
            AnimalSceneConfig(
                R.drawable.wolf_animal,
                R.drawable.wolf_habitat,
                200.dp,
                (-100).dp,
                250.dp,
                58.dp,
                (-8).dp,
                (-16).dp,
                0.80f,
                0.43f
            )

        AnimalType.DOG ->
            AnimalSceneConfig(
                R.drawable.dog_animal,
                R.drawable.dog_habitat,
                130.dp,
                (-30).dp,
                325.dp,
                52.dp,
                (-8).dp,
                (-12).dp,
                0.33f,
                0.42f
            )
    }
}


private fun clampDp(
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
fun AnimalScene(

    animal: AnimalType,

    onShortPress: () -> Unit,

    onLongPress: () -> Boolean,

    quickUseEnabled: Boolean,

    showPatTool: Boolean,

    showReaction: Boolean,

    reactionType: ReactionType,

    reactionEventId: Int,

    modifier: Modifier = Modifier

) {

    val config =
        configFor(animal)


    val density =
        LocalDensity.current


    BoxWithConstraints(

        modifier = modifier
            .fillMaxSize()
            .background(
                Color(0xFF8EAA7A)
            )

    ) {


        val widthScale =
            (
                    maxWidth.value /
                            REFERENCE_SCENE_WIDTH
                    )
                .coerceIn(
                    0.72f,
                    1.18f
                )


        val heightScale =
            (
                    maxHeight.value /
                            REFERENCE_SCENE_HEIGHT
                    )
                .coerceIn(
                    0.55f,
                    1.20f
                )


        val visualScale =
            minOf(
                widthScale,
                heightScale
            )
                .coerceIn(
                    0.68f,
                    1.08f
                )


        val animalSize =
            config.size *
                    visualScale


        val rawOffsetX =
            config.offsetX *
                    widthScale


        val rawOffsetY =
            config.offsetY *
                    heightScale


        val horizontalFreeSpace =
            (
                    maxWidth -
                            animalSize
                    ) / 2f


        val edgeAllowance =
            animalSize *
                    0.28f


        val maxHorizontalOffset =
            if (
                horizontalFreeSpace +
                edgeAllowance >
                0.dp
            ) {

                horizontalFreeSpace +
                        edgeAllowance

            } else {

                edgeAllowance
            }


        val responsiveOffsetX =
            clampDp(
                rawOffsetX,
                -maxHorizontalOffset,
                maxHorizontalOffset
            )


        val rawMaximumY =
            maxHeight -
                    animalSize +
                    animalSize *
                    0.08f


        val maximumY =
            if (
                rawMaximumY >
                0.dp
            ) {
                rawMaximumY
            } else {
                0.dp
            }


        val preferredMinimumY =
            64.dp *
                    visualScale


        val minimumY =
            if (
                preferredMinimumY <
                maximumY
            ) {
                preferredMinimumY
            } else {
                maximumY
            }


        val responsiveOffsetY =
            clampDp(
                rawOffsetY,
                minimumY,
                maximumY
            )


        val scaledReactionSize =
            clampDp(
                config.reactionSize *
                        visualScale,
                44.dp,
                68.dp
            )


        val scaledPatToolSize =
            clampDp(
                74.dp *
                        visualScale,
                52.dp,
                78.dp
            )


        val animalLeft =
            (
                    maxWidth -
                            animalSize
                    ) / 2f +
                    responsiveOffsetX


        val animalTop =
            responsiveOffsetY


        val animalRight =
            animalLeft +
                    animalSize


        val animalBottom =
            animalTop +
                    animalSize


        // Pat tool near head
        val patX =
            animalSize *
                    config.headAnchorX -
                    scaledPatToolSize / 2f


        val patY =
            animalSize *
                    config.headAnchorY -
                    scaledPatToolSize / 2f


        SceneLongPressQuickUse(

            holdIconRes =
                R.drawable.pat_hand,


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


                // Normal message only when animal touched.
                if (
                    tapX >= animalLeft &&
                    tapX <= animalRight &&
                    tapY >= animalTop &&
                    tapY <= animalBottom
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


            // Habitat
            Image(

                painter =
                    painterResource(
                        config.habitatImage
                    ),

                contentDescription =
                    "${animal.name} habitat",

                modifier =
                    Modifier.fillMaxSize(),

                contentScale =
                    ContentScale.Crop
            )


            Box(

                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.TopCenter

            ) {


                Box(

                    modifier = Modifier
                        .size(
                            animalSize
                        )
                        .offset(
                            x =
                                responsiveOffsetX,
                            y =
                                responsiveOffsetY
                        )

                ) {


                    Image(

                        painter =
                            painterResource(
                                config.animalImage
                            ),

                        contentDescription =
                            animal.name,

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
                            ReactionType.SLEEP,

                        modifier = Modifier
                            .align(
                                Alignment.TopEnd
                            )
                            .offset(
                                x =
                                    config.reactionOffsetX *
                                            visualScale,
                                y =
                                    config.reactionOffsetY *
                                            visualScale
                            ),

                        bubbleSize =
                            scaledReactionSize,

                        iconSize =
                            scaledReactionSize -
                                    10.dp
                    )


                    AnimatedVisibility(

                        visible =
                            showPatTool,

                        modifier =
                            Modifier.offset(
                                x = patX,
                                y = patY
                            ),

                        enter =
                            fadeIn(),

                        exit =
                            fadeOut()

                    ) {


                        Image(

                            painter =
                                painterResource(
                                    R.drawable.pat_hand
                                ),

                            contentDescription =
                                "Pat hand",

                            modifier =
                                Modifier.size(
                                    scaledPatToolSize
                                )
                        )
                    }
                }
            }
        }
    }
}