package com.example.flightdeck.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an aircraft with its specifications
 */
@Entity(tableName = "aircraft")
data class Aircraft(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val make: String,
    val model: String,
    val registration: String,
    val category: AircraftCategory,
    val cruiseSpeed: Int, // knots
    val fuelCapacity: Double, // gallons
    val fuelBurnRate: Double, // gallons per hour
    val homeAirport: String, // ICAO code
    val notes: String = ""
)

enum class AircraftCategory {
    SINGLE_ENGINE_LAND,
    MULTI_ENGINE_LAND,
    SINGLE_ENGINE_SEA,
    MULTI_ENGINE_SEA,
    ROTORCRAFT
}
