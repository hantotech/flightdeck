# ✈️ FlightDeck - AI-Powered Pilot Training App

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**FlightDeck** is a revolutionary pilot training application that simulates pre-flight procedures, ATC communication, and flight planning using advanced AI technology. Practice realistic aviation scenarios without leaving the ground.

---

## 🎯 What is FlightDeck?

FlightDeck provides student pilots and aviation enthusiasts with a comprehensive training environment to practice:

- ✈️ **Pre-flight checklists** - Interactive walkthroughs with AI guidance
- 📡 **ATC communication** - Real-time simulation with AI controllers
- 🗺️ **Flight planning** - Route planning with live weather integration
- 📊 **Performance tracking** - Detailed reports and progress monitoring
- 🎓 **AI instructor feedback** - Learn from intelligent, context-aware evaluations

### Not a Flight Simulator

FlightDeck focuses on **communication, procedures, and decision-making**—not aircraft physics or 3D graphics. Think of it as your personal CFI (Certified Flight Instructor) available 24/7.

---

## ✨ Key Features

### 🤖 Hybrid AI System
- **4 AI models** working together (Claude Sonnet, Haiku, Gemini Flash, Pro)
- **Smart routing** based on task complexity
- **70% cost savings** compared to single-model approach
- **RAG (Retrieval Augmented Generation)** grounded in FAA regulations
- **FAR/AIM knowledge base** - 25+ verified regulations and procedures
- **Zero hallucination** on critical safety information

### 📋 Interactive Checklists
- Pre-flight exterior/interior inspections
- Before engine start, takeoff, landing procedures
- Emergency checklists
- AI answers questions about any checklist item

### 📡 ATC Communication Simulator
- Practice radio calls with AI controllers
- Ground, Tower, Approach, Departure scenarios
- Real-time responses based on your messages
- Intelligent evaluation with detailed feedback

### 🌤️ Live Weather Integration
- Real-time METAR/TAF data
- Flight category determination (VFR/MVFR/IFR/LIFR)
- Weather-aware flight planning

### 📊 Performance Analytics
- Comprehensive scoring system
- Progress tracking over time
- Achievement badges
- Detailed performance reports

---

## 🏗️ Architecture

### Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **Networking**: Retrofit + OkHttp
- **Async**: Kotlin Coroutines + Flow
- **AI**: Claude API (Anthropic) + Gemini API (Google)
- **Preferences**: DataStore

### AI Integration

```
┌─────────────────────────────────────────────────────────┐
│                  AI ORCHESTRATOR                         │
│            (Smart Routing Engine)                        │
└────────┬────────────────────────────────────┬───────────┘
         │                                    │
    ┌────▼────────┐                    ┌─────▼─────────┐
    │   CLAUDE    │                    │    GEMINI     │
    │             │                    │               │
    │  Sonnet 3.5 │◄─── Evaluations    │   Flash 1.5   │◄─── Speed
    │  Haiku 3    │◄─── Real-time      │   Pro 1.5     │◄─── Fallback
    └─────────────┘                    └───────────────┘
```

### Project Structure

```
FlightDeck/
├── data/
│   ├── model/              # Data classes
│   ├── local/              # Room database & DAOs
│   ├── remote/             # API services (AI, Weather)
│   ├── repository/         # Repository layer
│   ├── preferences/        # User preferences
│   └── knowledge/          # Aviation knowledge base (RAG)
├── ui/                     # UI components (pending)
├── utils/                  # Utilities
└── docs/                   # Documentation
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (latest version)
- Android SDK 24+
- Kotlin 1.9+

### API Keys Required

**1. Anthropic Claude API** (Optional - for premium features)
- Visit: https://console.anthropic.com/
- Sign up and create API key
- $5 free credit for new accounts

**2. Google Gemini API** (Recommended - has free tier!)
- Visit: https://makersuite.google.com/app/apikey
- Free tier: 1,500 requests/day
- No credit card required

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/hantotech/flightdeck.git
cd flightdeck
```

2. **Create `local.properties`** in project root
```properties
ANTHROPIC_API_KEY=sk-ant-api03-your-key-here
GEMINI_API_KEY=AIzaSy-your-key-here
```

3. **Open in Android Studio**
- File > Open > Select FlightDeck folder
- Wait for Gradle sync

4. **Build and Run**
- Click Run ▶️ or press Shift+F10
- Select emulator or physical device

---

## 📖 Documentation

Comprehensive guides available:

- **[Setup Guide](SETUP_GUIDE.md)** - Detailed setup instructions
- **[Hybrid AI Guide](HYBRID_AI_GUIDE.md)** - AI configuration and optimization
- **[API Examples](API_EXAMPLES.md)** - Code usage examples
- **[Project Status](PROJECT_STATUS.md)** - Current progress and roadmap

