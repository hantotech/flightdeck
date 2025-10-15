package com.example.flightdeck.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents overall user performance metrics
 */
@Entity(tableName = "performance_reports")
data class PerformanceReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String? = null,
    val generatedAt: Long = System.currentTimeMillis(),
    val periodStart: Long,
    val periodEnd: Long,
    val overallScore: Float,
    val checklistAccuracy: Float,
    val atcAccuracy: Float,
    val flightPlansCompleted: Int,
    val checklistsCompleted: Int,
    val atcSessionsCompleted: Int,
    val totalPracticeTime: Long, // milliseconds
    val strengths: List<String> = emptyList(),
    val areasForImprovement: List<String> = emptyList()
)

/**
 * Detailed metrics for a specific skill area
 */
data class SkillMetrics(
    val skillName: String,
    val score: Float,
    val trend: Trend,
    val practiceCount: Int,
    val averageTime: Long, // milliseconds
    val lastPracticed: Long
)

enum class Trend {
    IMPROVING,
    STABLE,
    DECLINING
}

/**
 * Achievement/Badge system
 */
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val iconName: String,
    val unlockedAt: Long? = null,
    val category: AchievementCategory
)

enum class AchievementCategory {
    CHECKLIST_MASTERY,
    ATC_COMMUNICATION,
    FLIGHT_PLANNING,
    CONSISTENCY,
    SPECIAL
}

/**
 * User progress tracking
 */
@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey
    val userId: String,
    val experiencePoints: Int = 0,
    val level: Int = 1,
    val totalFlightHours: Double = 0.0,
    val simulatedFlights: Int = 0,
    val lastActivityDate: Long = System.currentTimeMillis(),
    val isPremium: Boolean = false
)
