package com.example.assignment.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.assignment.R

@Composable
fun PageTopBar(

    title: String,

    onBackClick: () -> Unit

) {

    Row(

        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),

        verticalAlignment = Alignment.CenterVertically

    ) {

        IconButton(

            onClick = onBackClick

        ) {

            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = "Back"
            )

        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(

            text = title,

            style = MaterialTheme.typography.titleLarge

        )

    }

}