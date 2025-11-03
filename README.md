# ✈️ FlightDeck - AI-Powered Voice ATC Practice

**The smarter alternative to ARSim.**

Practice radio communication with an AI controller that talks back, adapts to your skill level, and coaches you in real-time.

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 🎯 What is FlightDeck?

FlightDeck is a **voice-based ATC practice app** that uses AI to simulate realistic radio communications. Enter your departure and arrival airports, and practice with an AI-powered controller that uses real weather and generates realistic traffic scenarios.

**Perfect for:**
- ✈️ Student pilots preparing for first solo
- 🎓 Pilots practicing for checkrides
- 🌐 International pilots learning US phraseology
- 📻 Anyone nervous about radio communication

---

## 🏆 Why FlightDeck vs ARSim?

| Feature | ARSim | FlightDeck |
|---------|-------|------------|
| **Voice practice** | ✅ Pre-recorded scripts | ✅ AI-powered, infinite variety |
| **Adapts to mistakes** | ❌ Rigid scripts | ✅ Real-time coaching |
| **Scenario variety** | Limited patterns | ✅ AI generates infinite scenarios |
| **Practice YOUR flight** | ❌ Generic scenarios | ✅ Enter KPAO→KSQL, practice that exact route |
| **Real weather** | ❌ | ✅ Uses actual METAR/TAF forecasts |
| **Personalized feedback** | Basic scores | ✅ Detailed coaching + suggestions |
| **Price** | $50-100 one-time | **$19.99 one-time** |
| **Airport database** | Limited | ✅ 20,000+ US/Canada airports |
| **Progress tracking** | Basic | ✅ Digital logbook with proficiency tracking |

**The key difference:** ARSim teaches you to repeat scripts. FlightDeck teaches you to think like a pilot.

---

## 🎤 How It Works

1. **Choose your flight** - Enter departure and arrival airports (e.g., KPAO → KSQL)
2. **Press to talk** - Use real push-to-talk interface like in the plane
3. **AI responds** - Realistic controller voice with proper phraseology
4. **Get instant feedback** - AI tells you what was good and what to improve
5. **Practice unlimited** - No session limits, no subscription

### Example Session:

```
👨‍✈️ YOU (speaking): "Palo Alto Ground, Skyhawk N12345,
                     VFR to San Carlos with information Alpha"

🎧 AI CONTROLLER: "Skyhawk N12345, Palo Alto Ground,
                   taxi to runway 31 via Alpha,
                   expect right downwind departure"

👨‍✈️ YOU: "Taxi 31 via Alpha, right downwind, Skyhawk 345"

✅ AI FEEDBACK: "Great readback! Just remember to include your
                full callsign on first transmission."
```

---

## 💰 Pricing

**Free Tier:**
- 30 major US/Canada airports
- 50 practice sessions
- Basic scenarios (ground, tower)

**Premium: $19.99 one-time**
- ✅ Unlimited practice sessions
- ✅ All 20,000+ US/Canada airports
- ✅ Advanced scenarios (approach, departure, emergencies)
- ✅ Digital logbook with proficiency tracking
- ✅ Export practice history for CFI review

**No subscription. No limits. Practice as much as you need.**

---

## 📱 Requirements

- Android 7.0 (API 24) or higher
- Microphone for voice input
- Internet connection for AI responses
- Recommended: Headset for best experience

---

## 🚀 Installation

### For Users (Coming Soon)
Download from Google Play Store (link coming after beta testing)

### For Developers

1. **Clone the repository**
```bash
git clone https://github.com/hantotech/flightdeck.git
cd flightdeck
```

2. **Add API keys to `local.properties`**
```properties
ANTHROPIC_API_KEY=sk-ant-api03-your-key-here
GEMINI_API_KEY=AIzaSy-your-key-here
```

Get your API keys:
- **Anthropic Claude API**: https://console.anthropic.com/ ($5 free credit)
- **Google Gemini API**: https://makersuite.google.com/app/apikey (Free tier: 1,500 requests/day)

3. **Open in Android Studio**
- File → Open → Select FlightDeck folder
- Wait for Gradle sync

4. **Build and Run**
- Click Run ▶️ or press Shift+F10
- Select emulator or physical device

---

## 🎓 What You Can Practice

### Ground Operations
- Ground clearances with taxi instructions
- Runway hold-short procedures
- Progressive taxi in complex airports

### Tower Operations
- Takeoff clearances
- Landing clearances
- Pattern work (touch-and-go, full stop)
- Go-arounds

### Approach/Departure
- Flight following requests
- VFR transitions through Class B/C
- Flight plan activations
- Radar services

