package com.example.wildguard.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.wildguard.components.ReactionType
import com.example.wildguard.data.Achievement
import com.example.wildguard.data.AchievementType
import com.example.wildguard.data.PointEarningEvent
import com.example.wildguard.data.PointSource
import com.example.wildguard.data.PurchaseResult
import com.example.wildguard.data.RewardItem
import com.example.wildguard.data.achievementItems as initialAchievementItems
import com.example.wildguard.data.rewardItems as initialRewardItems
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random


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


    // ============================================================
    // REWARD ITEMS
    // ============================================================

    private val _rewardItems =
        mutableStateListOf<RewardItem>().apply {

            addAll(
                initialRewardItems
            )
        }


    val rewardItems: List<RewardItem>
        get() = _rewardItems


    // ============================================================
    // CURRENT REWARD TYPE
    //
    // PLANT / ANIMAL
    // ============================================================

    var rewardType by
    mutableStateOf(
        RewardType.PLANT
    )
        private set


    // ============================================================
    // CURRENT SELECTED ANIMAL
    // ============================================================

    var selectedAnimal by
    mutableStateOf(
        AnimalType.TIGER
    )
        private set


    // ============================================================
    // CURRENT SPENDABLE POINTS
    // ============================================================

    var currentPoint by
    mutableIntStateOf(
        5000
    )
        private set


    // ============================================================
    // PLANT GROWTH POINTS
    //
    // This is separate from currentPoint.
    //
    // currentPoint      = spendable balance
    // plantGrowthPoint  = non-decreasing plant progression
    //
    // The plant starts at the same 5000-point stage as the
    // current demo balance. Earning points increases this value,
    // but purchases do NOT reduce it.
    //
    // This is also the value that should later be persisted
    // to Supabase for plant growth.
    // ============================================================

    var plantGrowthPoint by
    mutableIntStateOf(
        currentPoint
    )
        private set


    // ============================================================
    // LIFETIME EARNED POINTS
    // ============================================================

    var totalEarnedPoints by
    mutableIntStateOf(
        0
    )
        private set


    // ============================================================
    // POINT EVENTS
    // ============================================================

    private val _pendingPointEvents =
        mutableStateListOf<PointEarningEvent>()


    val pendingPointEvents:
            List<PointEarningEvent>
        get() = _pendingPointEvents


    private var lastDailyLoginDate by
    mutableStateOf<String?>(
        null
    )


    // ============================================================
    // ACHIEVEMENTS
    // ============================================================

    private val _achievements =
        mutableStateListOf<Achievement>()
            .apply {

                addAll(
                    initialAchievementItems
                )
            }


    val achievements:
            List<Achievement>
        get() = _achievements


    private var reportCount by
    mutableIntStateOf(0)


    private var loginCount by
    mutableIntStateOf(0)


    private var guidebookCount by
    mutableIntStateOf(0)


    private val discoveredAnimals =
        mutableStateListOf<String>()


    // ============================================================
    // INTERACTION MESSAGES
    // ============================================================

    private val unlockedInteractions =
        mutableListOf<String>()


    var currentInteractionIndex by
    mutableStateOf(0)
        private set


    var interactionMessage by
    mutableStateOf("")
        private set


    var showInteraction by
    mutableStateOf(false)
        private set


    // ============================================================
    // PART 3
    // REACTION STATE
    // ============================================================

    var showReaction by
    mutableStateOf(false)
        private set


    var currentReaction by
    mutableStateOf(
        ReactionType.SLEEP
    )
        private set


    // Every reaction increases this value.
    //
    // This is important when the same reaction
    // happens repeatedly:
    //
    // HEART -> HEART
    // WATER -> WATER
    //
    // The value still changes, so the animation
    // can restart.
    var reactionEventId by
    mutableIntStateOf(0)
        private set


    val showInteractionButton: Boolean
        get() =
            unlockedInteractions.size > 1


    // ============================================================
    // ACHIEVEMENT MAX POINT
    // ============================================================

    val maxPoint: Int
        get() =
            when {

                totalEarnedPoints < 50 ->
                    50

                totalEarnedPoints < 250 ->
                    250

                totalEarnedPoints < 500 ->
                    500

                totalEarnedPoints < 1_000 ->
                    1_000

                totalEarnedPoints < 5_000 ->
                    5_000

                totalEarnedPoints < 7_500 ->
                    7_500

                else ->
                    10_000
            }


    // ============================================================
    // ACHIEVEMENT STATUS
    // ============================================================

    val status: String
        get() =
            when {

                totalEarnedPoints < 50 ->
                    "Starter"

                totalEarnedPoints < 250 ->
                    "Entry-level"

                totalEarnedPoints < 500 ->
                    "Junior"

                totalEarnedPoints < 1_000 ->
                    "Apprentice"

                totalEarnedPoints < 5_000 ->
                    "Expert"

                totalEarnedPoints < 7_500 ->
                    "Veteran"

                else ->
                    "Master"
            }


    // ============================================================
    // STATUS COLOR
    // ============================================================

    val statusColor: Color
        get() =
            when (status) {

                "Starter" ->
                    Color(0xFFE7B0B0)

                "Entry-level" ->
                    Color(0xFFE8A827)

                "Junior" ->
                    Color(0xFF9BCB2E)

                "Apprentice" ->
                    Color(0xFF3DBA8A)

                "Expert" ->
                    Color(0xFF3DA9C8)

                "Veteran" ->
                    Color(0xFF7567C8)

                else ->
                    Color(0xFFB95BE3)
            }


    // ============================================================
    // SWITCH:
    //
    // PLANT <-> ANIMAL
    //
    // Hide active reaction before changing scene.
    //
    // Idle reaction will automatically appear
    // in the new scene.
    // ============================================================

    fun switchRewardType() {

        hideReaction()


        rewardType =

            if (
                rewardType ==
                RewardType.PLANT
            ) {

                RewardType.ANIMAL

            } else {

                RewardType.PLANT
            }
    }


    // ============================================================
    // SELECT ANIMAL
    //
    // Also hide active reaction so a Tiger reaction
    // does not remain when switching to another animal.
    // ============================================================

    fun selectAnimal(
        animal: AnimalType
    ) {

        hideReaction()

        selectedAnimal =
            animal
    }


    // ============================================================
    // EARN POINTS
    // ============================================================

    fun earnPoints(

        amount: Int,

        source:
        PointSource =
            PointSource.OTHER,

        title: String =
            "Points earned",

        description: String =
            "",

        eventId: String =
            UUID.randomUUID()
                .toString()

    ) {

        if (amount <= 0) {
            return
        }


        currentPoint +=
            amount


        // Plant growth follows earned progress, not spendable
        // balance. It never decreases when rewards are purchased.
        plantGrowthPoint +=
            amount


        totalEarnedPoints +=
            amount


        _pendingPointEvents.add(

            PointEarningEvent(

                id =
                    eventId,

                source =
                    source,

                points =
                    amount,

                title =
                    title,

                description =
                    description

            )
        )
    }


    // ============================================================
    // SPEND POINTS
    // ============================================================

    fun spendPoints(
        amount: Int
    ): Boolean {

        if (
            amount <= 0 ||
            currentPoint < amount
        ) {

            return false
        }


        currentPoint -=
            amount


        return true
    }


    // ============================================================
    // UPDATE POINT
    //
    // Kept for testing / existing code.
    // ============================================================

    fun updatePoint(
        point: Int
    ) {

        currentPoint =
            point.coerceAtLeast(
                0
            )


        // Testing/loading a higher point value may advance the
        // plant, but a lower value must never make it regress.
        plantGrowthPoint =
            maxOf(
                plantGrowthPoint,
                currentPoint
            )
    }


    // ============================================================
    // DAILY LOGIN
    // ============================================================

    fun recordDailyLogin() {

        val today =
            LocalDate
                .now()
                .toString()


        if (
            lastDailyLoginDate ==
            today
        ) {

            return
        }


        lastDailyLoginDate =
            today


        loginCount++


        earnPoints(

            amount =
                10,

            source =
                PointSource.DAILY_LOGIN,

            title =
                "Daily Visit",

            description =
                "Thanks for visiting WildGuard today.",

            eventId =
                "daily_login_$today"

        )


        refreshAchievements()
    }


    fun recordLogin() {

        recordDailyLogin()
    }


    // ============================================================
    // REPORT
    // ============================================================

    fun recordReportSubmitted() {

        reportCount++


        earnPoints(

            amount =
                50,

            source =
                PointSource.REPORT,

            title =
                "Report Submitted",

            description =
                "Thank you for helping protect wildlife."

        )


        refreshAchievements()

        // Report -> Reward interaction.
        // The message is queued for the Reward scene and appears
        // when the user opens the Reward module after submitting.
        unlockInteraction(
            "Thanks for protecting me! Your report helped the wildlife."
        )
    }


    // ============================================================
    // ANIMAL SIGHTING
    // ============================================================

    fun recordAnimalSightingSubmitted() {

        earnPoints(

            amount =
                50,

            source =
                PointSource.ANIMAL_SIGHTING,

            title =
                "Animal Sighting",

            description =
                "Your animal sighting was submitted successfully."

        )
    }


    // ============================================================
    // ANIMAL DISCOVERED
    // ============================================================

    fun recordAnimalDiscovered(
        animalName: String
    ) {

        if (
            !discoveredAnimals.contains(
                animalName
            )
        ) {

            discoveredAnimals.add(
                animalName
            )


            refreshAchievements()
        }
    }


    // ============================================================
    // GUIDEBOOK
    // ============================================================

    fun recordGuidebookCompleted() {

        guidebookCount++


        earnPoints(

            amount =
                25,

            source =
                PointSource.GUIDEBOOK,

            title =
                "Guidebook Completed",

            description =
                "You completed a wildlife safety guide."

        )


        refreshAchievements()
    }


    // ============================================================
    // ACHIEVEMENT PROGRESS
    // ============================================================

    private fun progressFor(
        achievement: Achievement
    ): Int {

        return when (
            achievement.type
        ) {

            AchievementType.REPORTS ->
                reportCount


            AchievementType.UNIQUE_ANIMALS ->
                discoveredAnimals.size


            AchievementType.LION_TRACKER ->

                if (
                    discoveredAnimals.contains(
                        "LION"
                    )
                ) {

                    1

                } else {

                    0
                }


            AchievementType.LOGINS ->
                loginCount


            AchievementType.GUIDEBOOK ->
                guidebookCount


            AchievementType.NULL ->
                0

        }.coerceAtMost(
            achievement.target
        )
    }


    // ============================================================
    // REFRESH ACHIEVEMENTS
    // ============================================================

    private fun refreshAchievements() {

        _achievements.indices
            .forEach { index ->


                val achievement =
                    _achievements[index]


                val newProgress =
                    progressFor(
                        achievement
                    )


                val newlyUnlocked =

                    !achievement.unlocked &&

                            newProgress >=
                            achievement.target


                if (
                    achievement.type ==
                    AchievementType.NULL
                ) {

                    return@forEach
                }


                if (
                    newlyUnlocked
                ) {


                    _achievements[index] =
                        achievement.copy(

                            progress =
                                newProgress,

                            unlocked =
                                true,

                            rewardGranted =
                                true

                        )


                    earnPoints(

                        amount =
                            achievement
                                .rewardPoints,

                        source =
                            PointSource.ACHIEVEMENT,

                        title =
                            achievement.title,

                        description =
                            "Achievement unlocked: ${achievement.title}",

                        eventId =
                            "achievement_${achievement.id}"

                    )


                } else if (
                    newProgress !=
                    achievement.progress
                ) {


                    _achievements[index] =
                        achievement.copy(

                            progress =
                                newProgress

                        )
                }
            }
    }


    // ============================================================
    // REMOVE POINT POPUP
    // ============================================================

    fun removePointEvent(
        eventId: String
    ) {

        _pendingPointEvents
            .removeAll {
                it.id == eventId
            }
    }


    // ============================================================
    // ANIMAL NORMAL TAP MESSAGES
    // ============================================================

    private val animalShortMessages =
        mapOf(


            AnimalType.TIGER to
                    listOf(

                        "The tiger looks calm today.",

                        "Someone is watching you from the den.",

                        "The tiger gives a sleepy little blink."

                    ),


            AnimalType.FOX to
                    listOf(

                        "The fox twitches its ears.",

                        "The fox seems curious about you.",

                        "A quiet fox nap continues."

                    ),


            AnimalType.BEAR to
                    listOf(

                        "The bear is enjoying a peaceful moment.",

                        "The bear looks comfortable.",

                        "A gentle growl echoes from the den."

                    ),


            AnimalType.PANDA to
                    listOf(

                        "The panda looks happy to see you.",

                        "The panda is having a peaceful day.",

                        "The panda slowly looks your way."

                    ),


            AnimalType.KOALA to
                    listOf(

                        "The koala is enjoying the quiet.",

                        "The koala gives a tiny yawn.",

                        "The koala looks comfortably sleepy."

                    ),


            AnimalType.LION to
                    listOf(

                        "The lion watches the area carefully.",

                        "The lion seems relaxed today.",

                        "A quiet roar comes from the habitat."

                    ),


            AnimalType.CAT to
                    listOf(

                        "The cat looks ready for attention.",

                        "The cat slowly flicks its tail.",

                        "The cat gives you a curious stare."

                    ),


            AnimalType.WOLF to
                    listOf(

                        "The wolf listens to the surroundings.",

                        "The wolf looks peaceful today.",

                        "The wolf raises its ears curiously."

                    ),


            AnimalType.DOG to
                    listOf(

                        "The dog looks excited to see you.",

                        "The dog gives a happy little wag.",

                        "The dog is enjoying the peaceful habitat."

                    )
        )


    // ============================================================
    // PLANT NORMAL TAP MESSAGES
    // ============================================================

    private val plantShortMessages =
        listOf(

            "The plant looks healthy today.",

            "A little more sunlight would be nice.",

            "The leaves gently move with the breeze.",

            "The plant is growing steadily."

        )


    // ============================================================
    // PART 3
    // WEIGHTED REACTION FUNCTION
    //
    // Example:
    //
    // HEART 75
    // SMILE 25
    //
    // total = 100
    //
    // HEART approximately 75%
    // SMILE approximately 25%
    // ============================================================

    private fun weightedReaction(

        vararg choices:
        Pair<ReactionType, Int>

    ): ReactionType {


        val totalWeight =
            choices.sumOf {

                it.second
                    .coerceAtLeast(
                        0
                    )
            }


        if (
            totalWeight <= 0
        ) {

            return ReactionType.SLEEP
        }


        val randomValue =
            Random.nextInt(
                totalWeight
            )


        var currentWeight =
            0


        for (
        choice in choices
        ) {


            currentWeight +=
                choice.second
                    .coerceAtLeast(
                        0
                    )


            if (
                randomValue <
                currentWeight
            ) {

                return choice.first
            }
        }


        return choices
            .last()
            .first
    }


    // ============================================================
    // NORMAL ANIMAL TAP
    //
    // Message:
    // random animal message
    //
    // Reaction:
    //
    // SLEEP = 60%
    // SMILE = 25%
    // HEART = 15%
    // ============================================================

    fun showRandomAnimalMessage() {


        val messages =
            animalShortMessages[
                selectedAnimal
            ].orEmpty()


        if (
            messages.isEmpty()
        ) {

            return
        }


        // Random message
        unlockInteraction(

            messages[
                Random.nextInt(
                    messages.size
                )
            ]

        )


        // Weighted reaction
        showReaction(

            weightedReaction(

                ReactionType.SLEEP to
                        60,

                ReactionType.SMILE to
                        25,

                ReactionType.HEART to
                        15

            )
        )
    }


    // ============================================================
    // NORMAL PLANT TAP
    //
    // Message:
    // random plant message
    //
    // Reaction:
    //
    // LEAF = 60%
    // GROW = 25%
    // WATER = 15%
    // ============================================================

    fun showRandomPlantMessage() {


        if (
            plantShortMessages
                .isEmpty()
        ) {

            return
        }


        // Random message
        unlockInteraction(

            plantShortMessages[
                Random.nextInt(
                    plantShortMessages.size
                )
            ]

        )


        // Weighted reaction
        showReaction(

            weightedReaction(

                ReactionType.LEAF to
                        60,

                ReactionType.GROW to
                        25,

                ReactionType.WATER to
                        15

            )
        )
    }


    // ============================================================
    // PAT ANIMAL
    //
    // Reaction:
    //
    // HEART = 75%
    // SMILE = 25%
    // ============================================================

    fun quickPatAnimal():
            Boolean {


        unlockInteraction(

            "${selectedAnimal.name} enjoyed the pat! ❤️"

        )


        showReaction(

            weightedReaction(

                ReactionType.HEART to
                        75,

                ReactionType.SMILE to
                        25

            )
        )


        return true
    }


    // ============================================================
    // WATER PLANT
    //
    // Reaction:
    //
    // WATER = 75%
    // GROW = 25%
    // ============================================================

    fun quickWaterPlant():
            Boolean {


        unlockInteraction(

            "The plant feels refreshed after watering! 💧"

        )


        showReaction(

            weightedReaction(

                ReactionType.WATER to
                        75,

                ReactionType.GROW to
                        25

            )
        )


        return true
    }


    // ============================================================
    // NO TOOL MESSAGE
    // ============================================================

    fun showNoToolMessage(
        tool: String
    ) {


        interactionMessage =
            when (tool) {


                "pat" ->
                    "You don't have a Pat Pat Hand in your inventory."


                "watering" ->
                    "You don't have a Watering Tool in your inventory."


                else ->
                    "You don't have the required tool."

            }


        showInteraction =
            true
    }


    // ============================================================
    // UPDATE INTERACTION
    // ============================================================

    fun updateInteraction(
        message: String
    ) {

        interactionMessage =
            message
    }


    // ============================================================
    // COMPLETE OBJECTIVE
    // ============================================================

    fun completeObjective(
        objective: String
    ) {


        interactionMessage =
            when (objective) {


                "report" ->
                    "Thanks for protecting me!"


                "plant" ->
                    "The forest feels healthier!"


                "login" ->
                    "Welcome back!"


                "redeem" ->
                    "I love your gift!"


                else ->
                    "Let's continue protecting wildlife!"

            }
    }


    // ============================================================
    // UNLOCK / DISPLAY INTERACTION
    // ============================================================

    fun unlockInteraction(
        message: String
    ) {


        if (
            !unlockedInteractions
                .contains(
                    message
                )
        ) {

            unlockedInteractions
                .add(
                    message
                )
        }


        currentInteractionIndex =
            unlockedInteractions
                .lastIndex


        interactionMessage =
            message


        showInteraction =
            true
    }


    // ============================================================
    // HIDE INTERACTION
    // ============================================================

    fun hideInteraction() {

        showInteraction =
            false
    }


    // ============================================================
    // NEXT INTERACTION
    // ============================================================

    fun nextInteraction() {


        if (
            unlockedInteractions
                .isEmpty()
        ) {

            return
        }


        currentInteractionIndex++


        if (
            currentInteractionIndex >=
            unlockedInteractions.size
        ) {

            currentInteractionIndex =
                0
        }


        interactionMessage =
            unlockedInteractions[
                currentInteractionIndex
            ]
    }


    // ============================================================
    // PURCHASE REWARD
    // ============================================================

    fun purchaseReward(

        reward: RewardItem,

        quantity: Int

    ): PurchaseResult {


        if (
            reward.stock < 0
        ) {

            return PurchaseResult.COMING_SOON
        }


        if (
            reward.stock == 0
        ) {

            return PurchaseResult.SOLD_OUT
        }


        if (
            quantity >
            reward.stock
        ) {

            return PurchaseResult.EXCEEDS_STOCK
        }


        val totalPrice =
            reward.price *
                    quantity


        if (
            !spendPoints(
                totalPrice
            )
        ) {

            return PurchaseResult.INSUFFICIENT_POINTS
        }


        val index =
            _rewardItems
                .indexOfFirst {

                    it.id ==
                            reward.id
                }


        val oldReward =
            _rewardItems[
                index
            ]


        _rewardItems[index] =
            oldReward.copy(

                stock =
                    oldReward.stock -
                            quantity,

                purchased =
                    true

            )


        return PurchaseResult.SUCCESS
    }


    // ============================================================
    // PART 3
    // SHOW REACTION
    //
    // IMPORTANT:
    //
    // reactionEventId MUST increase every time.
    //
    // Example:
    //
    // HEART
    // ↓
    // event 1
    //
    // HEART again
    // ↓
    // event 2
    //
    // This causes the reaction animation and timer
    // to restart.
    // ============================================================

    fun showReaction(
        reaction: ReactionType
    ) {


        currentReaction =
            reaction


        reactionEventId++


        showReaction =
            true
    }


    // ============================================================
    // HIDE ACTIVE REACTION
    //
    // Scene idle SLEEP / LEAF will automatically
    // appear again.
    // ============================================================

    fun hideReaction() {

        showReaction =
            false
    }


    // ============================================================
    // INVENTORY QUICK ACCESS
    //
    // ID 1:
    // Pat Pat Hand
    //
    // ID 2:
    // Watering Tool
    // ============================================================

    fun useQuickAccessItem(
        rewardId: Int
    ): Boolean {


        return when (
            rewardId
        ) {


            // Pat Pat Hand
            1 -> {


                if (
                    rewardType ==
                    RewardType.ANIMAL
                ) {

                    quickPatAnimal()

                } else {

                    false
                }
            }


            // Watering Tool
            2 -> {


                if (
                    rewardType ==
                    RewardType.PLANT
                ) {

                    quickWaterPlant()

                } else {

                    false
                }
            }


            else ->
                false
        }
    }
}