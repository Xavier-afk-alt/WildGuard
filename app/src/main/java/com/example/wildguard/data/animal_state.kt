package com.example.wildguard.data

import com.example.wildguard.viewmodel.AnimalType

data class AnimalState(

    val type: AnimalType,

    var affection:Int,

    var sleeping:Boolean,

    var fedToday:Boolean,

    var lastInteraction:String

)