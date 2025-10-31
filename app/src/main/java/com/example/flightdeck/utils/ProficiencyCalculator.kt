package com.example.flightdeck.utils

import com.example.flightdeck.data.model.*
import kotlin.math.abs

/**
 * Calculates and tracks pilot proficiency across different skill categories
 * Based on performance in training sessions
 */
object ProficiencyCalculator {

    /**
     * Update proficiency rating based on a new logbook entry
     */
    fun updateProficiency(
        currentRating: ProficiencyRating?,
        newScore: Float,
        logbookEntry: LogbookEntry
    ): ProficiencyRating {
        val now = System.currentTimeMillis()

        if (currentRating == null) {
            // First time rating this skill
            return ProficiencyRating(
                skillCategory = determineSkillCategory(logbookEntry),
                currentRating = ProficiencyLevel.fromScore(newScore),
                currentScore = newScore,
                lastUpdated = now,
                totalSessions = 1,
                averageScore = newScore,
                bestScore = newScore,
                worstScore = newScore,
                recentScores = newScore.toString(),
                trend = TrendDirection.STABLE,
                needsImprovement = newScore < 70f
            )
        }

        // Update existing rating
        val totalSessions = currentRating.totalSessions + 1
        val newAverage = ((currentRating.averageScore * currentRating.totalSessions) + newScore) / totalSessions
        val newBest = maxOf(currentRating.bestScore, newScore)
        val newWorst = minOf(currentRating.worstScore, newScore)

        // Update recent scores (keep last 5)
        val recentScores = updateRecentScores(currentRating.recentScores, newScore)
        val trend = calculateTrend(recentScores)

        // Determine new rating level
        val newLevel = ProficiencyLevel.fromScore(newScore)
        val levelChanged = newLevel != currentRating.currentRating

        return currentRating.copy(
            currentRating = newLevel,
            currentScore = newScore,
            lastUpdated = now,
            previousRating = if (levelChanged) currentRating.currentRating else currentRating.previousRating,
            previousScore = if (levelChanged) currentRating.currentScore else currentRating.previousScore,
            ratingChangedDate = if (levelChanged) now else currentRating.ratingChangedDate,
            totalSessions = totalSessions,
            averageScore = newAverage,
            bestScore = newBest,
            worstScore = newWorst,
            recentScores = recentScores,
            trend = trend,
            needsImprovement = newScore < 70f || trend == TrendDirection.DECLINING
        )
    }

    /**
     * Calculate proficiency for all skill categories from a logbook entry
     */
    fun calculateSkillScores(entry: LogbookEntry): Map<SkillCategory, Float> {
        val scores = mutableMapOf<SkillCategory, Float>()

        // Communication skills
        scores[SkillCategory.RADIO_COMMUNICATION] = entry.communicationScore

        // Navigation skills
        scores[SkillCategory.NAVIGATION] = entry.navigationScore

        // Weather decision making
        scores[SkillCategory.WEATHER_DECISION_MAKING] = entry.weatherDecisionScore

        // Traffic management
        scores[SkillCategory.TRAFFIC_MANAGEMENT] = entry.trafficManagementScore

        // Emergency procedures
        if (entry.emergencyHandlingScore > 0) {
            scores[SkillCategory.EMERGENCY_PROCEDURES] = entry.emergencyHandlingScore
        }

        // Airspace knowledge (derived from communication and navigation)
        if (entry.getChallengesList().any { it.category == ChallengeCategory.ADVANCED_PROCEDURES }) {
            scores[SkillCategory.AIRSPACE_KNOWLEDGE] =
                (entry.communicationScore + entry.navigationScore) / 2f
        }

        // Situational awareness (overall performance indicator)
        scores[SkillCategory.SITUATIONAL_AWARENESS] = entry.overallScore

        // Decision making (derived from multiple factors)
        val decisionFactors = listOfNotNull(
            entry.weatherDecisionScore.takeIf { it > 0 },
            entry.emergencyHandlingScore.takeIf { it > 0 },
            entry.trafficManagementScore.takeIf { it > 0 }
        )
        if (decisionFactors.isNotEmpty()) {
            scores[SkillCategory.DECISION_MAKING] = decisionFactors.average().toFloat()
        }

        // Fuel management (if fuel challenge was involved)
        if (entry.getChallengesList().contains(ChallengeModule.FUEL_MANAGEMENT)) {
            scores[SkillCategory.FUEL_MANAGEMENT] =
                (entry.navigationScore + entry.overallScore) / 2f
        }

        return scores
    }

