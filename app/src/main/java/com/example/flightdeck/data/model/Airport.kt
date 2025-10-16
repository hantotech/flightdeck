package com.example.flightdeck.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Airport information entity
 * Data sourced from OurAirports (Public Domain)
 */
@Entity(tableName = "airports")
data class Airport(
    @PrimaryKey
    val icao: String,
    val iata: String?,
    val name: String,
    val city: String,
    val state: String?,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Int, // feet MSL
    val airspaceClass: AirspaceClass?,
    val towerControlled: Boolean = false,
    val type: AirportType,
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Runway information
 */
@Entity(tableName = "runways")
data class Runway(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val airportIcao: String,
    val identifier: String, // e.g., "09/27"
    val length: Int, // feet
    val width: Int, // feet
    val surface: SurfaceType,
    val lighted: Boolean = false,
    val closedMarking: Boolean = false,
    // Low end
    val leIdentifier: String, // e.g., "09"
    val leLatitude: Double?,
    val leLongitude: Double?,
    val leElevation: Int?, // feet MSL
    val leHeading: Int?, // magnetic degrees
    // High end
    val heIdentifier: String, // e.g., "27"
    val heLatitude: Double?,
    val heLongitude: Double?,
    val heElevation: Int?, // feet MSL
    val heHeading: Int? // magnetic degrees
)

/**
 * Communication frequencies for airport
 */
@Entity(tableName = "frequencies")
data class Frequency(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val airportIcao: String,
    val type: FrequencyType,
    val description: String,
    val frequency: Double // MHz
)

enum class AirportType {
    SMALL_AIRPORT,      // Small general aviation
    MEDIUM_AIRPORT,     // Medium commercial
    LARGE_AIRPORT,      // Major commercial
    HELIPORT,
    SEAPLANE_BASE,
    CLOSED
}

enum class AirspaceClass {
    A, B, C, D, E, G
}

enum class SurfaceType {
    ASPHALT,
    CONCRETE,
    GRASS,
    DIRT,
    GRAVEL,
    WATER,
    TURF,
    UNKNOWN
}

enum class FrequencyType {
    GROUND,
    TOWER,
    CLEARANCE_DELIVERY,
    APPROACH,
    DEPARTURE,
    ATIS,
    UNICOM,
    CTAF,
    MULTICOM,
    FSS,
    AWOS,
    ASOS
}
