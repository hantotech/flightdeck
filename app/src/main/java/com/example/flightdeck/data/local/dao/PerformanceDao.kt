package com.example.flightdeck.data.local.dao

import androidx.room.*
import com.example.flightdeck.data.model.Achievement
import com.example.flightdeck.data.model.PerformanceReport
import com.example.flightdeck.data.model.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface PerformanceDao {

    // Performance Reports
    @Query("SELECT * FROM performance_reports ORDER BY generatedAt DESC")
    fun getAllPerformanceReports(): Flow<List<PerformanceReport>>

    @Query("SELECT * FROM performance_reports WHERE id = :id")
    suspend fun getPerformanceReportById(id: Long): PerformanceReport?

    @Query("SELECT * FROM performance_reports ORDER BY generatedAt DESC LIMIT 1")
    suspend fun getLatestPerformanceReport(): PerformanceReport?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerformanceReport(report: PerformanceReport): Long

    @Delete
    suspend fun deletePerformanceReport(report: PerformanceReport)

    // Achievements
    @Query("SELECT * FROM achievements ORDER BY category, title")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE unlockedAt IS NOT NULL ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE unlockedAt IS NULL")
    fun getLockedAchievements(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)

    @Update
    suspend fun updateAchievement(achievement: Achievement)

    // User Progress
    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    suspend fun getUserProgress(userId: String): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(progress: UserProgress)

    @Update
    suspend fun updateUserProgress(progress: UserProgress)

    @Query("UPDATE user_progress SET experiencePoints = experiencePoints + :points WHERE userId = :userId")
    suspend fun addExperiencePoints(userId: String, points: Int)
}
