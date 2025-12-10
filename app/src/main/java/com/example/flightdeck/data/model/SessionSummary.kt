package com.example.flightdeck.data.model

import com.example.flightdeck.ui.atc.ChatMessage
import java.io.Serializable

/**
 * Summary data for a completed ATC practice session
 * Used to display feedback and evaluation to the user
 */
data class SessionSummary(
    val sessionId: Long,
    val sessionTitle: String,
    val startTime: Long,
    val endTime: Long,
    val durationMinutes: Int,
    val overallScore: Float, // 0-100
    val scoreDescription: String, // e.g., "Excellent", "Very Good", "Good", "Needs Practice"
    val strengths: List<String>, // What went well
    val improvements: List<String>, // Areas to improve
    val transcript: List<ChatMessage>, // Full conversation history
    val metrics: SessionMetrics
) : Serializable

/**
 * Detailed metrics for session performance
 */
data class SessionMetrics(
    val totalExchanges: Int,
    val phraseologyScore: Float, // 0-100
    val clarityScore: Float, // 0-100
    val procedureScore: Float, // 0-100
    val responseTimeAverage: Float // seconds
) : Serializable

/**
 * Result of AI evaluation for a single communication
 */
data class CommunicationEvaluation(
    val score: Float, // 0-100
    val phraseologyCorrect: Boolean,
    val requiredElementsPresent: List<String>, // Elements that were included
    val missingElements: List<String>, // Elements that should have been included
    val feedback: String, // Detailed feedback
    val suggestions: List<String> // Specific suggestions for improvement
)