    /**
     * Generate feedback for a specific skill based on score
     */
    fun generateSkillFeedback(
        skillCategory: SkillCategory,
        score: Float,
        previousScore: Float?,
        entry: LogbookEntry
    ): String {
        val improvement = previousScore?.let { score - it }
        val level = ProficiencyLevel.fromScore(score)

        return when (skillCategory) {
            SkillCategory.RADIO_COMMUNICATION -> generateCommunicationFeedback(score, improvement, entry)
            SkillCategory.NAVIGATION -> generateNavigationFeedback(score, improvement, entry)
            SkillCategory.WEATHER_DECISION_MAKING -> generateWeatherFeedback(score, improvement, entry)
            SkillCategory.TRAFFIC_MANAGEMENT -> generateTrafficFeedback(score, improvement, entry)
            SkillCategory.EMERGENCY_PROCEDURES -> generateEmergencyFeedback(score, improvement, entry)
            SkillCategory.AIRSPACE_KNOWLEDGE -> generateAirspaceFeedback(score, improvement)
            SkillCategory.SITUATIONAL_AWARENESS -> generateSituationalFeedback(score, improvement)
            SkillCategory.DECISION_MAKING -> generateDecisionMakingFeedback(score, improvement)
            SkillCategory.FUEL_MANAGEMENT -> generateFuelFeedback(score, improvement)
            SkillCategory.CHECKLIST_DISCIPLINE -> generateChecklistFeedback(score, improvement)
        }
    }

    /**
     * Determine recommended challenges for improvement
     */
    fun recommendChallengesForSkill(skillCategory: SkillCategory): List<ChallengeModule> {
        return when (skillCategory) {
            SkillCategory.RADIO_COMMUNICATION -> listOf(
                ChallengeModule.RAPID_FREQUENCY_CHANGES,
                ChallengeModule.COMPLEX_CLEARANCES,
                ChallengeModule.READBACK_CORRECTIONS
            )
            SkillCategory.NAVIGATION -> listOf(
                ChallengeModule.CLASS_B_TRANSITION,
                ChallengeModule.AIRSPACE_RESTRICTIONS
            )
            SkillCategory.WEATHER_DECISION_MAKING -> listOf(
                ChallengeModule.WEATHER_DETERIORATION,
                ChallengeModule.VISIBILITY_CHANGES,
                ChallengeModule.CEILING_LOWERING
            )
            SkillCategory.TRAFFIC_MANAGEMENT -> listOf(
                ChallengeModule.TRAFFIC_CONFLICTS,
                ChallengeModule.GO_AROUND_PRACTICE,
                ChallengeModule.TRAFFIC_PATTERN_FULL
            )
            SkillCategory.EMERGENCY_PROCEDURES -> listOf(
                ChallengeModule.ENGINE_ROUGHNESS,
                ChallengeModule.ELECTRICAL_FAILURE,
                ChallengeModule.NAVIGATION_FAILURE
            )
            SkillCategory.AIRSPACE_KNOWLEDGE -> listOf(
                ChallengeModule.CLASS_B_TRANSITION,
                ChallengeModule.SPECIAL_VFR,
                ChallengeModule.AIRSPACE_RESTRICTIONS
            )
            SkillCategory.FUEL_MANAGEMENT -> listOf(
                ChallengeModule.FUEL_MANAGEMENT
            )
            else -> emptyList()
        }
    }

    /**
     * Calculate overall pilot rank based on proficiency ratings
     */
    fun calculatePilotRank(proficiencies: List<ProficiencyRating>, totalSessions: Int): PilotRank {
        if (proficiencies.isEmpty() || totalSessions == 0) {
            return PilotRank.STUDENT_PILOT
        }

        val averageScore = proficiencies.map { it.currentScore }.average().toFloat()
        val expertCount = proficiencies.count { it.currentRating == ProficiencyLevel.EXPERT }
        val advancedCount = proficiencies.count { it.currentRating == ProficiencyLevel.ADVANCED }

        return when {
            averageScore >= 95f && expertCount >= 7 && totalSessions >= 50 ->
                PilotRank.MASTER_INSTRUCTOR

            averageScore >= 90f && expertCount >= 5 && totalSessions >= 30 ->
                PilotRank.CERTIFIED_INSTRUCTOR

            averageScore >= 85f && advancedCount >= 6 && totalSessions >= 20 ->
                PilotRank.COMMERCIAL_PILOT

            averageScore >= 75f && totalSessions >= 15 ->
                PilotRank.PRIVATE_PILOT

            averageScore >= 65f && totalSessions >= 10 ->
                PilotRank.SOLO_STUDENT

            else -> PilotRank.STUDENT_PILOT
        }
    }

