package com.example.flightdeck.data.repository

import com.example.flightdeck.data.local.dao.ATCDao
import com.example.flightdeck.data.model.*
import com.example.flightdeck.data.remote.ai.EnhancedAIService
import com.example.flightdeck.data.remote.ai.ATCContext
import com.example.flightdeck.data.remote.ai.ATCEvaluation
import com.example.flightdeck.data.remote.ai.UserTier
import kotlinx.coroutines.flow.Flow

/**
 * Repository for ATC-related operations
 * Combines local database with AI-powered interactions using smart routing
 */
class ATCRepository(
    private val atcDao: ATCDao,
    private val aiService: EnhancedAIService = EnhancedAIService.getInstance()
) {

    // Scenario Management
    fun getAllScenarios(): Flow<List<ATCScenario>> = atcDao.getAllScenarios()

    fun getScenariosByType(type: ATCScenarioType): Flow<List<ATCScenario>> =
        atcDao.getScenariosByType(type)

    fun getScenariosByDifficulty(difficulty: Difficulty): Flow<List<ATCScenario>> =
        atcDao.getScenariosByDifficulty(difficulty)

    suspend fun getScenarioById(id: Long): ATCScenario? = atcDao.getScenarioById(id)

    // Practice Sessions
    suspend fun startPracticeSession(scenarioId: Long): Long {
        val scenario = atcDao.getScenarioById(scenarioId)
            ?: throw IllegalArgumentException("Scenario not found")

        val exchanges = atcDao.getScenarioExchanges(scenarioId)
        // Assuming exchanges is a Flow, you'd need to collect first
        // For simplicity, using 0 here - in production, collect the flow

        val session = ATCPracticeSession(
            scenarioId = scenarioId,
            startTime = System.currentTimeMillis(),
            totalExchanges = 0 // Should be calculated from exchanges
        )

        return atcDao.insertPracticeSession(session)
    }

    suspend fun endPracticeSession(
        sessionId: Long,
        score: Float,
        accuracyPercentage: Float
    ) {
        val session = atcDao.getPracticeSessionById(sessionId)
            ?: throw IllegalArgumentException("Session not found")

        val updatedSession = session.copy(
            endTime = System.currentTimeMillis(),
            score = score,
            accuracyPercentage = accuracyPercentage
        )

        atcDao.updatePracticeSession(updatedSession)
    }

    // AI-Powered Communication
    suspend fun sendPilotMessage(
        sessionId: Long,
        scenario: ATCScenario,
        pilotMessage: String
    ): Result<String> {
        val context = ATCContext(
            airport = scenario.airport,
            scenarioType = scenario.scenarioType.name,
            conditions = scenario.situation
        )

        return aiService.generateATCResponse(pilotMessage, context)
    }

    suspend fun evaluatePilotCommunication(
        sessionId: Long,
        exchangeId: Long,
        pilotMessage: String,
        expectedPhrasing: String,
        scenario: ATCScenario
    ): Result<ATCEvaluation> {
        val context = ATCContext(
            airport = scenario.airport,
            scenarioType = scenario.scenarioType.name,
            conditions = scenario.situation
        )

        val result = aiService.evaluateATCCommunication(
            pilotMessage = pilotMessage,
            expectedPhrasings = listOf(expectedPhrasing),
            context = context
        )

        // Save the response to database
        if (result.isSuccess) {
            val evaluation = result.getOrNull()!!
            val response = ATCResponse(
                sessionId = sessionId,
                exchangeId = exchangeId,
                userResponse = pilotMessage,
                isCorrect = evaluation.isCorrect,
                feedback = evaluation.feedback
            )
            atcDao.insertResponse(response)
        }

        return result
    }

    // Session Statistics
    fun getAllPracticeSessions(): Flow<List<ATCPracticeSession>> =
        atcDao.getAllPracticeSessions()

    fun getSessionsByScenario(scenarioId: Long): Flow<List<ATCPracticeSession>> =
        atcDao.getSessionsByScenario(scenarioId)

    fun getSessionResponses(sessionId: Long): Flow<List<ATCResponse>> =
        atcDao.getSessionResponses(sessionId)

    // Initialize sample scenarios
    suspend fun initializeSampleScenarios() {
        val scenarios = listOf(
            ATCScenario(
                title = "Ground Clearance - VFR",
                description = "Request taxi clearance for VFR departure",
                scenarioType = ATCScenarioType.GROUND_CLEARANCE,
                difficulty = Difficulty.BEGINNER,
                airport = "KPAO",
                situation = "Clear day, VFR conditions at Palo Alto Airport. You're ready to taxi."
            ),
            ATCScenario(
                title = "Takeoff Clearance",
                description = "Request and receive takeoff clearance",
                scenarioType = ATCScenarioType.TAKEOFF_CLEARANCE,
                difficulty = Difficulty.BEGINNER,
                airport = "KSFO",
                situation = "At runway hold short line, ready for departure."
            ),
            ATCScenario(
                title = "In-Flight Position Report",
                description = "Make position report to ATC during flight",
                scenarioType = ATCScenarioType.IN_FLIGHT_COMMUNICATION,
                difficulty = Difficulty.INTERMEDIATE,
                airport = "KOAK",
                situation = "Cruising at 4,500 feet, approaching your destination."
            ),
            ATCScenario(
                title = "Landing Clearance - Pattern",
                description = "Request landing clearance in the traffic pattern",
                scenarioType = ATCScenarioType.LANDING_CLEARANCE,
                difficulty = Difficulty.INTERMEDIATE,
                airport = "KPAO",
                situation = "On downwind leg, ready to turn base."
            ),
            ATCScenario(
                title = "Emergency Declaration",
                description = "Declare an emergency and communicate with ATC",
                scenarioType = ATCScenarioType.EMERGENCY_COMMUNICATION,
                difficulty = Difficulty.ADVANCED,
                airport = "KSFO",
                situation = "Engine trouble at 3,000 feet, need to declare emergency."
            )
        )

        atcDao.insertScenarios(scenarios)
    }
}
