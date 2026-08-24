package com.example.wildguard.data

import com.example.wildguard.navigation.Page

data class ProfileMenu(

    val title:String,

    val description:String,

    val route:String

)

val profileMenus=listOf(

    ProfileMenu(

        "My Activities",

        "View report history",

        Page.Activities.route

    ),

    ProfileMenu(

        "My Rewards",

        "Redeemed reward history",

        Page.MyRewards.route

    ),

    ProfileMenu(

        "Achievements",

        "View unlocked badges",

        Page.Achievements.route

    ),

    ProfileMenu(

        "Edit Profile",

        "Change account information",

        Page.EditProfile.route

    ),

    ProfileMenu(

        "Settings",

        "Application settings",

        Page.Settings.route

    ),

    ProfileMenu(

        "Help & Support",

        "Contact us",

        Page.Help.route

    )

)