### Advanced Scenarios (Premium)
- Class B operations (complex airports)
- Emergency communications (engine failure, lost comms)
- Weather diversions
- Multiple aircraft traffic

---

## 📊 Features

### Voice Recognition
- Android SpeechRecognizer with aviation terminology
- Push-to-talk interface
- Background noise filtering

### AI-Powered ATC
- Claude AI for intelligent, context-aware responses
- Realistic traffic scenarios
- Weather-appropriate instructions
- Adapts to your skill level

### Digital Logbook
- Track all practice sessions
- Monitor proficiency across 10 skill categories
- Export for CFI review
- Progress tracking over time

### Airport Database
- 30 major airports (free tier)
- 20,000+ US/Canada airports (premium)
- Actual runways and frequencies
- Real-time METAR/TAF weather

---

## 🛠️ Technology Stack

- **Language**: Kotlin
- **Architecture**: MVVM with Repository pattern
- **Database**: Room (SQLite)
- **UI**: Material Design 3, View Binding
- **Voice**: Android SpeechRecognizer + TextToSpeech
- **AI**: Claude API (Anthropic) + Gemini API (Google)
- **Weather**: Aviation Weather Center API

---

## 📖 Documentation

- **[PRODUCT_REFOCUS.md](PRODUCT_REFOCUS.md)** - Product strategy and ARSim competitive analysis
- **[VOICE_IMPLEMENTATION_GUIDE.md](VOICE_IMPLEMENTATION_GUIDE.md)** - Voice integration technical details
- **[SETUP_GUIDE.md](SETUP_GUIDE.md)** - Detailed developer setup
- **[AIRPORT_PREMIUM_STRATEGY.md](AIRPORT_PREMIUM_STRATEGY.md)** - Two-tier airport pricing model
- **[BRAND_GUIDELINES.md](BRAND_GUIDELINES.md)** - Design system and branding

---

## 🗺️ Roadmap

### Current Status: **MVP Development** 🚧

**Phase 1: Voice ATC Core (Weeks 1-2)**
- [x] Backend architecture (MVVM, repositories, database)
- [x] AI integration (Claude + Gemini APIs)
- [x] Airport database (30 free + 20K premium)
- [ ] Voice input (Android SpeechRecognizer)
- [ ] Voice output (TextToSpeech)
- [ ] Push-to-talk UI
- [ ] Basic practice session flow

**Phase 2: Core Scenarios (Week 3)**
- [ ] Ground clearance scenarios
- [ ] Tower operations (takeoff/landing)
- [ ] Pattern work scenarios
- [ ] Session feedback and scoring

**Phase 3: Polish & Beta (Week 4)**
- [ ] Digital logbook UI
- [ ] Practice history
- [ ] Premium purchase flow
- [ ] Beta testing with 20+ student pilots

**Phase 4: Launch (Week 5-6)**
- [ ] Google Play Store submission
- [ ] Marketing materials
- [ ] Flight school outreach
- [ ] Public launch

---

## 🤝 Contributing

Contributions are welcome! This project is actively developed.

### Areas Where We Need Help:
- Voice recognition accuracy improvements
- Aviation phraseology validation (CFI input)
- UI/UX testing with real pilots
- Additional airport data
- Scenario testing

---

## ⚠️ Disclaimer

**FlightDeck is a training aid and should not replace actual flight instruction.**

This app is designed to supplement, not replace, formal flight training with a certified flight instructor. Always follow proper training procedures and consult with a CFI before attempting any flight operations.

The aviation information provided is for educational purposes and may not reflect the most current regulations. Always verify information with official FAA sources and your aircraft's Pilot Operating Handbook (POH).

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **FAA** - For publicly available regulations and guidance
- **Anthropic** - Claude API for intelligent AI responses
- **Google** - Gemini API with generous free tier
- **Aviation Weather Center** - Real-time METAR/TAF data
- **OurAirports** - Public domain airport database

---

## 📞 Contact & Feedback

Found a bug? Have a suggestion? Want to help beta test?

- **GitHub Issues**: [Report bugs or request features](https://github.com/hantotech/flightdeck/issues)
- **Email**: [Your email here]

---

## 🎯 For Flight Schools

Interested in FlightDeck for your students?

**Benefits:**
- Reduce CFI time spent on radio communication basics
- Students arrive prepared for radio work
- Track student progress across your school
- Site license pricing available

Contact us for flight school partnerships.

---

<p align="center">
  <b>Built with ❤️ for the aviation community</b><br>
  <sub>Helping pilots master radio communication, one call at a time</sub>
</p>

<p align="center">
  <i>"Practice YOUR flight before you fly it"</i>
</p>
