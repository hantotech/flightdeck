package com.example.flightdeck.ui.weather

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flightdeck.data.local.FlightDeckDatabase
import com.example.flightdeck.data.model.ATISBroadcast
import com.example.flightdeck.data.model.Airport
import com.example.flightdeck.data.repository.ATISRepository
import com.example.flightdeck.data.repository.AirportRepository
import com.example.flightdeck.data.remote.weather.AviationWeatherService
import kotlinx.coroutines.launch

/**
 * ViewModel for ATIS viewer screen
 */
class ATISViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FlightDeckDatabase.getDatabase(application)
    private val airportRepo = AirportRepository.getInstance(db.airportDao())
    private val atisRepo = ATISRepository.getInstance(
        db.atisDao(),
        airportRepo,
        AviationWeatherService.getInstance()
    )

    // UI State
    private val _uiState = MutableLiveData<ATISUiState>(ATISUiState.Initial)
    val uiState: LiveData<ATISUiState> = _uiState

    // Current ATIS
    private val _currentATIS = MutableLiveData<ATISBroadcast?>()
    val currentATIS: LiveData<ATISBroadcast?> = _currentATIS

    // Current Airport
    private val _currentAirport = MutableLiveData<Airport?>()
    val currentAirport: LiveData<Airport?> = _currentAirport

    // ATIS Readback text
    private val _atisReadback = MutableLiveData<String?>()
    val atisReadback: LiveData<String?> = _atisReadback

    /**
     * Get ATIS for airport by ICAO code
     */
    fun getATIS(icao: String) {
        if (icao.isBlank()) {
            _uiState.value = ATISUiState.Error("Please enter an airport code")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = ATISUiState.Loading

                // Get airport info
                val airportResult = airportRepo.getAirportByIcao(icao.uppercase())
                if (airportResult.isFailure) {
                    _uiState.value = ATISUiState.Error("Airport $icao not found")
                    return@launch
                }

                val airportData = airportResult.getOrThrow()
                _currentAirport.value = airportData.airport

                // Get ATIS
                val atisResult = atisRepo.getCurrentATIS(icao.uppercase())
                if (atisResult.isFailure) {
                    _uiState.value = ATISUiState.Error("Unable to fetch ATIS: ${atisResult.exceptionOrNull()?.message}")
                    return@launch
                }

                val atis = atisResult.getOrThrow()
                _currentATIS.value = atis

                // Get readback text
                val readbackResult = atisRepo.getATISReadback(icao.uppercase())
                if (readbackResult.isSuccess) {
                    _atisReadback.value = readbackResult.getOrNull()
                }

                _uiState.value = ATISUiState.Success
            } catch (e: Exception) {
                _uiState.value = ATISUiState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Refresh ATIS (force new broadcast)
     */
    fun refreshATIS() {
        val icao = _currentAirport.value?.icao ?: return
        viewModelScope.launch {
            try {
                _uiState.value = ATISUiState.Loading
                atisRepo.refreshATIS(icao)
                getATIS(icao)
            } catch (e: Exception) {
                _uiState.value = ATISUiState.Error("Refresh failed: ${e.message}")
            }
        }
    }

    /**
     * Clear current ATIS
     */
    fun clearATIS() {
        _currentATIS.value = null
        _currentAirport.value = null
        _atisReadback.value = null
        _uiState.value = ATISUiState.Initial
    }
}

/**
 * UI State for ATIS screen
 */
sealed class ATISUiState {
    object Initial : ATISUiState()
    object Loading : ATISUiState()
    object Success : ATISUiState()
    data class Error(val message: String) : ATISUiState()
}
