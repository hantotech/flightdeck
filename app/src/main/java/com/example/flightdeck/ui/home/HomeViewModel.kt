package com.example.flightdeck.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flightdeck.data.local.FlightDeckDatabase
import com.example.flightdeck.data.repository.AirportRepository
import com.example.flightdeck.data.repository.ATCRepository
import com.example.flightdeck.data.repository.TrafficSimulator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for Home/Dashboard screen
 * Shows app status and provides quick navigation to features
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FlightDeckDatabase.getDatabase(application)
    private val airportRepo = AirportRepository.getInstance(db.airportDao())
    private val trafficSimulator = TrafficSimulator.getInstance(db.trafficDao(), airportRepo)
    private val atcRepo = ATCRepository(db.atcDao(), airportRepo, trafficSimulator)

    // App Status
    private val _databaseStatus = MutableLiveData<DatabaseStatus>(DatabaseStatus.LOADING)
    val databaseStatus: LiveData<DatabaseStatus> = _databaseStatus

    // Counts
    private val _airportCount = MutableLiveData<Int>(0)
    val airportCount: LiveData<Int> = _airportCount

    private val _scenarioCount = MutableLiveData<Int>(0)
    val scenarioCount: LiveData<Int> = _scenarioCount

    init {
        loadDatabaseStats()
    }

    /**
     * Load database statistics
     */
    private fun loadDatabaseStats() {
        viewModelScope.launch {
            try {
                _databaseStatus.value = DatabaseStatus.LOADING

                // Get database stats
                val stats = airportRepo.getDatabaseStats()
                _airportCount.value = stats.totalAirports

                // Get scenario count - get first value from Flow
                val scenarios = atcRepo.getAllScenarios().first()
                _scenarioCount.value = scenarios.size

                _databaseStatus.value = DatabaseStatus.READY
            } catch (e: Exception) {
                _databaseStatus.value = DatabaseStatus.ERROR
            }
        }
    }

    /**
     * Refresh database stats
     */
    fun refresh() {
        loadDatabaseStats()
    }
}

/**
 * Database status for UI
 */
enum class DatabaseStatus {
    LOADING,
    READY,
    ERROR
}
