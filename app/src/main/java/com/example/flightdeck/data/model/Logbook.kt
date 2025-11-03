package com.example.flightdeck.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Embedded

/**
 * Digital logbook entry for each mission/training session
 * Mirrors a real pilot logbook with additional digital features
 */
@Entity(tableName = "logbook_entries")
data class LogbookEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // ATC Practice Session Information
    val missionName: String, // e.g., "Ground Clearance Practice at KPAO"

    // Route Information
    val departureAirport: String, // ICAO
    val arrivalAirport: String, // ICAO
    val route: String = "Direct",
    val distance: Int = 0, // NM

    // Time Tracking (in minutes, like real logbook)
    val totalTime: Int, // Total mission time
    val simulatedInstrumentTime: Int = 0, // For IFR practice
    val nightTime: Int = 0, // Night conditions

    // Session Details
    val date: Long = System.currentTimeMillis(),
    val startTime: Long,
    val endTime: Long,

    // Difficulty & Configuration
    val difficulty: Difficulty,
    val scenarioType: String = "GROUND_CLEARANCE", // e.g., GROUND_CLEARANCE, TAKEOFF, LANDING
    val trafficDensity: TrafficDensity = TrafficDensity.MODERATE,
    val weatherComplexity: WeatherComplexity = WeatherComplexity.TYPICAL_VFR,

    // Performance Metrics
    val overallScore: Float, // 0-100
    val communicationScore: Float = 0f, // 0-100
    val navigationScore: Float = 0f, // 0-100
    val weatherDecisionScore: Float = 0f, // 0-100
    val emergencyHandlingScore: Float = 0f, // 0-100
    val trafficManagementScore: Float = 0f, // 0-100

    // Communication Statistics
    val totalCommunications: Int = 0,
    val correctCommunications: Int = 0,
    val frequencyChanges: Int = 0,
    val emergenciesDeclared: Int = 0,

    // Events & Incidents
    val goArounds: Int = 0,
    val weatherDiversions: Int = 0,
    val trafficConflicts: Int = 0,
    val equipmentFailures: Int = 0,

    // Notes & Feedback
    val userNotes: String = "",
    val aiInstructorFeedback: String = "",
    val areasForImprovement: String = "", // Comma-separated skill areas

    // Validation & Certification (for future CFI integration)
    val isCertified: Boolean = false, // CFI reviewed and approved
    val certifiedBy: String? = null, // CFI name/ID
    val certificationDate: Long? = null,

    // Metadata
    val appVersion: String = "1.0.0",
    val isCompleted: Boolean = true,
    val wasAborted: Boolean = false,
    val abortReason: String? = null
)

/**
 * Proficiency rating for specific skill categories
 * Tracks improvement over time
 */
@Entity(tableName = "proficiency_ratings")
data class ProficiencyRating(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val skillCategory: SkillCategory,

    // Current Rating
    val currentRating: ProficiencyLevel,
    val currentScore: Float, // 0-100, detailed score
    val lastUpdated: Long = System.currentTimeMillis(),

    // Historical Data
    val previousRating: ProficiencyLevel? = null,
    val previousScore: Float? = null,
    val ratingChangedDate: Long? = null,

    // Trend Analysis
    val totalSessions: Int = 0, // Sessions where this skill was evaluated
    val averageScore: Float = 0f, // Average score across all sessions
    val bestScore: Float = 0f,
    val worstScore: Float = 0f,

    // Recent Performance (last 5 sessions)
    val recentScores: String = "", // Comma-separated last 5 scores
    val trend: TrendDirection = TrendDirection.STABLE,

    // Recommendations
    val needsImprovement: Boolean = false,
    val recommendedChallenges: String = "",
    val cfiFeedback: String? = null
)

/**
 * Skill categories for proficiency tracking
 */
