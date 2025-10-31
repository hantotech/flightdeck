package com.example.flightdeck.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Mission configuration with flexible difficulty and challenge modules
 * Allows users to customize their training missions with specific scenarios
 */
@Entity(tableName = "mission_configs")
data class MissionConfig(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String, // "Quick VFR Flight" or custom user name
    val flightPlanId: Long,

    // Base difficulty setting
    val baseDifficulty: Difficulty,

    // Mission parameters
    val trafficDensity: TrafficDensity = TrafficDensity.MODERATE,
    val weatherComplexity: WeatherComplexity = WeatherComplexity.TYPICAL_VFR,
    val communicationPace: CommunicationPace = CommunicationPace.MODERATE,

    // Optional challenge modules (stored as comma-separated string)
    val selectedChallenges: String = "", // Comma-separated ChallengeModule names

    // Computed properties
    val estimatedDifficultyStars: Int = 1, // 1-5 stars
    val estimatedDurationMinutes: Int = 0,

    // Usage statistics
    val timesFlown: Int = 0,
    val averageScore: Float? = null,
    val lastFlown: Long? = null,

    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val isPreset: Boolean = false, // Built-in preset vs user-created
    val isUserCreated: Boolean = true
)

/**
 * Challenge modules that can be added to any base difficulty
 * These are specific scenarios users can practice
 */
enum class ChallengeModule(val displayName: String, val difficultyStars: Int, val category: ChallengeCategory) {

    // Communication challenges
    RAPID_FREQUENCY_CHANGES("Rapid Frequency Changes", 2, ChallengeCategory.COMMUNICATION),
    COMPLEX_CLEARANCES("Complex Clearances", 3, ChallengeCategory.COMMUNICATION),
    READBACK_CORRECTIONS("Readback Corrections", 2, ChallengeCategory.COMMUNICATION),
    SIMILAR_CALLSIGNS("Similar Callsigns", 3, ChallengeCategory.COMMUNICATION),

    // Traffic challenges
    TRAFFIC_CONFLICTS("Traffic Conflicts", 2, ChallengeCategory.TRAFFIC),
    GO_AROUND_PRACTICE("Go-Around Practice", 2, ChallengeCategory.TRAFFIC),
    TRAFFIC_PATTERN_FULL("Extended Pattern Work", 3, ChallengeCategory.TRAFFIC),
    WAKE_TURBULENCE("Wake Turbulence Avoidance", 3, ChallengeCategory.TRAFFIC),

    // Weather challenges
    WEATHER_DETERIORATION("Weather Deterioration", 3, ChallengeCategory.WEATHER),
    WIND_SHEAR_ALERTS("Wind Shear Alerts", 4, ChallengeCategory.WEATHER),
    VISIBILITY_CHANGES("Visibility Changes", 3, ChallengeCategory.WEATHER),
    CEILING_LOWERING("Ceiling Lowering", 3, ChallengeCategory.WEATHER),
    CROSSWIND_LANDING("Crosswind Landing", 3, ChallengeCategory.WEATHER),

    // Operational challenges
    RUNWAY_CHANGES("Runway Changes", 2, ChallengeCategory.OPERATIONAL),
    AIRSPACE_RESTRICTIONS("Airspace Restrictions", 3, ChallengeCategory.OPERATIONAL),
    EQUIPMENT_ISSUES("Equipment Issues", 4, ChallengeCategory.OPERATIONAL),
    FUEL_MANAGEMENT("Fuel Management", 2, ChallengeCategory.OPERATIONAL),

    // Emergency scenarios (only one allowed per mission)
    ENGINE_ROUGHNESS("Engine Roughness", 4, ChallengeCategory.EMERGENCY),
    ELECTRICAL_FAILURE("Electrical Failure", 5, ChallengeCategory.EMERGENCY),
    PASSENGER_MEDICAL("Passenger Medical Emergency", 4, ChallengeCategory.EMERGENCY),
    NAVIGATION_FAILURE("Navigation Failure", 4, ChallengeCategory.EMERGENCY),
    RANDOM_EMERGENCY("Random Emergency", 5, ChallengeCategory.EMERGENCY),

    // Advanced procedures
    HOLD_INSTRUCTIONS("Hold Instructions", 4, ChallengeCategory.ADVANCED_PROCEDURES),
    INSTRUMENT_APPROACHES("Instrument Approaches", 4, ChallengeCategory.ADVANCED_PROCEDURES),
    SPECIAL_VFR("Special VFR Request", 3, ChallengeCategory.ADVANCED_PROCEDURES),
    CLASS_B_TRANSITION("Class B Transition", 4, ChallengeCategory.ADVANCED_PROCEDURES);

    companion object {
        fun fromString(name: String): ChallengeModule? {
            return values().find { it.name == name }
        }

        fun getEmergencyChallenges(): List<ChallengeModule> {
            return values().filter { it.category == ChallengeCategory.EMERGENCY }
        }

        fun getChallengesByCategory(category: ChallengeCategory): List<ChallengeModule> {
            return values().filter { it.category == category }
        }
    }
}

