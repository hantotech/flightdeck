package com.example.flightdeck.data.remote.ai.providers

import com.example.flightdeck.BuildConfig
import com.example.flightdeck.data.remote.ai.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Claude AI Provider supporting both Sonnet and Haiku models
 */
class ClaudeProvider(
    private val model: AIModel,
    private val apiKey: String = BuildConfig.ANTHROPIC_API_KEY
) : AIProvider {

    private val api: AnthropicApiService by lazy {
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

    override suspend fun generateResponse(
        systemPrompt: String,
        userMessage: String,
        temperature: Double,
        maxTokens: Int
    ): Result<AIResponse> = withContext(Dispatchers.IO) {
        try {
            val request = ClaudeRequest(
                model = getClaudeModelId(),
                system = systemPrompt,
                messages = listOf(Message(role = "user", content = userMessage)),
                maxTokens = maxTokens,
                temperature = temperature
            )

            val response = api.sendMessage(
                apiKey = apiKey,
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(
                    AIResponse(
                        text = body.content.firstOrNull()?.text ?: "",
                        model = body.model,
                        usage = TokenUsage(
                            inputTokens = body.usage.inputTokens,
                            outputTokens = body.usage.outputTokens
                        )
                    )
                )
            } else {
                Result.failure(Exception("Claude API Error: ${response.code()}"))
            }
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
            val claudeMessages = messages.map { msg ->
                Message(
                    role = when (msg.role) {
                        MessageRole.USER -> "user"
                        MessageRole.ASSISTANT -> "assistant"
                        MessageRole.SYSTEM -> "user" // Claude doesn't support system in messages
                    },
                    content = msg.content
                )
            }

            val request = ClaudeRequest(
                model = getClaudeModelId(),
                system = systemPrompt,
                messages = claudeMessages,
                maxTokens = maxTokens,
                temperature = temperature
            )

            val response = api.sendMessage(
                apiKey = apiKey,
                request = request
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                Result.success(
                    AIResponse(
                        text = body.content.firstOrNull()?.text ?: "",
                        model = body.model,
                        usage = TokenUsage(
                            inputTokens = body.usage.inputTokens,
                            outputTokens = body.usage.outputTokens
                        )
                    )
                )
            } else {
                Result.failure(Exception("Claude API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getProviderName(): String = "Anthropic Claude"

    override fun getModelName(): String = model.displayName

    override fun estimateCost(inputTokens: Int, outputTokens: Int): Double {
        return model.estimateCost(inputTokens, outputTokens)
    }

    private fun getClaudeModelId(): String {
        return when (model) {
            AIModel.CLAUDE_SONNET_35 -> "claude-3-5-sonnet-20241022"
            AIModel.CLAUDE_HAIKU_3 -> "claude-3-haiku-20240307"
            else -> throw IllegalArgumentException("Unsupported Claude model: $model")
        }
    }

    companion object {
        fun createSonnet(apiKey: String = BuildConfig.ANTHROPIC_API_KEY): ClaudeProvider {
            return ClaudeProvider(AIModel.CLAUDE_SONNET_35, apiKey)
        }

        fun createHaiku(apiKey: String = BuildConfig.ANTHROPIC_API_KEY): ClaudeProvider {
            return ClaudeProvider(AIModel.CLAUDE_HAIKU_3, apiKey)
        }
    }
}