enum class SkillCategory(val displayName: String, val icon: String, val description: String) {
    RADIO_COMMUNICATION(
        "Radio Communication",
        "📻",
        "ATC phraseology, readbacks, and radio procedures"
    ),
    NAVIGATION(
        "Navigation",
        "🧭",
        "Route planning, waypoint tracking, position awareness"
    ),
    WEATHER_DECISION_MAKING(
        "Weather Decision Making",
        "🌤️",
        "Weather interpretation, go/no-go decisions, diversions"
    ),
    TRAFFIC_MANAGEMENT(
        "Traffic Management",
        "✈️",
        "See and avoid, traffic pattern procedures, spacing"
    ),
    EMERGENCY_PROCEDURES(
        "Emergency Procedures",
        "🚨",
        "Emergency recognition, declaration, and handling"
    ),
    AIRSPACE_KNOWLEDGE(
        "Airspace Knowledge",
        "🗺️",
        "Class B/C/D procedures, clearances, restrictions"
    ),
    CHECKLIST_DISCIPLINE(
        "Checklist Discipline",
        "✅",
        "Proper checklist usage and procedural compliance"
    ),
    SITUATIONAL_AWARENESS(
        "Situational Awareness",
        "🎯",
        "Overall awareness of aircraft state and environment"
    ),
    DECISION_MAKING(
        "Decision Making",
        "🧠",
        "ADM (Aeronautical Decision Making) and judgment"
    ),
    FUEL_MANAGEMENT(
        "Fuel Management",
        "⛽",
        "Fuel planning, monitoring, and reserve management"
    );

    // TODO: Uncomment when implementing challenge system (Phase 2)
    /*
    companion object {
        fun fromChallengeModule(challenge: ChallengeModule): List<SkillCategory> {
            return when (challenge.category) {
                ChallengeCategory.COMMUNICATION -> listOf(
                    RADIO_COMMUNICATION,
                    SITUATIONAL_AWARENESS
                )
                ChallengeCategory.TRAFFIC -> listOf(
                    TRAFFIC_MANAGEMENT,
                    SITUATIONAL_AWARENESS,
                    DECISION_MAKING
                )
                ChallengeCategory.WEATHER -> listOf(
                    WEATHER_DECISION_MAKING,
                    DECISION_MAKING
                )
                ChallengeCategory.EMERGENCY -> listOf(
                    EMERGENCY_PROCEDURES,
                    DECISION_MAKING,
                    SITUATIONAL_AWARENESS
                )
                ChallengeCategory.OPERATIONAL -> listOf(
                    FUEL_MANAGEMENT,
                    DECISION_MAKING,
                    SITUATIONAL_AWARENESS
                )
                ChallengeCategory.ADVANCED_PROCEDURES -> listOf(
                    AIRSPACE_KNOWLEDGE,
                    RADIO_COMMUNICATION,
                    NAVIGATION
                )
            }
        }
    }
    */
}

/**
 * Proficiency levels (based on FAA practical test standards)
 */
enum class ProficiencyLevel(val displayName: String, val minScore: Float, val color: String) {
    UNSATISFACTORY("Unsatisfactory", 0f, "#EF4444"),      // 0-59
    DEVELOPING("Developing", 60f, "#F59E0B"),              // 60-69
    PROFICIENT("Proficient", 70f, "#10B981"),              // 70-84
    ADVANCED("Advanced", 85f, "#06B6D4"),                  // 85-94
    EXPERT("Expert", 95f, "#8B5CF6");                      // 95-100

    companion object {
        fun fromScore(score: Float): ProficiencyLevel {
            return when {
                score >= 95f -> EXPERT
                score >= 85f -> ADVANCED
                score >= 70f -> PROFICIENT
                score >= 60f -> DEVELOPING
                else -> UNSATISFACTORY
            }
        }
    }
}

/**
 * Trend direction for skill progression
 */
enum class TrendDirection(val displayName: String, val icon: String) {
    IMPROVING("Improving", "📈"),
    STABLE("Stable", "➡️"),
    DECLINING("Declining", "📉")
}

/**
 * Aggregated logbook totals for quick access
 * Updated after each session
 */