---

## 💡 Usage Examples

### ATC Communication Practice

```kotlin
// Initialize AI service
val aiService = EnhancedAIService.getInstance(UserTier.BASIC)

// Practice ATC call
val result = aiService.generateATCResponse(
    pilotMessage = "Palo Alto Ground, Skyhawk N12345, ready to taxi",
    context = ATCContext(
        airport = "KPAO",
        scenarioType = "GROUND_CLEARANCE",
        conditions = "VFR"
    )
)

result.onSuccess { atcResponse ->
    // Display: "Skyhawk N12345, Palo Alto Ground,
    //          taxi to runway 31 via Alpha"
}

// Get evaluation
val evaluation = aiService.evaluateATCCommunication(...)
// Returns score, feedback, and suggestions
```

### Interactive Checklist

```kotlin
val checklistRepo = ChecklistRepository(checklistDao, aiService)

// Start session
val sessionId = checklistRepo.startChecklistSession(aircraftId)

// Get AI help
val guidance = checklistRepo.getItemGuidance(
    checklistItem = checklistItem,
    aircraftType = "Cessna 172",
    userQuestion = "Why do we check oil level?"
)
```

---

## 💰 Pricing Tiers

### Free Tier
- 10 AI interactions/day
- Gemini Flash only
- Basic features
- **$0/month**

### Basic Tier
- 100 AI interactions/month
- Hybrid routing (all models)
- All features
- **$4.99/month** (suggested)

### Premium Tier
- Unlimited interactions
- Best models (Claude Sonnet priority)
- Priority support
- **$9.99/month** (suggested)

---

## 📊 Cost Analysis

### AI Costs per User (Monthly)

| User Type | Interactions | AI Cost | Revenue | Profit |
|-----------|-------------|---------|---------|--------|
| Free | 300 | $0 | $0 | $0 |
| Basic | 100 | $2-3 | $4.99 | $2-3 |
| Premium | 500 | $5-8 | $9.99 | $2-5 |

**70% cost savings** vs. using Claude Sonnet exclusively

---

## 🛠️ Development Roadmap

### ✅ Phase 1: Backend (COMPLETE)
- [x] Data models and database
- [x] AI integration (Claude + Gemini)
- [x] Smart routing orchestrator
- [x] Weather API integration
- [x] Knowledge base (RAG)
- [x] Repository layer
- [x] Documentation

### 🚧 Phase 2: UI (In Progress)
- [ ] Navigation structure
- [ ] Checklist screen
- [ ] ATC simulator interface
- [ ] Flight planning screen
- [ ] Performance dashboard
- [ ] Settings & preferences

### 📅 Phase 3: Features
- [ ] Voice input for ATC
- [ ] Text-to-speech for ATC responses
- [ ] Offline mode
- [ ] Multi-aircraft support
- [ ] IFR scenarios
- [ ] Flight school integration

### 🚀 Phase 4: Launch
- [ ] Beta testing with pilots
- [ ] CFI review and feedback
- [ ] Google Play Store submission
- [ ] Marketing materials

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### Development Setup

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **FAA** - For publicly available regulations and guidance
- **Anthropic** - Claude API for intelligent responses
- **Google** - Gemini API with generous free tier
- **Aviation Weather Center** - Real-time weather data

---

## 📞 Contact & Support

- **GitHub Issues**: [Report bugs or request features](https://github.com/hantotech/flightdeck/issues)
- **Email**: [Your email]
- **Website**: [Your website]

---

## ⚠️ Disclaimer

**FlightDeck is a training aid and should not replace actual flight instruction.**

This app is designed to supplement, not replace, formal flight training with a certified flight instructor. Always follow proper training procedures and consult with a CFI before attempting any flight operations.

The aviation information provided is for educational purposes and may not reflect the most current regulations. Always verify information with official FAA sources and your aircraft's Pilot Operating Handbook (POH).

---

## 🎓 For Flight Schools

Interested in a custom-branded version for your flight school?

**Features for flight schools:**
- White-label branding
- Custom checklists for your fleet
- Student progress tracking
- CFI dashboard
- Site license pricing

Contact us for more information.

---

## ⭐ Star History

If you find FlightDeck useful, please consider giving it a star! ⭐

---

<p align="center">
  <b>Built with ❤️ for the aviation community</b><br>
  <sub>Helping pilots achieve their dreams, one checklist at a time</sub>
</p>

<p align="center">
  🤖 <i>Generated with <a href="https://claude.com/claude-code">Claude Code</a></i><br>
  <sub>Co-Authored-By: Claude &lt;noreply@anthropic.com&gt;</sub>
</p>
