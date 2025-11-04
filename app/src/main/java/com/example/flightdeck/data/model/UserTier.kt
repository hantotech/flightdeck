package com.example.flightdeck.data.model

/**
 * User subscription tier for FlightDeck
 * Controls access to premium features
 */
enum class UserTier(val displayName: String, val description: String) {
    BASIC(
        "Basic",
        "30 free airports + core voice ATC practice"
    ),
    PREMIUM(
        "Premium",
        "20,000+ airports + advanced features"
    )
}

/**
 * Pilot rank based on proficiency and experience
 * Used for logbook tracking and progression
 */
enum class PilotRank(val displayName: String, val icon: String, val minTotalTime: Int) {
    STUDENT_PILOT("Student Pilot", "\uD83D\uDCDA", 0),           // 0+ hours
    PRIVATE_PILOT("Private Pilot", "✈\uFE0F", 40),       // 40+ hours
    INSTRUMENT_RATED("Instrument Rated", "\uD83C\uDF27\uFE0F", 100),  // 100+ hours
    COMMERCIAL_PILOT("Commercial Pilot", "\uD83D\uDE80", 250),  // 250+ hours
    ATP("Airline Transport Pilot", "\uD83C\uDF1F", 1500)       // 1500+ hours
}
