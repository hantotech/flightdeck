# FlightDeck - API Usage Examples

## Table of Contents
1. [ATC Communication](#atc-communication)
2. [Checklist Management](#checklist-management)
3. [Flight Planning](#flight-planning)
4. [Performance Tracking](#performance-tracking)

---

## ATC Communication

### Example 1: Basic Ground Clearance Practice

```kotlin
class ATCSimulatorViewModel(
    private val atcRepository: ATCRepository
) : ViewModel() {

    private val _atcResponse = MutableLiveData<String>()
    val atcResponse: LiveData<String> = _atcResponse

    private val _evaluation = MutableLiveData<ATCEvaluation>()
    val evaluation: LiveData<ATCEvaluation> = _evaluation

    private var currentSessionId: Long = 0

    fun startGroundClearanceScenario() {
        viewModelScope.launch {
            // Get the scenario
            val scenario = atcRepository.getScenarioById(1) ?: return@launch

            // Start practice session
            currentSessionId = atcRepository.startPracticeSession(scenario.id)

            // Initial ATC prompt
            _atcResponse.value = "Palo Alto Ground listening on 121.7, ready for your call."
        }
    }

    fun sendPilotMessage(message: String) {
        viewModelScope.launch {
            val scenario = atcRepository.getScenarioById(1) ?: return@launch

            // Get AI ATC response
            val result = atcRepository.sendPilotMessage(
                sessionId = currentSessionId,
                scenario = scenario,
                pilotMessage = message
            )

            result.onSuccess { response ->
                _atcResponse.value = response
            }.onFailure { error ->
                _atcResponse.value = "Error: ${error.message}"
            }
        }
    }

    fun evaluateLastMessage(pilotMessage: String) {
        viewModelScope.launch {
            val scenario = atcRepository.getScenarioById(1) ?: return@launch

            val result = atcRepository.evaluatePilotCommunication(
                sessionId = currentSessionId,
                exchangeId = 1L,
                pilotMessage = pilotMessage,
                expectedPhrasing = "Airport Ground, Callsign, Location, Request with ATIS",
                scenario = scenario
            )

            result.onSuccess { eval ->
                _evaluation.value = eval
            }
        }
    }
}
```

### Example 2: Real-time ATC Chat Interface

```kotlin
@Composable
fun ATCChatScreen(viewModel: ATCSimulatorViewModel) {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val atcResponse by viewModel.atcResponse.observeAsState()
    var userInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat messages
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }

        // Input field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Your message...") }
            )

            Button(onClick = {
                if (userInput.isNotEmpty()) {
                    messages.add(ChatMessage(userInput, isUser = true))
                    viewModel.sendPilotMessage(userInput)
                    userInput = ""
                }
            }) {
                Text("Send")
            }
        }
    }

    // Observe ATC responses
    LaunchedEffect(atcResponse) {
        atcResponse?.let {
            messages.add(ChatMessage(it, isUser = false))
        }
    }
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
```

---

## Checklist Management

### Example 3: Interactive Checklist with AI Help

```kotlin
class ChecklistViewModel(
    private val checklistRepository: ChecklistRepository,
    private val aircraftId: Long
) : ViewModel() {

    private val _checklistItems = MutableLiveData<List<ChecklistItem>>()
    val checklistItems: LiveData<List<ChecklistItem>> = _checklistItems

    private val _aiGuidance = MutableLiveData<String>()
    val aiGuidance: LiveData<String> = _aiGuidance

    private var currentSessionId: Long = 0

    init {
        loadChecklist()
        startSession()
    }

    private fun loadChecklist() {
        viewModelScope.launch {
            checklistRepository.getChecklistItems(1L).collect { items ->
                _checklistItems.value = items
            }
        }
    }

    private fun startSession() {
        viewModelScope.launch {
            currentSessionId = checklistRepository.startChecklistSession(aircraftId)
        }
    }

    fun completeItem(item: ChecklistItem, notes: String? = null) {
        viewModelScope.launch {
            checklistRepository.completeChecklistItem(
                sessionId = currentSessionId,
                itemId = item.id,
                notes = notes
            )
        }
    }

    fun askAboutItem(item: ChecklistItem, question: String) {
        viewModelScope.launch {
            val result = checklistRepository.getItemGuidance(
                checklistItem = item,
                aircraftType = "Cessna 172",
                userQuestion = question
            )

            result.onSuccess { guidance ->
                _aiGuidance.value = guidance
            }.onFailure { error ->
                _aiGuidance.value = "Error: ${error.message}"
            }
        }
    }

    fun endSession() {
        viewModelScope.launch {
            checklistRepository.endChecklistSession(currentSessionId)
        }
    }
}
```

### Example 4: Checklist UI with Progress

```kotlin
@Composable
fun ChecklistScreen(viewModel: ChecklistViewModel) {
    val items by viewModel.checklistItems.observeAsState(emptyList())
    val aiGuidance by viewModel.aiGuidance.observeAsState()

    var selectedItem by remember { mutableStateOf<ChecklistItem?>(null) }
    var showAIDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Progress bar
        LinearProgressIndicator(
            progress = items.count { it.isCompleted } / items.size.toFloat(),
            modifier = Modifier.fillMaxWidth()
        )

        // Checklist items
        LazyColumn {
            items(items) { item ->
                ChecklistItemRow(
                    item = item,
                    onComplete = { viewModel.completeItem(item) },
                    onAskAI = {
                        selectedItem = item
                        showAIDialog = true
                    }
                )
            }
        }
    }

    // AI Guidance Dialog
    if (showAIDialog && selectedItem != null) {
        AlertDialog(
            onDismissRequest = { showAIDialog = false },
            title = { Text("Ask about: ${selectedItem!!.title}") },
            text = {
                Column {
                    TextField(
                        value = questionInput,
                        onValueChange = { questionInput = it },
                        label = { Text("Your question") }
                    )

                    aiGuidance?.let {
                        Text(it, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.askAboutItem(selectedItem!!, questionInput)
                }) {
                    Text("Ask")
                }
            }
        )
    }
}
```

---

## Flight Planning

### Example 5: Flight Plan with Weather and AI Advice

```kotlin
class FlightPlanViewModel(
    private val flightPlanRepository: FlightPlanRepository
) : ViewModel() {

    private val _flightPlan = MutableLiveData<FlightPlan>()
    val flightPlan: LiveData<FlightPlan> = _flightPlan

    private val _weatherBriefing = MutableLiveData<WeatherBriefing>()
    val weatherBriefing: LiveData<WeatherBriefing> = _weatherBriefing

    private val _aiAdvice = MutableLiveData<String>()
    val aiAdvice: LiveData<String> = _aiAdvice

    fun createFlightPlan(
        departure: String,
        arrival: String,
        aircraftId: Long
    ) {
        viewModelScope.launch {
            // Get weather for both airports
            val departureWeather = flightPlanRepository.getWeatherBriefing(departure)
            departureWeather.onSuccess { briefing ->
                _weatherBriefing.value = briefing
            }

            // Get AI flight planning advice
            val advice = flightPlanRepository.getFlightPlanningAdvice(
                departure = departure,
                arrival = arrival,
                aircraft = "Cessna 172"
            )

            advice.onSuccess { suggestions ->
                _aiAdvice.value = suggestions
            }

            // Calculate flight metrics
            val metrics = flightPlanRepository.calculateFlightMetrics(
                distance = 50.0, // nautical miles
                cruiseSpeed = 110, // knots
                fuelBurnRate = 8.5 // gph
            )

            // Create flight plan
            val plan = FlightPlan(
                aircraftId = aircraftId,
                departureAirport = departure,
                arrivalAirport = arrival,
                route = "Direct",
                altitude = 4500,
                cruiseSpeed = 110,
                estimatedFlightTime = metrics.estimatedFlightTime,
                fuelRequired = metrics.fuelRequired,
                isActive = true
            )

            val planId = flightPlanRepository.createFlightPlan(plan)
            _flightPlan.value = plan.copy(id = planId)
        }
    }
}
```

### Example 6: Weather Display

```kotlin
@Composable
fun WeatherCard(weather: WeatherReport) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = weather.airport,
                style = MaterialTheme.typography.h6
            )

            // Flight Category with color
            val categoryColor = when (weather.flightCategory) {
                FlightCategory.VFR -> Color.Green
                FlightCategory.MVFR -> Color.Blue
                FlightCategory.IFR -> Color.Red
                FlightCategory.LIFR -> Color.Magenta
            }

            Text(
                text = weather.flightCategory.name,
                color = categoryColor,
                style = MaterialTheme.typography.subtitle1
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Weather details
            Text("Temperature: ${weather.temperature}°C")
            Text("Wind: ${weather.windDirection}° at ${weather.windSpeed} kt")
            Text("Visibility: ${weather.visibility} SM")
            Text("Altimeter: ${weather.altimeter} inHg")

            // Sky conditions
            weather.skyConditions.forEach { sky ->
                Text("${sky.coverage.name} at ${sky.altitude} ft")
            }

            // Raw METAR
            Text(
                text = weather.rawMetar,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
```

---

## Performance Tracking

### Example 7: Performance Dashboard

```kotlin
class PerformanceViewModel(
    private val performanceDao: PerformanceDao
) : ViewModel() {

    val recentReports: LiveData<List<PerformanceReport>> =
        performanceDao.getAllPerformanceReports().asLiveData()

    val achievements: LiveData<List<Achievement>> =
        performanceDao.getAllAchievements().asLiveData()

    fun generateWeeklyReport(userId: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val weekAgo = now - (7 * 24 * 60 * 60 * 1000)

            val report = PerformanceReport(
                userId = userId,
                periodStart = weekAgo,
                periodEnd = now,
                overallScore = 85.5f,
                checklistAccuracy = 92.0f,
                atcAccuracy = 78.5f,
                flightPlansCompleted = 5,
                checklistsCompleted = 12,
                atcSessionsCompleted = 8,
                totalPracticeTime = 3600000, // 1 hour
                strengths = listOf("Excellent checklist discipline", "Clear radio communication"),
                areasForImprovement = listOf("Practice emergency procedures", "Review IFR phraseology")
            )

            performanceDao.insertPerformanceReport(report)
        }
    }

    fun unlockAchievement(achievementId: Long) {
        viewModelScope.launch {
            val achievement = performanceDao.getAllAchievements()
                .first()
                .find { it.id == achievementId }

            achievement?.let {
                val unlocked = it.copy(unlockedAt = System.currentTimeMillis())
                performanceDao.updateAchievement(unlocked)
            }
        }
    }
}
```

---

## Integration Example: Complete Flow

### Example 8: Full Practice Session

```kotlin
class PracticeSessionManager(
    private val atcRepository: ATCRepository,
    private val checklistRepository: ChecklistRepository,
    private val flightPlanRepository: FlightPlanRepository,
    private val performanceDao: PerformanceDao
) {

    suspend fun runCompletePreFlightPractice(
        aircraftId: Long,
        departure: String,
        arrival: String
    ): SessionResult {

        // 1. Start checklist session
        val checklistSessionId = checklistRepository.startChecklistSession(aircraftId)

        // 2. Get weather briefing
        val weather = flightPlanRepository.getWeatherBriefing(departure)

        // 3. Create flight plan
        val flightPlan = FlightPlan(
            aircraftId = aircraftId,
            departureAirport = departure,
            arrivalAirport = arrival,
            route = "Direct",
            altitude = 4500,
            cruiseSpeed = 110,
            estimatedFlightTime = 30,
            fuelRequired = 6.0
        )
        val planId = flightPlanRepository.createFlightPlan(flightPlan)

        // 4. Practice ATC ground clearance
        val atcScenario = atcRepository.getScenarioById(1)!!
        val atcSessionId = atcRepository.startPracticeSession(atcScenario.id)

        // 5. Track performance metrics
        val sessionStart = System.currentTimeMillis()

        return SessionResult(
            checklistSessionId = checklistSessionId,
            atcSessionId = atcSessionId,
            flightPlanId = planId,
            startTime = sessionStart
        )
    }
}

data class SessionResult(
    val checklistSessionId: Long,
    val atcSessionId: Long,
    val flightPlanId: Long,
    val startTime: Long
)
```

---

## Testing with Mock Data

```kotlin
// For development without API costs
class MockAIService : AIService() {
    override suspend fun generateATCResponse(
        pilotMessage: String,
        context: ATCContext
    ): Result<String> {
        // Simulate response based on scenario
        val response = when {
            pilotMessage.contains("ground", ignoreCase = true) ->
                "Skyhawk N12345, Palo Alto Ground, taxi to runway 31 via Alpha."
            pilotMessage.contains("ready for takeoff", ignoreCase = true) ->
                "Skyhawk N12345, runway 31, cleared for takeoff."
            else ->
                "Skyhawk N12345, say again."
        }

        return Result.success(response)
    }

    override suspend fun evaluateATCCommunication(
        pilotMessage: String,
        expectedPhrasings: List<String>,
        context: ATCContext
    ): Result<ATCEvaluation> {
        // Simple mock evaluation
        val score = if (pilotMessage.contains("N12345")) 85 else 60
        return Result.success(
            ATCEvaluation(
                score = score,
                isCorrect = score >= 70,
                feedback = "Good communication. Consider using more standard phraseology.",
                suggestions = listOf("Include ATIS information letter")
            )
        )
    }
}
```
