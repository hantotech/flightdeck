package com.example.flightdeck.utils

import com.example.flightdeck.data.model.*

/**
 * Validates mission configurations and provides suggestions
 * Ensures logical combinations of challenges and difficulty levels
 */
object ChallengeValidator {

    /**
     * Challenges that work well together (synergies)
     */
    private val synergies = mapOf(
        ChallengeModule.WEATHER_DETERIORATION to listOf(
            ChallengeModule.RUNWAY_CHANGES,
            ChallengeModule.HOLD_INSTRUCTIONS,
            ChallengeModule.VISIBILITY_CHANGES
        ),
        ChallengeModule.TRAFFIC_CONFLICTS to listOf(
            ChallengeModule.GO_AROUND_PRACTICE,
            ChallengeModule.RAPID_FREQUENCY_CHANGES,
            ChallengeModule.TRAFFIC_PATTERN_FULL
        ),
        ChallengeModule.ENGINE_ROUGHNESS to listOf(
            ChallengeModule.FUEL_MANAGEMENT,
            ChallengeModule.WEATHER_DETERIORATION
        ),
        ChallengeModule.EQUIPMENT_ISSUES to listOf(
            ChallengeModule.NAVIGATION_FAILURE
        ),
        ChallengeModule.CROSSWIND_LANDING to listOf(
            ChallengeModule.GO_AROUND_PRACTICE,
            ChallengeModule.WIND_SHEAR_ALERTS
        )
    )

    /**
     * Minimum difficulty required for certain challenges
     */
    private val minimumDifficulty = mapOf(
        ChallengeModule.HOLD_INSTRUCTIONS to Difficulty.ADVANCED,
        ChallengeModule.INSTRUMENT_APPROACHES to Difficulty.ADVANCED,
        ChallengeModule.ELECTRICAL_FAILURE to Difficulty.ADVANCED,
        ChallengeModule.CLASS_B_TRANSITION to Difficulty.INTERMEDIATE,
        ChallengeModule.SPECIAL_VFR to Difficulty.INTERMEDIATE,
        ChallengeModule.WIND_SHEAR_ALERTS to Difficulty.INTERMEDIATE,
        ChallengeModule.RANDOM_EMERGENCY to Difficulty.ADVANCED
    )

    /**
     * Validate a mission configuration
     */
    fun validate(missionConfig: MissionConfig): MissionValidationResult {
        val challenges = missionConfig.getChallengesList()
        val conflicts = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val suggestions = mutableListOf<ChallengeModule>()

        // Check for multiple emergencies
        val emergencies = challenges.filter { it.category == ChallengeCategory.EMERGENCY }
        if (emergencies.size > 1) {
            conflicts.add("Only one emergency scenario can be active per mission. You have selected: ${emergencies.joinToString(", ") { it.displayName }}")
        }

        // Check difficulty requirements
        challenges.forEach { challenge ->
            val minDiff = minimumDifficulty[challenge]
            if (minDiff != null && missionConfig.baseDifficulty.ordinal < minDiff.ordinal) {
                warnings.add("${challenge.displayName} is recommended for ${minDiff.name} difficulty or higher")
            }
        }

        // Check for good synergies and suggest additions
        challenges.forEach { challenge ->
            synergies[challenge]?.forEach { synergy ->
                if (!challenges.contains(synergy)) {
                    suggestions.add(synergy)
                }
            }
        }

        // Limit suggestions to top 3
        val uniqueSuggestions = suggestions.distinct().take(3)

        return MissionValidationResult(
            isValid = conflicts.isEmpty(),
            conflicts = conflicts,
            warnings = warnings,
            suggestions = uniqueSuggestions
        )
    }

    /**
     * Validate a list of challenges before creating a config
     */
    fun validateChallenges(
        challenges: List<ChallengeModule>,
        baseDifficulty: Difficulty
    ): MissionValidationResult {
        val tempConfig = MissionConfig(
            name = "temp",
            flightPlanId = 0,
            baseDifficulty = baseDifficulty,
            selectedChallenges = challenges.joinToString(",") { it.name }
        )
        return validate(tempConfig)
    }

    /**
     * Get recommended challenges based on user's weak areas
     */
    fun getRecommendations(
        userStats: UserStats,
        difficulty: Difficulty
    ): List<ChallengeRecommendation> {
        val recommendations = mutableListOf<ChallengeRecommendation>()

        // Check readback accuracy
        if (userStats.readbackAccuracy < 85f) {
            recommendations.add(
                ChallengeRecommendation(
                    challenge = ChallengeModule.READBACK_CORRECTIONS,
                    reason = "Your readback accuracy is ${userStats.readbackAccuracy.toInt()}%. This challenge will help you improve.",
                    priority = 3
                )
            )
        }

        // Check traffic response
        if (userStats.trafficResponseScore < 80f) {
            recommendations.add(
                ChallengeRecommendation(
                    challenge = ChallengeModule.TRAFFIC_CONFLICTS,
                    reason = "Challenge yourself with more complex traffic scenarios to improve your ${userStats.trafficResponseScore.toInt()}% score.",
                    priority = 2
                )
            )
        }

        // Check frequency change proficiency
        if (userStats.frequencyChangeAccuracy < 90f && difficulty >= Difficulty.INTERMEDIATE) {
            recommendations.add(
                ChallengeRecommendation(
                    challenge = ChallengeModule.RAPID_FREQUENCY_CHANGES,
                    reason = "Practice rapid handoffs to improve your frequency change skills.",
                    priority = 2
                )
            )
        }

        // Check emergency handling (if advanced)
        if (userStats.emergencyScenariosPracticed < 5 && difficulty >= Difficulty.ADVANCED) {
            recommendations.add(
                ChallengeRecommendation(
                    challenge = ChallengeModule.ENGINE_ROUGHNESS,
                    reason = "Practice emergency procedures to build confidence.",
                    priority = 1
                )
            )
        }

        // Sort by priority (high to low) and return
        return recommendations.sortedByDescending { it.priority }
    }

