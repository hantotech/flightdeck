package com.example.flightdeck.data.repository

import com.example.flightdeck.data.local.dao.ATCDao
import com.example.flightdeck.data.model.ATCScenario
import com.example.flightdeck.data.model.ATCScenarioType
import com.example.flightdeck.data.model.Difficulty
import com.example.flightdeck.data.model.ATCPracticeSession
import com.example.flightdeck.data.model.TrafficDensity
import com.example.flightdeck.data.model.ATCResponse
import com.example.flightdeck.data.model.TrafficPosition
import com.example.flightdeck.data.model.TrafficGenerator
import com.example.flightdeck.data.remote.ai.EnhancedAIService
import com.example.flightdeck.data.remote.ai.ATCContext
import com.example.flightdeck.data.remote.ai.ATCEvaluation
import com.example.flightdeck.data.remote.ai.UserTier
import kotlinx.coroutines.flow.Flow

/**
 * Repository for ATC-related operations
 * Combines local database with AI-powered interactions using smart routing
 * Now includes real-time airport data and traffic simulation
 */
class ATCRepository(
    private val atcDao: ATCDao,
    private val airportRepository: AirportRepository,
    private val trafficSimulator: TrafficSimulator,
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

        val session = ATCPracticeSession(
            scenarioId = scenarioId,
            startTime = System.currentTimeMillis(),
            totalExchanges = 0 // Will be updated as session progresses
        )

        val sessionId = atcDao.insertPracticeSession(session)

        // Get airport data
        val airportData = airportRepository.getAirportByIcao(scenario.airport)

        // Start traffic simulation if airport found
        airportData.onSuccess { data ->
            trafficSimulator.startSimulation(
                sessionId = sessionId,
                airport = data.airport,
                density = getScenarioDensity(scenario)
            )
        }

        return sessionId
    }

    /**
     * Get appropriate traffic density for scenario
     */
    private fun getScenarioDensity(scenario: ATCScenario): TrafficDensity {
        return when (scenario.difficulty) {
            Difficulty.BEGINNER -> TrafficDensity.LIGHT
            Difficulty.INTERMEDIATE -> TrafficDensity.MODERATE
            Difficulty.ADVANCED -> TrafficDensity.BUSY
            else -> TrafficDensity.MODERATE
        }
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

        // Stop traffic simulation
        trafficSimulator.stopSimulation(sessionId)
    }

    // Airport & Traffic Information
    suspend fun getAirportInfo(icao: String) = airportRepository.getAirportByIcao(icao)

    suspend fun getRunways(icao: String) = airportRepository.getRunwaysForAirport(icao)

    suspend fun getFrequencies(icao: String) = airportRepository.getFrequenciesForAirport(icao)

    suspend fun getActiveTraffic(sessionId: Long) = trafficSimulator.getActiveTraffic(sessionId)

    fun observeActiveTraffic(sessionId: Long) = trafficSimulator.observeActiveTraffic(sessionId)

    suspend fun getTrafficAdvisories(sessionId: Long, userPosition: TrafficPosition) =
        trafficSimulator.getTrafficAdvisories(sessionId, userPosition)

    // AI-Powered Communication with Context
    suspend fun sendPilotMessage(
        sessionId: Long,
        scenario: ATCScenario,
        pilotMessage: String,
        userPosition: TrafficPosition? = null
    ): Result<String> {
        // Get airport data for enhanced context
        val airportData = airportRepository.getAirportByIcao(scenario.airport)
        val activeTraffic = trafficSimulator.getActiveTraffic(sessionId)

        // Build traffic context string
        val trafficContext = if (activeTraffic.isNotEmpty()) {
            "\n\nCURRENT TRAFFIC:\n" + activeTraffic.take(5).joinToString("\n") { traffic ->
                "- ${TrafficGenerator.generateTrafficAnnouncement(traffic)}"
            }
        } else ""

        // Build runway context string
        val runwayContext = airportData.fold(
            onSuccess = { data ->
                if (data.runways.isNotEmpty()) {
                    "\n\nACTIVE RUNWAYS:\n" + data.runways.take(2).joinToString(", ") { it.identifier }
                } else ""
            },
            onFailure = { "" }
        )

        val enhancedConditions = scenario.situation + trafficContext + runwayContext

        val context = ATCContext(
            airport = scenario.airport,
            scenarioType = scenario.scenarioType.name,
            conditions = enhancedConditions
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
