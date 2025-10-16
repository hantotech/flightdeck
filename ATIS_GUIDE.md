# ATIS/AWOS System Guide

## Overview

FlightDeck includes a complete ATIS (Automatic Terminal Information Service) and AWOS/ASOS (Automated Weather Observing System) simulation that generates realistic weather broadcasts for pre-flight and pre-landing procedures.

---

## 🎯 Why ATIS/AWOS Matters

### Regulatory Requirement

**14 CFR 91.103 - Preflight Action** requires pilots to "become familiar with all available information concerning that flight," which includes:
- Current weather reports (METAR)
- Weather forecasts (TAF)
- Runway information
- Altimeter settings

### Checklist Integration

ATIS/AWOS listening is a **critical checklist item** for:

1. **Pre-Flight Preparation**
   - Listen to departure airport ATIS/AWOS
   - Note information code (Alpha, Bravo, Charlie, etc.)
   - Record altimeter setting
   - Note active runway and wind
   - Check for any NOTAMs or special instructions

2. **Before Landing**
   - Listen to destination ATIS at least 25 miles out
   - Update altimeter setting
   - Confirm expected runway
   - Advise tower you have current information code

3. **Initial Contact with ATC**
   - "Palo Alto Tower, Skyhawk N12345, **with information Alpha**"
   - Tells ATC you have current weather and runway information

---

## 📻 ATIS vs AWOS vs ASOS

### ATIS (Automatic Terminal Information Service)
- **Used at**: Towered airports (Class B, C, D)
- **Frequency**: Dedicated ATIS frequency (usually 128.x MHz)
- **Content**: Weather + runway info + NOTAMs + instructions
- **Updated**: Hourly or when conditions change significantly
- **Information Code**: Changes each update (Alpha, Bravo, Charlie...)

### AWOS (Automated Weather Observing System)
- **Used at**: Non-towered airports and some smaller towered airports
- **Frequency**: Published frequency (varies by airport)
- **Content**: Weather only (no runway/NOTAM info)
- **Updated**: Continuous, real-time

### ASOS (Automated Surface Observing System)
- **Used at**: Similar to AWOS, newer system
- **Frequency**: Published frequency
- **Content**: Weather only, more sophisticated sensors
- **Updated**: Continuous

---

## 🎙️ Sample ATIS Readback

### Example: KPAO (Palo Alto Airport)

**Full ATIS Broadcast:**

> "Palo Alto Airport information Alpha. Time one eight five six Zulu. Wind zero niner zero at eight. Visibility one zero or greater. Sky few five thousand, broken two five thousand. Temperature two three, dewpoint one four. Altimeter three zero one two. Runway three one in use. Notices to airmen: Airport is uncontrolled. Pilots read back all runway hold short instructions. Advise on initial contact you have information Alpha."

**Breakdown:**
- **Information Code**: Alpha
- **Time**: 1856Z (6:56 PM UTC)
- **Wind**: 090° at 8 knots
- **Visibility**: 10+ statute miles
- **Ceiling**: Few clouds at 5,000 ft, broken at 25,000 ft
- **Temp/Dewpoint**: 23°C / 14°C
- **Altimeter**: 30.12 inHg
- **Active Runway**: 31
- **NOTAMs**: Airport uncontrolled
- **Instruction**: Advise on initial contact

---

## 💻 Implementation

### Data Model

```kotlin
@Entity(tableName = "atis_broadcasts")
data class ATISBroadcast(
    val airportIcao: String,
    val broadcastType: BroadcastType,        // ATIS, AWOS, ASOS
    val informationCode: String,             // "Alpha", "Bravo", etc.
    val observationTime: String,             // "1856 Zulu"
    val weatherReport: WeatherReport,
    val activeRunway: String?,               // "Runway 31"
    val approachesInUse: String?,            // "ILS Runway 31 in use"
    val notams: List<String>,
    val remarks: String?,
    val frequency: Double,                   // MHz
    val timestamp: Long,
    val isActive: Boolean
)
```

### Information Codes

ATIS uses the phonetic alphabet, cycling through every hour:

```kotlin
Alpha → Bravo → Charlie → Delta → Echo → Foxtrot → Golf → Hotel
→ India → Juliet → Kilo → Lima → Mike → November → Oscar → Papa
→ Quebec → Romeo → Sierra → Tango → Uniform → Victor → Whiskey
→ X-ray → Yankee → Zulu → Alpha (repeat)
```

---

## 📝 Usage Examples

### Get Current ATIS

