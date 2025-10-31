package com.example.flightdeck.data.local.dao

import androidx.room.*
import com.example.flightdeck.data.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for digital logbook
 */
@Dao
interface LogbookDao {

    // ========================================
    // Logbook Entry CRUD
    // ========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: LogbookEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<LogbookEntry>)

    @Update
    suspend fun updateEntry(entry: LogbookEntry)

    @Delete
    suspend fun deleteEntry(entry: LogbookEntry)

    @Query("SELECT * FROM logbook_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): LogbookEntry?

    @Query("SELECT * FROM logbook_entries WHERE id = :id")
    fun getEntryByIdFlow(id: Long): Flow<LogbookEntry?>

    @Query("SELECT * FROM logbook_entries ORDER BY date DESC, startTime DESC")
    suspend fun getAllEntries(): List<LogbookEntry>

    @Query("SELECT * FROM logbook_entries ORDER BY date DESC, startTime DESC")
    fun getAllEntriesFlow(): Flow<List<LogbookEntry>>

    @Query("SELECT * FROM logbook_entries ORDER BY date DESC, startTime DESC LIMIT :limit")
    suspend fun getRecentEntries(limit: Int = 10): List<LogbookEntry>

    @Query("SELECT * FROM logbook_entries ORDER BY date DESC, startTime DESC LIMIT :limit")
    fun getRecentEntriesFlow(limit: Int = 10): Flow<List<LogbookEntry>>

    // ========================================
    // Filtering Queries
    // ========================================

    @Query("SELECT * FROM logbook_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getEntriesByDateRange(startDate: Long, endDate: Long): List<LogbookEntry>

    @Query("SELECT * FROM logbook_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getEntriesByDateRangeFlow(startDate: Long, endDate: Long): Flow<List<LogbookEntry>>

    @Query("SELECT * FROM logbook_entries WHERE difficulty = :difficulty ORDER BY date DESC")
    suspend fun getEntriesByDifficulty(difficulty: Difficulty): List<LogbookEntry>

    @Query("SELECT * FROM logbook_entries WHERE departureAirport = :icao OR arrivalAirport = :icao ORDER BY date DESC")
    suspend fun getEntriesByAirport(icao: String): List<LogbookEntry>

    @Query("SELECT * FROM logbook_entries WHERE overallScore >= :minScore ORDER BY overallScore DESC")
    suspend fun getEntriesByMinScore(minScore: Float): List<LogbookEntry>

    @Query("SELECT * FROM logbook_entries WHERE isCompleted = 1 ORDER BY date DESC")
    suspend fun getCompletedEntries(): List<LogbookEntry>

    @Query("SELECT * FROM logbook_entries WHERE wasAborted = 1 ORDER BY date DESC")
    suspend fun getAbortedEntries(): List<LogbookEntry>

    @Query("SELECT * FROM logbook_entries WHERE isCertified = 1 ORDER BY certificationDate DESC")
    suspend fun getCertifiedEntries(): List<LogbookEntry>

    @Query("SELECT * FROM logbook_entries WHERE challengesCompleted != '' ORDER BY date DESC")
    suspend fun getEntriesWithChallenges(): List<LogbookEntry>

    // ========================================
    // Statistics Queries
    // ========================================

    @Query("SELECT COUNT(*) FROM logbook_entries")
    suspend fun getTotalEntries(): Int

    @Query("SELECT COUNT(*) FROM logbook_entries WHERE isCompleted = 1")
    suspend fun getCompletedCount(): Int

    @Query("SELECT SUM(totalTime) FROM logbook_entries WHERE isCompleted = 1")
    suspend fun getTotalFlightTime(): Int?

    @Query("SELECT AVG(overallScore) FROM logbook_entries WHERE isCompleted = 1")
    suspend fun getAverageScore(): Float?

    @Query("SELECT AVG(communicationScore) FROM logbook_entries WHERE isCompleted = 1")
    suspend fun getAverageCommunicationScore(): Float?

    @Query("SELECT AVG(navigationScore) FROM logbook_entries WHERE isCompleted = 1")
    suspend fun getAverageNavigationScore(): Float?

    @Query("SELECT AVG(weatherDecisionScore) FROM logbook_entries WHERE isCompleted = 1")
    suspend fun getAverageWeatherScore(): Float?

    @Query("SELECT AVG(emergencyHandlingScore) FROM logbook_entries WHERE isCompleted = 1")
    suspend fun getAverageEmergencyScore(): Float?

    @Query("SELECT AVG(trafficManagementScore) FROM logbook_entries WHERE isCompleted = 1")
    suspend fun getAverageTrafficScore(): Float?

    @Query("SELECT COUNT(*) FROM logbook_entries WHERE overallScore = 100")
    suspend fun getPerfectScoreCount(): Int

    @Query("SELECT SUM(goArounds) FROM logbook_entries WHERE isCompleted = 1")
    suspend fun getTotalGoArounds(): Int?

    @Query("SELECT SUM(weatherDiversions) FROM logbook_entries WHERE isCompleted = 1")
    suspend fun getTotalWeatherDiversions(): Int?

    @Query("SELECT SUM(emergenciesDeclared) FROM logbook_entries WHERE isCompleted = 1")
    suspend fun getTotalEmergenciesDeclared(): Int?

    @Query("SELECT COUNT(DISTINCT departureAirport) + COUNT(DISTINCT arrivalAirport) FROM logbook_entries")
    suspend fun getUniqueAirportsCount(): Int

    // ========================================
    // Analytics Queries
    // ========================================

    @Query("""
        SELECT * FROM logbook_entries
        WHERE date BETWEEN :startDate AND :endDate
        AND isCompleted = 1
        ORDER BY overallScore DESC
        LIMIT 1
    """)
    suspend fun getBestSessionInPeriod(startDate: Long, endDate: Long): LogbookEntry?

    @Query("""
        SELECT * FROM logbook_entries
        WHERE isCompleted = 1
        ORDER BY overallScore DESC
        LIMIT 1
    """)
    suspend fun getBestSessionAllTime(): LogbookEntry?

    @Query("""
        SELECT difficulty, COUNT(*) as count
        FROM logbook_entries
        WHERE isCompleted = 1
        GROUP BY difficulty
    """)
    suspend fun getSessionCountByDifficulty(): List<DifficultyCount>

    @Query("""
        SELECT date, AVG(overallScore) as avgScore
        FROM logbook_entries
        WHERE isCompleted = 1
        AND date BETWEEN :startDate AND :endDate
        GROUP BY date
        ORDER BY date ASC
    """)
    suspend fun getDailyAverageScores(startDate: Long, endDate: Long): List<DailyScore>

    // ========================================
    // Proficiency Rating CRUD
    // ========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProficiencyRating(rating: ProficiencyRating): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProficiencyRatings(ratings: List<ProficiencyRating>)

    @Update
    suspend fun updateProficiencyRating(rating: ProficiencyRating)

    @Query("SELECT * FROM proficiency_ratings WHERE skillCategory = :category")
    suspend fun getProficiencyByCategory(category: SkillCategory): ProficiencyRating?

    @Query("SELECT * FROM proficiency_ratings WHERE skillCategory = :category")
    fun getProficiencyByCategoryFlow(category: SkillCategory): Flow<ProficiencyRating?>

    @Query("SELECT * FROM proficiency_ratings ORDER BY currentScore DESC")
    suspend fun getAllProficiencyRatings(): List<ProficiencyRating>

    @Query("SELECT * FROM proficiency_ratings ORDER BY currentScore DESC")
    fun getAllProficiencyRatingsFlow(): Flow<List<ProficiencyRating>>

    @Query("SELECT * FROM proficiency_ratings WHERE needsImprovement = 1 ORDER BY currentScore ASC")
    suspend fun getProficienciesNeedingImprovement(): List<ProficiencyRating>

    @Query("SELECT * FROM proficiency_ratings WHERE needsImprovement = 1 ORDER BY currentScore ASC")
    fun getProficienciesNeedingImprovementFlow(): Flow<List<ProficiencyRating>>

    @Query("SELECT * FROM proficiency_ratings WHERE currentRating >= :minLevel ORDER BY currentScore DESC")
    suspend fun getProficienciesByMinLevel(minLevel: ProficiencyLevel): List<ProficiencyRating>

    @Query("SELECT * FROM proficiency_ratings WHERE trend = :trend")
    suspend fun getProficienciesByTrend(trend: TrendDirection): List<ProficiencyRating>

    @Query("SELECT AVG(currentScore) FROM proficiency_ratings")
    suspend fun getAverageProficiencyScore(): Float?

    // ========================================
    // Logbook Totals CRUD
    // ========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTotals(totals: LogbookTotals)

    @Update
    suspend fun updateTotals(totals: LogbookTotals)

    @Query("SELECT * FROM logbook_totals WHERE userId = :userId")
    suspend fun getTotals(userId: String = "default_user"): LogbookTotals?

    @Query("SELECT * FROM logbook_totals WHERE userId = :userId")
    fun getTotalsFlow(userId: String = "default_user"): Flow<LogbookTotals?>

    @Query("""
        UPDATE logbook_totals
        SET totalTime = totalTime + :minutes,
            totalSessions = totalSessions + 1,
            completedSessions = completedSessions + 1,
            lastSessionDate = :sessionDate,
            lastUpdated = :timestamp
        WHERE userId = :userId
    """)
    suspend fun incrementTotals(
        userId: String = "default_user",
        minutes: Int,
        sessionDate: Long,
        timestamp: Long = System.currentTimeMillis()
    )

    // ========================================
    // Search Queries
    // ========================================

    @Query("""
        SELECT * FROM logbook_entries
        WHERE departureAirport LIKE '%' || :query || '%'
        OR arrivalAirport LIKE '%' || :query || '%'
        OR missionName LIKE '%' || :query || '%'
        OR route LIKE '%' || :query || '%'
        OR userNotes LIKE '%' || :query || '%'
        ORDER BY date DESC
    """)
    suspend fun searchEntries(query: String): List<LogbookEntry>

    @Query("""
        SELECT * FROM logbook_entries
        WHERE departureAirport LIKE '%' || :query || '%'
        OR arrivalAirport LIKE '%' || :query || '%'
        OR missionName LIKE '%' || :query || '%'
        OR route LIKE '%' || :query || '%'
        OR userNotes LIKE '%' || :query || '%'
        ORDER BY date DESC
    """)
    fun searchEntriesFlow(query: String): Flow<List<LogbookEntry>>

    // ========================================
    // Batch Operations
    // ========================================

    @Query("DELETE FROM logbook_entries")
    suspend fun deleteAllEntries()

    @Query("DELETE FROM proficiency_ratings")
    suspend fun deleteAllProficiencyRatings()

    @Query("DELETE FROM logbook_entries WHERE date < :cutoffDate")
    suspend fun deleteEntriesBeforeDate(cutoffDate: Long)

    // ========================================
    // Export Queries
    // ========================================

    @Query("""
        SELECT * FROM logbook_entries
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY date ASC, startTime ASC
    """)
    suspend fun getEntriesForExport(startDate: Long, endDate: Long): List<LogbookEntry>

    @Query("SELECT * FROM logbook_entries WHERE isCertified = 1 ORDER BY certificationDate ASC")
    suspend fun getCertifiedEntriesForExport(): List<LogbookEntry>
}
