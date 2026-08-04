package com.example.assignment.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.example.assignment.R
import com.example.assignment.data.RewardItem

@Composable
fun RewardCard(

    reward: RewardItem,

    onPurchase:(RewardItem)->Unit

){

    Card{

        Column(

            horizontalAlignment= Alignment.CenterHorizontally

        ){

            Image(

                painter = painterResource(reward.image),

                contentDescription = reward.title

            )

            Text(reward.title)

            Text(reward.description)

            Text("🪙 ${reward.price}")

            Button(
                onClick = {onPurchase(reward)},
                enabled = reward.stock>0

            ){

                Text(

                    if(reward.stock>0)

                        "Purchase"

                    else

                        "Sold Out"

                )

            }

        }

    }

}