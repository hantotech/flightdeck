package com.example.flightdeck.ui.logbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightdeck.data.model.*
import com.example.flightdeck.data.repository.LogbookRepository
import com.example.flightdeck.utils.PilotRank
import kotlinx.coroutines.launch

/**
 * ViewModel for Logbook Overview screen
 * Displays user's logbook summary, proficiency ratings, and recent sessions
 */
class LogbookOverviewViewModel(
    private val logbookRepository: LogbookRepository
) : ViewModel() {

    // Logbook totals
    private val _totals = MutableLiveData<LogbookTotals>()
    val totals: LiveData<LogbookTotals> = _totals

    // Pilot rank
    private val _pilotRank = MutableLiveData<PilotRank>()
    val pilotRank: LiveData<PilotRank> = _pilotRank

    // Recent logbook entries
    private val _recentEntries = MutableLiveData<List<LogbookEntry>>()
    val recentEntries: LiveData<List<LogbookEntry>> = _recentEntries

    // Proficiency ratings
    private val _proficiencyRatings = MutableLiveData<List<ProficiencyRating>>()
    val proficiencyRatings: LiveData<List<ProficiencyRating>> = _proficiencyRatings

    // Top proficiency ratings (for display)
    private val _topProficiencies = MutableLiveData<List<ProficiencyRating>>()
    val topProficiencies: LiveData<List<ProficiencyRating>> = _topProficiencies

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadLogbookData()
    }

    /**
     * Load all logbook data
     */
    fun loadLogbookData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load totals
                val totals = logbookRepository.getTotals()
                _totals.value = totals

                // Load pilot rank
                val rank = logbookRepository.getPilotRank()
                _pilotRank.value = rank

                // Load recent entries
                val entries = logbookRepository.getRecentEntries(5)
                _recentEntries.value = entries

                // Load proficiency ratings
                val ratings = logbookRepository.getAllProficiencyRatings()
                _proficiencyRatings.value = ratings
                _topProficiencies.value = ratings.sortedByDescending { it.currentScore }.take(5)

                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load logbook: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Get formatted total flight time
     */
    fun getFormattedFlightTime(): String {
        val totalMinutes = _totals.value?.totalTime ?: 0
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return String.format("%d:%02d hrs", hours, minutes)
    }

    /**
     * Get average proficiency score
     */
    fun getAverageProficiencyScore(): Int {
        val ratings = _proficiencyRatings.value ?: return 0
        if (ratings.isEmpty()) return 0
        return ratings.map { it.currentScore }.average().toInt()
    }

    /**
     * Get star rating based on average proficiency
     */
    fun getStarRating(): Int {
        val avgScore = getAverageProficiencyScore()
        return when {
            avgScore >= 95 -> 5
            avgScore >= 85 -> 4
            avgScore >= 70 -> 3
            avgScore >= 60 -> 2
            avgScore >= 40 -> 1
            else -> 0
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Refresh all data
     */
    fun refresh() {
        loadLogbookData()
    }
}
