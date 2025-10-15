package com.example.flightdeck.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single item in a pre-flight checklist
 */
@Entity(tableName = "checklist_items")
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val checklistId: Long,
    val title: String,
    val description: String,
    val category: ChecklistCategory,
    val sequence: Int,
    val isCritical: Boolean = false,
    val expectedResponse: String? = null
)

enum class ChecklistCategory {
    PRE_FLIGHT_EXTERIOR,
    PRE_FLIGHT_INTERIOR,
    BEFORE_ENGINE_START,
    ENGINE_START,
    BEFORE_TAKEOFF,
    AFTER_TAKEOFF,
    CRUISE,
    DESCENT,
    BEFORE_LANDING,
    AFTER_LANDING,
    SHUTDOWN,
    EMERGENCY
}

/**
 * Represents a checklist session with completion status
 */
@Entity(tableName = "checklist_sessions")
data class ChecklistSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val aircraftId: Long,
    val startTime: Long,
    val endTime: Long? = null,
    val isCompleted: Boolean = false,
    val completionPercentage: Float = 0f
)

/**
 * Tracks completion of individual checklist items
 */
@Entity(tableName = "checklist_item_completions")
data class ChecklistItemCompletion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val itemId: Long,
    val isCompleted: Boolean,
    val completedAt: Long? = null,
    val notes: String? = null
)
