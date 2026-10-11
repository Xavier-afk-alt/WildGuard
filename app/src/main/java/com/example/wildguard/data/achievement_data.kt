package com.example.wildguard.data

import com.example.wildguard.localization.AppLanguage

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
        description = "Open the app on five separate calendar days.",
        unlockCondition = "Visit on 5 different days",
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

private val achievementTitleTranslations: Map<Int, Pair<String, String>> = mapOf(
    1 to ("首次报告" to "Laporan Pertama"), 2 to ("十次报告" to "10 Laporan"),
    3 to ("动物探索者" to "Peneroka Haiwan"), 4 to ("狮子追踪者" to "Pengesan Singa"),
    5 to ("每日访客" to "Pelawat Harian"), 6 to ("安全学习者" to "Pelajar Keselamatan"),
    7 to ("敬请期待" to "Akan Datang")
)

private val achievementDescriptionTranslations: Map<Int, Pair<String, String>> = mapOf(
    1 to ("提交您的第一份野生动物报告。" to "Hantar laporan hidupan liar pertama anda."),
    2 to ("协助野生动物团队，提交十份报告。" to "Bantu pasukan hidupan liar dengan menghantar sepuluh laporan."),
    3 to ("在奖励动物视图中发现五种不同的动物。" to "Temui lima haiwan berbeza dalam paparan haiwan Ganjaran."),
    4 to ("首次访问狮子动物视图。" to "Lawati paparan haiwan Singa buat kali pertama."),
    5 to ("在五个不同日历日打开应用程序。" to "Buka aplikasi pada lima hari kalendar berasingan."),
    6 to ("完成五个指南手册活动。" to "Lengkapkan lima aktiviti buku panduan."),
    7 to ("未来将推出更多成就。" to "Pencapaian tambahan akan tersedia pada masa hadapan.")
)

private val achievementUnlockConditionTranslations: Map<Int, Pair<String, String>> = mapOf(
    1 to ("提交 1 份报告" to "Hantar 1 laporan"), 2 to ("提交 10 份报告" to "Hantar 10 laporan"),
    3 to ("发现 5 种不同的动物" to "Temui 5 haiwan berbeza"), 4 to ("发现狮子" to "Temui Singa"),
    5 to ("在 5 个不同的日子访问" to "Lawati pada 5 hari berbeza"),
    6 to ("完成 5 个指南手册活动" to "Lengkapkan 5 aktiviti buku panduan"),
    7 to ("目前无法访问" to "Tidak boleh diakses buat masa ini")
)

fun Achievement.localizedTitle(): String {
    val (chinese, malay) = achievementTitleTranslations[id] ?: (title to title)
    return AppLanguage.text(title, chinese, malay)
}

fun Achievement.localizedDescription(): String {
    val (chinese, malay) = achievementDescriptionTranslations[id] ?: (description to description)
    return AppLanguage.text(description, chinese, malay)
}

fun Achievement.localizedUnlockCondition(): String {
    val (chinese, malay) = achievementUnlockConditionTranslations[id] ?: (unlockCondition to unlockCondition)
    return AppLanguage.text(unlockCondition, chinese, malay)
}
