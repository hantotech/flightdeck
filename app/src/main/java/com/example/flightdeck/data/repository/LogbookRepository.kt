package com.example.flightdeck.data.repository

import com.example.flightdeck.data.local.dao.LogbookDao
import com.example.flightdeck.data.model.*
import com.example.flightdeck.utils.ProficiencyCalculator
import com.example.flightdeck.utils.PilotRank
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository for digital logbook operations
 * Handles logging sessions, tracking proficiency, and generating analytics
 */
class LogbookRepository(
    private val logbookDao: LogbookDao
) {

    // ========================================
    // Logbook Entry Operations
    // ========================================

    /**
     * Record a new training session in the logbook
     * Automatically updates proficiency ratings and totals
     */
    suspend fun recordSession(entry: LogbookEntry): Long {
        // Insert logbook entry
        val entryId = logbookDao.insertEntry(entry)

        // Update proficiency ratings for all relevant skills
        updateProficiencies(entry)

        // Update totals
        updateTotals(entry)

        return entryId
    }

    suspend fun getEntry(id: Long): LogbookEntry? {
        return logbookDao.getEntryById(id)
    }

    fun getEntryFlow(id: Long): Flow<LogbookEntry?> {
        return logbookDao.getEntryByIdFlow(id)
    }

    suspend fun getAllEntries(): List<LogbookEntry> {
        return logbookDao.getAllEntries()
    }

    fun getAllEntriesFlow(): Flow<List<LogbookEntry>> {
        return logbookDao.getAllEntriesFlow()
    }

    suspend fun getRecentEntries(limit: Int = 10): List<LogbookEntry> {
        return logbookDao.getRecentEntries(limit)
    }

    fun getRecentEntriesFlow(limit: Int = 10): Flow<List<LogbookEntry>> {
        return logbookDao.getRecentEntriesFlow(limit)
    }

    suspend fun updateEntry(entry: LogbookEntry) {
        logbookDao.updateEntry(entry)
    }

    suspend fun deleteEntry(entry: LogbookEntry) {
        logbookDao.deleteEntry(entry)
        // Note: This doesn't recalculate proficiencies - that would require full recalc
    }

    // ========================================
    // Filtering & Search
    // ========================================

    suspend fun getEntriesByDateRange(startDate: Long, endDate: Long): List<LogbookEntry> {
        return logbookDao.getEntriesByDateRange(startDate, endDate)
    }

    fun getEntriesByDateRangeFlow(startDate: Long, endDate: Long): Flow<List<LogbookEntry>> {
        return logbookDao.getEntriesByDateRangeFlow(startDate, endDate)
    }

    suspend fun getEntriesByDifficulty(difficulty: Difficulty): List<LogbookEntry> {
        return logbookDao.getEntriesByDifficulty(difficulty)
    }

    suspend fun getEntriesByAirport(icao: String): List<LogbookEntry> {
        return logbookDao.getEntriesByAirport(icao)
    }

    suspend fun searchEntries(query: String): List<LogbookEntry> {
        return logbookDao.searchEntries(query)
    }

    fun searchEntriesFlow(query: String): Flow<List<LogbookEntry>> {
        return logbookDao.searchEntriesFlow(query)
    }

    // ========================================
    // Proficiency Management
    // ========================================

    suspend fun getProficiencyRating(category: SkillCategory): ProficiencyRating? {
        return logbookDao.getProficiencyByCategory(category)
    }

    fun getProficiencyRatingFlow(category: SkillCategory): Flow<ProficiencyRating?> {
        return logbookDao.getProficiencyByCategoryFlow(category)
    }

    suspend fun getAllProficiencyRatings(): List<ProficiencyRating> {
        return logbookDao.getAllProficiencyRatings()
    }

    fun getAllProficiencyRatingsFlow(): Flow<List<ProficiencyRating>> {
        return logbookDao.getAllProficiencyRatingsFlow()
    }

    suspend fun getProficienciesNeedingImprovement(): List<ProficiencyRating> {
        return logbookDao.getProficienciesNeedingImprovement()
    }

    fun getProficienciesNeedingImprovementFlow(): Flow<List<ProficiencyRating>> {
        return logbookDao.getProficienciesNeedingImprovementFlow()
    }

    /**
     * Update proficiency ratings based on a new session
     */
    private suspend fun updateProficiencies(entry: LogbookEntry) {
        val skillScores = ProficiencyCalculator.calculateSkillScores(entry)

        skillScores.forEach { (category, score) ->
            val currentProficiency = getProficiencyRating(category)
            val updatedProficiency = ProficiencyCalculator.updateProficiency(
                currentProficiency,
                score,
                entry
            )

            // Add recommended challenges for skills needing improvement
            if (updatedProficiency.needsImprovement) {
                val recommendations = ProficiencyCalculator.recommendChallengesForSkill(category)
                logbookDao.updateProficiencyRating(
                    updatedProficiency.copy(
                        recommendedChallenges = recommendations.joinToString(",") { it.name }
                    )
                )
            } else {
                logbookDao.updateProficiencyRating(updatedProficiency)
            }
        }
    }

    // ========================================
    // Logbook Totals
    // ========================================

    suspend fun getTotals(): LogbookTotals {
        return logbookDao.getTotals() ?: createInitialTotals()
    }

    fun getTotalsFlow(): Flow<LogbookTotals> {
        return logbookDao.getTotalsFlow().map { it ?: createInitialTotals() }
    }

    private suspend fun createInitialTotals(): LogbookTotals {
        val totals = LogbookTotals()
        logbookDao.insertTotals(totals)
        return totals
    }

    private suspend fun updateTotals(entry: LogbookEntry) {
        val current = getTotals()

        // Calculate unique airports
        val allEntries = getAllEntries()
        val uniqueAirports = allEntries.flatMap {
            listOf(it.departureAirport, it.arrivalAirport)
        }.distinct().size

        // Calculate unique routes
        val uniqueRoutes = allEntries.map {
            "${it.departureAirport}-${it.arrivalAirport}"
        }.distinct().size

        // Update difficulty counts
        val difficultyCounts = allEntries.filter { it.isCompleted }.groupingBy { it.difficulty }.eachCount()

        // Calculate new averages
        val completedEntries = allEntries.filter { it.isCompleted }
        val totalCompleted = completedEntries.size

        val updated = current.copy(
            totalTime = current.totalTime + entry.totalTime,
            simulatedInstrumentTime = current.simulatedInstrumentTime + entry.simulatedInstrumentTime,
            nightTime = current.nightTime + entry.nightTime,
            totalSessions = current.totalSessions + 1,
            completedSessions = if (entry.isCompleted) current.completedSessions + 1 else current.completedSessions,
            abortedSessions = if (entry.wasAborted) current.abortedSessions + 1 else current.abortedSessions,
            totalDistance = current.totalDistance + entry.distance,
            uniqueAirportsVisited = uniqueAirports,
            uniqueRoutes = uniqueRoutes,
            averageOverallScore = if (totalCompleted > 0) completedEntries.map { it.overallScore }.average().toFloat() else 0f,
            averageCommunicationScore = if (totalCompleted > 0) completedEntries.map { it.communicationScore }.average().toFloat() else 0f,
            averageNavigationScore = if (totalCompleted > 0) completedEntries.map { it.navigationScore }.average().toFloat() else 0f,
            averageWeatherScore = if (totalCompleted > 0) completedEntries.map { it.weatherDecisionScore }.average().toFloat() else 0f,
            averageEmergencyScore = if (totalCompleted > 0) completedEntries.filter { it.emergencyHandlingScore > 0 }.map { it.emergencyHandlingScore }.average().toFloat() else 0f,
            averageTrafficScore = if (totalCompleted > 0) completedEntries.map { it.trafficManagementScore }.average().toFloat() else 0f,
            perfectScores = if (entry.overallScore == 100f) current.perfectScores + 1 else current.perfectScores,
            emergenciesHandled = current.emergenciesHandled + entry.emergenciesDeclared,
            goAroundsExecuted = current.goAroundsExecuted + entry.goArounds,
            weatherDiversions = current.weatherDiversions + entry.weatherDiversions,
            totalChallengesCompleted = current.totalChallengesCompleted + entry.getChallengesList().size,
            uniqueChallengesCompleted = allEntries.flatMap { it.getChallengesList() }.distinct().size,
            beginnerMissions = difficultyCounts[Difficulty.BEGINNER] ?: 0,
            intermediateMissions = difficultyCounts[Difficulty.INTERMEDIATE] ?: 0,
            advancedMissions = difficultyCounts[Difficulty.ADVANCED] ?: 0,
            expertMissions = difficultyCounts[Difficulty.EXPERT] ?: 0,
            lastSessionDate = entry.date,
            longestSessionMinutes = maxOf(current.longestSessionMinutes, entry.totalTime),
            currentStreak = calculateCurrentStreak(allEntries + entry),
            lastUpdated = System.currentTimeMillis()
        )

        logbookDao.updateTotals(updated)
    }

    private fun calculateCurrentStreak(entries: List<LogbookEntry>): Int {
        if (entries.isEmpty()) return 0

        val sortedDates = entries
            .map { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(it.date)) }
            .distinct()
            .sortedDescending()

        if (sortedDates.isEmpty()) return 0

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        if (sortedDates.first() != today) return 0 // Streak broken

        var streak = 1
        val calendar = Calendar.getInstance()

        for (i in 0 until sortedDates.size - 1) {
            calendar.time = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(sortedDates[i])!!
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val expectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)

            if (sortedDates[i + 1] == expectedDate) {
                streak++
            } else {
                break
            }
        }

        return streak
    }

    // ========================================
    // Analytics & Statistics
    // ========================================

    suspend fun getSessionSummary(entryId: Long): LogbookSessionSummary? {
        val entry = getEntry(entryId) ?: return null
        val skillScores = ProficiencyCalculator.calculateSkillScores(entry)

        val skillBreakdowns = skillScores.map { (category, score) ->
            val proficiency = getProficiencyRating(category)
            SessionSkillBreakdown(
                skillCategory = category,
                score = score,
                performanceLevel = ProficiencyLevel.fromScore(score),
                previousScore = proficiency?.previousScore,
                improvement = proficiency?.previousScore?.let { score - it },
                feedback = ProficiencyCalculator.generateSkillFeedback(
                    category,
                    score,
                    proficiency?.previousScore,
                    entry
                )
            )
        }

        val totals = getTotals()
        val comparisonToAverage = mapOf(
            "Overall" to (entry.overallScore - totals.averageOverallScore),
            "Communication" to (entry.communicationScore - totals.averageCommunicationScore),
            "Navigation" to (entry.navigationScore - totals.averageNavigationScore),
            "Weather" to (entry.weatherDecisionScore - totals.averageWeatherScore),
            "Traffic" to (entry.trafficManagementScore - totals.averageTrafficScore)
        )

        val highlightedAchievements = buildList {
            if (entry.overallScore == 100f) add("Perfect Score! 🏆")
            if (entry.goArounds > 0) add("Successfully executed ${entry.goArounds} go-around(s)")
            if (entry.emergenciesDeclared > 0) add("Handled emergency situation professionally")
            if (entry.frequencyChanges >= 5) add("Managed ${entry.frequencyChanges} frequency changes")
            if (entry.getChallengesList().isNotEmpty()) add("Completed ${entry.getChallengesList().size} challenge(s)")
        }

        val areasNeedingWork = skillBreakdowns
            .filter { it.score < 70f }
            .map { "${it.skillCategory.displayName} (${it.score.toInt()}%)" }

        return LogbookSessionSummary(
            entry = entry,
            skillBreakdowns = skillBreakdowns,
            highlightedAchievements = highlightedAchievements,
            areasNeedingWork = areasNeedingWork,
            comparisonToAverage = comparisonToAverage
        )
    }

    suspend fun getPeriodSummary(startDate: Long, endDate: Long): LogbookPeriodSummary {
        val entries = getEntriesByDateRange(startDate, endDate).filter { it.isCompleted }

        val totalSessions = entries.size
        val totalTime = entries.sumOf { it.totalTime }
        val averageScore = if (entries.isNotEmpty()) {
            entries.map { it.overallScore }.average().toFloat()
        } else 0f

        // Calculate improvement rate
        val firstHalf = entries.take(entries.size / 2)
        val secondHalf = entries.drop(entries.size / 2)
        val improvementRate = if (firstHalf.isNotEmpty() && secondHalf.isNotEmpty()) {
            val firstAvg = firstHalf.map { it.overallScore }.average()
            val secondAvg = secondHalf.map { it.overallScore }.average()
            ((secondAvg - firstAvg) / firstAvg * 100).toFloat()
        } else 0f

        // Top skills
        val allRatings = getAllProficiencyRatings()
        val topSkills = allRatings
            .sortedByDescending { it.currentScore }
            .take(3)
            .map { it.skillCategory to it.currentScore }

        // Skills needing work
        val needsWork = allRatings
            .filter { it.needsImprovement }
            .sortedBy { it.currentScore }
            .take(3)
            .map { it.skillCategory to it.currentScore }

        // Challenges completed
        val challengesCompleted = entries.flatMap { it.getChallengesList() }.size

        // Difficulties attempted
        val difficultiesAttempted = entries.groupingBy { it.difficulty }.eachCount()

        return LogbookPeriodSummary(
            startDate = startDate,
            endDate = endDate,
            totalSessions = totalSessions,
            totalTime = totalTime,
            averageScore = averageScore,
            improvementRate = improvementRate,
            topSkills = topSkills,
            needsWork = needsWork,
            challengesCompleted = challengesCompleted,
            difficultiesAttempted = difficultiesAttempted
        )
    }

    suspend fun getPilotRank(): PilotRank {
        val proficiencies = getAllProficiencyRatings()
        val totals = getTotals()
        return ProficiencyCalculator.calculatePilotRank(proficiencies, totals.totalSessions)
    }

    suspend fun getBestSession(): LogbookEntry? {
        return logbookDao.getBestSessionAllTime()
    }

    suspend fun getBestSessionInPeriod(startDate: Long, endDate: Long): LogbookEntry? {
        return logbookDao.getBestSessionInPeriod(startDate, endDate)
    }

    // ========================================
    // Export Functionality
    // ========================================

    /**
     * Generate export data for a date range
     */
    suspend fun generateExportData(startDate: Long, endDate: Long): LogbookExportData {
        val entries = getEntriesForExport(startDate, endDate)
        val proficiencies = getAllProficiencyRatings()
        val totals = getTotals()

        // Generate period summaries (monthly)
        val periodSummaries = generateMonthlySummaries(entries)

        val userInfo = UserExportInfo(
            userId = "default_user",
            exportDate = System.currentTimeMillis(),
            appVersion = "1.0.0",
            totalFlightHours = totals.totalTime.toFlightHours(),
            currentRank = getPilotRank().displayName,
            joinDate = entries.minByOrNull { it.date }?.date ?: System.currentTimeMillis()
        )

        return LogbookExportData(
            totals = totals,
            entries = entries,
            proficiencyRatings = proficiencies,
            periodSummaries = periodSummaries,
            userInfo = userInfo
        )
    }

    suspend fun getEntriesForExport(startDate: Long, endDate: Long): List<LogbookEntry> {
        return logbookDao.getEntriesForExport(startDate, endDate)
    }

    suspend fun getCertifiedEntriesForExport(): List<LogbookEntry> {
        return logbookDao.getCertifiedEntriesForExport()
    }

    private suspend fun generateMonthlySummaries(entries: List<LogbookEntry>): List<LogbookPeriodSummary> {
        val grouped = entries.groupBy { entry ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = entry.date
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}"
        }

        return grouped.map { (_, monthEntries) ->
            val firstDate = monthEntries.minOf { it.date }
            val lastDate = monthEntries.maxOf { it.date }
            getPeriodSummary(firstDate, lastDate)
        }
    }

    /**
     * Generate CSV export string
     */
    suspend fun exportToCSV(startDate: Long, endDate: Long): String {
        val entries = getEntriesForExport(startDate, endDate)

        val csv = StringBuilder()
        // Header
        csv.appendLine("Date,Departure,Arrival,Route,Distance (NM),Duration (min),Difficulty,Score,Communication,Navigation,Weather,Traffic,Emergency,Notes")

        // Data rows
        entries.forEach { entry ->
            csv.appendLine(
                "${entry.getFormattedDate()}," +
                        "${entry.departureAirport}," +
                        "${entry.arrivalAirport}," +
                        "${entry.route}," +
                        "${entry.distance}," +
                        "${entry.totalTime}," +
                        "${entry.difficulty.name}," +
                        "${entry.overallScore}," +
                        "${entry.communicationScore}," +
                        "${entry.navigationScore}," +
                        "${entry.weatherDecisionScore}," +
                        "${entry.trafficManagementScore}," +
                        "${entry.emergencyHandlingScore}," +
                        "\"${entry.userNotes.replace("\"", "\"\"")}\""
            )
        }

        return csv.toString()
    }

    // ========================================
    // CFI Certification
    // ========================================

    /**
     * Mark an entry as certified by a CFI
     */
    suspend fun certifyEntry(entryId: Long, cfiName: String) {
        val entry = getEntry(entryId) ?: return
        val certified = entry.copy(
            isCertified = true,
            certifiedBy = cfiName,
            certificationDate = System.currentTimeMillis()
        )
        updateEntry(certified)
    }

    suspend fun getCertifiedEntries(): List<LogbookEntry> {
        return logbookDao.getCertifiedEntries()
    }
}