    /**
     * Check if a challenge is unlocked for the user
     */
    fun isUnlocked(
        challenge: ChallengeModule,
        userStats: UserStats
    ): UnlockStatus {
        // Emergency scenarios require completion of basic training
        if (challenge.category == ChallengeCategory.EMERGENCY) {
            if (userStats.totalMissionsCompleted < 10) {
                return UnlockStatus.Locked(
                    reason = "Complete 10 missions to unlock emergency scenarios",
                    progress = userStats.totalMissionsCompleted,
                    required = 10
                )
            }
            if (userStats.averageScore < 75f) {
                return UnlockStatus.Locked(
                    reason = "Maintain 75% average score to unlock emergency scenarios",
                    progress = userStats.averageScore.toInt(),
                    required = 75
                )
            }
        }

        // Advanced procedures require proficiency
        if (challenge.category == ChallengeCategory.ADVANCED_PROCEDURES) {
            if (userStats.totalMissionsCompleted < 15) {
                return UnlockStatus.Locked(
                    reason = "Complete 15 missions to unlock advanced procedures",
                    progress = userStats.totalMissionsCompleted,
                    required = 15
                )
            }
            if (userStats.averageScore < 80f) {
                return UnlockStatus.Locked(
                    reason = "Maintain 80% average score for advanced procedures",
                    progress = userStats.averageScore.toInt(),
                    required = 80
                )
            }
        }

        // All other challenges are unlocked
        return UnlockStatus.Unlocked
    }

    /**
     * Get description and tips for a challenge
     */
    fun getChallengeInfo(challenge: ChallengeModule): ChallengeInfo {
        return when (challenge) {
            ChallengeModule.RAPID_FREQUENCY_CHANGES -> ChallengeInfo(
                description = "Practice quick handoffs between multiple ATC facilities every 2-3 minutes.",
                whatToExpect = listOf(
                    "Frequent frequency changes",
                    "Quick transition between controllers",
                    "Practice writing down frequencies"
                ),
                tips = listOf(
                    "Have your kneeboard ready",
                    "Write down frequencies immediately",
                    "Verify new frequency before switching"
                )
            )

            ChallengeModule.ENGINE_ROUGHNESS -> ChallengeInfo(
                description = "Experience partial power loss and practice emergency procedures.",
                whatToExpect = listOf(
                    "Unexpected engine roughness en route",
                    "Need to declare emergency",
                    "Divert to nearest suitable airport",
                    "Emergency landing procedures"
                ),
                tips = listOf(
                    "Stay calm and maintain aircraft control",
                    "Use proper emergency phraseology",
                    "Aviate, Navigate, Communicate",
                    "Review POH emergency procedures first"
                )
            )

            ChallengeModule.GO_AROUND_PRACTICE -> ChallengeInfo(
                description = "Tower will send you around due to traffic on the runway.",
                whatToExpect = listOf(
                    "Last-minute go-around instructions",
                    "Immediate action required",
                    "Re-sequencing in traffic pattern"
                ),
                tips = listOf(
                    "React immediately to go-around call",
                    "Full power, positive climb established",
                    "Acknowledge tower clearly",
                    "Follow standard go-around procedures"
                )
            )

            ChallengeModule.WEATHER_DETERIORATION -> ChallengeInfo(
                description = "Weather conditions worsen during your flight.",
                whatToExpect = listOf(
                    "Lowering ceilings and visibility",
                    "Updated ATIS information",
                    "Possible runway changes",
                    "Decision-making challenges"
                ),
                tips = listOf(
                    "Monitor weather throughout flight",
                    "Have an alternate plan ready",
                    "Don't hesitate to divert if needed",
                    "Review VFR minimums"
                )
            )

            ChallengeModule.TRAFFIC_CONFLICTS -> ChallengeInfo(
                description = "Multiple traffic advisories requiring vigilance and response.",
                whatToExpect = listOf(
                    "Frequent traffic calls from ATC",
                    "Need to locate and avoid traffic",
                    "Possible altitude or heading changes"
                ),
                tips = listOf(
                    "Acknowledge all traffic calls",
                    "Report traffic in sight",
                    "Maintain appropriate scan",
                    "Be ready for altitude/heading changes"
                )
            )

            // Add more as needed...
            else -> ChallengeInfo(
                description = "Practice ${challenge.displayName}",
                whatToExpect = listOf("Realistic ${challenge.displayName} scenario"),
                tips = listOf("Stay focused and communicate clearly")
            )
        }
    }
}

/**
 * User statistics for recommendations
 */
data class UserStats(
    val totalMissionsCompleted: Int = 0,
    val averageScore: Float = 0f,
    val readbackAccuracy: Float = 0f,
    val trafficResponseScore: Float = 0f,
    val frequencyChangeAccuracy: Float = 0f,
    val emergencyScenariosPracticed: Int = 0
)

/**
 * Challenge recommendation with reasoning
 */
data class ChallengeRecommendation(
    val challenge: ChallengeModule,
    val reason: String,
    val priority: Int // 1-3, higher is more important
)

/**
 * Unlock status for challenges
 */
sealed class UnlockStatus {
    object Unlocked : UnlockStatus()
    data class Locked(
        val reason: String,
        val progress: Int,
        val required: Int
    ) : UnlockStatus()
}

/**
 * Detailed information about a challenge
 */
data class ChallengeInfo(
    val description: String,
    val whatToExpect: List<String>,
    val tips: List<String>
)
