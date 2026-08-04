package com.example.assignment.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.assignment.components.*
import com.example.assignment.viewmodel.RewardType
import com.example.assignment.viewmodel.RewardViewModel

@Composable
fun RewardPage(
    navController: NavController,
    rewardViewModel: RewardViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F3D6))
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            RewardHeader()

            ProgressCard(

                currentPoint = rewardViewModel.currentPoint,

                maxPoint = rewardViewModel.maxPoint,

                status = rewardViewModel.status

            )

            RedeemButton(

                coin = rewardViewModel.currentPoint

            ) {

                navController.navigate("redeem")

            }


            Spacer(modifier = Modifier.weight(1f))

            when (rewardViewModel.rewardType) {

                RewardType.PLANT ->

                    PlantContent(
                        rewardViewModel.currentPoint
                    )

                RewardType.ANIMAL ->

                    AnimalContent(
                        rewardViewModel
                    )
            }
        }
        RewardToggleButton(
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomEnd)
                .padding(16.dp),
            currentType = rewardViewModel.rewardType
        ) {
            rewardViewModel.switchRewardType()
        }
    }
}