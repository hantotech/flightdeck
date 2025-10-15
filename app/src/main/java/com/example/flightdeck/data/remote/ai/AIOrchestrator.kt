package com.example.flightdeck.data.remote.ai

import com.example.flightdeck.BuildConfig
import com.example.flightdeck.data.remote.ai.providers.ClaudeProvider
import com.example.flightdeck.data.remote.ai.providers.GeminiProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Smart AI Orchestrator that routes requests to optimal providers
 * based on task type, user tier, and cost considerations
 */
class AIOrchestrator(
    private val userTier: UserTier = UserTier.BASIC,
    private val preferredProvider: String? = null
) {

    // Provider instances (lazy initialization)
    private val claudeSonnet: AIProvider by lazy { ClaudeProvider.createSonnet() }
    private val claudeHaiku: AIProvider by lazy { ClaudeProvider.createHaiku() }
    private val geminiFlash: AIProvider by lazy { GeminiProvider.createFlash() }
    private val geminiPro: AIProvider by lazy { GeminiProvider.createPro() }

    // Usage tracking
    private val usageStats = mutableMapOf<String, UsageMetrics>()

    /**
     * Execute an AI task with automatic provider selection
     */
    suspend fun execute(
        task: AITask,
        systemPrompt: String,
        userMessage: String,
        temperature: Double = 0.7,
        maxTokens: Int = 2048
    ): Result<AIResponse> = withContext(Dispatchers.IO) {
        val provider = selectProvider(task)

        try {
            val result = provider.generateResponse(
                systemPrompt = systemPrompt,
                userMessage = userMessage,
                temperature = temperature,
                maxTokens = maxTokens
            )

            // Track usage
            result.onSuccess { response ->
                trackUsage(provider, response.usage)
            }

            result
        } catch (e: Exception) {
            // Fallback to alternative provider if primary fails
            val fallbackProvider = getFallbackProvider(provider, task)
            if (fallbackProvider != null) {
                fallbackProvider.generateResponse(
                    systemPrompt = systemPrompt,
                    userMessage = userMessage,
                    temperature = temperature,
                    maxTokens = maxTokens
                )
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Execute with conversation history
     */
    suspend fun executeWithHistory(
        task: AITask,
        systemPrompt: String,
        messages: List<ConversationMessage>,
        temperature: Double = 0.7,
        maxTokens: Int = 2048
    ): Result<AIResponse> = withContext(Dispatchers.IO) {
        val provider = selectProvider(task)

        try {
            val result = provider.generateResponseWithHistory(
                systemPrompt = systemPrompt,
                messages = messages,
                temperature = temperature,
                maxTokens = maxTokens
            )

            result.onSuccess { response ->
                trackUsage(provider, response.usage)
            }

            result
        } catch (e: Exception) {
            val fallbackProvider = getFallbackProvider(provider, task)
            if (fallbackProvider != null) {
                fallbackProvider.generateResponseWithHistory(
                    systemPrompt = systemPrompt,
                    messages = messages,
                    temperature = temperature,
                    maxTokens = maxTokens
                )
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Select the optimal provider based on task and user tier
     */
    private fun selectProvider(task: AITask): AIProvider {
        // Check if API keys are available
        val hasClaudeKey = BuildConfig.ANTHROPIC_API_KEY.isNotEmpty()
        val hasGeminiKey = BuildConfig.GEMINI_API_KEY.isNotEmpty()

        // Respect user preference if set
        preferredProvider?.let { pref ->
            return when (pref.lowercase()) {
                "claude-sonnet" -> if (hasClaudeKey) claudeSonnet else geminiPro
                "claude-haiku" -> if (hasClaudeKey) claudeHaiku else geminiFlash
                "gemini-flash" -> if (hasGeminiKey) geminiFlash else claudeHaiku
                "gemini-pro" -> if (hasGeminiKey) geminiPro else claudeSonnet
                else -> selectByTask(task, hasClaudeKey, hasGeminiKey)
            }
        }

        return selectByTask(task, hasClaudeKey, hasGeminiKey)
    }

    private fun selectByTask(
        task: AITask,
        hasClaudeKey: Boolean,
        hasGeminiKey: Boolean
    ): AIProvider {
        return when (userTier) {
            UserTier.FREE -> selectForFreeTier(task, hasGeminiKey)
            UserTier.BASIC -> selectForBasicTier(task, hasClaudeKey, hasGeminiKey)
            UserTier.PREMIUM -> selectForPremiumTier(task, hasClaudeKey, hasGeminiKey)
        }
    }

    private fun selectForFreeTier(task: AITask, hasGeminiKey: Boolean): AIProvider {
        // Free tier: Use Gemini Flash for everything (has free quota)
        return if (hasGeminiKey) {
            geminiFlash
        } else {
            // Fallback to Claude Haiku if no Gemini key
            claudeHaiku
        }
    }

    private fun selectForBasicTier(
        task: AITask,
        hasClaudeKey: Boolean,
        hasGeminiKey: Boolean
    ): AIProvider {
        return when (task) {
            // High accuracy tasks -> Claude Sonnet
            AITask.EVALUATE_ATC_COMMUNICATION,
            AITask.FLIGHT_PLANNING_ADVICE,
            AITask.GENERATE_PERFORMANCE_REPORT -> {
                if (hasClaudeKey) claudeSonnet else geminiPro
            }

            // Medium accuracy -> Claude Haiku or Gemini Flash
            AITask.GENERATE_ATC_RESPONSE,
            AITask.CHECKLIST_GUIDANCE,
            AITask.WEATHER_INTERPRETATION,
            AITask.COMPLEX_CHECKLIST_QUESTION -> {
                if (hasClaudeKey) claudeHaiku else geminiFlash
            }

            // Low accuracy / speed priority -> Gemini Flash
            AITask.SIMPLE_ATC_CHAT,
            AITask.BASIC_CHECKLIST_LOOKUP,
            AITask.QUICK_QUESTION -> {
                if (hasGeminiKey) geminiFlash else claudeHaiku
            }
        }
    }

    private fun selectForPremiumTier(
        task: AITask,
        hasClaudeKey: Boolean,
        hasGeminiKey: Boolean
    ): AIProvider {
        // Premium: Use best models for everything
        return when (task) {
            AITask.EVALUATE_ATC_COMMUNICATION,
            AITask.FLIGHT_PLANNING_ADVICE,
            AITask.COMPLEX_CHECKLIST_QUESTION,
            AITask.GENERATE_PERFORMANCE_REPORT -> {
                if (hasClaudeKey) claudeSonnet else geminiPro
            }

            AITask.GENERATE_ATC_RESPONSE,
            AITask.CHECKLIST_GUIDANCE,
            AITask.WEATHER_INTERPRETATION -> {
                if (hasClaudeKey) claudeHaiku else geminiFlash
            }

            AITask.SIMPLE_ATC_CHAT,
            AITask.BASIC_CHECKLIST_LOOKUP,
            AITask.QUICK_QUESTION -> {
                if (hasGeminiKey) geminiFlash else claudeHaiku
            }
        }
    }

    private fun getFallbackProvider(primary: AIProvider, task: AITask): AIProvider? {
        // Provide fallback providers based on the primary
        return when (primary.getModelName()) {
            AIModel.CLAUDE_SONNET_35.displayName -> claudeHaiku
            AIModel.CLAUDE_HAIKU_3.displayName -> geminiFlash
            AIModel.GEMINI_FLASH_15.displayName -> claudeHaiku
            AIModel.GEMINI_PRO_15.displayName -> claudeSonnet
            else -> null
        }
    }

    private fun trackUsage(provider: AIProvider, usage: TokenUsage) {
        val providerName = provider.getProviderName()
        val current = usageStats.getOrDefault(providerName, UsageMetrics())

        usageStats[providerName] = UsageMetrics(
            totalRequests = current.totalRequests + 1,
            totalInputTokens = current.totalInputTokens + usage.inputTokens,
            totalOutputTokens = current.totalOutputTokens + usage.outputTokens,
            estimatedCost = current.estimatedCost + usage.estimateCost(provider)
        )
    }

    /**
     * Get usage statistics
     */
    fun getUsageStats(): Map<String, UsageMetrics> = usageStats.toMap()

    /**
     * Get total estimated cost across all providers
     */
    fun getTotalEstimatedCost(): Double {
        return usageStats.values.sumOf { it.estimatedCost }
    }

    /**
     * Reset usage statistics
     */
    fun resetUsageStats() {
        usageStats.clear()
    }

    companion object {
        @Volatile
        private var INSTANCE: AIOrchestrator? = null

        fun getInstance(
            userTier: UserTier = UserTier.BASIC,
            preferredProvider: String? = null
        ): AIOrchestrator {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AIOrchestrator(userTier, preferredProvider).also { INSTANCE = it }
            }
        }

        fun resetInstance() {
            INSTANCE = null
        }
    }
}

/**
 * Usage metrics for tracking API consumption
 */
data class UsageMetrics(
    val totalRequests: Int = 0,
    val totalInputTokens: Int = 0,
    val totalOutputTokens: Int = 0,
    val estimatedCost: Double = 0.0
) {
    fun getTotalTokens(): Int = totalInputTokens + totalOutputTokens

    fun getAverageCostPerRequest(): Double {
        return if (totalRequests > 0) estimatedCost / totalRequests else 0.0
    }

    fun getFormattedCost(): String {
        return "$%.4f".format(estimatedCost)
    }
}
