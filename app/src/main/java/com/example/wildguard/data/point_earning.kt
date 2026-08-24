package com.example.wildguard.data

/**
 * Where a user's redeemable points came from.
 *
 * Keep these values stable because they can later be stored in Supabase.
 */
enum class PointSource {
    DAILY_LOGIN,
    ANIMAL_SIGHTING,
    REPORT,
    GUIDEBOOK,
    ACHIEVEMENT,
    OTHER
}

data class PointEarningEvent(
    val id: String,
    val source: PointSource,
    val points: Int,
    val title: String,
    val description: String = ""
)