package com.example.assignment.data

import androidx.annotation.DrawableRes
import com.example.assignment.viewmodel.AnimalType

data class Animal(

    val type: AnimalType,

    @DrawableRes
    val icon:Int,

    @DrawableRes
    val image:Int,

    val name:String

)