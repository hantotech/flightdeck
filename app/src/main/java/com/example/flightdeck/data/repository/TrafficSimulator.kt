package com.example.flightdeck.data.repository

import com.example.flightdeck.data.local.dao.TrafficDao
import com.example.flightdeck.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Simulates realistic air traffic for ATC practice scenarios
 * NOT real-time data - generated for educational purposes
 */
class TrafficSimulator(
    private val trafficDao: TrafficDao,
    private val airportRepository: AirportRepository
) {

    /**
     * Start a traffic simulation session
     */
    suspend fun startSimulation(
        sessionId: Long,
        airport: Airport,
        density: TrafficDensity = getRealisticDensity(airport),
        durationMinutes: Int = 30
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Generate initial traffic
            val trafficCount = getInitialTrafficCount(density)
            val traffic = TrafficGenerator.generateTrafficForAirport(airport, sessionId, count = trafficCount)
            trafficDao.insertTraffic(traffic)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get realistic traffic density based on airport characteristics
     */
    private fun getRealisticDensity(airport: Airport): TrafficDensity {
        return when {
            !airport.towerControlled -> TrafficDensity.LIGHT
            airport.airspaceClass == AirspaceClass.B -> TrafficDensity.VERY_BUSY
            airport.airspaceClass == AirspaceClass.C -> TrafficDensity.BUSY
            airport.airspaceClass == AirspaceClass.D -> TrafficDensity.MODERATE
            airport.type == AirportType.LARGE_AIRPORT -> TrafficDensity.BUSY
            airport.type == AirportType.MEDIUM_AIRPORT -> TrafficDensity.MODERATE
            else -> TrafficDensity.LIGHT
        }
    }

    /**
     * Get initial traffic count based on density
     */
    private fun getInitialTrafficCount(density: TrafficDensity): Int {
        return when (density) {
            TrafficDensity.NONE -> 0
            TrafficDensity.LIGHT -> Random.nextInt(1, 4)
            TrafficDensity.MODERATE -> Random.nextInt(3, 7)
            TrafficDensity.BUSY -> Random.nextInt(6, 12)
            TrafficDensity.VERY_BUSY -> Random.nextInt(10, 20)
        }
    }

    /**
     * Get active traffic for session
     */
    suspend fun getActiveTraffic(sessionId: Long): List<SimulatedTraffic> = withContext(Dispatchers.IO) {
        trafficDao.getActiveTrafficForSession(sessionId)
    }

    /**
     * Observe active traffic with real-time updates
     */
    fun observeActiveTraffic(sessionId: Long): Flow<List<SimulatedTraffic>> {
        return trafficDao.observeActiveTrafficForSession(sessionId)
    }

    /**
     * Add new traffic to simulation
     */
    suspend fun addTraffic(
        sessionId: Long,
        airport: Airport,
        count: Int = 1
    ): List<SimulatedTraffic> = withContext(Dispatchers.IO) {
        val newTraffic = TrafficGenerator.generateTrafficForAirport(airport, sessionId, count = count)
        trafficDao.insertTraffic(newTraffic)
        newTraffic
    }

    /**
     * Update traffic position (simulates movement)
     */
    suspend fun updateTrafficPosition(
        trafficId: Long,
        newPosition: TrafficPosition,
        altitude: Int? = null,
        heading: Int? = null,
        speed: Int? = null
    ) = withContext(Dispatchers.IO) {
        val traffic = trafficDao.getTrafficById(trafficId) ?: return@withContext

        val updated = traffic.copy(
            currentPosition = newPosition,
            altitude = altitude ?: traffic.altitude,
            heading = heading ?: traffic.heading,
            speed = speed ?: traffic.speed,
            lastUpdate = System.currentTimeMillis()
        )

        trafficDao.updateTraffic(updated)
    }

    /**
     * Generate traffic events for scenario realism
     */
    suspend fun generateTrafficEvents(
        sessionId: Long,
        includeConflicts: Boolean = false
    ): List<TrafficEvent> = withContext(Dispatchers.IO) {
        val activeTraffic = trafficDao.getActiveTrafficForSession(sessionId)
        val events = mutableListOf<TrafficEvent>()

        activeTraffic.forEach { traffic ->
            val eventType = when (traffic.currentPosition) {
                TrafficPosition.ON_GROUND_RUNWAY_HOLDING -> TrafficEventType.AIRCRAFT_HOLDING_SHORT
                TrafficPosition.DEPARTING -> TrafficEventType.AIRCRAFT_CALLED_READY
                TrafficPosition.IN_PATTERN_FINAL -> TrafficEventType.AIRCRAFT_ON_FINAL
                TrafficPosition.LANDED_ROLLOUT -> TrafficEventType.AIRCRAFT_LANDED
                TrafficPosition.ON_GROUND_TAXIWAY -> TrafficEventType.AIRCRAFT_TAXIING
                else -> null
            }

            if (eventType != null) {
                events.add(
                    TrafficEvent(
                        traffic = traffic,
                        eventType = eventType,
                        description = TrafficGenerator.generateTrafficAnnouncement(traffic),
                        affectsUserOperation = shouldAffectUser(traffic)
                    )
                )
            }
        }

        // Add conflict scenarios if requested (educational)
        if (includeConflicts && Random.nextDouble() < 0.2) {
            activeTraffic.randomOrNull()?.let { traffic ->
                events.add(
                    TrafficEvent(
                        traffic = traffic,
                        eventType = TrafficEventType.CONFLICT_POTENTIAL,
                        description = "Traffic conflict potential: ${traffic.aircraftCallsign} also requesting runway access",
                        affectsUserOperation = true
                    )
                )
            }
        }

        events
    }

    /**
     * Determine if traffic should affect user's operation
     */
    private fun shouldAffectUser(traffic: SimulatedTraffic): Boolean {
        return when (traffic.currentPosition) {
            TrafficPosition.ON_GROUND_RUNWAY_HOLDING,
            TrafficPosition.ON_GROUND_RUNWAY_ACTIVE,
            TrafficPosition.IN_PATTERN_FINAL,
            TrafficPosition.DEPARTING -> true
            else -> false
        }
    }

    /**
     * Get traffic advisories for ATC communication
     */
    suspend fun getTrafficAdvisories(
        sessionId: Long,
        userPosition: TrafficPosition
    ): List<String> = withContext(Dispatchers.IO) {
        val activeTraffic = trafficDao.getActiveTrafficForSession(sessionId)
        val advisories = mutableListOf<String>()

        // Filter relevant traffic based on user position
        val relevantTraffic = activeTraffic.filter { traffic ->
            isTrafficRelevant(userPosition, traffic.currentPosition)
        }

        relevantTraffic.forEach { traffic ->
            advisories.add(TrafficGenerator.generateTrafficAnnouncement(traffic))
        }

        advisories
    }

    /**
     * Determine if traffic is relevant to user's current position
     */
    private fun isTrafficRelevant(userPosition: TrafficPosition, trafficPosition: TrafficPosition): Boolean {
        return when (userPosition) {
            TrafficPosition.ON_GROUND_RUNWAY_HOLDING -> {
                // User waiting to takeoff - care about runway traffic
                trafficPosition in listOf(
                    TrafficPosition.ON_GROUND_RUNWAY_ACTIVE,
                    TrafficPosition.IN_PATTERN_FINAL,
                    TrafficPosition.DEPARTING,
                    TrafficPosition.LANDED_ROLLOUT
                )
            }
            TrafficPosition.IN_PATTERN_FINAL -> {
                // User on final - care about other pattern traffic and runway
                trafficPosition in listOf(
                    TrafficPosition.IN_PATTERN_DOWNWIND,
                    TrafficPosition.IN_PATTERN_BASE,
                    TrafficPosition.IN_PATTERN_FINAL,
                    TrafficPosition.ON_GROUND_RUNWAY_ACTIVE,
                    TrafficPosition.DEPARTING
                )
            }
            TrafficPosition.ON_GROUND_TAXIWAY -> {
                // User taxiing - care about taxiway and runway crossings
                trafficPosition in listOf(
                    TrafficPosition.ON_GROUND_TAXIWAY,
                    TrafficPosition.ON_GROUND_RUNWAY_ACTIVE
                )
            }
            else -> {
                // General awareness
                trafficPosition != TrafficPosition.ON_GROUND_RAMP
            }
        }
    }

    /**
     * Advance traffic simulation (move aircraft to next positions)
     */
    suspend fun advanceSimulation(sessionId: Long) = withContext(Dispatchers.IO) {
        val activeTraffic = trafficDao.getActiveTrafficForSession(sessionId)

        activeTraffic.forEach { traffic ->
            val nextPosition = getNextPosition(traffic)
            if (nextPosition != null) {
                updateTrafficPosition(traffic.id, nextPosition)
            } else {
                // Traffic has completed its cycle - remove it
                trafficDao.deactivateTraffic(traffic.id)
            }
        }
    }

    /**
     * Get logical next position for traffic progression
     */
    private fun getNextPosition(traffic: SimulatedTraffic): TrafficPosition? {
        return when (traffic.currentPosition) {
            TrafficPosition.ON_GROUND_RAMP -> TrafficPosition.ON_GROUND_TAXIWAY
            TrafficPosition.ON_GROUND_TAXIWAY -> TrafficPosition.ON_GROUND_RUNWAY_HOLDING
            TrafficPosition.ON_GROUND_RUNWAY_HOLDING -> TrafficPosition.ON_GROUND_RUNWAY_ACTIVE
            TrafficPosition.ON_GROUND_RUNWAY_ACTIVE -> TrafficPosition.DEPARTING
            TrafficPosition.DEPARTING -> null // Leaves airspace
            TrafficPosition.APPROACHING -> TrafficPosition.IN_PATTERN_DOWNWIND
            TrafficPosition.IN_PATTERN_DOWNWIND -> TrafficPosition.IN_PATTERN_BASE
            TrafficPosition.IN_PATTERN_BASE -> TrafficPosition.IN_PATTERN_FINAL
            TrafficPosition.IN_PATTERN_FINAL -> TrafficPosition.LANDED_ROLLOUT
            TrafficPosition.LANDED_ROLLOUT -> null // Clear of runway, done
            TrafficPosition.OVERFLYING -> null // Passes through
        }
    }

    /**
     * Stop simulation and clean up
     */
    suspend fun stopSimulation(sessionId: Long) = withContext(Dispatchers.IO) {
        trafficDao.deactivateAllTrafficForSession(sessionId)
    }

    /**
     * Clean up old inactive traffic (older than 24 hours)
     */
    suspend fun cleanupOldTraffic() = withContext(Dispatchers.IO) {
        val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        trafficDao.deleteOldInactiveTraffic(oneDayAgo)
    }

    companion object {
        @Volatile
        private var INSTANCE: TrafficSimulator? = null

        fun getInstance(
            trafficDao: TrafficDao,
            airportRepository: AirportRepository
        ): TrafficSimulator {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TrafficSimulator(trafficDao, airportRepository).also { INSTANCE = it }
            }
        }
    }
}
