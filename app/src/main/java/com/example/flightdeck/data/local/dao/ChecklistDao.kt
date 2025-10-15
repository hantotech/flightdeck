package com.example.flightdeck.data.local.dao

import androidx.room.*
import com.example.flightdeck.data.model.ChecklistItem
import com.example.flightdeck.data.model.ChecklistItemCompletion
import com.example.flightdeck.data.model.ChecklistSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {

    // Checklist Items
    @Query("SELECT * FROM checklist_items WHERE checklistId = :checklistId ORDER BY sequence")
    fun getChecklistItems(checklistId: Long): Flow<List<ChecklistItem>>

    @Query("SELECT * FROM checklist_items WHERE id = :id")
    suspend fun getChecklistItemById(id: Long): ChecklistItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: ChecklistItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ChecklistItem>)

    @Update
    suspend fun updateChecklistItem(item: ChecklistItem)

    @Delete
    suspend fun deleteChecklistItem(item: ChecklistItem)

    // Checklist Sessions
    @Query("SELECT * FROM checklist_sessions WHERE aircraftId = :aircraftId ORDER BY startTime DESC")
    fun getChecklistSessions(aircraftId: Long): Flow<List<ChecklistSession>>

    @Query("SELECT * FROM checklist_sessions WHERE id = :sessionId")
    suspend fun getChecklistSessionById(sessionId: Long): ChecklistSession?

    @Query("SELECT * FROM checklist_sessions WHERE isCompleted = 0 LIMIT 1")
    suspend fun getActiveSession(): ChecklistSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistSession(session: ChecklistSession): Long

    @Update
    suspend fun updateChecklistSession(session: ChecklistSession)

    // Checklist Item Completions
    @Query("SELECT * FROM checklist_item_completions WHERE sessionId = :sessionId")
    fun getSessionCompletions(sessionId: Long): Flow<List<ChecklistItemCompletion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: ChecklistItemCompletion): Long

    @Update
    suspend fun updateCompletion(completion: ChecklistItemCompletion)

    @Query("DELETE FROM checklist_item_completions WHERE sessionId = :sessionId")
    suspend fun deleteSessionCompletions(sessionId: Long)
}
