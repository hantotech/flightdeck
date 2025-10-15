package com.example.flightdeck.data.remote.ai.providers

import com.example.flightdeck.BuildConfig
import com.example.flightdeck.data.remote.ai.*
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Google Gemini AI Provider supporting Flash and Pro models
 */
class GeminiProvider(
    private val model: AIModel,
    private val apiKey: String = BuildConfig.GEMINI_API_KEY
) : AIProvider {

    private val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = getGeminiModelId(),
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.7f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 2048
            }
        )
    }

    override suspend fun generateResponse(
        systemPrompt: String,
        userMessage: String,
        temperature: Double,
        maxTokens: Int
    ): Result<AIResponse> = withContext(Dispatchers.IO) {
        try {
            // Combine system prompt with user message for Gemini
            val fullPrompt = buildString {
                if (systemPrompt.isNotEmpty()) {
                    append("System Instructions:\n")
                    append(systemPrompt)
                    append("\n\n")
                }
                append("User Request:\n")
                append(userMessage)
            }

            val response = generativeModel.generateContent(fullPrompt)
            val text = response.text ?: ""

            // Estimate token usage (Gemini doesn't always provide exact counts)
            val estimatedInputTokens = estimateTokens(fullPrompt)
            val estimatedOutputTokens = estimateTokens(text)

            Result.success(
                AIResponse(
                    text = text,
                    model = getGeminiModelId(),
                    usage = TokenUsage(
                        inputTokens = estimatedInputTokens,
                        outputTokens = estimatedOutputTokens
                    ),
                    metadata = mapOf(
                        "candidates" to (response.candidates?.size ?: 0)
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateResponseWithHistory(
        systemPrompt: String,
        messages: List<ConversationMessage>,
        temperature: Double,
        maxTokens: Int
    ): Result<AIResponse> = withContext(Dispatchers.IO) {
        try {
            val chat = generativeModel.startChat(
                history = messages.filter { it.role != MessageRole.SYSTEM }.map { msg ->
                    content(role = mapRole(msg.role)) {
                        text(msg.content)
                    }
                }
            )

            val lastMessage = messages.lastOrNull()?.content ?: ""
            val response = chat.sendMessage(lastMessage)
            val text = response.text ?: ""

            // Estimate tokens
            val inputTokens = messages.sumOf { estimateTokens(it.content) }
            val outputTokens = estimateTokens(text)

            Result.success(
                AIResponse(
                    text = text,
                    model = getGeminiModelId(),
                    usage = TokenUsage(
                        inputTokens = inputTokens,
                        outputTokens = outputTokens
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getProviderName(): String = "Google Gemini"

    override fun getModelName(): String = model.displayName

    override fun estimateCost(inputTokens: Int, outputTokens: Int): Double {
        return model.estimateCost(inputTokens, outputTokens)
    }

    private fun getGeminiModelId(): String {
        return when (model) {
            AIModel.GEMINI_FLASH_15 -> "gemini-1.5-flash"
            AIModel.GEMINI_PRO_15 -> "gemini-1.5-pro"
            else -> throw IllegalArgumentException("Unsupported Gemini model: $model")
        }
    }

    private fun mapRole(role: MessageRole): String {
        return when (role) {
            MessageRole.USER -> "user"
            MessageRole.ASSISTANT -> "model"
            MessageRole.SYSTEM -> "user" // Gemini doesn't have separate system role
        }
    }

    /**
     * Rough token estimation (4 chars ≈ 1 token)
     */
    private fun estimateTokens(text: String): Int {
        return (text.length / 4).coerceAtLeast(1)
    }

    companion object {
        fun createFlash(apiKey: String = BuildConfig.GEMINI_API_KEY): GeminiProvider {
            return GeminiProvider(AIModel.GEMINI_FLASH_15, apiKey)
        }

        fun createPro(apiKey: String = BuildConfig.GEMINI_API_KEY): GeminiProvider {
            return GeminiProvider(AIModel.GEMINI_PRO_15, apiKey)
        }
    }
}
