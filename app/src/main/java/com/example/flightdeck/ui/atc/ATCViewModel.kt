package com.example.flightdeck.ui.atc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightdeck.data.model.ATCScenario
import com.example.flightdeck.data.model.FrequencyType
import com.example.flightdeck.data.model.TrafficPosition
import com.example.flightdeck.data.repository.ATCRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel for ATC Simulator
 * Manages scenarios, chat messages, and AI interactions
 */
class ATCViewModel(private val atcRepo: ATCRepository) : ViewModel() {

    // Current scenario
    private val _currentScenario = MutableLiveData<ATCScenario?>()
    val currentScenario: LiveData<ATCScenario?> = _currentScenario

    // Chat messages
    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    // UI state
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Session tracking
    private var currentSessionId: Long? = null

    init {
        loadDefaultScenario()
    }

    /**
     * Load the first available scenario as default
     */
    private fun loadDefaultScenario() {
        viewModelScope.launch {
            try {
                val scenarios = atcRepo.getAllScenarios().first()
                if (scenarios.isNotEmpty()) {
                    selectScenario(scenarios[0])
                }
            } catch (e: Exception) {
                _error.value = "Failed to load scenarios: ${e.message}"
            }
        }
    }

    /**
     * Select and start a new scenario
     */
    fun selectScenario(scenario: ATCScenario) {
        viewModelScope.launch {
            try {
                // End current session if exists
                currentSessionId?.let { sessionId ->
                    atcRepo.endPracticeSession(sessionId, 0f, 0f)
                }

                // Start new session
                currentSessionId = atcRepo.startPracticeSession(scenario.id)
                _currentScenario.value = scenario

                // Clear messages and add welcome message
                _messages.value = listOf(
                    ChatMessage(
                        text = "Scenario started: ${scenario.title}",
                        type = MessageType.SYSTEM
                    ),
                    ChatMessage(
                        text = scenario.situation,
                        type = MessageType.SYSTEM
                    ),
                    ChatMessage(
                        text = "Begin your radio call to ATC...",
                        type = MessageType.SYSTEM
                    )
                )

                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to start scenario: ${e.message}"
            }
        }
    }

    /**
     * Start a custom practice session with departure and arrival airports
     * Creates a dynamic scenario based on the flight route
     */
    fun startCustomSession(departureIcao: String, arrivalIcao: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // End current session if exists
                currentSessionId?.let { sessionId ->
                    atcRepo.endPracticeSession(sessionId, 0f, 0f)
                }

                // Get airport information
                val departureResult = atcRepo.getAirportInfo(departureIcao)
                val arrivalResult = atcRepo.getAirportInfo(arrivalIcao)

                // Create a custom scenario for this flight
                val departureAirport = departureResult.getOrNull()?.airport
                val arrivalAirport = arrivalResult.getOrNull()?.airport

                val scenarioTitle = "$departureIcao → $arrivalIcao Practice"
                val scenarioDescription = buildString {
                    append("VFR flight from ")
                    append(departureAirport?.name ?: departureIcao)
                    append(" to ")
                    append(arrivalAirport?.name ?: arrivalIcao)
                    if (departureAirport != null) {
                        append(". Field elevation: ${departureAirport.elevation}ft")
                    }
                }

                // Create temporary scenario (we'll use the first available scenario ID as placeholder)
                val scenarios = atcRepo.getAllScenarios().first()
                val baseScenario = scenarios.firstOrNull() ?: run {
                    _error.value = "No scenarios available. Please initialize sample scenarios."
                    _isLoading.value = false
                    return@launch
                }

                // Start session with base scenario
                currentSessionId = atcRepo.startPracticeSession(baseScenario.id)

                // Override scenario details for custom flight
                _currentScenario.value = baseScenario.copy(
                    title = scenarioTitle,
                    airport = departureIcao,
                    situation = scenarioDescription
                )

                // Get frequency information for departure airport
                val frequencies = atcRepo.getFrequencies(departureIcao)
                val groundFreq = frequencies.firstOrNull { it.type == FrequencyType.GROUND }
                val towerFreq = frequencies.firstOrNull { it.type == FrequencyType.TOWER }

                // Build initial ATC greeting
                val initialGreeting = buildString {
                    append("Welcome to ")
                    append(departureAirport?.name ?: departureIcao)
                    append(". ")
                    if (groundFreq != null) {
                        append("Ground frequency ${groundFreq.frequency}. ")
                    }
                    if (towerFreq != null) {
                        append("Tower ${towerFreq.frequency}. ")
                    }
                    append("Ready for your radio call.")
                }

                // Set up initial messages
                _messages.value = listOf(
                    ChatMessage(
                        text = "Practice Session: $scenarioTitle",
                        type = MessageType.SYSTEM
                    ),
                    ChatMessage(
                        text = scenarioDescription,
                        type = MessageType.SYSTEM
                    ),
                    ChatMessage(
                        text = initialGreeting,
                        type = MessageType.ATC
                    ),
                    ChatMessage(
                        text = "Press and hold the PTT button to begin your radio call...",
                        type = MessageType.SYSTEM
                    )
                )

                _error.value = null
                _isLoading.value = false

            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Failed to start session: ${e.message}"
            }
        }
    }

    /**
     * Send a pilot message and get ATC response
     */
    fun sendPilotMessage(message: String) {
        if (message.isBlank()) return

        val scenario = _currentScenario.value
        val sessionId = currentSessionId

        if (scenario == null || sessionId == null) {
            _error.value = "No active scenario"
            return
        }

        viewModelScope.launch {
            try {
                // Add pilot message to chat
                val pilotMsg = ChatMessage(text = message, type = MessageType.PILOT)
                _messages.value = _messages.value.orEmpty() + pilotMsg

                // Show loading
                _isLoading.value = true

                // Get AI response
                val result = atcRepo.sendPilotMessage(
                    sessionId = sessionId,
                    scenario = scenario,
                    pilotMessage = message,
                    userPosition = null // TODO: Add position tracking later
                )

                _isLoading.value = false

                result.fold(
                    onSuccess = { atcResponse ->
                        // Add ATC response to chat
                        val atcMsg = ChatMessage(text = atcResponse, type = MessageType.ATC)
                        _messages.value = _messages.value.orEmpty() + atcMsg
                    },
                    onFailure = { exception ->
                        _error.value = "ATC response error: ${exception.message}"
                        val errorMsg = ChatMessage(
                            text = "Unable to contact ATC. Please try again.",
                            type = MessageType.SYSTEM
                        )
                        _messages.value = _messages.value.orEmpty() + errorMsg
                    }
                )
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Error sending message: ${e.message}"
            }
        }
    }

    /**
     * Get all available scenarios
     */
    suspend fun getAllScenarios(): List<ATCScenario> {
        return atcRepo.getAllScenarios().first()
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * End current session when fragment is destroyed
     */
    override fun onCleared() {
        super.onCleared()
        currentSessionId?.let { sessionId ->
            viewModelScope.launch {
                atcRepo.endPracticeSession(sessionId, 0f, 0f)
            }
        }
    }
}
