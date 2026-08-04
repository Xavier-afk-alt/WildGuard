package com.example.assignment.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.assignment.components.RedeemHeader
import com.example.assignment.components.RewardCard
import com.example.assignment.data.rewardItems
import com.example.assignment.viewmodel.RewardViewModel
import androidx.compose.foundation.lazy.grid.items

@Composable
fun RedeemShopPage(

    navController: NavController,

    rewardViewModel: RewardViewModel

){
    Column {

        RedeemHeader(
            coin = rewardViewModel.currentPoint,
            onBack = {navController.popBackStack()}
        )

        LazyVerticalGrid(

            columns = GridCells.Fixed(2)

        ){

            items(rewardItems) { reward ->

                RewardCard(
                    reward = reward,
                    onPurchase = {
                        rewardViewModel.purchaseReward(reward)
                    }
                )

            }

        }

    }

}