```kotlin
val atisRepo = ATISRepository.getInstance(atisDao, airportRepo, weatherService)

// Get current ATIS for KPAO
val atisResult = atisRepo.getCurrentATIS("KPAO")
atisResult.onSuccess { atis ->
    println("Information ${atis.informationCode}")
    println("Wind: ${atis.weatherReport.windDirection}°/${atis.weatherReport.windSpeed}kt")
    println("Altimeter: ${atis.weatherReport.altimeter}")
    println("Active Runway: ${atis.activeRunway}")
}
```

### Get ATIS Readback (Voice-Like Text)

```kotlin
// Get full ATIS readback as pilot would hear it
val readback = atisRepo.getATISReadback("KPAO")
readback.onSuccess { text ->
    // Display to user or use for text-to-speech
    println(text)
}

// Output:
// "Palo Alto Airport information Alpha. Time 1856 Zulu.
//  Wind zero niner zero at eight. Visibility one zero or greater..."
```

### Check ATIS Before Flight

```kotlin
// Pre-flight checklist integration
suspend fun completePreFlightWeatherCheck(airportIcao: String): ChecklistResult {
    // Get ATIS
    val atis = atisRepo.getCurrentATIS(airportIcao).getOrThrow()

    // Verify conditions are acceptable
    val weather = atis.weatherReport
    val isSafeToFly = when {
        weather.flightCategory == FlightCategory.LIFR -> false
        weather.windSpeed > 20 -> false // Exceeds student limits
        weather.visibility < 3.0 -> false // Below VFR minimums
        else -> true
    }

    return ChecklistResult(
        completed = true,
        safe = isSafeToFly,
        notes = "ATIS Information ${atis.informationCode} - " +
                "Altimeter ${weather.altimeter}, Runway ${atis.activeRunway}"
    )
}
```

### Advise ATC You Have ATIS

```kotlin
// When calling tower, include information code
fun generateInitialContact(
    airport: String,
    callsign: String,
    atisCode: String,
    position: String
): String {
    return "$airport Tower, $callsign, $position, with information $atisCode"
}

// Example usage
val callsign = "Skyhawk N12345"
val atisCode = atisRepo.getATISInformationCode("KPAO") ?: "Alpha"
val message = generateInitialContact("Palo Alto", callsign, atisCode, "downwind runway 31")

// Result: "Palo Alto Tower, Skyhawk N12345, downwind runway 31, with information Alpha"
```

### Observe ATIS Updates

```kotlin
// Watch for ATIS changes in real-time
viewModelScope.launch {
    atisRepo.observeCurrentATIS("KPAO").collect { atis ->
        if (atis != null) {
            // Update UI with new ATIS
            updateATISDisplay(atis)

            // Alert pilot if information code changed
            if (lastATISCode != null && lastATISCode != atis.informationCode) {
                showNotification("New ATIS: Information ${atis.informationCode}")
            }
        }
    }
}
```

### Force ATIS Refresh

```kotlin
// Manually refresh ATIS (updates information code)
val newATIS = atisRepo.refreshATIS("KPAO")
```

---

## 🔧 Active Runway Determination

The system automatically determines the active runway based on wind direction:

```kotlin
/**
 * Pilots use the runway most aligned with wind direction
 * to minimize crosswind component
 */
fun determineActiveRunway(runways: List<Runway>, windDirection: Int): String? {
    // Find runway with heading closest to wind
    // Example: Wind 090° would favor Runway 09 over Runway 27

    // Returns: "09" or "27" based on best alignment
}
```

**Example:**
- **Airport**: KPAO (Runways 13/31)
- **Wind**: 310° at 8 knots
- **Active Runway**: 31 (heading 310°)
- **Reasoning**: Runway 31 aligns with wind, minimizing crosswind

---

## ✅ Checklist Integration

### Pre-Flight Interior Checklist

```
1. Weather Briefing .......................... OBTAINED
2. ATIS/AWOS - Listen ....................... INFORMATION [CODE]
3. Altimeter - Set .......................... [SETTING]
4. NOTAMs - Review .......................... CHECKED
```

### Before Landing Checklist

```
1. Destination ATIS - Listen ................ INFORMATION [CODE]
2. Altimeter - Reset ........................ [SETTING]
3. Landing Clearance - Obtain ............... RECEIVED
```

### Sample Checklist Flow

```kotlin
// Pre-flight checklist with ATIS
val preFlight = ChecklistSamples.getPreFlightChecklist()

// Items include:
// 1. Weather Briefing (METAR/TAF)
// 2. ATIS/AWOS - Listen
// 3. Altimeter - Set
// 4. NOTAMs - Review
// ... additional items
```

