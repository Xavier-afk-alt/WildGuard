package com.example.wildguard.data

data class News(

    val title: String,

    val description: String,

    val url: String

)

val newsList = listOf(

    News(

        title = "Monkey, Rhino Horns and Dead Pangolins",

        description = "Facebook wildlife trading case",

        url = "https://www.google.com"

    ),

    News(

        title = "Elephant Crossing",

        description = "Wild elephant spotted",

        url = "https://www.google.com"

    ),

    News(

        title = "Forest Fire",

        description = "Illegal burning reported",

        url = "https://www.google.com"

    )

)