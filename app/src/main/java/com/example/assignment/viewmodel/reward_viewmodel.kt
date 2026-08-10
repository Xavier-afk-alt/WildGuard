package com.example.assignment.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.assignment.data.RewardItem
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.assignment.data.Achievement
import com.example.assignment.data.AchievementType
import com.example.assignment.data.rewardItems as initialRewardItems
import com.example.assignment.data.achievementItems as initialAchievementItems

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

    // Spendable reward points. This balance can go up when points are earned
    // and down when a reward is purchased.
    var currentPoint by mutableIntStateOf(0)
        private set

    // Lifetime points earned. This is intentionally separate from currentPoint
    // so spending points never reduces achievement/rank progress.
    var totalEarnedPoints by mutableIntStateOf(0)
        private set

    private val _achievements =
        mutableStateListOf<Achievement>().apply {
            addAll(initialAchievementItems)
        }

    val achievements: List<Achievement>
        get() = _achievements

    private var reportCount by mutableIntStateOf(0)
    private var loginCount by mutableIntStateOf(0)
    private var guidebookCount by mutableIntStateOf(0)
    private val discoveredAnimals = mutableStateListOf<String>()

    private val unlockedInteractions = mutableListOf<String>()

    var currentInteractionIndex by mutableStateOf(0)
        private set

    var interactionMessage by mutableStateOf("")
        private set

    var showInteraction by mutableStateOf(false)
        private set

    val showInteractionButton: Boolean
        get() = unlockedInteractions.size > 1

    // Current achievement/rank threshold. It is based on lifetime earned points,
    // not the spendable balance.
    val maxPoint: Int
        get() = when {
            totalEarnedPoints < 50 -> 50
            totalEarnedPoints < 250 -> 250
            totalEarnedPoints < 500 -> 500
            totalEarnedPoints < 1_000 -> 1_000
            totalEarnedPoints < 5_000 -> 5_000
            totalEarnedPoints < 7_500 -> 7_500
            else -> 10_000
        }

    // Current achievement/rank title.
    val status: String
        get() = when {
            totalEarnedPoints < 50 -> "Starter"
            totalEarnedPoints < 250 -> "Entry-level"
            totalEarnedPoints < 500 -> "Junior"
            totalEarnedPoints < 1_000 -> "Apprentice"
            totalEarnedPoints < 5_000 -> "Expert"
            totalEarnedPoints < 7_500 -> "Veteran"
            else -> "Master"
        }

    // Colour used by the achievement banner to match the current rank.
    val statusColor: androidx.compose.ui.graphics.Color
        get() = when (status) {
            "Starter" -> androidx.compose.ui.graphics.Color(0xFFE7B0B0)
            "Entry-level" -> androidx.compose.ui.graphics.Color(0xFFE8A827)
            "Junior" -> androidx.compose.ui.graphics.Color(0xFF9BCB2E)
            "Apprentice" -> androidx.compose.ui.graphics.Color(0xFF3DBA8A)
            "Expert" -> androidx.compose.ui.graphics.Color(0xFF3DA9C8)
            "Veteran" -> androidx.compose.ui.graphics.Color(0xFF7567C8)
            else -> androidx.compose.ui.graphics.Color(0xFFB95BE3)
        }

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

    /** Add spendable points and lifetime earned points together. */
    fun earnPoints(amount: Int) {
        if (amount <= 0) return

        currentPoint += amount
        totalEarnedPoints += amount
    }

    /** Spend points. This only changes the spendable balance. */
    fun spendPoints(amount: Int): Boolean {
        if (amount <= 0 || currentPoint < amount) return false

        currentPoint -= amount
        return true
    }

    /**
     * Kept for simple testing/backward compatibility. New earning logic should
     * use earnPoints() so lifetime achievement progress stays correct.
     */
    fun updatePoint(point: Int) {
        currentPoint = point.coerceAtLeast(0)
    }

    fun recordReportSubmitted() {
        reportCount++
        refreshAchievements()
    }

    fun recordAnimalDiscovered(animalName: String) {
        if (!discoveredAnimals.contains(animalName)) {
            discoveredAnimals.add(animalName)
            refreshAchievements()
        }
    }

    fun recordLogin() {
        loginCount++
        refreshAchievements()
    }

    fun recordGuidebookCompleted() {
        guidebookCount++
        refreshAchievements()
    }

    private fun progressFor(achievement: Achievement): Int {
        return when (achievement.type) {
            AchievementType.REPORTS -> reportCount
            AchievementType.UNIQUE_ANIMALS -> discoveredAnimals.size
            AchievementType.LION_TRACKER -> if (discoveredAnimals.contains("LION")) 1 else 0
            AchievementType.LOGINS -> loginCount
            AchievementType.GUIDEBOOK -> guidebookCount
        }.coerceAtMost(achievement.target)
    }

    private fun refreshAchievements() {
        _achievements.indices.forEach { index ->
            val achievement = _achievements[index]
            val newProgress = progressFor(achievement)
            val newlyUnlocked = !achievement.unlocked && newProgress >= achievement.target

            if (newlyUnlocked) {
                // Mark the achievement first so repeated events cannot grant the
                // same reward more than once.
                _achievements[index] = achievement.copy(
                    progress = newProgress,
                    unlocked = true,
                    rewardGranted = true
                )

                earnPoints(achievement.rewardPoints)
            } else if (newProgress != achievement.progress) {
                _achievements[index] = achievement.copy(progress = newProgress)
            }
        }
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

        // Deduct only spendable points. Achievement/rank progress is unchanged.
        if (!spendPoints(totalPrice)) return

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