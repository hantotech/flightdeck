# FlightDeck - Setup Guide

## Overview
FlightDeck is an AI-powered pilot training application that provides realistic simulation of pre-flight procedures, ATC communication, flight planning, and performance tracking.

## Architecture

```
FlightDeck/
├── data/
│   ├── model/           # Data models (Aircraft, Checklist, FlightPlan, etc.)
│   ├── local/           # Room Database & DAOs
│   │   ├── dao/         # Database Access Objects
│   │   └── FlightDeckDatabase.kt
│   ├── remote/          # API Services
│   │   ├── ai/          # Claude AI Integration
│   │   └── weather/     # Aviation Weather Service
│   └── repository/      # Repository layer (combines data sources)
├── ui/                  # UI Components (Fragments, ViewModels)
└── utils/              # Utilities (EmailValidator, etc.)
```

## Key Features

### 1. AI-Powered Interactions (Claude API)
- **Real-time ATC Communication**: Practice radio calls with AI air traffic controller
- **Intelligent Evaluation**: Get detailed feedback on communication accuracy
- **Checklist Guidance**: Ask questions about any checklist item
- **Flight Planning Assistant**: Get route recommendations based on weather and aircraft

### 2. Data Models

**Aircraft** - Manage aircraft specifications
- Make, model, registration
- Performance specs (cruise speed, fuel capacity, burn rate)
- Home airport

**Checklist System**
- Pre-flight, engine start, takeoff, landing procedures
- Track completion sessions
- Critical items flagged
- Expected responses

**Flight Planning**
- Departure/arrival airports
- Route planning with waypoints
- Fuel calculations
- Weather integration

**ATC Scenarios**
- Ground clearance, taxi, takeoff/landing
- In-flight communication
- Emergency procedures
- Multiple difficulty levels

**Performance Tracking**
- Session history
- Accuracy metrics
- Achievements/badges
- Progress reports

## Setup Instructions

### Prerequisites
- Android Studio (latest version)
- Android SDK 24+
- Anthropic API Key (for Claude AI)

### Step 1: Get API Keys

#### Anthropic API Key (Required for AI features)
1. Visit https://console.anthropic.com/
2. Sign up or log in
3. Navigate to API Keys
4. Create a new API key
5. Copy the key

#### Weather API (Optional - uses free Aviation Weather API)
The app uses https://aviationweather.gov/ which requires no API key for basic usage.

### Step 2: Configure API Keys

Create or edit `local.properties` in the project root:

```properties
# Anthropic Claude API Key
ANTHROPIC_API_KEY=your_api_key_here

# Optional: Weather API Key (if using paid service)
WEATHER_API_KEY=your_weather_key_here
```

Then update `app/build.gradle.kts` to read from local.properties:

```kotlin
android {
    defaultConfig {
        // Load from local.properties
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        buildConfigField("String", "ANTHROPIC_API_KEY",
            "\"${properties.getProperty("ANTHROPIC_API_KEY", "")}\"")
        buildConfigField("String", "WEATHER_API_KEY",
            "\"${properties.getProperty("WEATHER_API_KEY", "")}\"")
    }
}
```

### Step 3: Sync and Build

1. Open project in Android Studio
2. Click "Sync Project with Gradle Files"
3. Build > Make Project
4. Run on emulator or device

## Using the AI Features

### ATC Communication Simulator

```kotlin
// In your ViewModel or Repository
val aiService = AIService.getInstance()
val atcRepository = ATCRepository(atcDao, aiService)

// Start a practice session
val sessionId = atcRepository.startPracticeSession(scenarioId)

// Send pilot message and get ATC response
val result = atcRepository.sendPilotMessage(
    sessionId = sessionId,
    scenario = scenario,
    pilotMessage = "Palo Alto Ground, Skyhawk N12345, ready to taxi with information Alpha"
)

result.onSuccess { atcResponse ->
    // Display ATC response to user
    println(atcResponse)
}

// Evaluate pilot's communication
val evaluation = atcRepository.evaluatePilotCommunication(
    sessionId = sessionId,
    exchangeId = exchangeId,
    pilotMessage = pilotMessage,
    expectedPhrasing = "Airport Ground, Callsign, Location, Request with ATIS",
    scenario = scenario
)

evaluation.onSuccess { eval ->
    println("Score: ${eval.score}/100")
    println("Feedback: ${eval.feedback}")
}
```

