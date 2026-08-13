package com.example.assignment.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.assignment.data.Animal
import com.example.assignment.data.animalList
import com.example.assignment.viewmodel.AnimalType

@Composable
fun AnimalSelector(
    selectedAnimal: AnimalType,
    expanded: Boolean,
    onToggle: () -> Unit,
    onAnimalSelected: (AnimalType) -> Unit
) {

    val selectedAnimalData =
        animalList.first { it.type == selectedAnimal }

    /*
     * =========================================================
     * MAIN SELECTOR CONTAINER
     * =========================================================
     */

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /*
             * =================================================
             * EXPANDED ANIMAL LIST
             * =================================================
             */

            AnimatedVisibility(
                visible = expanded,

                enter =
                    expandHorizontally(
                        expandFrom = Alignment.Start
                    ) + fadeIn(),

                exit =
                    shrinkHorizontally(
                        shrinkTowards = Alignment.Start
                    ) + fadeOut(),

                modifier = Modifier.weight(1f)
            ) {

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    items(animalList) { animal ->

                        AnimalSelectorItem(
                            animal = animal,
                            selected =
                                animal.type == selectedAnimal,
                            onClick = {
                                onAnimalSelected(animal.type)
                            }
                        )
                    }
                }
            }

            /*
             * =================================================
             * COLLAPSED SELECTED ANIMAL
             * =================================================
             */

            if (!expanded) {

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onToggle()
                        }
                        .padding(horizontal = 8.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Image(
                        painter =
                            painterResource(
                                selectedAnimalData.icon
                            ),
                        contentDescription =
                            selectedAnimalData.name,
                        modifier = Modifier.size(48.dp)
                    )

                    Text(
                        text = selectedAnimalData.name,
                        modifier = Modifier
                            .padding(start = 10.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            /*
             * =================================================
             * EXPAND / COLLAPSE BUTTON
             * =================================================
             */

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(
                        Color(0xFFE8F0E4)
                    )
                    .clickable {
                        onToggle()
                    },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text =
                        if (expanded) "↩"
                        else "⇄",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    color = Color(0xFF4F6547)
                )
            }
        }
    }
}

@Composable
private fun AnimalSelectorItem(
    animal: Animal,
    selected: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor =
        if (selected) {
            Color(0xFFDCEBCF)
        } else {
            Color(0xFFF5F5F5)
        }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(64.dp)
            .height(50.dp)
            .clickable {
                onClick()
            }
    ) {

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter =
                    painterResource(animal.icon),
                contentDescription =
                    animal.name,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}