/**
 * Categories for organizing challenges in the UI
 */
enum class ChallengeCategory(val displayName: String, val icon: String) {
    COMMUNICATION("Communication", "💬"),
    TRAFFIC("Traffic", "✈️"),
    WEATHER("Weather", "🌤️"),
    OPERATIONAL("Operational", "⚙️"),
    EMERGENCY("Emergency", "🚨"),
    ADVANCED_PROCEDURES("Advanced Procedures", "🎓")
}

/**
 * Traffic density levels
 */
enum class TrafficDensity(val displayName: String, val description: String, val aircraftCount: IntRange) {
    NONE("None", "Solo sky", 0..0),
    LIGHT("Light", "1-2 aircraft", 1..2),
    MODERATE("Moderate", "3-5 aircraft", 3..5),
    BUSY("Busy", "6-10 aircraft", 6..10),
    CONGESTED("Congested", "10+ aircraft", 10..15)
}

/**
 * Weather complexity levels
 */
enum class WeatherComplexity(val displayName: String, val description: String) {
    CLEAR_CALM("Clear & Calm", "Perfect VFR conditions"),
    TYPICAL_VFR("Typical VFR", "Normal VFR conditions"),
    CHALLENGING("Challenging", "MVFR, wind, gusty"),
    MARGINAL("Marginal", "Low MVFR, close to IFR"),
    DYNAMIC("Dynamic", "Changing conditions en route")
}

/**
 * Communication pace (instructions per minute)
 */
enum class CommunicationPace(val displayName: String, val instructionsPerMinute: Int) {
    RELAXED("Relaxed", 1),
    MODERATE("Moderate", 2),
    BUSY("Busy", 3),
    RAPID("Rapid", 4)
}

/**
 * Helper data class for mission validation results
 */
data class MissionValidationResult(
    val isValid: Boolean,
    val conflicts: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val suggestions: List<ChallengeModule> = emptyList()
)

/**
 * Helper extensions for MissionConfig
 */
fun MissionConfig.getChallengesList(): List<ChallengeModule> {
    if (selectedChallenges.isEmpty()) return emptyList()
    return selectedChallenges.split(",")
        .mapNotNull { ChallengeModule.fromString(it.trim()) }
}

fun MissionConfig.hasEmergency(): Boolean {
    return getChallengesList().any { it.category == ChallengeCategory.EMERGENCY }
}

fun MissionConfig.getEmergencyChallenge(): ChallengeModule? {
    return getChallengesList().find { it.category == ChallengeCategory.EMERGENCY }
}

/**
 * Calculate overall mission difficulty (1-5 stars)
 */
fun MissionConfig.calculateDifficulty(): Int {
    var score = when (baseDifficulty) {
        Difficulty.BEGINNER -> 1.0
        Difficulty.INTERMEDIATE -> 2.0
        Difficulty.ADVANCED -> 3.0
        Difficulty.EXPERT -> 4.0
    }

    // Add challenge difficulty
    getChallengesList().forEach { challenge ->
        score += when (challenge.difficultyStars) {
            1, 2 -> 0.3
            3 -> 0.5
            4 -> 0.7
            5 -> 1.0
            else -> 0.0
        }
    }

    // Traffic density impact
    score += when (trafficDensity) {
        TrafficDensity.NONE -> 0.0
        TrafficDensity.LIGHT -> 0.1
        TrafficDensity.MODERATE -> 0.2
        TrafficDensity.BUSY -> 0.3
        TrafficDensity.CONGESTED -> 0.5
    }

    // Weather complexity impact
    score += when (weatherComplexity) {
        WeatherComplexity.CLEAR_CALM -> 0.0
        WeatherComplexity.TYPICAL_VFR -> 0.1
        WeatherComplexity.CHALLENGING -> 0.3
        WeatherComplexity.MARGINAL -> 0.5
        WeatherComplexity.DYNAMIC -> 0.7
    }

    // Communication pace impact
    score += when (communicationPace) {
        CommunicationPace.RELAXED -> 0.0
        CommunicationPace.MODERATE -> 0.1
        CommunicationPace.BUSY -> 0.3
        CommunicationPace.RAPID -> 0.5
    }

    // Cap at 5 stars
    return minOf(score.toInt(), 5)
}

/**
 * Estimate mission duration based on flight plan and complexity
 */
fun MissionConfig.estimateDuration(baseFlightTimeMinutes: Int): Int {
    var duration = baseFlightTimeMinutes

    // Add time for challenges
    getChallengesList().forEach { challenge ->
        duration += when (challenge.category) {
            ChallengeCategory.EMERGENCY -> 10
            ChallengeCategory.ADVANCED_PROCEDURES -> 5
            ChallengeCategory.WEATHER -> 3
            else -> 2
        }
    }

    // Add time for traffic delays
    duration += when (trafficDensity) {
        TrafficDensity.NONE -> 0
        TrafficDensity.LIGHT -> 2
        TrafficDensity.MODERATE -> 5
        TrafficDensity.BUSY -> 8
        TrafficDensity.CONGESTED -> 12
    }

    return duration
}
