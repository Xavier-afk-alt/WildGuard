package com.example.wildguard.data

/**
 * The event that can move an achievement's progress forward.
 *
 * These are intentionally based on user actions rather than points, so
 * spending points in the Redeem Shop does not affect achievements.
 */
enum class AchievementType {
    REPORTS,
    UNIQUE_ANIMALS,
    LION_TRACKER,
    LOGINS,
    GUIDEBOOK,
    NULL
}

data class Achievement(
    val id: Int,
    val title: String,
    val description: String,
    val unlockCondition: String,
    val target: Int,
    val rewardPoints: Int,
    val type: AchievementType,
    val icon: String,
    val progress: Int = 0,
    val unlocked: Boolean = false,
    val rewardGranted: Boolean = false
)

val achievementItems = listOf(
    Achievement(
        id = 1,
        title = "First Report",
        description = "Submit your first wildlife report.",
        unlockCondition = "Submit 1 report",
        target = 1,
        rewardPoints = 50,
        type = AchievementType.REPORTS,
        icon = "📋"
    ),
    Achievement(
        id = 2,
        title = "10 Reports",
        description = "Help the wildlife team by submitting ten reports.",
        unlockCondition = "Submit 10 reports",
        target = 10,
        rewardPoints = 100,
        type = AchievementType.REPORTS,
        icon = "🏅"
    ),
    Achievement(
        id = 3,
        title = "Animal Explorer",
        description = "Discover five different animals in the Reward animal view.",
        unlockCondition = "Discover 5 different animals",
        target = 5,
        rewardPoints = 75,
        type = AchievementType.UNIQUE_ANIMALS,
        icon = "🐾"
    ),
    Achievement(
        id = 4,
        title = "Lion Tracker",
        description = "Visit the Lion animal view for the first time.",
        unlockCondition = "Discover the Lion",
        target = 1,
        rewardPoints = 50,
        type = AchievementType.LION_TRACKER,
        icon = "🦁"
    ),
    Achievement(
        id = 5,
        title = "Daily Visitor",
        description = "Open the app on five separate login events.",
        unlockCondition = "Log in 5 times",
        target = 5,
        rewardPoints = 75,
        type = AchievementType.LOGINS,
        icon = "🌱"
    ),
    Achievement(
        id = 6,
        title = "Safety Learner",
        description = "Complete five guidebook activities.",
        unlockCondition = "Complete 5 guidebook activities",
        target = 5,
        rewardPoints = 100,
        type = AchievementType.GUIDEBOOK,
        icon = "📖"
    ),
    Achievement(
        id = 7,
        title = "Coming Soon",
        description = "Additional achievement available in future.",
        unlockCondition = "Currently not accessible",
        target = 0,
        rewardPoints = 0,
        type = AchievementType.NULL,
        icon = "?"
    )
)
