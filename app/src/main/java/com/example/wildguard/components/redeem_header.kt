package com.example.wildguard.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wildguard.R

@Composable
fun RedeemHeader(

    coin:Int,

    onBack:()->Unit

){
    Row(

        modifier= Modifier
            .fillMaxWidth()
            .padding(16.dp),

        verticalAlignment= Alignment.CenterVertically

    ){

        IconButton(

            onClick=onBack

        ){

            Icon(painter = painterResource(R.drawable.back),null)

        }

        Spacer(Modifier.weight(1f))

        Text(

            "Redeem Shop",

            fontWeight= FontWeight.Bold

        )

        Spacer(Modifier.weight(1f))

        Text(

            "🪙 $coin"

        )

    }

}