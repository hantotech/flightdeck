package com.example.flightdeck.data.remote.ai

/**
 * Common interface for all AI providers
 */
interface AIProvider {

    /**
     * Generate a response based on system prompt and user message
     */
    suspend fun generateResponse(
        systemPrompt: String,
        userMessage: String,
        temperature: Double = 0.7,
        maxTokens: Int = 2048
    ): Result<AIResponse>

    /**
     * Generate a response with conversation history
     */
    suspend fun generateResponseWithHistory(
        systemPrompt: String,
        messages: List<ConversationMessage>,
        temperature: Double = 0.7,
        maxTokens: Int = 2048
    ): Result<AIResponse>

    /**
     * Get the provider name
     */
    fun getProviderName(): String

    /**
     * Get the model being used
     */
    fun getModelName(): String

    /**
     * Estimate cost for a request
     */
    fun estimateCost(inputTokens: Int, outputTokens: Int): Double
}

/**
 * Standard AI response format
 */
data class AIResponse(
    val text: String,
    val model: String,
    val usage: TokenUsage,
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * Token usage information
 */
data class TokenUsage(
    val inputTokens: Int,
    val outputTokens: Int,
    val totalTokens: Int = inputTokens + outputTokens
) {
    fun estimateCost(provider: AIProvider): Double {
        return provider.estimateCost(inputTokens, outputTokens)
    }
}

/**
 * Conversation message for multi-turn conversations
 */
data class ConversationMessage(
    val role: MessageRole,
    val content: String
)

enum class MessageRole {
    SYSTEM,
    USER,
    ASSISTANT
}

/**
 * AI model identifiers
 */
enum class AIModel(
    val displayName: String,
    val estimatedCostPer1MInputTokens: Double,
    val estimatedCostPer1MOutputTokens: Double
) {
    // Claude models
    CLAUDE_SONNET_35(
        displayName = "Claude 3.5 Sonnet",
        estimatedCostPer1MInputTokens = 3.0,
        estimatedCostPer1MOutputTokens = 15.0
    ),
    CLAUDE_HAIKU_3(
        displayName = "Claude 3 Haiku",
        estimatedCostPer1MInputTokens = 0.25,
        estimatedCostPer1MOutputTokens = 1.25
    ),

    // Gemini models
    GEMINI_FLASH_15(
        displayName = "Gemini 1.5 Flash",
        estimatedCostPer1MInputTokens = 0.075,
        estimatedCostPer1MOutputTokens = 0.30
    ),
    GEMINI_PRO_15(
        displayName = "Gemini 1.5 Pro",
        estimatedCostPer1MInputTokens = 1.25,
        estimatedCostPer1MOutputTokens = 5.0
    );

    fun estimateCost(inputTokens: Int, outputTokens: Int): Double {
        val inputCost = (inputTokens / 1_000_000.0) * estimatedCostPer1MInputTokens
        val outputCost = (outputTokens / 1_000_000.0) * estimatedCostPer1MOutputTokens
        return inputCost + outputCost
    }
}

/**
 * Task types for AI routing
 */
enum class AITask {
    // High accuracy required
    EVALUATE_ATC_COMMUNICATION,
    FLIGHT_PLANNING_ADVICE,
    COMPLEX_CHECKLIST_QUESTION,
    GENERATE_PERFORMANCE_REPORT,

    // Medium accuracy
    GENERATE_ATC_RESPONSE,
    CHECKLIST_GUIDANCE,
    WEATHER_INTERPRETATION,

    // Low accuracy / speed priority
    SIMPLE_ATC_CHAT,
    BASIC_CHECKLIST_LOOKUP,
    QUICK_QUESTION
}

/**
 * User tier for determining which models to use
 */
enum class UserTier {
    FREE,      // Use free/cheap models only
    BASIC,     // Mix of cheap and mid-tier
    PREMIUM    // Use best models available
}
