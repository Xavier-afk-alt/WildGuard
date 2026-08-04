package com.example.assignment.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.assignment.data.RewardItem

enum class RewardType {
    PLANT,
    ANIMAL
}

enum class AnimalType {
    TIGER,
    FOX,
    BEAR,
    PANDA,
    KOALA,
    LION,
    CAT,
    WOLF,
    DOG
}

class RewardViewModel : ViewModel() {

    var rewardItems by mutableStateOf(
        mutableStateListOf<RewardItem>()
    )
        private set
    // Current display page
    var rewardType by mutableStateOf(RewardType.PLANT)
        private set

    // Current selected animal
    var selectedAnimal by mutableStateOf(AnimalType.FOX)
        private set

    // User points
    var currentPoint by mutableIntStateOf(0)
        private set

    var interactionMessage by mutableStateOf(
        "Thanks for protecting me!"
    )
        private set

    var showInteraction by mutableStateOf(true)
        private set

    // Maximum points for current level
    var maxPoint by mutableIntStateOf(50)
        private set

    // Current title
    var status by mutableStateOf("Starter")
        private set

    fun switchRewardType() {
        rewardType =
            if (rewardType == RewardType.PLANT)
                RewardType.ANIMAL
            else
                RewardType.PLANT
    }

    fun selectAnimal(animal: AnimalType) {
        selectedAnimal = animal
    }

    fun updatePoint(point: Int) {
        currentPoint = point
    }

    fun updateInteraction(message: String) {
        interactionMessage = message
    }

    fun completeObjective(objective: String) {

        when (objective) {

            "report" -> {
                interactionMessage =
                    "Thanks for protecting me!"
            }

            "plant" -> {
                interactionMessage =
                    "The forest feels healthier!"
            }

            "login" -> {
                interactionMessage =
                    "Welcome back!"
            }

            "redeem" -> {
                interactionMessage =
                    "I love your gift!"
            }

            else -> {
                interactionMessage =
                    "Let's continue protecting wildlife!"
            }
        }
    }

    fun purchaseReward(

        reward:RewardItem

    ){

        if(currentPoint>=reward.price
            && reward.stock>0){

            currentPoint-=reward.price

            reward.stock--

        }

    }
}