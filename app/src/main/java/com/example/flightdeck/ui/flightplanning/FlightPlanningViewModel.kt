package com.example.flightdeck.ui.flightplanning

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flightdeck.data.local.FlightDeckDatabase
import com.example.flightdeck.data.local.dao.CompleteAirportData
import com.example.flightdeck.data.model.Aircraft
import com.example.flightdeck.data.model.Airport
import com.example.flightdeck.data.model.FlightPlan
import com.example.flightdeck.data.model.WeatherReport
import com.example.flightdeck.data.repository.AirportRepository
import com.example.flightdeck.data.repository.FlightMetrics
import com.example.flightdeck.data.repository.FlightPlanRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.*

/**
 * ViewModel for flight planning screen
 * Handles departure/arrival selection, aircraft selection, weather, and AI advice
 */
class FlightPlanningViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FlightDeckDatabase.getDatabase(application)
    private val airportRepo = AirportRepository.getInstance(db.airportDao())
    private val flightPlanRepo = FlightPlanRepository(db.flightPlanDao())

    // Loading states
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Departure airport
    private val _departureAirport = MutableLiveData<CompleteAirportData?>()
    val departureAirport: LiveData<CompleteAirportData?> = _departureAirport

    private val _departureMetar = MutableLiveData<WeatherReport?>()
    val departureMetar: LiveData<WeatherReport?> = _departureMetar

    // Arrival airport
    private val _arrivalAirport = MutableLiveData<CompleteAirportData?>()
    val arrivalAirport: LiveData<CompleteAirportData?> = _arrivalAirport

    private val _arrivalMetar = MutableLiveData<WeatherReport?>()
    val arrivalMetar: LiveData<WeatherReport?> = _arrivalMetar

    // Aircraft selection
    private val _availableAircraft = MutableLiveData<List<Aircraft>>(emptyList())
    val availableAircraft: LiveData<List<Aircraft>> = _availableAircraft

    private val _selectedAircraft = MutableLiveData<Aircraft?>()
    val selectedAircraft: LiveData<Aircraft?> = _selectedAircraft

    // Flight metrics (auto-calculated)
    private val _flightMetrics = MutableLiveData<FlightMetrics?>()
    val flightMetrics: LiveData<FlightMetrics?> = _flightMetrics

    // AI advice
    private val _aiAdvice = MutableLiveData<String?>()
    val aiAdvice: LiveData<String?> = _aiAdvice

    // Premium status (for waypoints)
    private val _isPremiumUser = MutableLiveData<Boolean>(false)
    val isPremiumUser: LiveData<Boolean> = _isPremiumUser

    // Error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadAircraft()
        checkPremiumStatus()
    }

    /**
     * Load available aircraft
     */
    private fun loadAircraft() {
        viewModelScope.launch {
            try {
                val aircraft = db.aircraftDao().getAllAircraft().first()
                _availableAircraft.value = aircraft

                // Auto-select first aircraft if available
                if (aircraft.isNotEmpty()) {
                    _selectedAircraft.value = aircraft[0]
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load aircraft: ${e.message}"
            }
        }
    }

    /**
     * Check premium status
     * TODO: Implement actual premium status check
     */
    private fun checkPremiumStatus() {
        // For now, hardcoded to false (free tier)
        // TODO: Integrate with actual in-app purchase system
        _isPremiumUser.value = false
    }

    /**
     * Set departure airport by ICAO code
     */
    fun setDepartureAirport(icao: String) {
        if (icao.length != 4) return

        viewModelScope.launch {
            try {
                _isLoading.value = true

                val result = airportRepo.getAirportByIcao(icao.uppercase())
                if (result.isSuccess) {
                    _departureAirport.value = result.getOrNull()
                    loadDepartureWeather(icao.uppercase())
                    calculateMetrics()
                    loadAIAdvice()
                } else {
                    _errorMessage.value = "Airport $icao not found"
                    _departureAirport.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading airport: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Set arrival airport by ICAO code
     */
    fun setArrivalAirport(icao: String) {
        if (icao.length != 4) return

        viewModelScope.launch {
            try {
                _isLoading.value = true

                val result = airportRepo.getAirportByIcao(icao.uppercase())
                if (result.isSuccess) {
                    _arrivalAirport.value = result.getOrNull()
                    loadArrivalWeather(icao.uppercase())
                    calculateMetrics()
                    loadAIAdvice()
                } else {
                    _errorMessage.value = "Airport $icao not found"
                    _arrivalAirport.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading airport: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load departure weather (METAR)
     */
    private fun loadDepartureWeather(icao: String) {
        viewModelScope.launch {
            try {
                val result = flightPlanRepo.getWeatherBriefing(icao)
                if (result.isSuccess) {
                    _departureMetar.value = result.getOrNull()?.currentWeather
                }
            } catch (e: Exception) {
                // Weather is optional, don't show error
            }
        }
    }

    /**
     * Load arrival weather (METAR)
     */
    private fun loadArrivalWeather(icao: String) {
        viewModelScope.launch {
            try {
                val result = flightPlanRepo.getWeatherBriefing(icao)
                if (result.isSuccess) {
                    _arrivalMetar.value = result.getOrNull()?.currentWeather
                }
            } catch (e: Exception) {
                // Weather is optional, don't show error
            }
        }
    }

    /**
     * Calculate flight metrics (distance, time, fuel)
     */
    private fun calculateMetrics() {
        val departure = _departureAirport.value?.airport ?: return
        val arrival = _arrivalAirport.value?.airport ?: return
        val aircraft = _selectedAircraft.value ?: return

        viewModelScope.launch {
            try {
                // Calculate distance using Haversine formula
                val distance = calculateDistance(
                    departure.latitude, departure.longitude,
                    arrival.latitude, arrival.longitude
                )

                // Calculate metrics
                val metrics = flightPlanRepo.calculateFlightMetrics(
                    distance = distance,
                    cruiseSpeed = aircraft.cruiseSpeed,
                    fuelBurnRate = aircraft.fuelBurnRate
                )

                _flightMetrics.value = metrics
            } catch (e: Exception) {
                _errorMessage.value = "Error calculating metrics: ${e.message}"
            }
        }
    }

    /**
     * Load AI flight advice
     */
    private fun loadAIAdvice() {
        val departure = _departureAirport.value?.airport ?: return
        val arrival = _arrivalAirport.value?.airport ?: return
        val aircraft = _selectedAircraft.value ?: return

        viewModelScope.launch {
            try {
                val result = flightPlanRepo.getFlightPlanningAdvice(
                    departure = departure.icao,
                    arrival = arrival.icao,
                    aircraft = "${aircraft.make} ${aircraft.model}"
                )

                if (result.isSuccess) {
                    _aiAdvice.value = result.getOrNull()
                }
            } catch (e: Exception) {
                // AI advice is optional, don't show error
            }
        }
    }

    /**
     * Set selected aircraft
     */
    fun setSelectedAircraft(aircraft: Aircraft) {
        _selectedAircraft.value = aircraft
        calculateMetrics()
    }

    /**
     * Search airports by query
     */
    suspend fun searchAirports(query: String): List<Airport> {
        if (query.length < 2) return emptyList()

        return try {
            airportRepo.searchAirports(query.uppercase(), limit = 10)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Save flight plan as favorite
     */
    fun saveAsFavorite(customName: String?) {
        val departure = _departureAirport.value?.airport ?: return
        val arrival = _arrivalAirport.value?.airport ?: return
        val aircraft = _selectedAircraft.value ?: return
        val metrics = _flightMetrics.value ?: return

        viewModelScope.launch {
            try {
                val flightPlan = FlightPlan(
                    aircraftId = aircraft.id,
                    departureAirport = departure.icao,
                    arrivalAirport = arrival.icao,
                    route = "Direct", // For Phase 1, always direct
                    altitude = 3500, // Default VFR cruising altitude
                    cruiseSpeed = aircraft.cruiseSpeed,
                    estimatedFlightTime = metrics.estimatedFlightTime,
                    fuelRequired = metrics.fuelRequired,
                    isFavorite = true,
                    customName = customName
                )

                flightPlanRepo.createFlightPlan(flightPlan)
                _errorMessage.value = "Saved to favorites!"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save: ${e.message}"
            }
        }
    }

    /**
     * Create flight plan and navigate to ATC practice
     */
    fun createFlightPlanAndStartATC(): Long? {
        val departure = _departureAirport.value?.airport ?: return null
        val arrival = _arrivalAirport.value?.airport ?: return null
        val aircraft = _selectedAircraft.value ?: return null
        val metrics = _flightMetrics.value ?: return null

        var flightPlanId: Long? = null

        viewModelScope.launch {
            try {
                val flightPlan = FlightPlan(
                    aircraftId = aircraft.id,
                    departureAirport = departure.icao,
                    arrivalAirport = arrival.icao,
                    route = "Direct",
                    altitude = 3500, // Default VFR cruising altitude
                    cruiseSpeed = aircraft.cruiseSpeed,
                    estimatedFlightTime = metrics.estimatedFlightTime,
                    fuelRequired = metrics.fuelRequired,
                    isActive = true
                )

                flightPlanId = flightPlanRepo.createFlightPlan(flightPlan)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create flight plan: ${e.message}"
            }
        }

        return flightPlanId
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     * Returns distance in nautical miles
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 3440.065 // Earth radius in nautical miles
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
