package com.example.assignment.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignment.components.ReportTypeCard
import com.example.assignment.navigation.Page

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportPage(
    navController: NavController
) {

    Column {

        TopAppBar(

            title = {

                Text("Report Module")

            },

            actions = {

                TextButton(

                    onClick = {

                        navController.navigate(Page.Emergency.route)

                    }

                ) {

                    Icon(
                        Icons.Default.Warning,
                        null
                    )

                    Text("Emergency")

                }

            }

        )

        LazyVerticalGrid(

            columns = GridCells.Fixed(2),

            modifier = Modifier.padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(16.dp),

            horizontalArrangement = Arrangement.spacedBy(16.dp)

        ){

            item{

                ReportTypeCard(

                    title="Injured Animal",

                    icon=Icons.Default.Pets

                ){

                    navController.navigate(
                        "report_detail/Injured Animal"
                    )

                }

            }

            item{

                ReportTypeCard(

                    title="Illegal Logging",

                    icon=Icons.Default.Forest

                ){

                    navController.navigate(
                        "report_detail/Illegal Logging"
                    )

                }

            }

            item{

                ReportTypeCard(

                    title="Forest Fire",

                    icon=Icons.Default.LocalFireDepartment

                ){

                    navController.navigate(
                        "report_detail/Forest Fire"
                    )

                }

            }

            item{

                ReportTypeCard(

                    title="Poaching",

                    icon=Icons.Default.Gavel

                ){

                    navController.navigate(
                        "report_detail/Poaching"
                    )

                }

            }

            item{

                ReportTypeCard(

                    title="Conflict",

                    icon=Icons.Default.Warning

                ){

                    navController.navigate(
                        "report_detail/Human Wildlife Conflict"
                    )

                }

            }

            item{

                ReportTypeCard(

                    title="Others",

                    icon=Icons.Default.MoreHoriz

                ){

                    navController.navigate(
                        "report_detail/Others"
                    )

                }

            }

        }

    }

}