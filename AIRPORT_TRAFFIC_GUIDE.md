# Airport Data & Traffic Simulation Guide

## Overview

FlightDeck now includes comprehensive airport information and realistic traffic simulation for enhanced ATC training. This guide covers how the system works and how to use these features.

---

## 🛫 Airport Database System

### Features

- **Comprehensive Airport Data**: ICAO/IATA codes, coordinates, elevation, airspace class
- **Runway Information**: Length, width, surface type, headings, lighting
- **Communication Frequencies**: Tower, Ground, ATIS, CTAF, Approach/Departure
- **Smart Caching**: Database storage with optional online lookup
- **Proximity Search**: Find airports within a radius using Haversine formula

### Data Source

Airport data is sourced from **OurAirports** (Public Domain):
- 60,000+ airports worldwide
- Regularly updated runway and frequency data
- Free to use for commercial applications

### Database Schema

#### Airport Entity
```kotlin
@Entity(tableName = "airports")
data class Airport(
    val icao: String,                 // Primary key: "KPAO", "KSFO"
    val iata: String?,                // "SFO", "PAO"
    val name: String,                 // "San Francisco International"
    val city: String,
    val state: String?,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Int,               // feet MSL
    val airspaceClass: AirspaceClass?, // A, B, C, D, E, G
    val towerControlled: Boolean,
    val type: AirportType             // SMALL, MEDIUM, LARGE, HELIPORT
)
```

#### Runway Entity
```kotlin
@Entity(tableName = "runways")
data class Runway(
    val airportIcao: String,
    val identifier: String,           // "09/27"
    val length: Int,                  // feet
    val width: Int,                   // feet
    val surface: SurfaceType,         // ASPHALT, CONCRETE, GRASS, etc.
    val lighted: Boolean,
    val leIdentifier: String,         // "09"
    val leHeading: Int?,              // magnetic degrees
    val heIdentifier: String,         // "27"
    val heHeading: Int?
)
```

#### Frequency Entity
```kotlin
@Entity(tableName = "frequencies")
data class Frequency(
    val airportIcao: String,
    val type: FrequencyType,          // TOWER, GROUND, ATIS, CTAF, etc.
    val description: String,
    val frequency: Double             // MHz (e.g., 118.3)
)
```

### Usage Examples

#### Get Complete Airport Data
```kotlin
val airportRepo = AirportRepository.getInstance(airportDao)

// Get airport with runways and frequencies
val result = airportRepo.getAirportByIcao("KPAO")
result.onSuccess { data ->
    println("Airport: ${data.airport.name}")
    println("Elevation: ${data.airport.elevation} ft MSL")
    println("Runways: ${data.runways.size}")
    data.runways.forEach { runway ->
        println("  - Runway ${runway.identifier}: ${runway.length} ft")
    }
    data.frequencies.forEach { freq ->
        println("  - ${freq.type}: ${freq.frequency}")
    }
}
```

#### Search for Airports
```kotlin
// Search by ICAO, IATA, name, or city
val airports = airportRepo.searchAirports("San Francisco", limit = 10)

// Get towered airports only
val toweredAirports = airportRepo.getToweredAirports()

// Get airports by state
val californiaAirports = airportRepo.getAirportsByState("CA")
```

#### Find Nearby Airports
```kotlin
// Find airports within 50 miles of coordinates
val nearby = airportRepo.getAirportsNearby(
    latitude = 37.5,
    longitude = -122.0,
    radiusMiles = 50.0
)

nearby.forEach { airportWithDistance ->
    println("${airportWithDistance.airport.icao}: ${airportWithDistance.distanceMiles} miles")
}
```

#### Get Specific Information
```kotlin
// Get longest runway
val longestRunway = airportRepo.getLongestRunway("KSFO")
println("Longest: ${longestRunway?.identifier} - ${longestRunway?.length} ft")

// Check runway requirements
val hasSufficientRunway = airportRepo.hasRunwayLongerThan("KPAO", 2400)

// Get Tower frequency
val tower = airportRepo.getFrequency("KPAO", FrequencyType.TOWER)
println("Tower: ${tower?.frequency}")

// Get ATIS/AWOS
val weather = airportRepo.getWeatherFrequency("KPAO")

// Get CTAF for non-towered
val ctaf = airportRepo.getCTAF("KPAO")
```

