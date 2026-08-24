package com.example.wildguard.data

import androidx.annotation.DrawableRes

enum class PurchaseMode {
    SINGLE,
    MULTIPLE
}

data class RewardItem(

    val id:Int,

    val type:String,

    val title:String,

    val description:String,

    val price:Int,

    @DrawableRes
    val image:Int,

    val stock:Int,

    val purchaseMode: PurchaseMode = PurchaseMode.SINGLE,

    val purchased:Boolean=false

)