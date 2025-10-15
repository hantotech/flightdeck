package com.example.flightdeck.data.remote.ai

import com.example.flightdeck.data.knowledge.AviationDocument
import com.example.flightdeck.data.local.dao.KnowledgeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Enhanced AI Service with RAG (Retrieval Augmented Generation)
 * Retrieves authoritative aviation documents before generating responses
 * This ensures AI responses are grounded in real regulations and procedures
 */
class EnhancedAIServiceWithRAG(
    private val orchestrator: AIOrchestrator = AIOrchestrator.getInstance(),
    private val knowledgeDao: KnowledgeDao
) {

    /**
     * Generate ATC response with relevant regulations and procedures
     */
    suspend fun generateATCResponse(
        pilotMessage: String,
        context: ATCContext
    ): Result<String> = withContext(Dispatchers.IO) {

        // 1. Retrieve relevant knowledge
        val relevantDocs = retrieveRelevantKnowledge(
            query = pilotMessage,
            airport = context.airport,
            category = "phraseology",
            limit = 3
        )

        // 2. Build enhanced system prompt with authoritative sources
        val systemPrompt = buildATCSystemPromptWithRAG(context, relevantDocs)

        // 3. Generate response using orchestrator
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
     * Evaluate ATC communication with regulations and best practices
     */
    suspend fun evaluateATCCommunication(
        pilotMessage: String,
        expectedPhrasings: List<String>,
        context: ATCContext
    ): Result<ATCEvaluation> = withContext(Dispatchers.IO) {

        // 1. Retrieve evaluation criteria
        val relevantDocs = retrieveRelevantKnowledge(
            query = "ATC communication ${context.scenarioType}",
            airport = context.airport,
            category = "phraseology",
            limit = 5
        )

        // 2. Build evaluation prompt with authoritative standards
        val systemPrompt = """
            You are an expert FAA-certified flight instructor (CFI) evaluating student pilot radio communications.

            YOUR EVALUATION MUST BE BASED ON THESE AUTHORITATIVE SOURCES:
            ${relevantDocs.joinToString("\n\n") { doc ->
                "SOURCE: ${doc.source}\n${doc.content}"
            }}

            Evaluate the pilot's message for:
            1. Proper phraseology per AIM standards
            2. Inclusion of all required information
            3. Clarity and professionalism
            4. Safety considerations
            5. Compliance with regulations

            Provide a JSON response with:
            - score (0-100)
            - isCorrect (boolean)
            - feedback (string explaining what was good/needs improvement)
            - suggestions (list of specific improvements with regulation references)
            - sources (list regulations/AIM sections cited)
        """.trimIndent()

        val userPrompt = """
            Scenario: ${context.scenarioType} at ${context.airport}
            Expected phraseology examples: ${expectedPhrasings.joinToString(" OR ")}

            Pilot's communication: "$pilotMessage"

            Evaluate this communication against FAA standards and provide specific regulatory references.
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
     * Get checklist guidance with aircraft-specific POH information
     */
    suspend fun getChecklistGuidance(
        checklistItem: String,
        aircraftType: String,
        userQuestion: String?
    ): Result<String> = withContext(Dispatchers.IO) {

        // 1. Retrieve POH sections and regulations
        val relevantDocs = retrieveRelevantKnowledge(
            query = "$checklistItem $aircraftType",
            aircraft = aircraftType,
            category = "procedures",
            limit = 3
        )

        // 2. Build prompt with POH information
        val systemPrompt = """
            You are an experienced flight instructor helping a student pilot with pre-flight checklists.

            ALWAYS BASE YOUR ANSWER ON THESE AUTHORITATIVE SOURCES:
            ${relevantDocs.joinToString("\n\n") { doc ->
                "SOURCE: ${doc.source}\n${doc.content}"
            }}

            Provide clear, concise, safety-focused guidance.
            Reference specific POH sections and regulations.
            Keep responses brief and practical.

            IMPORTANT: If the retrieved information doesn't cover the question, state:
            "This specific information should be verified in the aircraft's POH."
        """.trimIndent()

        val userPrompt = if (userQuestion != null) {
            "Aircraft: $aircraftType\nChecklist item: $checklistItem\nQuestion: $userQuestion"
        } else {
            "Aircraft: $aircraftType\nChecklist item: $checklistItem\nProvide a brief explanation with POH reference."
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
     * Get flight planning advice with regulations and airspace info
     */
    suspend fun getFlightPlanningAdvice(
        departure: String,
        arrival: String,
        aircraft: String,
        weatherConditions: String?
    ): Result<String> = withContext(Dispatchers.IO) {

        // 1. Retrieve airspace and weather minimums
        val relevantDocs = retrieveRelevantKnowledge(
            query = "VFR weather minimums airspace $departure $arrival",
            category = "regulations",
            limit = 5
        )

        // 2. Build prompt with regulations
        val systemPrompt = """
            You are an experienced flight instructor helping with flight planning.

            CRITICAL REGULATIONS TO FOLLOW:
            ${relevantDocs.joinToString("\n\n") { doc ->
                "REGULATION: ${doc.regulation ?: doc.source}\n${doc.content}"
            }}

            Consider factors like:
            - Weather minimums for airspace classes
            - Fuel requirements (day/night VFR reserves)
            - Alternate airport requirements
            - Airspace restrictions

            Provide practical, safety-focused advice with regulatory references.
            If weather is below minimums, clearly state this and explain requirements.
        """.trimIndent()

        val userPrompt = buildString {
            append("Flight Planning Request:\n")
            append("From: $departure\n")
            append("To: $arrival\n")
            append("Aircraft: $aircraft\n")
            if (weatherConditions != null) {
                append("Weather: $weatherConditions\n")
            }
            append("\nProvide flight planning recommendations with regulatory references.")
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
     * Retrieve relevant knowledge from database
     */
    private suspend fun retrieveRelevantKnowledge(
        query: String,
        airport: String? = null,
        aircraft: String? = null,
        category: String? = null,
        limit: Int = 5
    ): List<AviationDocument> {

        // Search by query
        val searchResults = knowledgeDao.searchDocuments(query, limit)

        // Get airport-specific docs if applicable
        val airportDocs = if (airport != null) {
            knowledgeDao.getAirportSpecificDocs(airport)
        } else emptyList()

        // Get aircraft-specific docs if applicable
        val aircraftDocs = if (aircraft != null) {
            knowledgeDao.getAircraftSpecificDocs(aircraft)
        } else emptyList()

        // Combine and deduplicate
        return (searchResults + airportDocs + aircraftDocs)
            .distinctBy { it.id }
            .take(limit)
    }

    private fun buildATCSystemPromptWithRAG(
        context: ATCContext,
        relevantDocs: List<AviationDocument>
    ): String {
        return buildString {
            append("You are an air traffic controller at ${context.airport}.\n")
            append("Scenario: ${context.scenarioType}\n")
            append("Current conditions: ${context.conditions}\n\n")

            if (relevantDocs.isNotEmpty()) {
                append("YOU MUST FOLLOW THESE STANDARD PROCEDURES:\n")
                relevantDocs.forEach { doc ->
                    append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
                    append("SOURCE: ${doc.source}\n")
                    append("${doc.content}\n\n")
                }
                append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n")
            }

            append("Respond using EXACT phraseology from the procedures above.\n")
            append("Be realistic but educational. If the pilot makes errors, respond appropriately.\n")
            append("Keep responses concise and professional.\n")
            append("Use standard aviation terminology and format.\n")
            append("\nIMPORTANT: Follow procedures exactly as documented above.")
        }
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

    /**
     * Get usage statistics
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

    companion object {
        @Volatile
        private var INSTANCE: EnhancedAIServiceWithRAG? = null

        fun getInstance(
            knowledgeDao: KnowledgeDao,
            userTier: UserTier = UserTier.BASIC
        ): EnhancedAIServiceWithRAG {
            return INSTANCE ?: synchronized(this) {
                val orchestrator = AIOrchestrator.getInstance(userTier)
                INSTANCE ?: EnhancedAIServiceWithRAG(orchestrator, knowledgeDao).also {
                    INSTANCE = it
                }
            }
        }
    }
}
