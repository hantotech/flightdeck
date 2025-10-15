package com.example.flightdeck.data.remote.ai

import com.google.gson.annotations.SerializedName

/**
 * Request model for Anthropic Claude API
 */
data class ClaudeRequest(
    val model: String = "claude-3-5-sonnet-20241022",
    val messages: List<Message>,
    @SerializedName("max_tokens")
    val maxTokens: Int = 4096,
    val temperature: Double = 0.7,
    val system: String? = null,
    val stream: Boolean = false
)

data class Message(
    val role: String, // "user" or "assistant"
    val content: String
)

/**
 * Response model for Anthropic Claude API
 */
data class ClaudeResponse(
    val id: String,
    val type: String,
    val role: String,
    val content: List<ContentBlock>,
    val model: String,
    @SerializedName("stop_reason")
    val stopReason: String?,
    @SerializedName("stop_sequence")
    val stopSequence: String?,
    val usage: Usage
)

data class ContentBlock(
    val type: String,
    val text: String
)

data class Usage(
    @SerializedName("input_tokens")
    val inputTokens: Int,
    @SerializedName("output_tokens")
    val outputTokens: Int
)

/**
 * Streaming response for real-time interactions
 */
data class StreamEvent(
    val type: String,
    val message: ClaudeResponse?,
    @SerializedName("content_block")
    val contentBlock: ContentBlock?,
    val delta: Delta?
)

data class Delta(
    val type: String,
    val text: String?
)

/**
 * Error response
 */
data class ClaudeError(
    val type: String,
    val error: ErrorDetail
)

data class ErrorDetail(
    val type: String,
    val message: String
)
