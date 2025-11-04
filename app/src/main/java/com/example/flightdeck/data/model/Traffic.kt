package com.example.flightdeck.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

/**
 * Simulated traffic for realistic ATC practice
 * NOT real-time data - generated for training purposes
 */
@Entity(tableName = "simulated_traffic")
data class SimulatedTraffic(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long, // Links to practice session
    val aircraftCallsign: String,
    val aircraftType: String,
    val airportIcao: String,
    val currentPosition: TrafficPosition,
    val intent: TrafficIntent,
    val altitude: Int, // feet MSL
    val speed: Int, // knots
    val heading: Int, // magnetic degrees
    val flightRules: FlightRules,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdate: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

enum class TrafficPosition {
    ON_GROUND_RAMP,
    ON_GROUND_TAXIWAY,
    ON_GROUND_RUNWAY_HOLDING,
    ON_GROUND_RUNWAY_ACTIVE,
    DEPARTING,
    IN_PATTERN_DOWNWIND,
    IN_PATTERN_BASE,
    IN_PATTERN_FINAL,
    APPROACHING,
    OVERFLYING,
    LANDED_ROLLOUT
}

enum class TrafficIntent {
    TAXI_TO_RUNWAY,
    AWAITING_TAKEOFF_CLEARANCE,
    DEPARTING_PATTERN,
    DEPARTING_DIRECT,
    INBOUND_LANDING,
    INBOUND_TOUCH_AND_GO,
    TOUCH_AND_GO_PATTERN,
    CROSSING_AIRPORT,
    GROUND_OPS_ONLY
}

enum class FlightRules {
    VFR,
    IFR,
    SVFR
}

/**
 * Traffic density levels for scenario realism
 */
enum class TrafficDensity {
    NONE,
    LIGHT,
    MODERATE,
    BUSY,
    CONGESTED
}

/**
 * Weather complexity levels for training scenarios
 */
enum class WeatherComplexity {
    CLEAR_CALM,
    TYPICAL_VFR,
    CHALLENGING,
    MARGINAL_VFR,
    IFR_CONDITIONS
}

/**
 * Traffic event for ATC scenario realism
 * Examples: "Skyhawk 345 holding short runway 31", "Citation on 3-mile final"
 */
data class TrafficEvent(
    val timestamp: Long = System.currentTimeMillis(),
    val traffic: SimulatedTraffic,
    val eventType: TrafficEventType,
    val description: String,
    val affectsUserOperation: Boolean = false
)

enum class TrafficEventType {
    AIRCRAFT_CALLED_READY,
    AIRCRAFT_TAXI_REQUEST,
    AIRCRAFT_HOLDING_SHORT,
    AIRCRAFT_CROSSING_RUNWAY,
    AIRCRAFT_ON_FINAL,
    AIRCRAFT_TOUCH_AND_GO,
    AIRCRAFT_LANDED,
    AIRCRAFT_TAXIING,
    CONFLICT_POTENTIAL, // Educational: shows traffic conflict scenario
    GO_AROUND_REQUIRED  // Educational: demonstrates go-around situation
}

/**
 * Traffic density configuration for airports
 */
data class TrafficDensityConfig(
    val airportIcao: String,
    val baseTrafficRate: TrafficDensity,
    val timeOfDayMultiplier: Double = 1.0, // Higher during day
    val weatherMultiplier: Double = 1.0, // Lower in poor weather
    val typicalAircraftMix: AircraftMix
)

/**
 * Typical aircraft types at different airports
 */
data class AircraftMix(
    val singleEngine: Int = 70,  // Percentage
    val multiEngine: Int = 15,
    val jet: Int = 10,
    val helicopter: Int = 5
) {
    init {
        require(singleEngine + multiEngine + jet + helicopter == 100) {
            "Aircraft mix percentages must sum to 100"
        }
    }
}

/**
 * Common training aircraft callsigns and types
 */
object TrafficGenerator {

    private val singleEngineTypes = listOf(
        "Skyhawk", "Warrior", "Cherokee", "Archer", "Arrow",
        "Skylane", "Bonanza", "Mooney", "Cirrus", "Diamond"
    )

    private val multiEngineTypes = listOf(
        "Baron", "Seminole", "Seneca", "Duchess", "Twin Comanche"
    )

    private val jetTypes = listOf(
        "Citation", "Learjet", "Gulfstream", "Challenger", "Phenom"
    )

    private val helicopterTypes = listOf(
        "Robinson", "Schweizer", "Bell"
    )

    private val callsignPrefixes = listOf("N", "N1", "N2", "N3", "N4", "N5", "N6", "N7", "N8", "N9")

    /**
     * Generate realistic callsign like "Skyhawk N12345" or "Citation N500XR"
     */
    fun generateCallsign(aircraftCategory: String? = null): Pair<String, String> {
        val category = aircraftCategory ?: when (Random.nextInt(100)) {
            in 0..69 -> "single"
            in 70..84 -> "multi"
            in 85..94 -> "jet"
            else -> "helicopter"
        }

        val type = when (category) {
            "single" -> singleEngineTypes.random()
            "multi" -> multiEngineTypes.random()
            "jet" -> jetTypes.random()
            "helicopter" -> helicopterTypes.random()
            else -> singleEngineTypes.random()
        }

        val number = Random.nextInt(100, 9999)
        val suffix = if (Random.nextBoolean() && category == "jet") {
            ('A'..'Z').random().toString() + ('A'..'Z').random().toString()
        } else {
            ""
        }

        val callsign = "$type ${callsignPrefixes.random()}$number$suffix"
        return Pair(callsign, type)
    }

    /**
     * Generate traffic appropriate for airport class and time
     */
    fun generateTrafficForAirport(
        airport: Airport,
        sessionId: Long,
        currentTime: Long = System.currentTimeMillis(),
        count: Int = 3
    ): List<SimulatedTraffic> {
        val traffic = mutableListOf<SimulatedTraffic>()

        // Determine aircraft mix based on airport type
        val mix = when (airport.airspaceClass) {
            AirspaceClass.B -> AircraftMix(40, 20, 35, 5)  // More jets
            AirspaceClass.C -> AircraftMix(60, 20, 15, 5)  // Balanced
            AirspaceClass.D -> AircraftMix(75, 15, 5, 5)   // Mostly GA
            else -> AircraftMix(85, 10, 0, 5)              // Almost all GA
        }

        repeat(count) {
            val category = selectAircraftCategory(mix)
            val (callsign, type) = generateCallsign(category)

            traffic.add(
                SimulatedTraffic(
                    sessionId = sessionId,
                    aircraftCallsign = callsign,
                    aircraftType = type,
                    airportIcao = airport.icao,
                    currentPosition = TrafficPosition.values().random(),
                    intent = TrafficIntent.values().random(),
                    altitude = airport.elevation + Random.nextInt(0, 3000),
                    speed = Random.nextInt(60, 180),
                    heading = Random.nextInt(0, 360),
                    flightRules = if (Random.nextDouble() > 0.8) FlightRules.IFR else FlightRules.VFR,
                    createdAt = currentTime,
                    lastUpdate = currentTime
                )
            )
        }

        return traffic
    }

    private fun selectAircraftCategory(mix: AircraftMix): String {
        val roll = Random.nextInt(100)
        return when {
            roll < mix.singleEngine -> "single"
            roll < mix.singleEngine + mix.multiEngine -> "multi"
            roll < mix.singleEngine + mix.multiEngine + mix.jet -> "jet"
            else -> "helicopter"
        }
    }

    /**
     * Generate realistic traffic announcements for CTAF/Tower
     */
    fun generateTrafficAnnouncement(traffic: SimulatedTraffic): String {
        return when (traffic.currentPosition) {
            TrafficPosition.ON_GROUND_TAXIWAY ->
                "${traffic.aircraftCallsign} taxiing to runway"

            TrafficPosition.ON_GROUND_RUNWAY_HOLDING ->
                "${traffic.aircraftCallsign} holding short"

            TrafficPosition.DEPARTING ->
                "${traffic.aircraftCallsign} departing"

            TrafficPosition.IN_PATTERN_DOWNWIND ->
                "${traffic.aircraftCallsign} ${traffic.aircraftType}, downwind"

            TrafficPosition.IN_PATTERN_BASE ->
                "${traffic.aircraftCallsign} ${traffic.aircraftType}, turning base"

            TrafficPosition.IN_PATTERN_FINAL ->
                "${traffic.aircraftCallsign} ${traffic.aircraftType}, ${Random.nextInt(1, 5)}-mile final"

            TrafficPosition.APPROACHING ->
                "${traffic.aircraftCallsign} ${Random.nextInt(5, 15)} miles out"

            TrafficPosition.LANDED_ROLLOUT ->
                "${traffic.aircraftCallsign} clear of runway"

            else -> "${traffic.aircraftCallsign} in the area"
        }
    }
}
