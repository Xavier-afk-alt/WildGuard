package com.example.wildguard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wildguard.data.Animal
import com.example.wildguard.data.animalList
import com.example.wildguard.viewmodel.AnimalType
import kotlinx.coroutines.launch


@Composable
fun AnimalSelector(

    selectedAnimal: AnimalType,

    expanded: Boolean,

    onToggle: () -> Unit,

    onAnimalSelected: (AnimalType) -> Unit

) {

    val selectedAnimalData =
        animalList.first {
            it.type == selectedAnimal
        }


    val animalListState =
        rememberLazyListState()


    val scope =
        rememberCoroutineScope()


    val selectedIndex =
        animalList.indexOfFirst {
            it.type == selectedAnimal
        }


    // ============================================================
    // OPEN LIST NEAR CURRENT ANIMAL
    // ============================================================

    LaunchedEffect(
        expanded,
        selectedAnimal
    ) {

        if (
            expanded &&
            selectedIndex >= 0
        ) {

            val targetStart =
                (
                        selectedIndex - 2
                        )
                    .coerceAtLeast(0)


            animalListState
                .animateScrollToItem(
                    targetStart
                )
        }
    }


    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 3.dp
            ),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    Color.White.copy(
                        alpha = 0.84f
                    )
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),

        shape =
            RoundedCornerShape(
                16.dp
            )

    ) {


        // ========================================================
        // EXPANDED
        //
        // IMPORTANT:
        //
        // Arrows are now REAL items beside LazyRow.
        //
        // No large start/end padding.
        //
        // Therefore:
        //
        // ‹ | animals animals animals | › | ↩
        // ========================================================

        if (expanded) {


            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        64.dp
                    )
                    .padding(
                        horizontal = 3.dp,
                        vertical = 5.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {


                // =================================================
                // LEFT ARROW
                //
                // Only takes space when actually needed.
                // =================================================

                if (
                    animalListState
                        .canScrollBackward
                ) {


                    SelectorControlButton(

                        text = "‹",

                        modifier =
                            Modifier.size(
                                34.dp
                            ),

                        onClick = {


                            scope.launch {


                                val target =
                                    (
                                            animalListState
                                                .firstVisibleItemIndex - 1
                                            )
                                        .coerceAtLeast(
                                            0
                                        )


                                animalListState
                                    .animateScrollToItem(
                                        target
                                    )
                            }
                        }
                    )
                }


                // =================================================
                // ANIMAL LIST
                //
                // No large blank padding anymore.
                // =================================================

                LazyRow(

                    state =
                        animalListState,

                    modifier = Modifier
                        .weight(
                            1f
                        )
                        .fillMaxHeight(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            6.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {


                    items(

                        items =
                            animalList,

                        key = {
                            it.type
                        }

                    ) { animal ->


                        AnimalSelectorItem(

                            animal =
                                animal,

                            selected =
                                animal.type ==
                                        selectedAnimal,

                            onClick = {

                                onAnimalSelected(
                                    animal.type
                                )
                            }
                        )
                    }
                }


                // =================================================
                // RIGHT ARROW
                //
                // Directly beside last visible animal.
                // =================================================

                if (
                    animalListState
                        .canScrollForward
                ) {


                    SelectorControlButton(

                        text = "›",

                        modifier =
                            Modifier.size(
                                34.dp
                            ),

                        onClick = {


                            scope.launch {


                                val target =
                                    (
                                            animalListState
                                                .firstVisibleItemIndex + 1
                                            )
                                        .coerceAtMost(
                                            animalList.lastIndex
                                        )


                                animalListState
                                    .animateScrollToItem(
                                        target
                                    )
                            }
                        }
                    )
                }


                // Small spacing only.
                Box(
                    modifier =
                        Modifier.width(
                            3.dp
                        )
                )


                // =================================================
                // RETURN BUTTON
                // =================================================

                SelectorControlButton(

                    text = "↩",

                    modifier =
                        Modifier.size(
                            40.dp
                        ),

                    emphasized =
                        true,

                    onClick =
                        onToggle
                )
            }


        } else {


            // =====================================================
            // COLLAPSED
            // =====================================================

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        64.dp
                    )
                    .clickable {
                        onToggle()
                    }
                    .padding(

                        start =
                            18.dp,

                        end =
                            8.dp
                    ),

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

                    modifier =
                        Modifier.size(
                            48.dp
                        )
                )


                Text(

                    text =
                        selectedAnimalData.name,

                    modifier =
                        Modifier
                            .padding(
                                start = 10.dp
                            )
                            .weight(
                                1f
                            ),

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        18.sp
                )


                SelectorControlButton(

                    text = "⇄",

                    modifier =
                        Modifier.size(
                            40.dp
                        ),

                    emphasized =
                        true,

                    onClick =
                        onToggle
                )
            }
        }
    }
}


// ============================================================
// SELECTOR CONTROL
//
// Used for:
// ‹
// ›
// ↩
// ⇄
// ============================================================

@Composable
private fun SelectorControlButton(

    text: String,

    modifier: Modifier,

    emphasized: Boolean = false,

    onClick: () -> Unit

) {


    Box(

        modifier = modifier
            .clip(
                RoundedCornerShape(
                    11.dp
                )
            )
            .background(

                if (emphasized) {

                    Color(
                        0xFFE8F0E4
                    )

                } else {

                    Color(
                        0xFFF4F7F2
                    )
                }
            )
            .clickable {
                onClick()
            },

        contentAlignment =
            Alignment.Center

    ) {


        Text(

            text =
                text,

            fontWeight =
                FontWeight.Bold,

            fontSize =

                if (emphasized) {

                    22.sp

                } else {

                    27.sp
                },

            color =
                Color(
                    0xFF4F6547
                )
        )
    }
}


// ============================================================
// ANIMAL ITEM
// ============================================================

@Composable
private fun AnimalSelectorItem(

    animal: Animal,

    selected: Boolean,

    onClick: () -> Unit

) {


    val backgroundColor =

        if (selected) {

            Color(
                0xFFDCEBCF
            )

        } else {

            Color(
                0xFFF7F7F7
            )
        }


    Card(

        modifier = Modifier
            .width(
                58.dp
            )
            .height(
                50.dp
            )
            .clickable {
                onClick()
            },

        colors =
            CardDefaults.cardColors(

                containerColor =
                    backgroundColor
            ),

        shape =
            RoundedCornerShape(
                12.dp
            ),

        elevation =
            CardDefaults.cardElevation(

                defaultElevation =

                    if (selected) {

                        3.dp

                    } else {

                        1.dp
                    }
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
                        animal.icon
                    ),

                contentDescription =
                    animal.name,

                modifier =
                    Modifier.size(
                        39.dp
                    )
            )
        }
    }
}