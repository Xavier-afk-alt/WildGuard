package com.example.wildguard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AlertSection(

    onViewDetail: () -> Unit

) {

    Card(

        modifier = Modifier.fillMaxWidth()

    ) {

        Column(

            modifier = Modifier.padding(16.dp)

        ) {

            Text("Wildlife Alert")

            Text("Elephant detected nearby.")

            TextButton(

                onClick = onViewDetail

            ) {

                Text("View Details")

            }

        }

    }

}