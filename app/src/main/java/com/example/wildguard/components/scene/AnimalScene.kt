package com.example.wildguard.components.scene

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wildguard.R
import com.example.wildguard.viewmodel.AnimalType

private data class AnimalSceneConfig(
    @DrawableRes val animalImage: Int,
    @DrawableRes val habitatImage: Int,
    val size: Dp,
    val offsetX: Dp,
    val offsetY: Dp
)

private fun configFor(animal: AnimalType): AnimalSceneConfig = when (animal) {
    AnimalType.TIGER -> AnimalSceneConfig(
        animalImage = R.drawable.tiger_animal,
        habitatImage = R.drawable.tiger_habitat,
        size = 230.dp,
        offsetX = 40.dp,
        offsetY = 300.dp
    )

    AnimalType.FOX -> AnimalSceneConfig(
        animalImage = R.drawable.fox_animal,
        habitatImage = R.drawable.fox_habitat,
        size = 160.dp,
        offsetX = (-65).dp,
        offsetY = 230.dp
    )
    AnimalType.BEAR -> AnimalSceneConfig(
        animalImage = R.drawable.bear_animal,
        habitatImage = R.drawable.bear_habitat,
        size = 210.dp,
        offsetX = 30.dp,
        offsetY = 375.dp
    )

    AnimalType.PANDA -> AnimalSceneConfig(
        animalImage = R.drawable.panda_animal,
        habitatImage = R.drawable.panda_habitat,
        size = 205.dp,
        offsetX = (-150).dp,
        offsetY = 300.dp
    )
    AnimalType.KOALA -> AnimalSceneConfig(
        animalImage = R.drawable.koala_animal,
        habitatImage = R.drawable.koala_habitat,
        size = 130.dp,
        offsetX = 135.dp,
        offsetY = 335.dp
    )
    AnimalType.LION -> AnimalSceneConfig(
        animalImage = R.drawable.lion_animal,
        habitatImage = R.drawable.lion_habitat,
        size = 240.dp,
        offsetX = (-90).dp,
        offsetY = 280.dp
    )
    AnimalType.CAT -> AnimalSceneConfig(
        animalImage = R.drawable.cat_animal,
        habitatImage = R.drawable.cat_habitat,
        size = 175.dp,
        offsetX = (-45).dp,
        offsetY = 250.dp
    )
    AnimalType.WOLF -> AnimalSceneConfig(
        animalImage = R.drawable.wolf_animal,
        habitatImage = R.drawable.wolf_habitat,
        size = 200.dp,
        offsetX = (-100).dp,
        offsetY = 250.dp
    )
    AnimalType.DOG -> AnimalSceneConfig(
        animalImage = R.drawable.dog_animal,
        habitatImage = R.drawable.dog_habitat,
        size = 130.dp,
        offsetX = (-30).dp,
        offsetY = 325.dp
    )
}

@Composable
fun AnimalScene(
    animal: AnimalType,
    onShortPress: () -> Unit,
    onLongPress: () -> Unit,
    showPatTool: Boolean,
    modifier: Modifier = Modifier
) {
    val config = configFor(animal)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF8EAA7A))
    ) {
        Image(
            painter = painterResource(config.habitatImage),
            contentDescription = "${animal.name} habitat",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(config.animalImage),
                contentDescription = animal.name,
                modifier = Modifier
                    .size(config.size)
                    .offset(config.offsetX, config.offsetY)
                    .twoSecondQuickUse(
                        onTap = onShortPress,
                        onLongPress = onLongPress
                    )
            )
        }

        AnimatedVisibility(
            visible = showPatTool,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 35.dp),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Image(
                painter = painterResource(R.drawable.pat_hand),
                contentDescription = "Pat hand",
                modifier = Modifier.size(80.dp)
            )
        }
    }
}