    // ========================================
    // Private Helper Functions
    // ========================================

    private fun determineSkillCategory(entry: LogbookEntry): SkillCategory {
        // Determine primary skill from entry
        val scores = mapOf(
            SkillCategory.RADIO_COMMUNICATION to entry.communicationScore,
            SkillCategory.NAVIGATION to entry.navigationScore,
            SkillCategory.WEATHER_DECISION_MAKING to entry.weatherDecisionScore,
            SkillCategory.TRAFFIC_MANAGEMENT to entry.trafficManagementScore
        )
        return scores.maxByOrNull { it.value }?.key ?: SkillCategory.RADIO_COMMUNICATION
    }

    private fun updateRecentScores(current: String, newScore: Float): String {
        val scores = if (current.isNotEmpty()) {
            current.split(",").map { it.toFloat() }.toMutableList()
        } else {
            mutableListOf()
        }

        scores.add(newScore)
        if (scores.size > 5) {
            scores.removeAt(0) // Keep only last 5
        }

        return scores.joinToString(",")
    }

    private fun calculateTrend(recentScoresStr: String): TrendDirection {
        if (recentScoresStr.isEmpty()) return TrendDirection.STABLE

        val scores = recentScoresStr.split(",").map { it.toFloat() }
        if (scores.size < 3) return TrendDirection.STABLE

        // Simple linear regression slope
        val n = scores.size
        val sumX = (0 until n).sum()
        val sumY = scores.sum()
        val sumXY = scores.mapIndexed { index, score -> index * score }.sum()
        val sumX2 = (0 until n).sumOf { it * it }

        val slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)

