package com.example.flightdeck.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a flight plan between two airports
 */
@Entity(tableName = "flight_plans")
data class FlightPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val aircraftId: Long,
    val departureAirport: String, // ICAO code
    val arrivalAirport: String, // ICAO code
    val route: String,
    val altitude: Int, // feet
    val cruiseSpeed: Int, // knots
    val estimatedFlightTime: Int, // minutes
    val fuelRequired: Double, // gallons
    val alternateAirport: String? = null,
    val remarks: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = false
)

/**
 * Represents a waypoint in a flight plan
 */
@Entity(tableName = "waypoints")
data class Waypoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val flightPlanId: Long,
    val sequence: Int,
    val identifier: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Int?, // feet
    val waypointType: WaypointType
)

enum class WaypointType {
    AIRPORT,
    VOR,
    NDB,
    FIX,
    GPS
}

/**
 * Represents airport information
 */
data class Airport(
    val icao: String,
    val iata: String?,
    val name: String,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Int, // feet
    val runways: List<Runway> = emptyList()
)

data class Runway(
    val identifier: String,
    val length: Int, // feet
    val width: Int, // feet
    val surface: String
)
