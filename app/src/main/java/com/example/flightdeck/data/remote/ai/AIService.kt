package com.example.flightdeck.data.remote.ai

import android.util.Log
import com.example.flightdeck.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AI Service for managing LLM interactions
 * Uses Gemini API for realistic aviation scenarios
 */
class AIService {

    private val geminiModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    /**
     * Generate ATC communication response with retry logic
     */
    suspend fun generateATCResponse(
        pilotMessage: String,
        context: ATCContext
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("AIService", "generateATCResponse called with: $pilotMessage")
            val systemPrompt = buildATCSystemPrompt(context)
            val fullPrompt = "$systemPrompt\n\nPilot: $pilotMessage\n\nATC Response:"

            Log.d("AIService", "Calling Gemini API...")

            // Retry with exponential backoff
            val text = com.example.flightdeck.utils.NetworkUtils.retryWithBackoff(
                maxRetries = 3,
                initialDelayMs = 1000
            ) {
                val response = geminiModel.generateContent(fullPrompt)
                response.text ?: throw IllegalStateException("Empty response from AI")
            }

            Log.d("AIService", "Gemini response: $text")
            Result.success(text)
        } catch (e: Exception) {
            Log.e("AIService", "Gemini API error after retries", e)
            Result.failure(e)
        }
    }

    /**
     * Evaluate pilot's ATC communication
     */
    suspend fun evaluateATCCommunication(
        pilotMessage: String,
        expectedPhrasings: List<String>,
        context: ATCContext
    ): Result<ATCEvaluation> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                You are an expert flight instructor evaluating student pilot radio communications.
                Evaluate the pilot's message for:
                1. Proper phraseology and format
                2. Inclusion of required information
                3. Clarity and professionalism
                4. Safety considerations

                Scenario: ${context.scenarioType} at ${context.airport}
                Expected phraseology examples: ${expectedPhrasings.joinToString(" OR ")}

                Pilot's communication: "$pilotMessage"

                Provide a score (0-100) and brief feedback.
            """.trimIndent()

            val response = geminiModel.generateContent(prompt)
            val text = response.text ?: ""
            Result.success(parseATCEvaluation(text))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get checklist guidance from AI
     */
    suspend fun getChecklistGuidance(
        checklistItem: String,
        aircraftType: String,
        userQuestion: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = buildString {
                append("You are an experienced flight instructor helping with pre-flight checklists.\n")
                append("Provide clear, concise, safety-focused guidance.\n\n")
                append("Aircraft: $aircraftType\n")
                append("Checklist item: $checklistItem\n")
                if (userQuestion != null) {
                    append("Question: $userQuestion")
                } else {
                    append("Provide a brief explanation of this checklist item.")
                }
            }

            val response = geminiModel.generateContent(prompt)
            val text = response.text ?: ""
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get flight planning assistance
     */
    suspend fun getFlightPlanningAdvice(
        departure: String,
        arrival: String,
        aircraft: String,
        weatherConditions: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = buildString {
                append("You are an experienced flight instructor helping with flight planning.\n")
                append("Consider weather, fuel, alternates, and regulations.\n\n")
                append("Flight Planning Request:\n")
                append("From: $departure\n")
                append("To: $arrival\n")
                append("Aircraft: $aircraft\n")
                if (weatherConditions != null) {
                    append("Weather: $weatherConditions\n")
                }
                append("\nProvide flight planning recommendations.")
            }

            val response = geminiModel.generateContent(prompt)
            val text = response.text ?: ""
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generate comprehensive session summary with feedback
     * Analyzes the entire conversation and provides detailed evaluation
     */
    suspend fun generateSessionSummary(
        transcript: String,
        context: ATCContext,
        sessionDuration: Int
    ): Result<SessionFeedback> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                You are an expert flight instructor reviewing a student's ATC practice session.

                Scenario: ${context.scenarioType} at ${context.airport}
                Conditions: ${context.conditions}
                Session Duration: $sessionDuration minutes

                CONVERSATION TRANSCRIPT:
                $transcript

                Provide a comprehensive evaluation in the following JSON format:
                {
                  "overallScore": <number 0-100>,
                  "scoreDescription": "<Excellent/Very Good/Good/Fair/Needs Practice>",
                  "strengths": [
                    "<specific positive observation 1>",
                    "<specific positive observation 2>",
                    "<specific positive observation 3>"
                  ],
                  "improvements": [
                    "<specific area to improve 1>",
                    "<specific area to improve 2>",
                    "<specific area to improve 3>"
                  ],
                  "phraseologyScore": <number 0-100>,
                  "clarityScore": <number 0-100>,
                  "procedureScore": <number 0-100>
                }

                Focus on:
                1. Proper aviation phraseology and terminology
                2. Completeness of required information
                3. Clarity and professionalism
                4. Following proper procedures
                5. Safety considerations

                Be specific and constructive in feedback. Highlight both strengths and areas for improvement.
                Return ONLY valid JSON, no additional text.
            """.trimIndent()

            // Retry with exponential backoff for session summary
            val text = com.example.flightdeck.utils.NetworkUtils.retryWithBackoff(
                maxRetries = 3,
                initialDelayMs = 2000
            ) {
                val response = geminiModel.generateContent(prompt)
                response.text ?: throw IllegalStateException("Empty response from AI")
            }

            Log.d("AIService", "Session summary response: $text")

            Result.success(parseSessionFeedback(text))
        } catch (e: Exception) {
            Log.e("AIService", "Error generating session summary", e)
            Result.failure(e)
        }
    }

    private fun parseSessionFeedback(response: String): SessionFeedback {
        return try {
            // Extract JSON from response (remove markdown code blocks if present)
            val jsonText = response
                .substringAfter("```json", "")
                .substringAfter("```", "")
                .substringBefore("```", response)
                .trim()

            // Simple manual parsing - in production, use a JSON library
            val overallScore = jsonText.substringAfter("\"overallScore\":", "80")
                .substringBefore(",").trim().toFloatOrNull() ?: 80f

            val scoreDescription = jsonText.substringAfter("\"scoreDescription\":", "Good")
                .substringBefore(",").trim().removeSurrounding("\"")
                .ifBlank { "Good" }

            val phraseologyScore = jsonText.substringAfter("\"phraseologyScore\":", "80")
                .substringBefore(",").trim().toFloatOrNull() ?: 80f

            val clarityScore = jsonText.substringAfter("\"clarityScore\":", "80")
                .substringBefore(",").trim().toFloatOrNull() ?: 80f

            val procedureScore = jsonText.substringAfter("\"procedureScore\":", "80")
                .trim().substringBefore("}").toFloatOrNull() ?: 80f

            // Parse arrays (simplified)
            val strengthsText = jsonText.substringAfter("\"strengths\":", "")
                .substringBefore("],", "")
            val strengths = parseStringArray(strengthsText)

            val improvementsText = jsonText.substringAfter("\"improvements\":", "")
                .substringBefore("],", "")
            val improvements = parseStringArray(improvementsText)

            SessionFeedback(
                overallScore = overallScore,
                scoreDescription = scoreDescription,
                strengths = strengths.ifEmpty {
                    listOf("Completed the practice session", "Engaged with ATC communications", "Showed effort to learn")
                },
                improvements = improvements.ifEmpty {
                    listOf("Continue practicing standard phraseology", "Review FAA communication standards", "Practice more complex scenarios")
                },
                phraseologyScore = phraseologyScore,
                clarityScore = clarityScore,
                procedureScore = procedureScore
            )
        } catch (e: Exception) {
            Log.e("AIService", "Error parsing session feedback, using defaults", e)
            // Return default feedback if parsing fails
            SessionFeedback(
                overallScore = 75f,
                scoreDescription = "Good",
                strengths = listOf(
                    "Completed the practice session",
                    "Engaged with ATC communications",
                    "Showed effort to learn proper procedures"
                ),
                improvements = listOf(
                    "Continue practicing standard phraseology",
                    "Review FAA communication standards",
                    "Practice more complex scenarios"
                ),
                phraseologyScore = 75f,
                clarityScore = 75f,
                procedureScore = 75f
            )
        }
    }

    private fun parseStringArray(arrayText: String): List<String> {
        return try {
            arrayText
                .substringAfter("[", "")
                .substringBefore("]", "")
                .split("\",")
                .map { it.trim().removeSurrounding("\"").trim() }
                .filter { it.isNotBlank() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun buildATCSystemPrompt(context: ATCContext): String {
        return """
            You are an air traffic controller at ${context.airport}.
            Scenario: ${context.scenarioType}
            Current conditions: ${context.conditions}

            Respond using proper ATC phraseology and procedures.
            Be realistic but educational. If the pilot makes errors, respond appropriately.
            Keep responses concise and professional.
            Use standard aviation terminology and format.
        """.trimIndent()
    }

    private fun parseATCEvaluation(response: String): ATCEvaluation {
        // Simple parsing - in production, use proper JSON parsing
        val score = response.substringAfter("score", "75")
            .substringBefore(",").trim().toIntOrNull() ?: 75

        return ATCEvaluation(
            score = score,
            isCorrect = score >= 70,
            feedback = response,
            suggestions = listOf()
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: AIService? = null

        fun getInstance(): AIService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AIService().also { INSTANCE = it }
            }
        }
    }
}

/**
 * Context for ATC scenarios
 */
data class ATCContext(
    val airport: String,
    val scenarioType: String,
    val conditions: String,
    val additionalInfo: Map<String, String> = emptyMap()
)

/**
 * Evaluation result for ATC communication
 */
data class ATCEvaluation(
    val score: Int,
    val isCorrect: Boolean,
    val feedback: String,
    val suggestions: List<String>
)

/**
 * Comprehensive feedback for a completed practice session
 */
data class SessionFeedback(
    val overallScore: Float,
    val scoreDescription: String,
    val strengths: List<String>,
    val improvements: List<String>,
    val phraseologyScore: Float,
    val clarityScore: Float,
    val procedureScore: Float
)
