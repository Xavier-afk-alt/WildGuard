package com.example.assignment.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.assignment.R

@Composable
fun PlantContent(currentPoint:Int

) {

    val image = when {

        currentPoint < 50 ->
            R.drawable.ic_launcher_foreground//seed

        currentPoint < 500 ->
            R.drawable.ic_launcher_foreground//small_tree

        currentPoint < 3000 ->
            R.drawable.ic_launcher_foreground//medium_tree

        else ->
            R.drawable.ic_launcher_foreground//big_tree

    }

    Image(

        painterResource(image),

        null

    )
}
/*){


    Column(

        modifier=Modifier.fillMaxWidth(),

        horizontalAlignment=Alignment.CenterHorizontally

    ){

        Image(

            painter=painterResource(R.drawable.ic_launcher_foreground/*R.drawable.seed*/),

            contentDescription=null,

            modifier=Modifier.size(90.dp)

        )

        Spacer(modifier=Modifier.height(30.dp))

        Text(

            "It's a nice day to start engaging in\nprotect the wildlife."

        )

    }

}*/