### Checklist with AI Assistance

```kotlin
val checklistRepository = ChecklistRepository(checklistDao, aiService)

// Start checklist session
val sessionId = checklistRepository.startChecklistSession(aircraftId)

// Get AI guidance for a checklist item
val guidance = checklistRepository.getItemGuidance(
    checklistItem = checklistItem,
    aircraftType = "Cessna 172",
    userQuestion = "Why do we set mixture to rich?"
)

guidance.onSuccess { explanation ->
    // Display to user
    println(explanation)
}

// Complete an item
checklistRepository.completeChecklistItem(sessionId, itemId, notes = "All good")
```

### Flight Planning with AI

```kotlin
val flightPlanRepository = FlightPlanRepository(
    flightPlanDao,
    aiService,
    weatherService
)

// Get weather briefing
val briefing = flightPlanRepository.getWeatherBriefing("KPAO")

// Get AI flight planning advice
val advice = flightPlanRepository.getFlightPlanningAdvice(
    departure = "KPAO",
    arrival = "KSFO",
    aircraft = "Cessna 172"
)

advice.onSuccess { suggestions ->
    // Display AI suggestions
    println(suggestions)
}
```

## Database Initialization

Initialize sample data on first launch:

```kotlin
// In your Application class or MainActivity
lifecycleScope.launch {
    val database = FlightDeckDatabase.getDatabase(context)

    // Initialize ATC scenarios
    val atcRepo = ATCRepository(database.atcDao(), aiService)
    atcRepo.initializeSampleScenarios()

    // Initialize default checklist
    val checklistRepo = ChecklistRepository(database.checklistDao(), aiService)
    checklistRepo.initializeDefaultChecklist()
}
```

## API Rate Limits & Costs

### Anthropic Claude API
- Model: Claude 3.5 Sonnet
- Pricing: ~$3 per million input tokens, ~$15 per million output tokens
- Typical ATC exchange: ~200-500 tokens total
- Estimated cost: ~$0.01-0.02 per interaction

### Optimization Tips
1. Cache common responses
2. Use lower temperature for evaluation (0.3) vs generation (0.7-0.8)
3. Limit max_tokens to what's needed
4. Consider Claude 3 Haiku for simpler tasks (cheaper)

## Free Tier Development

For development without API costs:
1. Use mock responses in `AIService`
2. Create a `MockAIService` class
3. Use dependency injection to switch between real and mock

Example mock service:

```kotlin
class MockAIService : AIService() {
    override suspend fun generateATCResponse(
        pilotMessage: String,
        context: ATCContext
    ): Result<String> {
        return Result.success(
            "Skyhawk N12345, Palo Alto Ground, taxi to runway 31 via taxiway Alpha."
        )
    }
}
```

## Next Steps

### UI Implementation (Pending)
1. Update navigation to include:
   - Home/Dashboard
   - Checklist
   - Flight Plan
   - ATC Simulator
   - Performance

2. Create ViewModels for each feature
3. Design UI with aviation-inspired theme (dark mode, instrument panel colors)
4. Implement real-time chat UI for ATC simulator

### Additional Features
- Voice input for ATC communication
- Text-to-speech for ATC responses
- Offline mode with cached scenarios
- Multi-aircraft support
- Flight school integration

## Troubleshooting

### API Key Issues
```
Error: 401 Unauthorized
```
- Check API key is correctly set in BuildConfig
- Verify key is valid at console.anthropic.com
- Ensure no extra spaces in local.properties

### Network Errors
```
Error: Unable to resolve host
```
- Check internet connection
- Verify permissions in AndroidManifest.xml
- Check firewall/proxy settings

### Database Errors
```
Error: Cannot find implementation for database
```
- Ensure kapt plugin is enabled
- Sync Gradle
- Clean and rebuild project

## Support & Contributing

For issues, feature requests, or contributions:
- Project repository: [Your repo URL]
- Documentation: [Your docs URL]

## License

[Your license here]