@Entity(tableName = "logbook_totals")
data class LogbookTotals(
    @PrimaryKey
    val userId: String = "default_user", // For future multi-user support

    // Time Totals (in minutes)
    val totalTime: Int = 0,
    val simulatedInstrumentTime: Int = 0,
    val nightTime: Int = 0,

    // Session Counts
    val totalSessions: Int = 0,
    val completedSessions: Int = 0,
    val abortedSessions: Int = 0,

    // Distance & Routes
    val totalDistance: Int = 0, // Total NM flown
    val uniqueAirportsVisited: Int = 0,
    val uniqueRoutes: Int = 0,

    // Performance Averages
    val averageOverallScore: Float = 0f,
    val averageCommunicationScore: Float = 0f,
    val averageNavigationScore: Float = 0f,
    val averageWeatherScore: Float = 0f,
    val averageEmergencyScore: Float = 0f,
    val averageTrafficScore: Float = 0f,

    // Achievements
    val perfectScores: Int = 0, // Sessions with 100% score
    val emergenciesHandled: Int = 0,
    val goAroundsExecuted: Int = 0,
    val weatherDiversions: Int = 0,

    // Challenges Completed
    val totalChallengesCompleted: Int = 0,
    val uniqueChallengesCompleted: Int = 0,

    // Difficulty Progress
    val beginnerMissions: Int = 0,
    val intermediateMissions: Int = 0,
    val advancedMissions: Int = 0,
    val expertMissions: Int = 0,

    // Last Activity
    val lastSessionDate: Long? = null,
    val longestSessionMinutes: Int = 0,
    val currentStreak: Int = 0, // Days with consecutive activity

    // Last Updated
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Skill breakdown for a specific session
 */
data class SessionSkillBreakdown(
    val skillCategory: SkillCategory,
    val score: Float,
    val performanceLevel: ProficiencyLevel,
    val previousScore: Float?,
    val improvement: Float?, // Difference from previous session
    val feedback: String
)

/**
 * Detailed session summary with all metrics
 */
data class LogbookSessionSummary(
    @Embedded
    val entry: LogbookEntry,
    val skillBreakdowns: List<SessionSkillBreakdown>,
    val highlightedAchievements: List<String>,
    val areasNeedingWork: List<String>,
    val comparisonToAverage: Map<String, Float> // How this session compares to user's average
)

/**
 * Time period summary for analytics
 */
data class LogbookPeriodSummary(
    val startDate: Long,
    val endDate: Long,
    val totalSessions: Int,
    val totalTime: Int, // minutes
    val averageScore: Float,
    val improvementRate: Float, // Percentage improvement over period
    val topSkills: List<Pair<SkillCategory, Float>>, // Best performing skills
    val needsWork: List<Pair<SkillCategory, Float>>, // Skills needing improvement
    val challengesCompleted: Int,
    val difficultiesAttempted: Map<Difficulty, Int>
)

// TODO: Uncomment and update when implementing export (Phase 2)
/*
/**
 * Export format for logbook data
 */
data class LogbookExportData(
    val generatedDate: Long = System.currentTimeMillis(),
    val entries: List<LogbookEntry>,
    val periodSummaries: List<LogbookPeriodSummary>,
    val userInfo: UserExportInfo
)

/**
 * User information for export
 */
data class UserExportInfo(
    val userId: String,
    val exportDate: Long,
    val appVersion: String,
    val totalFlightHours: Float, // Converted from minutes
    val currentRank: String, // Based on proficiency
    val joinDate: Long
)
*/

/**
 * Helper extensions for LogbookEntry
 */
fun LogbookEntry.getDurationMinutes(): Int {
    return ((endTime - startTime) / 60000).toInt()
}

// TODO: Uncomment when implementing challenge system (Phase 2)
/*
fun LogbookEntry.getChallengesList(): List<ChallengeModule> {
    if (challengesCompleted.isEmpty()) return emptyList()
    return challengesCompleted.split(",")
        .mapNotNull { ChallengeModule.fromString(it.trim()) }
}
*/

fun LogbookEntry.getAreasForImprovementList(): List<SkillCategory> {
    if (areasForImprovement.isEmpty()) return emptyList()
    return areasForImprovement.split(",")
        .mapNotNull {
            try {
                SkillCategory.valueOf(it.trim())
            } catch (e: Exception) {
                null
            }
        }
}

fun LogbookEntry.getFormattedDate(): String {
    val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US)
    return sdf.format(java.util.Date(date))
}

fun LogbookEntry.getFormattedRoute(): String {
    return "$departureAirport → $arrivalAirport"
}

/**
 * Calculate flight hours from minutes (like real logbook)
 */
fun Int.toFlightHours(): Float {
    return this / 60f
}

fun Float.formatFlightTime(): String {
    val hours = this.toInt()
    val minutes = ((this - hours) * 60).toInt()
    return String.format("%d:%02d", hours, minutes)
}

/**
 * Helper data classes for query results
 */
data class DifficultyCount(
    val difficulty: Difficulty,
    val count: Int
)

data class DailyScore(
    val date: Long,
    val avgScore: Float
)
