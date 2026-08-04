package com.example.assignment.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.assignment.data.News

@Composable
fun NewsCard(

    news: News

){

    val context = LocalContext.current

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable {

                val intent = Intent(

                    Intent.ACTION_VIEW,

                    Uri.parse(news.url)

                )

                context.startActivity(intent)

            }

    ){

        Column(

            modifier = Modifier.padding(16.dp)

        ){

            //replace this text into ...
            Text(

                "Image Placeholder"

            )
            //...this (same for other with same structure in this file
            /*
            Image(
                painter = painterResource(news.image),
                contentDescription = news.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            */



            Spacer(Modifier.height(12.dp))

            Text(news.title)

            Text(news.description)

        }

    }

}