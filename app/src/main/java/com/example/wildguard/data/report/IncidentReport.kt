package com.example.wildguard.data.report

enum class IncidentType(val displayName: String) {
    INJURED_ANIMAL("Injured Animal"),
    ILLEGAL_HUNTING("Illegal Hunting"),
    HABITAT_DAMAGE("Habitat Damage"),
    ANIMAL_TRAP("Animal Trap"),
    DEAD_ANIMAL("Dead Animal"),
    OTHER("Other");

    companion object {
        fun fromDisplayName(value: String): IncidentType =
            entries.firstOrNull { it.displayName.equals(value, ignoreCase = true) } ?: OTHER
    }
}

enum class Severity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

data class IncidentReport(
    val reportId: String = "",
    val userId: String = "",
    val incidentType: IncidentType = IncidentType.OTHER,
    val animalSpecies: String = "",
    val animalCount: Int = 1,
    val isAggressive: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String = "",
    val description: String = "",
    val imageUris: List<String> = emptyList(),
    val severity: Severity = Severity.MEDIUM,
    val weather: String = "Cloudy",
    val timestamp: Long = System.currentTimeMillis()
)
