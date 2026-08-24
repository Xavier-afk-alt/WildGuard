package com.example.wildguard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun PageIndicator(

    currentPage:Int,

    pageCount:Int

){

    Row(

        modifier = Modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.Center

    ){

        repeat(pageCount){ index->

            Box(

                modifier = Modifier

                    .padding(4.dp)

                    .size(

                        if(index==currentPage)

                            10.dp

                        else

                            8.dp

                    )

                    .background(

                        if(index==currentPage)

                            Color.Black

                        else

                            Color.LightGray,

                        CircleShape

                    )

            )

        }

    }

}