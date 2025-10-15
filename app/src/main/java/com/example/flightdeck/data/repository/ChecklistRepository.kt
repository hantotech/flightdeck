package com.example.flightdeck.data.repository

import com.example.flightdeck.data.local.dao.ChecklistDao
import com.example.flightdeck.data.model.*
import com.example.flightdeck.data.remote.ai.EnhancedAIService
import kotlinx.coroutines.flow.Flow

/**
 * Repository for checklist operations with AI assistance using smart routing
 */
class ChecklistRepository(
    private val checklistDao: ChecklistDao,
    private val aiService: EnhancedAIService = EnhancedAIService.getInstance()
) {

    // Checklist Items
    fun getChecklistItems(checklistId: Long): Flow<List<ChecklistItem>> =
        checklistDao.getChecklistItems(checklistId)

    suspend fun getChecklistItemById(id: Long): ChecklistItem? =
        checklistDao.getChecklistItemById(id)

    // Sessions
    suspend fun startChecklistSession(aircraftId: Long): Long {
        val session = ChecklistSession(
            aircraftId = aircraftId,
            startTime = System.currentTimeMillis()
        )
        return checklistDao.insertChecklistSession(session)
    }

    suspend fun completeChecklistItem(
        sessionId: Long,
        itemId: Long,
        notes: String? = null
    ) {
        val completion = ChecklistItemCompletion(
            sessionId = sessionId,
            itemId = itemId,
            isCompleted = true,
            completedAt = System.currentTimeMillis(),
            notes = notes
        )
        checklistDao.insertCompletion(completion)
    }

    suspend fun endChecklistSession(sessionId: Long) {
        val session = checklistDao.getChecklistSessionById(sessionId)
            ?: throw IllegalArgumentException("Session not found")

        val updatedSession = session.copy(
            endTime = System.currentTimeMillis(),
            isCompleted = true,
            completionPercentage = 100f
        )

        checklistDao.updateChecklistSession(updatedSession)
    }

    // AI Assistance
    suspend fun getItemGuidance(
        checklistItem: ChecklistItem,
        aircraftType: String,
        userQuestion: String? = null
    ): Result<String> {
        return aiService.getChecklistGuidance(
            checklistItem = checklistItem.title,
            aircraftType = aircraftType,
            userQuestion = userQuestion
        )
    }

    // Initialize default checklist
    suspend fun initializeDefaultChecklist(checklistId: Long = 1) {
        val items = listOf(
            ChecklistItem(
                checklistId = checklistId,
                title = "Documents",
                description = "ARROW - Airworthiness, Registration, Radio License, Operating Handbook, Weight & Balance",
                category = ChecklistCategory.PRE_FLIGHT_INTERIOR,
                sequence = 1,
                isCritical = true
            ),
            ChecklistItem(
                checklistId = checklistId,
                title = "Fuel Quantity",
                description = "Check fuel gauges and visually inspect fuel level",
                category = ChecklistCategory.PRE_FLIGHT_EXTERIOR,
                sequence = 2,
                isCritical = true,
                expectedResponse = "Sufficient for flight plus reserves"
            ),
            ChecklistItem(
                checklistId = checklistId,
                title = "Oil Level",
                description = "Check oil dipstick - minimum 6 quarts",
                category = ChecklistCategory.PRE_FLIGHT_EXTERIOR,
                sequence = 3,
                isCritical = true,
                expectedResponse = "Within normal range"
            ),
            ChecklistItem(
                checklistId = checklistId,
                title = "Control Surfaces",
                description = "Check ailerons, elevator, rudder for freedom of movement and damage",
                category = ChecklistCategory.PRE_FLIGHT_EXTERIOR,
                sequence = 4,
                isCritical = true,
                expectedResponse = "Free and correct"
            ),
            ChecklistItem(
                checklistId = checklistId,
                title = "Seat Belts",
                description = "Secure and adjusted",
                category = ChecklistCategory.BEFORE_ENGINE_START,
                sequence = 5,
                isCritical = true,
                expectedResponse = "Secure"
            ),
            ChecklistItem(
                checklistId = checklistId,
                title = "Fuel Selector",
                description = "Set to appropriate tank (typically BOTH)",
                category = ChecklistCategory.BEFORE_ENGINE_START,
                sequence = 6,
                isCritical = true,
                expectedResponse = "BOTH"
            ),
            ChecklistItem(
                checklistId = checklistId,
                title = "Mixture",
                description = "Set to RICH",
                category = ChecklistCategory.ENGINE_START,
                sequence = 7,
                isCritical = true,
                expectedResponse = "RICH"
            ),
            ChecklistItem(
                checklistId = checklistId,
                title = "Throttle",
                description = "Set to 1/4 inch open",
                category = ChecklistCategory.ENGINE_START,
                sequence = 8,
                isCritical = false,
                expectedResponse = "Set"
            ),
            ChecklistItem(
                checklistId = checklistId,
                title = "Propeller Area",
                description = "Clear - call out 'CLEAR PROP'",
                category = ChecklistCategory.ENGINE_START,
                sequence = 9,
                isCritical = true,
                expectedResponse = "Clear"
            ),
            ChecklistItem(
                checklistId = checklistId,
                title = "Flight Controls",
                description = "Check for full and free movement",
                category = ChecklistCategory.BEFORE_TAKEOFF,
                sequence = 10,
                isCritical = true,
                expectedResponse = "Free and correct"
            ),
            ChecklistItem(
                checklistId = checklistId,
                title = "Takeoff Briefing",
                description = "Review runway, abort point, emergency procedures",
                category = ChecklistCategory.BEFORE_TAKEOFF,
                sequence = 11,
                isCritical = true,
                expectedResponse = "Briefed"
            )
        )

        checklistDao.insertChecklistItems(items)
    }
}
