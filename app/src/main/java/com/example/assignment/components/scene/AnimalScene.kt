package com.example.assignment.components.scene

import androidx.annotation.DrawableRes
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
import com.example.assignment.viewmodel.AnimalType

private data class AnimalSceneConfig(
    @DrawableRes val image: Int,
    val size: Dp,
    val offsetX: Dp,
    val offsetY: Dp
)

private fun configFor(animal: AnimalType): AnimalSceneConfig = when (animal) {
    AnimalType.TIGER -> AnimalSceneConfig(R.drawable.tiger_animal, 230.dp, 0.dp, 72.dp)
    AnimalType.FOX -> AnimalSceneConfig(R.drawable.fox_animal, 190.dp, 0.dp, 105.dp)
    AnimalType.BEAR -> AnimalSceneConfig(R.drawable.bear_animal, 210.dp, -15.dp, 85.dp)
    AnimalType.PANDA -> AnimalSceneConfig(R.drawable.panda_animal, 205.dp, 18.dp, 95.dp)
    AnimalType.KOALA -> AnimalSceneConfig(R.drawable.koala_animal, 190.dp, 45.dp, 55.dp)
    AnimalType.LION -> AnimalSceneConfig(R.drawable.lion_animal, 240.dp, -15.dp, 85.dp)
    AnimalType.CAT -> AnimalSceneConfig(R.drawable.cat_animal, 195.dp, 35.dp, 115.dp)
    AnimalType.WOLF -> AnimalSceneConfig(R.drawable.wolf_animal, 215.dp, -20.dp, 95.dp)
    AnimalType.DOG -> AnimalSceneConfig(R.drawable.dog_animal, 220.dp, 5.dp, 115.dp)
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
        // Simple scene layers. The transparent animal PNG remains reusable and
        // the placement can be tuned independently for every animal.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF8EAA7A))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.30f)
                .align(Alignment.BottomCenter)
                .background(Color(0xFF5D5142))
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(config.image),
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
