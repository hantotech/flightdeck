package com.example.flightdeck.data.repository

import com.example.flightdeck.data.local.dao.MissionConfigDao
import com.example.flightdeck.data.model.*
import com.example.flightdeck.utils.ChallengeValidator
import com.example.flightdeck.utils.UserStats
import kotlinx.coroutines.flow.Flow

/**
 * Repository for mission configurations
 * Handles CRUD operations and provides preset templates
 */
class MissionConfigRepository(
    private val missionConfigDao: MissionConfigDao
) {

    // ========================================
    // CRUD Operations
    // ========================================

    suspend fun create(missionConfig: MissionConfig): Long {
        // Validate before inserting
        val validation = ChallengeValidator.validate(missionConfig)
        if (!validation.isValid) {
            throw IllegalArgumentException("Invalid mission configuration: ${validation.conflicts.joinToString()}")
        }

        // Calculate difficulty and duration
        val updatedConfig = missionConfig.copy(
            estimatedDifficultyStars = missionConfig.calculateDifficulty(),
            estimatedDurationMinutes = missionConfig.estimateDuration(30) // Default 30 min base flight
        )

        return missionConfigDao.insert(updatedConfig)
    }

    suspend fun update(missionConfig: MissionConfig) {
        missionConfigDao.update(missionConfig)
    }

    suspend fun delete(missionConfig: MissionConfig) {
        if (!missionConfig.isUserCreated) {
            throw IllegalArgumentException("Cannot delete preset configurations")
        }
        missionConfigDao.delete(missionConfig)
    }

    suspend fun getById(id: Long): MissionConfig? {
        return missionConfigDao.getById(id)
    }

    fun getByIdFlow(id: Long): Flow<MissionConfig?> {
        return missionConfigDao.getByIdFlow(id)
    }

    suspend fun getByFlightPlanId(flightPlanId: Long): List<MissionConfig> {
        return missionConfigDao.getByFlightPlanId(flightPlanId)
    }

    fun getByFlightPlanIdFlow(flightPlanId: Long): Flow<List<MissionConfig>> {
        return missionConfigDao.getByFlightPlanIdFlow(flightPlanId)
    }

    // ========================================
    // Presets
    // ========================================

    suspend fun getAllPresets(): List<MissionConfig> {
        return missionConfigDao.getAllPresets()
    }

    fun getAllPresetsFlow(): Flow<List<MissionConfig>> {
        return missionConfigDao.getAllPresetsFlow()
    }

    /**
     * Initialize default preset templates in the database
     * Call this once during app initialization
     */
    suspend fun initializePresets(flightPlanId: Long) {
        val existingPresets = missionConfigDao.getPresetCount()
        if (existingPresets > 0) return // Already initialized

        val presets = createDefaultPresets(flightPlanId)
        missionConfigDao.insertAll(presets)
    }

    private fun createDefaultPresets(flightPlanId: Long): List<MissionConfig> {
        return listOf(
            // ========== BEGINNER PRESETS ==========
            MissionConfig(
                name = "First Solo",
                flightPlanId = flightPlanId,
                baseDifficulty = Difficulty.BEGINNER,
                trafficDensity = TrafficDensity.LIGHT,
                weatherComplexity = WeatherComplexity.CLEAR_CALM,
                communicationPace = CommunicationPace.RELAXED,
                selectedChallenges = "",
                estimatedDifficultyStars = 1,
                estimatedDurationMinutes = 25,
                isPreset = true,
                isUserCreated = false
            ),

            MissionConfig(
                name = "Pattern Practice",
                flightPlanId = flightPlanId,
                baseDifficulty = Difficulty.BEGINNER,
                trafficDensity = TrafficDensity.MODERATE,
                weatherComplexity = WeatherComplexity.TYPICAL_VFR,
                communicationPace = CommunicationPace.MODERATE,
                selectedChallenges = "",
                estimatedDifficultyStars = 1,
                estimatedDurationMinutes = 30,
                isPreset = true,
                isUserCreated = false
            ),

            // ========== INTERMEDIATE PRESETS ==========
            MissionConfig(
                name = "Cross-Country VFR",
                flightPlanId = flightPlanId,
                baseDifficulty = Difficulty.INTERMEDIATE,
                trafficDensity = TrafficDensity.MODERATE,
                weatherComplexity = WeatherComplexity.TYPICAL_VFR,
                communicationPace = CommunicationPace.MODERATE,
                selectedChallenges = listOf(
                    ChallengeModule.RAPID_FREQUENCY_CHANGES,
                    ChallengeModule.TRAFFIC_CONFLICTS
                ).joinToString(",") { it.name },
                estimatedDifficultyStars = 2,
                estimatedDurationMinutes = 35,
                isPreset = true,
                isUserCreated = false
            ),

            MissionConfig(
                name = "Busy Airspace",
                flightPlanId = flightPlanId,
                baseDifficulty = Difficulty.INTERMEDIATE,
                trafficDensity = TrafficDensity.BUSY,
                weatherComplexity = WeatherComplexity.TYPICAL_VFR,
                communicationPace = CommunicationPace.BUSY,
                selectedChallenges = listOf(
                    ChallengeModule.TRAFFIC_CONFLICTS,
                    ChallengeModule.GO_AROUND_PRACTICE,
                    ChallengeModule.COMPLEX_CLEARANCES
                ).joinToString(",") { it.name },
                estimatedDifficultyStars = 3,
                estimatedDurationMinutes = 40,
                isPreset = true,
                isUserCreated = false
            ),

            MissionConfig(
                name = "Weather Challenge",
                flightPlanId = flightPlanId,
                baseDifficulty = Difficulty.INTERMEDIATE,
                trafficDensity = TrafficDensity.MODERATE,
                weatherComplexity = WeatherComplexity.CHALLENGING,
                communicationPace = CommunicationPace.MODERATE,
                selectedChallenges = listOf(
                    ChallengeModule.WEATHER_DETERIORATION,
                    ChallengeModule.CROSSWIND_LANDING,
                    ChallengeModule.RUNWAY_CHANGES
                ).joinToString(",") { it.name },
                estimatedDifficultyStars = 3,
                estimatedDurationMinutes = 38,
                isPreset = true,
                isUserCreated = false
            ),

            // ========== ADVANCED PRESETS ==========
            MissionConfig(
                name = "Complex Cross-Country",
                flightPlanId = flightPlanId,
                baseDifficulty = Difficulty.ADVANCED,
                trafficDensity = TrafficDensity.BUSY,
                weatherComplexity = WeatherComplexity.MARGINAL,
                communicationPace = CommunicationPace.BUSY,
                selectedChallenges = listOf(
                    ChallengeModule.RAPID_FREQUENCY_CHANGES,
                    ChallengeModule.WEATHER_DETERIORATION,
                    ChallengeModule.TRAFFIC_CONFLICTS,
                    ChallengeModule.CLASS_B_TRANSITION
                ).joinToString(",") { it.name },
                estimatedDifficultyStars = 4,
                estimatedDurationMinutes = 45,
                isPreset = true,
                isUserCreated = false
            ),

            MissionConfig(
                name = "Emergency Training",
                flightPlanId = flightPlanId,
                baseDifficulty = Difficulty.ADVANCED,
                trafficDensity = TrafficDensity.MODERATE,
                weatherComplexity = WeatherComplexity.TYPICAL_VFR,
                communicationPace = CommunicationPace.MODERATE,
                selectedChallenges = listOf(
                    ChallengeModule.ENGINE_ROUGHNESS,
                    ChallengeModule.FUEL_MANAGEMENT
                ).joinToString(",") { it.name },
                estimatedDifficultyStars = 4,
                estimatedDurationMinutes = 42,
                isPreset = true,
                isUserCreated = false
            ),

            MissionConfig(
                name = "Advanced Procedures",
                flightPlanId = flightPlanId,
                baseDifficulty = Difficulty.ADVANCED,
                trafficDensity = TrafficDensity.BUSY,
                weatherComplexity = WeatherComplexity.CHALLENGING,
                communicationPace = CommunicationPace.BUSY,
                selectedChallenges = listOf(
                    ChallengeModule.HOLD_INSTRUCTIONS,
                    ChallengeModule.COMPLEX_CLEARANCES,
                    ChallengeModule.AIRSPACE_RESTRICTIONS
                ).joinToString(",") { it.name },
                estimatedDifficultyStars = 4,
                estimatedDurationMinutes = 50,
                isPreset = true,
                isUserCreated = false
            ),

            // ========== EXPERT PRESETS ==========
            MissionConfig(
                name = "Maximum Challenge",
                flightPlanId = flightPlanId,
                baseDifficulty = Difficulty.EXPERT,
                trafficDensity = TrafficDensity.CONGESTED,
                weatherComplexity = WeatherComplexity.DYNAMIC,
                communicationPace = CommunicationPace.RAPID,
                selectedChallenges = listOf(
                    ChallengeModule.RAPID_FREQUENCY_CHANGES,
                    ChallengeModule.TRAFFIC_CONFLICTS,
                    ChallengeModule.WEATHER_DETERIORATION,
                    ChallengeModule.SIMILAR_CALLSIGNS,
                    ChallengeModule.COMPLEX_CLEARANCES
                ).joinToString(",") { it.name },
                estimatedDifficultyStars = 5,
                estimatedDurationMinutes = 55,
                isPreset = true,
                isUserCreated = false
            ),

            MissionConfig(
                name = "Total Emergency",
                flightPlanId = flightPlanId,
                baseDifficulty = Difficulty.EXPERT,
                trafficDensity = TrafficDensity.BUSY,
                weatherComplexity = WeatherComplexity.MARGINAL,
                communicationPace = CommunicationPace.BUSY,
                selectedChallenges = listOf(
                    ChallengeModule.RANDOM_EMERGENCY,
                    ChallengeModule.WEATHER_DETERIORATION,
                    ChallengeModule.TRAFFIC_CONFLICTS
                ).joinToString(",") { it.name },
                estimatedDifficultyStars = 5,
                estimatedDurationMinutes = 48,
                isPreset = true,
                isUserCreated = false
            )
        )
    }

    // ========================================
    // User-Created Missions
    // ========================================

    suspend fun getAllUserCreated(): List<MissionConfig> {
        return missionConfigDao.getAllUserCreated()
    }

    fun getAllUserCreatedFlow(): Flow<List<MissionConfig>> {
        return missionConfigDao.getAllUserCreatedFlow()
    }

    suspend fun getRecentlyFlown(limit: Int = 5): List<MissionConfig> {
        return missionConfigDao.getRecentlyFlown(limit)
    }

    fun getRecentlyFlownFlow(limit: Int = 5): Flow<List<MissionConfig>> {
        return missionConfigDao.getRecentlyFlownFlow(limit)
    }

    suspend fun getMostFlown(limit: Int = 5): List<MissionConfig> {
        return missionConfigDao.getMostFlown(limit)
    }

    // ========================================
    // Mission Completion
    // ========================================

    suspend fun recordMissionCompletion(missionId: Long, score: Float) {
        val mission = getById(missionId) ?: return

        // Update times flown
        missionConfigDao.incrementTimesFlown(missionId)

        // Calculate new average score
        val currentAvg = mission.averageScore ?: 0f
        val timesFlown = mission.timesFlown + 1
        val newAvg = ((currentAvg * mission.timesFlown) + score) / timesFlown

        missionConfigDao.updateAverageScore(missionId, newAvg)
    }

    // ========================================
    // Validation & Recommendations
    // ========================================

    fun validateConfig(missionConfig: MissionConfig): MissionValidationResult {
        return ChallengeValidator.validate(missionConfig)
    }

    fun getRecommendations(userStats: UserStats, difficulty: Difficulty) =
        ChallengeValidator.getRecommendations(userStats, difficulty)

    fun getChallengeInfo(challenge: ChallengeModule) =
        ChallengeValidator.getChallengeInfo(challenge)

    fun isUnlocked(challenge: ChallengeModule, userStats: UserStats) =
        ChallengeValidator.isUnlocked(challenge, userStats)
}
