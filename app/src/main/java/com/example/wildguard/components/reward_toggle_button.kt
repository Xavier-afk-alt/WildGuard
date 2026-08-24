package com.example.wildguard.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.wildguard.R
import com.example.wildguard.viewmodel.RewardType

@Composable
fun RewardToggleButton(

    modifier: Modifier,

    currentType: RewardType,

    onClick: () -> Unit,

    ) {

    Box(

        modifier = modifier
            .size(72.dp)

    ) {

        ElevatedCard(

            modifier = Modifier.fillMaxSize(),

            shape = CircleShape,

            onClick = onClick

        ) {

            Column(

                modifier = Modifier.fillMaxSize(),

                horizontalAlignment = Alignment.CenterHorizontally,

                verticalArrangement = Arrangement.Center

            ) {

                Image(
                    painter = painterResource(
                        if (currentType == RewardType.PLANT)
                            R.drawable.animal
                        else
                            R.drawable.plant
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    if (currentType == RewardType.PLANT)
                        "Animals"
                    else
                        "Plants",
                    style = MaterialTheme.typography.labelSmall
                )

            }

        }

        // Small switch indicator
        Surface(

            modifier = Modifier
                .size(18.dp)
                .align(Alignment.BottomEnd),

            shape = CircleShape,

            tonalElevation = 4.dp,

            shadowElevation = 4.dp

        ) {

            Icon(

                painter = painterResource(R.drawable.cycle),

                contentDescription = null,

                modifier = Modifier.padding(3.dp)

            )

        }

    }
}