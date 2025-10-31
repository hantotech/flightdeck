# FlightDeck - Canned Demo Script

## Overview
This demo showcases FlightDeck, an AI-powered pilot training app that helps student pilots practice ATC communications, access weather information, and study aviation knowledge.

## Demo Flow (5-7 minutes)

### 1. Home Dashboard (30 seconds)
**What to show:**
- Welcome screen with app overview
- Quick action cards for main features
- Database stats (5 airports, 5 scenarios loaded)

**Talking points:**
- "FlightDeck is your personal flight training assistant"
- "All data is stored locally and integrates with real aviation databases"
- "Pre-loaded with California Bay Area airports for immediate use"

---

### 2. ATIS Weather Viewer (1 minute)
**Navigation:** Tap "Weather" tab or ATIS card

**Demo steps:**
1. Show ATIS search with ICAO code input
2. Enter "KPAO" (Palo Alto Airport)
3. Display current ATIS information:
   - Information code (Alpha, Bravo, etc.)
   - Wind direction and speed
   - Visibility
   - Temperature and dewpoint
   - Altimeter setting
   - Active runway
   - NOTAMs

**Talking points:**
- "Pilots must check weather before every flight"
- "ATIS provides automated airport weather updates"
- "Information code changes hourly - pilots confirm they have current info"

---

### 3. ATC Simulator - The Core Feature (3-4 minutes)

#### 3a. Scenario Selection (30 seconds)
**Navigation:** Tap "Practice" tab or ATC Simulator card

**Demo steps:**
1. Show current scenario: "Ground Clearance - VFR"
2. Tap "Change" button
3. Show 5 available scenarios:
   - Ground Clearance - VFR (Beginner)
   - Takeoff Clearance (Beginner)
   - In-Flight Position Report (Intermediate)
   - Landing Clearance - Pattern (Intermediate)
   - Emergency Declaration (Advanced)
4. Select "Ground Clearance - VFR"

**Talking points:**
- "5 realistic training scenarios covering common ATC situations"
- "Difficulty levels from beginner to advanced"
- "Each scenario has specific airport and weather context"

#### 3b. Radio Communication Practice (2-3 minutes)
**Demo conversation:**

**System Message:** "Scenario started: Ground Clearance - VFR"
**System Message:** "Clear day, VFR conditions at Palo Alto Airport. You're ready to taxi."
**System Message:** "Begin your radio call to ATC..."

**Pilot (type):** "Palo Alto Ground, Cessna 12345, at the ramp with information Alpha, ready to taxi for runway 31, VFR to the south."

*(Show loading indicator - AI is processing)*

**ATC Response:** "Cessna 12345, Palo Alto Ground, taxi to runway 31 via Alpha, hold short of runway 31."

**Pilot (type):** "Taxi to runway 31 via Alpha, hold short, Cessna 12345."

**ATC Response:** "Cessna 345, readback correct. Monitor tower on 118.6."

**Pilot (type):** "118.6, Cessna 345."

**Talking points:**
- "AI-powered ATC responds realistically to pilot communications"
- "Chat interface shows message history like real radio exchanges"
- "Pilot messages on right (blue), ATC on left (white), system in center"
- "Loading indicator shows when AI is generating response"
- "Proper radio phraseology is learned through practice"

#### 3c. Show Another Scenario (Optional - 30 seconds)
**Demo steps:**
1. Tap "Change"
2. Select "Emergency Declaration"
3. Show more complex scenario with urgent situation

**Pilot:** "Mayday, Mayday, Mayday, San Francisco Tower, Cessna 12345, engine failure at 3,000 feet, 5 miles south of the field, requesting immediate landing."

---

### 4. Navigation & Features Overview (30 seconds)
**Demo steps:**
1. Tap through bottom navigation tabs
2. Show Settings (placeholder for API keys)
3. Return to Home

**Talking points:**
- "Bottom navigation for easy access to all features"
- "Settings will allow API key configuration"
- "Future features: Airport database search, Knowledge base (FAR/AIM)"

---

### 5. Technical Highlights (30 seconds)
**Talking points:**
- "Built with Kotlin and modern Android architecture (MVVM, Room, Coroutines)"
- "Integrates with OpenRouter API for AI responses"
- "Local database with real airport data (runways, frequencies, weather)"
- "Traffic simulation for realistic practice scenarios"
- "Session tracking for progress analytics"

---

## Demo Reset Commands
To reset the app for another demo:
```bash
adb shell pm clear com.example.flightdeck
adb shell am start -n com.example.flightdeck/.MainActivity
```

## Screenshot Locations
Key moments to capture:
1. Home dashboard
2. ATIS viewer with KPAO data
3. ATC simulator scenario selection
4. Chat interface with pilot/ATC exchange
5. Emergency scenario

---

## Expected Questions

**Q: Does it work offline?**
A: App works offline with local database. AI responses require internet for OpenRouter API.

**Q: Is the weather data real?**
A: Yes, integrates with Aviation Weather Service API for real METAR data.

**Q: How accurate is the ATC AI?**
A: Uses aviation-specific prompts and context. Trained on proper phraseology patterns.

**Q: Can you add custom scenarios?**
A: Future feature - currently ships with 5 core scenarios covering essential operations.

**Q: Does it evaluate pilot communications?**
A: Yes, ATCRepository has evaluateATCCommunication() method for scoring and feedback.

**Q: What airports are included?**
A: Demo includes 5 California Bay Area airports. Full version will have comprehensive database.

---

## Demo Tips
- Keep emulator volume on for added effect (could add audio later)
- Have internet connection active for AI responses
- Pre-warm the app before demo (open once, let database initialize)
- Use portrait orientation for best viewing
- Have backup screenshots if live demo has issues
