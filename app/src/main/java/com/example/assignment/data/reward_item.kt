package com.example.assignment.data

import androidx.annotation.DrawableRes

data class RewardItem(

    val id:Int,

    val title:String,

    val description:String,

    val price:Int,

    @DrawableRes
    val image:Int,

    var stock:Int,

    val purchased:Boolean=false

)