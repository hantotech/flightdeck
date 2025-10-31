package com.example.flightdeck.data.local.dao

import androidx.room.*
import com.example.flightdeck.data.model.Difficulty
import com.example.flightdeck.data.model.MissionConfig
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for mission configurations
 */
@Dao
interface MissionConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(missionConfig: MissionConfig): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(configs: List<MissionConfig>)

    @Update
    suspend fun update(missionConfig: MissionConfig)

    @Delete
    suspend fun delete(missionConfig: MissionConfig)

    @Query("SELECT * FROM mission_configs WHERE id = :id")
    suspend fun getById(id: Long): MissionConfig?

    @Query("SELECT * FROM mission_configs WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<MissionConfig?>

    @Query("SELECT * FROM mission_configs WHERE flightPlanId = :flightPlanId")
    suspend fun getByFlightPlanId(flightPlanId: Long): List<MissionConfig>

    @Query("SELECT * FROM mission_configs WHERE flightPlanId = :flightPlanId")
    fun getByFlightPlanIdFlow(flightPlanId: Long): Flow<List<MissionConfig>>

    @Query("SELECT * FROM mission_configs WHERE isPreset = 1 ORDER BY baseDifficulty")
    suspend fun getAllPresets(): List<MissionConfig>

    @Query("SELECT * FROM mission_configs WHERE isPreset = 1 ORDER BY baseDifficulty")
    fun getAllPresetsFlow(): Flow<List<MissionConfig>>

    @Query("SELECT * FROM mission_configs WHERE isUserCreated = 1 ORDER BY lastFlown DESC, createdAt DESC")
    suspend fun getAllUserCreated(): List<MissionConfig>

    @Query("SELECT * FROM mission_configs WHERE isUserCreated = 1 ORDER BY lastFlown DESC, createdAt DESC")
    fun getAllUserCreatedFlow(): Flow<List<MissionConfig>>

    @Query("SELECT * FROM mission_configs WHERE baseDifficulty = :difficulty AND isPreset = 1")
    suspend fun getPresetsByDifficulty(difficulty: Difficulty): List<MissionConfig>

    @Query("SELECT * FROM mission_configs ORDER BY createdAt DESC")
    suspend fun getAll(): List<MissionConfig>

    @Query("SELECT * FROM mission_configs ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<MissionConfig>>

    @Query("SELECT * FROM mission_configs WHERE lastFlown IS NOT NULL ORDER BY lastFlown DESC LIMIT :limit")
    suspend fun getRecentlyFlown(limit: Int = 5): List<MissionConfig>

    @Query("SELECT * FROM mission_configs WHERE lastFlown IS NOT NULL ORDER BY lastFlown DESC LIMIT :limit")
    fun getRecentlyFlownFlow(limit: Int = 5): Flow<List<MissionConfig>>

    @Query("SELECT * FROM mission_configs WHERE timesFlown > 0 ORDER BY timesFlown DESC LIMIT :limit")
    suspend fun getMostFlown(limit: Int = 5): List<MissionConfig>

    @Query("UPDATE mission_configs SET timesFlown = timesFlown + 1, lastFlown = :timestamp WHERE id = :id")
    suspend fun incrementTimesFlown(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE mission_configs SET averageScore = :score WHERE id = :id")
    suspend fun updateAverageScore(id: Long, score: Float)

    @Query("DELETE FROM mission_configs WHERE isUserCreated = 1 AND id = :id")
    suspend fun deleteUserCreated(id: Long)

    @Query("DELETE FROM mission_configs WHERE isUserCreated = 1")
    suspend fun deleteAllUserCreated()

    @Query("SELECT COUNT(*) FROM mission_configs WHERE isPreset = 1")
    suspend fun getPresetCount(): Int

    @Query("SELECT COUNT(*) FROM mission_configs WHERE isUserCreated = 1")
    suspend fun getUserCreatedCount(): Int
}