        return when {
            slope > 2.0 -> TrendDirection.IMPROVING
            slope < -2.0 -> TrendDirection.DECLINING
            else -> TrendDirection.STABLE
        }
    }

    private fun generateCommunicationFeedback(score: Float, improvement: Float?, entry: LogbookEntry): String {
        val accuracy = if (entry.totalCommunications > 0) {
            (entry.correctCommunications.toFloat() / entry.totalCommunications) * 100
        } else 0f

        return when {
            score >= 95 -> "Excellent radio communication! Your phraseology is precise and professional."
            score >= 85 -> "Strong communication skills. ${if (improvement != null && improvement > 0) "Keep up the good progress!" else "Maintain this level of proficiency."}"
            score >= 70 -> "Solid communication foundation. ${if (accuracy < 85) "Focus on readback accuracy." else "Continue practicing."}"
            score >= 60 -> "Communication skills are developing. Practice more complex clearances and frequency changes."
            else -> "Radio communication needs significant work. Focus on basic phraseology and standard calls."
        }
    }

    private fun generateNavigationFeedback(score: Float, improvement: Float?, entry: LogbookEntry): String {
        return when {
            score >= 95 -> "Outstanding navigation skills! You maintain excellent situational awareness."
            score >= 85 -> "Very good navigation. Route planning and execution are solid."
            score >= 70 -> "Good navigation fundamentals. Continue practicing cross-country planning."
            score >= 60 -> "Navigation skills need refinement. Focus on waypoint tracking and position reporting."
            else -> "Navigation requires significant improvement. Practice basic pilotage and dead reckoning."
        }
    }

    private fun generateWeatherFeedback(score: Float, improvement: Float?, entry: LogbookEntry): String {
        return when {
            score >= 95 -> "Excellent weather decision making! You make safe, informed go/no-go decisions."
            score >= 85 -> "Strong weather analysis skills. You understand weather impacts on flight."
            score >= 70 -> "Good weather awareness. Continue studying METAR/TAF interpretation."
            score >= 60 -> "Weather decision making is developing. Practice more marginal VFR scenarios."
            else -> "Weather decision making needs work. Review VFR minimums and safety margins."
        }
    }

    private fun generateTrafficFeedback(score: Float, improvement: Float?, entry: LogbookEntry): String {
        return when {
            score >= 95 -> "Excellent traffic management! You maintain proper awareness and spacing."
            score >= 85 -> "Strong traffic awareness. You respond well to traffic conflicts."
            score >= 70 -> "Good traffic management basics. Continue practicing busy pattern work."
            score >= 60 -> "Traffic awareness is improving. Practice see-and-avoid and right-of-way rules."
            else -> "Traffic management needs significant attention. Review traffic pattern procedures."
        }
    }

    private fun generateEmergencyFeedback(score: Float, improvement: Float?, entry: LogbookEntry): String {
        return when {
            score >= 95 -> "Outstanding emergency handling! You stay calm and execute proper procedures."
            score >= 85 -> "Very good emergency response. You prioritize correctly: Aviate, Navigate, Communicate."
            score >= 70 -> "Solid emergency procedures. Continue practicing abnormal situations."
            score >= 60 -> "Emergency handling is developing. Review POH emergency procedures."
            else -> "Emergency procedures need more practice. Focus on emergency checklists and decision making."
        }
    }

    private fun generateAirspaceFeedback(score: Float, improvement: Float?): String {
        return when {
            score >= 95 -> "Excellent airspace knowledge! You navigate complex airspace confidently."
            score >= 85 -> "Strong understanding of airspace classifications and requirements."
            score >= 70 -> "Good airspace fundamentals. Continue studying Class B/C/D procedures."
            else -> "Airspace knowledge needs improvement. Review classifications and entry requirements."
        }
    }

    private fun generateSituationalFeedback(score: Float, improvement: Float?): String {
        return when {
            score >= 95 -> "Outstanding situational awareness! You maintain excellent awareness of all factors."
            score >= 85 -> "Strong situational awareness. You stay ahead of the aircraft."
            score >= 70 -> "Good situational awareness developing. Continue building your mental picture."
            else -> "Work on maintaining better awareness of aircraft state and environment."
        }
    }

    private fun generateDecisionMakingFeedback(score: Float, improvement: Float?): String {
        return when {
            score >= 95 -> "Excellent ADM! You make safe, timely decisions under pressure."
            score >= 85 -> "Strong decision-making skills. You assess risks appropriately."
            score >= 70 -> "Good decision-making foundation. Continue developing your judgment."
            else -> "Decision-making needs improvement. Review the DECIDE model and risk management."
        }
    }

    private fun generateFuelFeedback(score: Float, improvement: Float?): String {
        return when {
            score >= 95 -> "Excellent fuel management! You plan and monitor fuel meticulously."
            score >= 85 -> "Strong fuel planning. You maintain appropriate reserves."
            score >= 70 -> "Good fuel awareness. Continue practicing fuel calculations."
            else -> "Fuel management needs attention. Review fuel planning and reserve requirements."
        }
    }

    private fun generateChecklistFeedback(score: Float, improvement: Float?): String {
        return when {
            score >= 95 -> "Perfect checklist discipline! You follow procedures precisely."
            score >= 85 -> "Strong checklist usage. You're methodical and thorough."
            score >= 70 -> "Good checklist habits forming. Continue practicing proper flows."
            else -> "Checklist discipline needs improvement. Slow down and be more thorough."
        }
    }
}

/**
 * Pilot rank based on overall proficiency
 */
enum class PilotRank(val displayName: String, val icon: String, val description: String) {
    STUDENT_PILOT(
        "Student Pilot",
        "🎓",
        "Learning the fundamentals of flight training"
    ),
    SOLO_STUDENT(
        "Solo Student",
        "✈️",
        "Demonstrating basic proficiency for solo flight"
    ),
    PRIVATE_PILOT(
        "Private Pilot",
        "🛩️",
        "Proficient in VFR operations and procedures"
    ),
    COMMERCIAL_PILOT(
        "Commercial Pilot",
        "🚁",
        "Advanced skills and commercial-level proficiency"
    ),
    CERTIFIED_INSTRUCTOR(
        "CFI Level",
        "👨‍✈️",
        "Expert proficiency suitable for instruction"
    ),
    MASTER_INSTRUCTOR(
        "Master CFI",
        "🏆",
        "Mastery of all flight training aspects"
    )
}
