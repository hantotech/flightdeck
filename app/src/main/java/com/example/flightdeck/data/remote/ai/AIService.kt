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
     * Generate ATC communication response
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
            val response = geminiModel.generateContent(fullPrompt)
            val text = response.text ?: ""
            Log.d("AIService", "Gemini response: $text")

            Result.success(text)
        } catch (e: Exception) {
            Log.e("AIService", "Gemini API error", e)
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
