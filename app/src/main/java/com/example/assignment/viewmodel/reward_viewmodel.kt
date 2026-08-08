package com.example.assignment.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.assignment.data.RewardItem
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.assignment.data.rewardItems as initialRewardItems

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

    private val _rewardItems =
        mutableStateListOf<RewardItem>().apply {
            addAll(initialRewardItems)
        }

    val rewardItems: List<RewardItem>
        get() = _rewardItems

    // Current display page
    var rewardType by mutableStateOf(RewardType.PLANT)
        private set

    // Current selected animal
    var selectedAnimal by mutableStateOf(AnimalType.TIGER)
        private set

    // User points
    var currentPoint by mutableIntStateOf(10000) //should be 0
        private set

    private val unlockedInteractions = mutableListOf<String>()

    var currentInteractionIndex by mutableStateOf(0)
        private set

    var interactionMessage by mutableStateOf("")
        private set

    var showInteraction by mutableStateOf(false)
        private set

    val showInteractionButton: Boolean
        get() = unlockedInteractions.size > 1

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

    fun unlockInteraction(message: String) {

        if (!unlockedInteractions.contains(message)) {

            unlockedInteractions.add(message)

        }

        currentInteractionIndex = unlockedInteractions.lastIndex

        interactionMessage = message

        showInteraction = true

    }

    fun nextInteraction() {

        if (unlockedInteractions.isEmpty()) return

        currentInteractionIndex++

        if (currentInteractionIndex >= unlockedInteractions.size)

            currentInteractionIndex = 0

        interactionMessage =

            unlockedInteractions[currentInteractionIndex]

    }

    fun purchaseReward(
        reward: RewardItem,
        quantity: Int
    ) {

        if (quantity <= 0) {
            return
        }

        // Low stock / unavailable
        if (reward.stock < 0) {
            return
        }

        // Sold out
        if (reward.stock == 0) {
            return
        }

        // Quantity exceeds stock
        if (quantity > reward.stock) {
            return
        }

        val totalPrice =
            reward.price * quantity

        // Not enough points
        if (currentPoint < totalPrice) {
            return
        }

        // Deduct points
        currentPoint -= totalPrice

        // Find item
        val index =
            _rewardItems.indexOfFirst {
                it.id == reward.id
            }

        if (index != -1) {

            val oldReward =
                _rewardItems[index]

            _rewardItems[index] =
                oldReward.copy(

                    stock =
                        oldReward.stock - quantity,

                    purchased = true

                )
        }
    }
}