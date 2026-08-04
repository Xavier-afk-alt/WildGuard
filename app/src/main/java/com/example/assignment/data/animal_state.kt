package com.example.assignment.data

import com.example.assignment.viewmodel.AnimalType

data class AnimalState(

    val type: AnimalType,

    var affection:Int,

    var sleeping:Boolean,

    var fedToday:Boolean,

    var lastInteraction:String

)