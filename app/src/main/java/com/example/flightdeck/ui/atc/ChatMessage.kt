package com.example.flightdeck.ui.atc

/**
 * Represents a single message in the ATC chat interface
 */
data class ChatMessage(
    val text: String,
    val type: MessageType,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Types of messages in the ATC simulator
 */
enum class MessageType {
    PILOT,      // User's messages (right side, blue)
    ATC,        // ATC responses (left side, white)
    SYSTEM      // System messages (center, gray)
}
