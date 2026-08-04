package com.example.assignment.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RedeemButton(

    coin:Int,

    onClick: () -> Unit

){

    Button(

        onClick=onClick,

        modifier=Modifier
            .padding(top=10.dp)
            .padding(horizontal=70.dp)
            .fillMaxWidth(),

        shape=RoundedCornerShape(50.dp),

        colors=ButtonDefaults.buttonColors(

            containerColor=Color(0xFFD98AF3)

        )

    ){

        Text("🪙 $coin")

        Spacer(modifier=Modifier.width(10.dp))

        Text("REDEEM")

        Spacer(modifier=Modifier.width(6.dp))

        Icon(

            Icons.Default.ShoppingCart,

            null

        )

    }

}