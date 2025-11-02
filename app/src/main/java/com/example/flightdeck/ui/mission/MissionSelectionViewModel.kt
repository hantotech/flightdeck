package com.example.flightdeck.ui.mission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightdeck.data.model.Difficulty
import com.example.flightdeck.data.model.MissionConfig
import com.example.flightdeck.data.repository.MissionConfigRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for Mission Selection screen
 * Handles loading and filtering preset and user-created missions
 */
class MissionSelectionViewModel(
    private val missionRepository: MissionConfigRepository
) : ViewModel() {

    private val _presetMissions = MutableLiveData<List<MissionConfig>>()
    val presetMissions: LiveData<List<MissionConfig>> = _presetMissions

    private val _recentlyFlownMissions = MutableLiveData<List<MissionConfig>>()
    val recentlyFlownMissions: LiveData<List<MissionConfig>> = _recentlyFlownMissions

    private val _selectedDifficulty = MutableLiveData<Difficulty?>(null)
    val selectedDifficulty: LiveData<Difficulty?> = _selectedDifficulty

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // All presets (unfiltered)
    private var allPresets: List<MissionConfig> = emptyList()

    init {
        loadMissions()
    }

    /**
     * Load all missions (presets and recently flown)
     */
    fun loadMissions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load presets
                allPresets = missionRepository.getAllPresets()
                _presetMissions.value = allPresets

                // Load recently flown
                val recentlyFlown = missionRepository.getRecentlyFlown(3)
                _recentlyFlownMissions.value = recentlyFlown

            } catch (e: Exception) {
                _error.value = "Failed to load missions: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Filter presets by difficulty
     */
    fun filterByDifficulty(difficulty: Difficulty?) {
        _selectedDifficulty.value = difficulty

        _presetMissions.value = if (difficulty == null) {
            allPresets
        } else {
            allPresets.filter { it.baseDifficulty == difficulty }
        }
    }

    /**
     * Select a mission to start
     */
    fun selectMission(mission: MissionConfig): MissionConfig {
        // Return the selected mission for navigation
        return mission
    }

    fun clearError() {
        _error.value = null
    }
}
