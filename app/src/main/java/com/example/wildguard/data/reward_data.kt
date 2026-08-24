package com.example.wildguard.data

import com.example.wildguard.R

val rewardItems = mutableListOf(

    RewardItem(

        id=1,

        type = "Interaction",

        title="Pat Pat Hand",

        description="+5 animal affinity",

        price=25,

        image= R.drawable.pat_hand,

        stock=10,

        purchaseMode = PurchaseMode.MULTIPLE

    ),

    RewardItem(

        id=2,

        type = "Interaction",

        title="Watering Tool",

        description="+5 plant growth",

        price=25,

        image=R.drawable.watering_tool,

        stock=10,

        purchaseMode = PurchaseMode.MULTIPLE

    ),

    RewardItem(

        id=3,

        type = "Voucher",

        title="Grab Voucher RM5",

        description="Special voucher that can be used in Grab App for discount.",

        price=5000,

        image=R.drawable.grab_voucher,

        stock=1,

        purchaseMode = PurchaseMode.SINGLE

    ),

    RewardItem(

        id=4,

        type = "Voucher",

        title="FoodPanda Voucher RM5",

        description="Special voucher that can be used in FoodPanda App for discount.",

        price=5000,

        image=R.drawable.foodpanda_voucher,

        stock=0,

        purchaseMode = PurchaseMode.SINGLE

    ),

    RewardItem(

        id=5,

        type = "",

        title="test",

        description="",

        price=0,

        image=R.drawable.low_stock,

        stock=-1,

        purchaseMode = PurchaseMode.SINGLE

    ),

    RewardItem(

        id=6,

        type = "",

        title="test",

        description="",

        price=0,

        image=R.drawable.low_stock,

        stock=-1,

        purchaseMode = PurchaseMode.SINGLE

    ),

    RewardItem(

        id=7,

        type = "",

        title="test",

        description="",

        price=0,

        image=R.drawable.low_stock,

        stock=-1,

        purchaseMode = PurchaseMode.SINGLE

    ),

    RewardItem(

        id=8,

        type = "",

        title="test",

        description="",

        price=0,

        image=R.drawable.low_stock,

        stock=-1,

        purchaseMode = PurchaseMode.SINGLE

    ),

    RewardItem(

        id=9,

        type = "",

        title="test",

        description="",

        price=0,

        image=R.drawable.low_stock,

        stock=-1,

        purchaseMode = PurchaseMode.SINGLE

    ),

    RewardItem(

        id=10,

        type = "",

        title="test",

        description="",

        price=0,

        image=R.drawable.low_stock,

        stock=-1,

        purchaseMode = PurchaseMode.SINGLE

    )

)