package com.example.flightdeck.data.remote.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Enhanced AI Service using the AI Orchestrator for smart model selection
 * This replaces the original AIService with intelligent routing
 */
class EnhancedAIService(
    private val orchestrator: AIOrchestrator = AIOrchestrator.getInstance()
) {

    /**
     * Generate ATC communication response using optimal model
     */
    suspend fun generateATCResponse(
        pilotMessage: String,
        context: ATCContext
    ): Result<String> = withContext(Dispatchers.IO) {
        val systemPrompt = buildATCSystemPrompt(context)

        val result = orchestrator.execute(
            task = AITask.GENERATE_ATC_RESPONSE,
            systemPrompt = systemPrompt,
            userMessage = pilotMessage,
            temperature = 0.8,
            maxTokens = 1024
        )

        result.map { it.text }
    }

    /**
     * Evaluate pilot's ATC communication using high-accuracy model
     */
    suspend fun evaluateATCCommunication(
        pilotMessage: String,
        expectedPhrasings: List<String>,
        context: ATCContext
    ): Result<ATCEvaluation> = withContext(Dispatchers.IO) {
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

        val result = orchestrator.execute(
            task = AITask.EVALUATE_ATC_COMMUNICATION,
            systemPrompt = systemPrompt,
            userMessage = userPrompt,
            temperature = 0.3,
            maxTokens = 1024
        )

        result.map { parseATCEvaluation(it.text) }
    }

    /**
     * Get checklist guidance from AI
     */
    suspend fun getChecklistGuidance(
        checklistItem: String,
        aircraftType: String,
        userQuestion: String?
    ): Result<String> = withContext(Dispatchers.IO) {
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

        val task = if (userQuestion?.length ?: 0 > 50) {
            AITask.COMPLEX_CHECKLIST_QUESTION
        } else {
            AITask.CHECKLIST_GUIDANCE
        }

        val result = orchestrator.execute(
            task = task,
            systemPrompt = systemPrompt,
            userMessage = userPrompt,
            temperature = 0.5,
            maxTokens = 512
        )

        result.map { it.text }
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

        val result = orchestrator.execute(
            task = AITask.FLIGHT_PLANNING_ADVICE,
            systemPrompt = systemPrompt,
            userMessage = userPrompt,
            temperature = 0.6,
            maxTokens = 2048
        )

        result.map { it.text }
    }

    /**
     * Get usage statistics from the orchestrator
     */
    fun getUsageStats(): Map<String, UsageMetrics> {
        return orchestrator.getUsageStats()
    }

    /**
     * Get total estimated cost
     */
    fun getTotalEstimatedCost(): Double {
        return orchestrator.getTotalEstimatedCost()
    }

    /**
     * Reset usage statistics
     */
    fun resetUsageStats() {
        orchestrator.resetUsageStats()
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
        // Simple parsing - in production, use proper JSON parsing with Gson
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
        private var INSTANCE: EnhancedAIService? = null

        fun getInstance(
            userTier: UserTier = UserTier.BASIC
        ): EnhancedAIService {
            return INSTANCE ?: synchronized(this) {
                val orchestrator = AIOrchestrator.getInstance(userTier)
                INSTANCE ?: EnhancedAIService(orchestrator).also { INSTANCE = it }
            }
        }

        fun resetInstance() {
            INSTANCE = null
            AIOrchestrator.resetInstance()
        }
    }
}
