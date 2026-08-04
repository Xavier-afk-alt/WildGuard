package com.example.assignment.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.assignment.R
import com.example.assignment.viewmodel.RewardType

@Composable
fun RewardToggleButton(

    modifier: Modifier,

    currentType: RewardType,

    onClick:()->Unit

){

    FloatingActionButton(

        modifier=modifier,

        onClick=onClick

    ){

        if(currentType== RewardType.PLANT){

            Icon(

                painterResource(R.drawable.ic_launcher_foreground/*ic_animal*/),

                null

            )

        }else{

            Icon(

                painterResource(R.drawable.ic_launcher_foreground/*ic_plant*/),

                null

            )

        }

    }

}