---

## 🚁 Traffic Simulation System

### Features

- **Realistic Traffic Generation**: Callsigns, aircraft types, positions
- **Dynamic Density**: Adjusts based on airport class and scenario difficulty
- **Traffic Progression**: Aircraft move through logical positions (taxi → runway → departure)
- **Traffic Advisories**: Context-aware advisories based on user position
- **Conflict Scenarios**: Educational traffic conflict situations

### Traffic Entities

#### SimulatedTraffic
```kotlin
@Entity(tableName = "simulated_traffic")
data class SimulatedTraffic(
    val sessionId: Long,
    val aircraftCallsign: String,     // "Skyhawk N12345"
    val aircraftType: String,         // "Skyhawk", "Citation"
    val airportIcao: String,
    val currentPosition: TrafficPosition,
    val intent: TrafficIntent,
    val altitude: Int,                // feet MSL
    val speed: Int,                   // knots
    val heading: Int,                 // magnetic degrees
    val flightRules: FlightRules      // VFR, IFR, SVFR
)
```

#### Traffic Positions
```kotlin
enum class TrafficPosition {
    ON_GROUND_RAMP,
    ON_GROUND_TAXIWAY,
    ON_GROUND_RUNWAY_HOLDING,
    ON_GROUND_RUNWAY_ACTIVE,
    DEPARTING,
    IN_PATTERN_DOWNWIND,
    IN_PATTERN_BASE,
    IN_PATTERN_FINAL,
    APPROACHING,
    LANDED_ROLLOUT
}
```

### Traffic Density Levels

| Density | Aircraft per 10 min | Typical Airports |
|---------|---------------------|------------------|
| **NONE** | 0 | Remote, closed airports |
| **LIGHT** | 1-3 | Class G, small uncontrolled |
| **MODERATE** | 4-8 | Class D, medium towered |
| **BUSY** | 9-15 | Class C, large regional |
| **VERY_BUSY** | 16+ | Class B, major hubs |

### Aircraft Mix by Airport Type

**Class B (Major Hub)**
- 40% Single-engine GA
- 20% Multi-engine GA
- 35% Jets
- 5% Helicopters

**Class C/D (Regional)**
- 60-75% Single-engine GA
- 15-20% Multi-engine GA
- 5-15% Jets
- 5% Helicopters

**Uncontrolled**
- 85% Single-engine GA
- 10% Multi-engine GA
- 0% Jets
- 5% Helicopters

### Usage Examples

#### Start Traffic Simulation
```kotlin
val trafficSim = TrafficSimulator.getInstance(trafficDao, airportRepo)

// Get airport data
val airport = airportRepo.getAirportByIcao("KPAO").getOrThrow().airport

// Start simulation for ATC practice session
trafficSim.startSimulation(
    sessionId = sessionId,
    airport = airport,
    density = TrafficDensity.MODERATE,  // or let it auto-determine
    durationMinutes = 30
)
```

#### Observe Active Traffic
```kotlin
// Get current snapshot
val traffic = trafficSim.getActiveTraffic(sessionId)
traffic.forEach { aircraft ->
    println("${aircraft.aircraftCallsign} - ${aircraft.currentPosition}")
}

// Observe real-time updates
trafficSim.observeActiveTraffic(sessionId).collect { activeTraffic ->
    // UI updates as traffic changes
    updateTrafficDisplay(activeTraffic)
}
```

#### Get Traffic Advisories
```kotlin
// Get relevant traffic based on user position
val advisories = trafficSim.getTrafficAdvisories(
    sessionId = sessionId,
    userPosition = TrafficPosition.ON_GROUND_RUNWAY_HOLDING
)

advisories.forEach { advisory ->
    println("Traffic: $advisory")
}

// Example output:
// "Skyhawk N345 3-mile final"
// "Citation N500XR holding short"
```