---

## 🎓 Training Scenarios

### Scenario 1: Obtaining Departure ATIS

**Student Task:**
1. Tune ATIS frequency (from airport data)
2. Listen to ATIS broadcast
3. Copy information:
   - Information Code: _____
   - Wind: _____
   - Visibility: _____
   - Ceiling: _____
   - Temperature: _____
   - Altimeter: _____
   - Active Runway: _____
4. Set altimeter
5. Verify field elevation

**App Provides:**
- Realistic ATIS readback
- Step-by-step guidance
- Verification of copied info

### Scenario 2: ATIS Changed During Taxi

**Situation:**
- Student copied Information Alpha during pre-flight
- While taxiing, ATIS updates to Information Bravo
- Wind increased from 8 to 15 knots

**Learning Points:**
- Always get current ATIS before calling tower
- Weather can change quickly
- Be prepared to update altimeter setting

### Scenario 3: Destination ATIS En Route

**Student Task:**
1. Tune destination ATIS 25 miles out
2. Copy information code and runway
3. Reset altimeter to destination setting
4. Plan traffic pattern entry based on active runway
5. Call tower: "...with information [Code]"

---

## 🌤️ Weather Integration

### Automatic ATIS Generation

ATIS broadcasts are generated from real weather data:

```kotlin
// Fetch current weather
val weather = weatherService.getMetar("KPAO").getOrThrow()

// Generate ATIS with weather
val atis = SampleATISGenerator.generateSampleATIS(
    airport = airport,
    weather = weather,
    activeRunway = determineActiveRunway(runways, weather.windDirection)
)

// ATIS automatically includes:
// - Current METAR data
// - Computed flight category (VFR/MVFR/IFR/LIFR)
// - Sky conditions
// - Visibility
// - Active runway based on wind
```

### Flight Category in ATIS

ATIS remarks include flight category when relevant:

- **VFR**: No remark (conditions good)
- **MVFR**: "Marginal VFR"
- **IFR**: "IFR conditions"
- **LIFR**: "Low IFR conditions"

---

## 📊 ATIS Frequency Database

Each airport has its ATIS/AWOS frequency stored:

```kotlin
// Get ATIS frequency for airport
val frequency = airportRepo.getWeatherFrequency("KPAO")
// Returns: Frequency(type=ATIS, frequency=128.05, description="Palo Alto ATIS")

// Or get all communication frequencies
val allFreqs = airportRepo.getCommunicationFrequencies("KPAO")
// Returns: Tower, Ground, ATIS, etc.
```

---

## 🎯 Best Practices

### For Students

1. **Always Listen Before Flight**
   - Part of required pre-flight action (FAR 91.103)
   - Ensures you have current conditions
   - Verifies expected runway

2. **Copy ATIS Information**
   - Write down information code
   - Record altimeter setting
   - Note active runway
   - Check NOTAMs

3. **Verify Currency**
   - ATIS updates hourly minimum
   - Check information code hasn't changed
   - Re-listen if more than 30 minutes old

4. **Advise ATC**
   - Always include "with information [Code]" on initial contact
   - If you don't have current ATIS, say so

### For CFIs

1. **Emphasize Importance**
   - Legal requirement, not optional
   - Safety critical information
   - Professional habit to develop

2. **Practice ATIS Copying**
   - Have students write down all elements
   - Verify they understand each component
   - Teach proper readback phraseology

3. **Scenario Training**
   - Practice with outdated ATIS
   - Simulate changing conditions
   - Teach when to re-listen

---

## 🔄 ATIS Lifecycle

```
1. Weather changes or hour passes
2. System generates new ATIS
3. Information code advances (Alpha → Bravo)
4. Old ATIS marked inactive
5. New ATIS broadcast begins
6. Pilots advised to get new information
```

---

## 📖 Related Documentation

- [AIRPORT_TRAFFIC_GUIDE.md](AIRPORT_TRAFFIC_GUIDE.md) - Airport database
- [HYBRID_AI_GUIDE.md](HYBRID_AI_GUIDE.md) - AI integration
- [API_EXAMPLES.md](API_EXAMPLES.md) - Code examples

---

## ⚠️ Important Notes

1. **Training Simulation**: ATIS broadcasts are simulated for training, not real-time aviation data
2. **Regulatory Compliance**: Always verify actual weather with official sources for real flights
3. **Currency**: Information codes cycle for realism but may not match actual airport schedules
4. **Accuracy**: Weather data is pulled from Aviation Weather Center when available

---

**Questions or Suggestions?**
Report at: https://github.com/hantotech/flightdeck/issues
