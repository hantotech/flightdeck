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
    val isActive: Boolean = false,

    // Favorite routes functionality
    val isFavorite: Boolean = false,
    val customName: String? = null,
    val timesFlown: Int = 0,
    val averageScore: Float? = null,
    val lastFlownDate: Long? = null
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
