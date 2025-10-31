package com.example.flightdeck.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an ATC communication scenario for practice
 */
@Entity(tableName = "atc_scenarios")
data class ATCScenario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val scenarioType: ATCScenarioType,
    val difficulty: Difficulty,
    val airport: String, // ICAO code
    val situation: String
)

enum class ATCScenarioType {
    GROUND_CLEARANCE,
    TAXI_INSTRUCTIONS,
    TAKEOFF_CLEARANCE,
    IN_FLIGHT_COMMUNICATION,
    LANDING_CLEARANCE,
    EMERGENCY_COMMUNICATION,
    FREQUENCY_CHANGE,
    WEATHER_BRIEFING
}

enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT
}

/**
 * Represents a communication exchange in an ATC scenario
 */
@Entity(tableName = "atc_exchanges")
data class ATCExchange(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scenarioId: Long,
    val sequence: Int,
    val speaker: Speaker,
    val message: String,
    val expectedResponse: String? = null,
    val hints: List<String> = emptyList()
)

enum class Speaker {
    PILOT,
    CONTROLLER
}

/**
 * Tracks user's ATC practice session
 */
@Entity(tableName = "atc_practice_sessions")
data class ATCPracticeSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scenarioId: Long,
    val startTime: Long,
    val endTime: Long? = null,
    val score: Float? = null,
    val accuracyPercentage: Float? = null,
    val completedExchanges: Int = 0,
    val totalExchanges: Int = 0
)

/**
 * Tracks individual responses in a practice session
 */
@Entity(tableName = "atc_responses")
data class ATCResponse(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val exchangeId: Long,
    val userResponse: String,
    val isCorrect: Boolean,
    val feedback: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
