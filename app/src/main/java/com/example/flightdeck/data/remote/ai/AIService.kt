package com.example.flightdeck.data.remote.ai

import com.example.flightdeck.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * AI Service for managing LLM interactions
 * Handles communication with Claude API for realistic aviation scenarios
 */
class AIService {

    private val anthropicApi: AnthropicApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.anthropic.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnthropicApiService::class.java)
    }

    /**
     * Generate ATC communication response
     */
    suspend fun generateATCResponse(
        pilotMessage: String,
        context: ATCContext
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = buildATCSystemPrompt(context)

            val request = ClaudeRequest(
                model = "claude-3-5-sonnet-20241022",
                system = systemPrompt,
                messages = listOf(
                    Message(role = "user", content = pilotMessage)
                ),
                maxTokens = 1024,
                temperature = 0.8
            )

            val response = anthropicApi.sendMessage(
                apiKey = BuildConfig.ANTHROPIC_API_KEY,
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                val text = response.body()!!.content.firstOrNull()?.text ?: ""
                Result.success(text)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
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
            val systemPrompt = """
                You are an expert flight instructor evaluating student pilot radio communications.
                Evaluate the pilot's message for:
                1. Proper phraseology and format
                2. Inclusion of required information
                3. Clarity and professionalism
                4. Safety considerations

                Provide a JSON response with:
                - score (0-100)
                - isCorrect (boolean)
                - feedback (string explaining what was good/needs improvement)
                - suggestions (list of specific improvements)
            """.trimIndent()

            val userPrompt = """
                Scenario: ${context.scenarioType} at ${context.airport}
                Expected phraseology examples: ${expectedPhrasings.joinToString(" OR ")}

                Pilot's communication: "$pilotMessage"

                Evaluate this communication.
            """.trimIndent()

            val request = ClaudeRequest(
                system = systemPrompt,
                messages = listOf(Message(role = "user", content = userPrompt)),
                maxTokens = 1024,
                temperature = 0.3
            )

            val response = anthropicApi.sendMessage(
                apiKey = BuildConfig.ANTHROPIC_API_KEY,
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                val text = response.body()!!.content.firstOrNull()?.text ?: ""
                // Parse the response (would use proper JSON parsing in production)
                Result.success(parseATCEvaluation(text))
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
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
            val systemPrompt = """
                You are an experienced flight instructor helping a student pilot with pre-flight checklists.
                Provide clear, concise, safety-focused guidance.
                Reference the POH (Pilot's Operating Handbook) when appropriate.
                Keep responses brief and practical.
            """.trimIndent()

            val userPrompt = if (userQuestion != null) {
                "Aircraft: $aircraftType\nChecklist item: $checklistItem\nQuestion: $userQuestion"
            } else {
                "Aircraft: $aircraftType\nChecklist item: $checklistItem\nProvide a brief explanation of this checklist item."
            }

            val request = ClaudeRequest(
                system = systemPrompt,
                messages = listOf(Message(role = "user", content = userPrompt)),
                maxTokens = 512,
                temperature = 0.5
            )

            val response = anthropicApi.sendMessage(
                apiKey = BuildConfig.ANTHROPIC_API_KEY,
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                val text = response.body()!!.content.firstOrNull()?.text ?: ""
                Result.success(text)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
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
            val systemPrompt = """
                You are an experienced flight instructor helping with flight planning.
                Consider factors like weather, fuel requirements, alternates, and regulations.
                Provide practical, safety-focused advice.
            """.trimIndent()

            val userPrompt = buildString {
                append("Flight Planning Request:\n")
                append("From: $departure\n")
                append("To: $arrival\n")
                append("Aircraft: $aircraft\n")
                if (weatherConditions != null) {
                    append("Weather: $weatherConditions\n")
                }
                append("\nProvide flight planning recommendations.")
            }

            val request = ClaudeRequest(
                system = systemPrompt,
                messages = listOf(Message(role = "user", content = userPrompt)),
                maxTokens = 2048,
                temperature = 0.6
            )

            val response = anthropicApi.sendMessage(
                apiKey = BuildConfig.ANTHROPIC_API_KEY,
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                val text = response.body()!!.content.firstOrNull()?.text ?: ""
                Result.success(text)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
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
