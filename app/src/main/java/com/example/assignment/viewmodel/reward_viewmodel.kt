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
    var currentPoint by mutableIntStateOf(0)
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

    fun purchaseReward(reward: RewardItem) {

        // 1. Check stock
        if (reward.stock <= 0) {
            return
        }

        // 2. Check whether user has enough points
        if (currentPoint < reward.price) {
            return
        }

        // 3. Deduct points
        currentPoint -= reward.price

        // 4. Find the corresponding item
        val index = _rewardItems.indexOfFirst {
            it.id == reward.id
        }

        // 5. Decrease stock
        if (index != -1) {

            _rewardItems[index] =
                _rewardItems[index].copy(
                    stock = _rewardItems[index].stock - 1
                )
        }
    }
}