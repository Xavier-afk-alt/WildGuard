package com.example.wildguard.data

import androidx.annotation.DrawableRes
import com.example.wildguard.viewmodel.AnimalType

data class Animal(

    val type: AnimalType,

    @DrawableRes
    val icon:Int,

    @DrawableRes
    val image:Int,

    val name:String

)