#### Generate Traffic Events
```kotlin
// Get traffic events for scenario realism
val events = trafficSim.generateTrafficEvents(
    sessionId = sessionId,
    includeConflicts = true  // Educational conflict scenarios
)

events.forEach { event ->
    when (event.eventType) {
        TrafficEventType.AIRCRAFT_ON_FINAL ->
            println("⚠️ ${event.description}")

        TrafficEventType.CONFLICT_POTENTIAL ->
            println("⚠️ Traffic conflict: ${event.description}")

        else ->
            println("ℹ️ ${event.description}")
    }
}
```

#### Add Traffic Manually
```kotlin
// Add more traffic during session
val newTraffic = trafficSim.addTraffic(
    sessionId = sessionId,
    airport = airport,
    count = 2
)
```

#### Advance Simulation
```kotlin
// Move traffic to next logical positions
trafficSim.advanceSimulation(sessionId)

// Traffic progression example:
// ON_GROUND_TAXIWAY → ON_GROUND_RUNWAY_HOLDING →
// ON_GROUND_RUNWAY_ACTIVE → DEPARTING → (removed)
```

---

## 🎮 Integration with ATC Practice

### Automatic Integration

The `ATCRepository` automatically integrates airport and traffic data:

```kotlin
val atcRepo = ATCRepository(atcDao, airportRepo, trafficSim)

// When starting a practice session:
val sessionId = atcRepo.startPracticeSession(scenarioId)

// Automatically:
// 1. Retrieves airport data for scenario
// 2. Starts traffic simulation with appropriate density
// 3. Provides context to AI for realistic responses
```

### Enhanced ATC Context

ATC responses now include:

1. **Airport Information**
   - Runway configurations
   - Active runways
   - Frequencies

2. **Traffic Information**
   - Aircraft on final approach
   - Aircraft holding short
   - Aircraft in the pattern
   - Aircraft taxiing

3. **Realistic Clearances**
   - "Skyhawk N12345, Palo Alto Tower, runway 31, cleared for takeoff"
   - "Traffic is a Citation on 3-mile final"
   - "Caution wake turbulence, departing Citation"

### Example: Enhanced ATC Interaction

```kotlin
// Send pilot message with automatic context
val response = atcRepo.sendPilotMessage(
    sessionId = sessionId,
    scenario = scenario,
    pilotMessage = "Palo Alto Tower, Skyhawk N12345, ready for departure",
    userPosition = TrafficPosition.ON_GROUND_RUNWAY_HOLDING
)

response.onSuccess { atcResponse ->
    // AI response includes traffic awareness:
    // "Skyhawk N12345, Palo Alto Tower, traffic is a Cessna on
    //  2-mile final. Runway 31, cleared for takeoff."
}
```

---

## 📊 Traffic Realism Features

### Realistic Callsigns

Generated using actual aircraft type names:

- **Single-engine**: "Skyhawk N12345", "Cherokee N789", "Cirrus N42SR"
- **Multi-engine**: "Baron N234AB", "Seminole N456"
- **Jets**: "Citation N500XR", "Gulfstream N7GF"
- **Helicopters**: "Robinson N1RH"

### Aircraft Performance Characteristics

Traffic has realistic performance:

- **Cruise speeds**: 60-180 knots (type-appropriate)
- **Altitudes**: Pattern altitude to cruise levels
- **Flight rules**: 80% VFR, 20% IFR (adjustable)

### Position-Aware Traffic

Traffic relevance based on user position:

| User Position | Relevant Traffic |
|---------------|------------------|
| Holding short runway | Traffic on final, on runway, departing |
| On final approach | Other pattern traffic, runway traffic |
| Taxiing | Other taxiing aircraft, runway crossings |
| Ramp | (Most traffic not relevant) |

---

## 🔧 Database Population

### Initial Setup

Populate the database with curated airports:

```kotlin
// Create initializer for common training airports
class AirportDatabaseInitializer(
    private val airportDao: AirportDao
) {
    suspend fun initializeCommonAirports() {
        val airports = listOf(
            Airport(
                icao = "KPAO",
                iata = "PAO",
                name = "Palo Alto Airport",
                city = "Palo Alto",
                state = "CA",
                country = "US",
                latitude = 37.461,
                longitude = -122.115,
                elevation = 4,
                airspaceClass = AirspaceClass.E,
                towerControlled = true,
                type = AirportType.SMALL_AIRPORT
            ),
            // Add more airports...
        )

        airportDao.insertAirports(airports)
    }

    suspend fun initializeRunways() {
        val runways = listOf(
            Runway(
                airportIcao = "KPAO",
                identifier = "13/31",
                length = 2443,
                width = 75,
                surface = SurfaceType.ASPHALT,
                lighted = true,
                leIdentifier = "13",
                leHeading = 130,
                heIdentifier = "31",
                heHeading = 310
            )
        )

        airportDao.insertRunways(runways)
    }

    suspend fun initializeFrequencies() {
        val frequencies = listOf(
            Frequency(
                airportIcao = "KPAO",
                type = FrequencyType.TOWER,
                description = "Palo Alto Tower",
                frequency = 118.6
            ),
            Frequency(
                airportIcao = "KPAO",
                type = FrequencyType.ATIS,
                description = "Palo Alto ATIS",
                frequency = 128.05
            )
        )

        airportDao.insertFrequencies(frequencies)
    }
}
```

---

## 🎯 Best Practices

### 1. Traffic Density Selection

```kotlin
// Beginner students: Light traffic
trafficSim.startSimulation(
    airport = airport,
    density = TrafficDensity.LIGHT
)

// Advanced training: Busy Class C
trafficSim.startSimulation(
    airport = airport,
    density = TrafficDensity.BUSY
)
```

### 2. Cleanup Old Traffic

```kotlin
// Periodically clean up old inactive traffic
viewModelScope.launch {
    trafficSim.cleanupOldTraffic()  // Removes traffic older than 24 hours
}
```

### 3. Traffic Progression

```kotlin
// Advance traffic every 30-60 seconds for realism
viewModelScope.launch {
    while (sessionActive) {
        delay(45.seconds)
        trafficSim.advanceSimulation(sessionId)
    }
}
```

### 4. Airport Data Caching

```kotlin
// Always check cache first
val airport = airportRepo.getAirportByIcao(icao)

// Cache hit: Instant response
// Cache miss: Error (online lookup not yet implemented)

// Pre-populate important airports during app initialization
```

---

## 🚀 Future Enhancements

### Planned Features

1. **Online Airport Lookup**: Fetch any airport on-demand from OurAirports API
2. **Airport Charts**: Integrate taxi diagrams and approach plates
3. **Dynamic Weather**: Affect traffic patterns based on conditions
4. **Runway Selection**: AI chooses active runway based on wind
5. **Traffic Conflicts**: More sophisticated conflict detection
6. **Touch-and-Go Traffic**: Aircraft staying in the pattern
7. **Emergency Traffic**: Practice handling emergency scenarios with other traffic

### Data Sources Under Consideration

- **FAA NASR Subscription**: Official US airport data (updated every 28 days)
- **AirportDB.io API**: Real-time online lookup
- **SkyVector**: Airport diagrams and charts

---

## 📖 Related Documentation

- [HYBRID_AI_GUIDE.md](HYBRID_AI_GUIDE.md) - AI integration details
- [API_EXAMPLES.md](API_EXAMPLES.md) - Code usage examples
- [PROJECT_STATUS.md](PROJECT_STATUS.md) - Current implementation status

---

## ⚠️ Important Notes

1. **Training Purposes Only**: Traffic simulation is NOT real-time ADS-B data
2. **Database Accuracy**: Airport data may not reflect recent changes (verify with official sources)
3. **Regulation Compliance**: Always verify critical information with current FAA publications
4. **Performance**: Large airport databases may require pagination or lazy loading

---

**Questions or Issues?**
Report at: https://github.com/hantotech/flightdeck/issues
