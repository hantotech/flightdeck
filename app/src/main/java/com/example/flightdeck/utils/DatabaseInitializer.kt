package com.example.flightdeck.utils

import android.content.Context
import com.example.flightdeck.data.knowledge.AIMContent
import com.example.flightdeck.data.knowledge.AviationDocument
import com.example.flightdeck.data.knowledge.AviationKnowledge
import com.example.flightdeck.data.knowledge.FARContent
import com.example.flightdeck.data.local.FlightDeckDatabase
import com.example.flightdeck.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Initializes FlightDeck database with sample data
 * Run once on first app launch
 */
object DatabaseInitializer {

    /**
     * Initialize all database tables with sample data
     */
    suspend fun initialize(context: Context) = withContext(Dispatchers.IO) {
        val db = FlightDeckDatabase.getDatabase(context)

        // Check if already initialized
        if (isInitialized(db)) {
            return@withContext
        }

        // Initialize each table
        initializeAirports(db)
        initializeKnowledgeBase(db)
        initializeATCScenarios(db)
        initializeAircraft(db)
        initializeLogbookData(db)
    }

    private suspend fun isInitialized(db: FlightDeckDatabase): Boolean {
        return db.airportDao().getAirportCount() > 0
    }

    /**
     * Initialize airports with FREE TIER airports (30 major US/Canada training locations)
     * Premium tier unlocks 20,000+ additional airports via OurAirports CSV
     */
    private suspend fun initializeAirports(db: FlightDeckDatabase) {
        val airportDao = db.airportDao()

        val airports = listOf(
            // ========== CALIFORNIA (5) ==========
            Airport(
                icao = "KPAO",
                iata = "PAO",
                name = "Palo Alto Airport",
                city = "Palo Alto",
                state = "CA",
                country = "USA",
                latitude = 37.461,
                longitude = -122.115,
                elevation = 4,
                airspaceClass = AirspaceClass.E,
                towerControlled = true,
                type = AirportType.SMALL_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KSFO",
                iata = "SFO",
                name = "San Francisco International",
                city = "San Francisco",
                state = "CA",
                country = "USA",
                latitude = 37.619,
                longitude = -122.375,
                elevation = 13,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KSJC",
                iata = "SJC",
                name = "Norman Y. Mineta San Jose International",
                city = "San Jose",
                state = "CA",
                country = "USA",
                latitude = 37.363,
                longitude = -121.929,
                elevation = 62,
                airspaceClass = AirspaceClass.C,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KSQL",
                iata = "SQL",
                name = "San Carlos Airport",
                city = "San Carlos",
                state = "CA",
                country = "USA",
                latitude = 37.512,
                longitude = -122.250,
                elevation = 5,
                airspaceClass = AirspaceClass.D,
                towerControlled = true,
                type = AirportType.SMALL_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KLAX",
                iata = "LAX",
                name = "Los Angeles International",
                city = "Los Angeles",
                state = "CA",
                country = "USA",
                latitude = 33.9425,
                longitude = -118.408,
                elevation = 125,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),

            // ========== PACIFIC NORTHWEST (3) ==========
            Airport(
                icao = "KSEA",
                iata = "SEA",
                name = "Seattle-Tacoma International",
                city = "Seattle",
                state = "WA",
                country = "USA",
                latitude = 47.449,
                longitude = -122.309,
                elevation = 433,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KPDX",
                iata = "PDX",
                name = "Portland International",
                city = "Portland",
                state = "OR",
                country = "USA",
                latitude = 45.5887,
                longitude = -122.5975,
                elevation = 31,
                airspaceClass = AirspaceClass.C,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KBFI",
                iata = "BFI",
                name = "Boeing Field/King County International",
                city = "Seattle",
                state = "WA",
                country = "USA",
                latitude = 47.53,
                longitude = -122.302,
                elevation = 21,
                airspaceClass = AirspaceClass.D,
                towerControlled = true,
                type = AirportType.MEDIUM_AIRPORT,
                isPremium = false
            ),

            // ========== SOUTHWEST (3) ==========
            Airport(
                icao = "KPHX",
                iata = "PHX",
                name = "Phoenix Sky Harbor International",
                city = "Phoenix",
                state = "AZ",
                country = "USA",
                latitude = 33.4343,
                longitude = -112.0116,
                elevation = 1135,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KLAS",
                iata = "LAS",
                name = "Las Vegas McCarran International",
                city = "Las Vegas",
                state = "NV",
                country = "USA",
                latitude = 36.0840,
                longitude = -115.1537,
                elevation = 2181,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KDEN",
                iata = "DEN",
                name = "Denver International",
                city = "Denver",
                state = "CO",
                country = "USA",
                latitude = 39.8617,
                longitude = -104.6731,
                elevation = 5434,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),

            // ========== TEXAS (4) ==========
            Airport(
                icao = "KDFW",
                iata = "DFW",
                name = "Dallas/Fort Worth International",
                city = "Dallas",
                state = "TX",
                country = "USA",
                latitude = 32.8968,
                longitude = -97.0380,
                elevation = 607,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KIAH",
                iata = "IAH",
                name = "George Bush Intercontinental",
                city = "Houston",
                state = "TX",
                country = "USA",
                latitude = 29.9902,
                longitude = -95.3368,
                elevation = 97,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KAUS",
                iata = "AUS",
                name = "Austin-Bergstrom International",
                city = "Austin",
                state = "TX",
                country = "USA",
                latitude = 30.1945,
                longitude = -97.6699,
                elevation = 542,
                airspaceClass = AirspaceClass.C,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KSAT",
                iata = "SAT",
                name = "San Antonio International",
                city = "San Antonio",
                state = "TX",
                country = "USA",
                latitude = 29.5337,
                longitude = -98.4698,
                elevation = 809,
                airspaceClass = AirspaceClass.C,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),

            // ========== MIDWEST (4) ==========
            Airport(
                icao = "KORD",
                iata = "ORD",
                name = "Chicago O'Hare International",
                city = "Chicago",
                state = "IL",
                country = "USA",
                latitude = 41.9786,
                longitude = -87.9048,
                elevation = 672,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KMSP",
                iata = "MSP",
                name = "Minneapolis-St Paul International",
                city = "Minneapolis",
                state = "MN",
                country = "USA",
                latitude = 44.8848,
                longitude = -93.2223,
                elevation = 841,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KDTW",
                iata = "DTW",
                name = "Detroit Metropolitan Wayne County",
                city = "Detroit",
                state = "MI",
                country = "USA",
                latitude = 42.2124,
                longitude = -83.3534,
                elevation = 645,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KCLE",
                iata = "CLE",
                name = "Cleveland Hopkins International",
                city = "Cleveland",
                state = "OH",
                country = "USA",
                latitude = 41.4117,
                longitude = -81.8498,
                elevation = 791,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),

            // ========== SOUTHEAST (4) ==========
            Airport(
                icao = "KATL",
                iata = "ATL",
                name = "Hartsfield-Jackson Atlanta International",
                city = "Atlanta",
                state = "GA",
                country = "USA",
                latitude = 33.6367,
                longitude = -84.4281,
                elevation = 1026,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KMIA",
                iata = "MIA",
                name = "Miami International",
                city = "Miami",
                state = "FL",
                country = "USA",
                latitude = 25.7932,
                longitude = -80.2906,
                elevation = 8,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KMCO",
                iata = "MCO",
                name = "Orlando International",
                city = "Orlando",
                state = "FL",
                country = "USA",
                latitude = 28.4294,
                longitude = -81.3089,
                elevation = 96,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KTPA",
                iata = "TPA",
                name = "Tampa International",
                city = "Tampa",
                state = "FL",
                country = "USA",
                latitude = 27.9755,
                longitude = -82.5332,
                elevation = 26,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),

            // ========== NORTHEAST (4) ==========
            Airport(
                icao = "KJFK",
                iata = "JFK",
                name = "John F Kennedy International",
                city = "New York",
                state = "NY",
                country = "USA",
                latitude = 40.6398,
                longitude = -73.7789,
                elevation = 13,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KBOS",
                iata = "BOS",
                name = "Boston Logan International",
                city = "Boston",
                state = "MA",
                country = "USA",
                latitude = 42.3643,
                longitude = -71.0052,
                elevation = 20,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KDCA",
                iata = "DCA",
                name = "Ronald Reagan Washington National",
                city = "Washington",
                state = "DC",
                country = "USA",
                latitude = 38.8521,
                longitude = -77.0377,
                elevation = 15,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "KPHL",
                iata = "PHL",
                name = "Philadelphia International",
                city = "Philadelphia",
                state = "PA",
                country = "USA",
                latitude = 39.8719,
                longitude = -75.2411,
                elevation = 36,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),

            // ========== CANADA (3) ==========
            Airport(
                icao = "CYYZ",
                iata = "YYZ",
                name = "Toronto Pearson International",
                city = "Toronto",
                state = "ON",
                country = "Canada",
                latitude = 43.6777,
                longitude = -79.6248,
                elevation = 569,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "CYVR",
                iata = "YVR",
                name = "Vancouver International",
                city = "Vancouver",
                state = "BC",
                country = "Canada",
                latitude = 49.1947,
                longitude = -123.1815,
                elevation = 14,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            ),
            Airport(
                icao = "CYUL",
                iata = "YUL",
                name = "Montreal Pierre Elliott Trudeau International",
                city = "Montreal",
                state = "QC",
                country = "Canada",
                latitude = 45.4706,
                longitude = -73.7408,
                elevation = 118,
                airspaceClass = AirspaceClass.B,
                towerControlled = true,
                type = AirportType.LARGE_AIRPORT,
                isPremium = false
            )
        )

        airportDao.insertAirports(airports)

        // Add runways
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
                leLongitude = -122.115,
                leLatitude = 37.461,
                leElevation = 4,
                heIdentifier = "31",
                heHeading = 310,
                heLongitude = -122.115,
                heLatitude = 37.461,
                heElevation = 4
            ),
            Runway(
                airportIcao = "KSFO",
                identifier = "28L/10R",
                length = 11870,
                width = 200,
                surface = SurfaceType.ASPHALT,
                lighted = true,
                leIdentifier = "28L",
                leHeading = 280,
                leLongitude = -122.375,
                leLatitude = 37.619,
                leElevation = 13,
                heIdentifier = "10R",
                heHeading = 100,
                heLongitude = -122.375,
                heLatitude = 37.619,
                heElevation = 13
            )
        )

        airportDao.insertRunways(runways)

        // Add frequencies
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
            ),
            Frequency(
                airportIcao = "KSFO",
                type = FrequencyType.TOWER,
                description = "SFO Tower",
                frequency = 120.5
            ),
            Frequency(
                airportIcao = "KSFO",
                type = FrequencyType.GROUND,
                description = "SFO Ground",
                frequency = 121.8
            ),
            Frequency(
                airportIcao = "KSFO",
                type = FrequencyType.ATIS,
                description = "SFO ATIS",
                frequency = 118.85
            ),
            Frequency(
                airportIcao = "KHAF",
                type = FrequencyType.CTAF,
                description = "Half Moon Bay CTAF",
                frequency = 122.9
            ),
            Frequency(
                airportIcao = "KHAF",
                type = FrequencyType.AWOS,
                description = "Half Moon Bay AWOS",
                frequency = 119.025
            )
        )

        airportDao.insertFrequencies(frequencies)
    }

    /**
     * Initialize knowledge base with FAR/AIM content
     */
    private suspend fun initializeKnowledgeBase(db: FlightDeckDatabase) {
        val knowledgeDao = db.knowledgeDao()

        val documents = mutableListOf<AviationDocument>()

        // Add sample knowledge
        documents.addAll(AviationKnowledge.getSampleDocuments())

        // Add FAR regulations
        documents.addAll(FARContent.getEssentialRegulations())

        // Add AIM sections
        documents.addAll(AIMContent.getEssentialAIMSections())

        knowledgeDao.insertDocuments(documents)
    }

    /**
     * Initialize ATC scenarios
     */
    private suspend fun initializeATCScenarios(db: FlightDeckDatabase) {
        val atcDao = db.atcDao()

        val scenarios = listOf(
            ATCScenario(
                title = "Ground Clearance - VFR",
                description = "Request taxi clearance for VFR departure",
                scenarioType = ATCScenarioType.GROUND_CLEARANCE,
                difficulty = Difficulty.BEGINNER,
                airport = "KPAO",
                situation = "Clear VFR day at Palo Alto. You're parked at the ramp, ready to taxi for runway 31."
            ),
            ATCScenario(
                title = "Takeoff Clearance",
                description = "Request and receive takeoff clearance",
                scenarioType = ATCScenarioType.TAKEOFF_CLEARANCE,
                difficulty = Difficulty.BEGINNER,
                airport = "KPAO",
                situation = "Holding short of runway 31, run-up complete, ready for departure."
            ),
            ATCScenario(
                title = "Pattern Entry - Controlled",
                description = "Enter traffic pattern at towered airport",
                scenarioType = ATCScenarioType.IN_FLIGHT_COMMUNICATION,
                difficulty = Difficulty.INTERMEDIATE,
                airport = "KPAO",
                situation = "Inbound from the south, 10 miles out. Need to enter the pattern for landing."
            ),
            ATCScenario(
                title = "Landing Clearance",
                description = "Request landing clearance in the pattern",
                scenarioType = ATCScenarioType.LANDING_CLEARANCE,
                difficulty = Difficulty.INTERMEDIATE,
                airport = "KPAO",
                situation = "On downwind leg for runway 31, ready to turn base."
            ),
            ATCScenario(
                title = "Class Bravo Clearance",
                description = "Request clearance through Class B airspace",
                scenarioType = ATCScenarioType.IN_FLIGHT_COMMUNICATION,
                difficulty = Difficulty.ADVANCED,
                airport = "KSFO",
                situation = "Approaching San Francisco Class B airspace, need clearance to transition."
            )
        )

        atcDao.insertScenarios(scenarios)
    }

    /**
     * Initialize sample aircraft
     */
    private suspend fun initializeAircraft(db: FlightDeckDatabase) {
        val aircraftDao = db.aircraftDao()

        val aircraft = listOf(
            Aircraft(
                make = "Cessna",
                model = "172S Skyhawk",
                registration = "N12345",
                category = AircraftCategory.SINGLE_ENGINE_LAND,
                cruiseSpeed = 122,
                fuelCapacity = 56.0,
                fuelBurnRate = 8.5,
                homeAirport = "KPAO",
                notes = "Standard trainer aircraft"
            ),
            Aircraft(
                make = "Piper",
                model = "PA-28-181 Archer III",
                registration = "N98765",
                category = AircraftCategory.SINGLE_ENGINE_LAND,
                cruiseSpeed = 128,
                fuelCapacity = 50.0,
                fuelBurnRate = 9.0,
                homeAirport = "KPAO",
                notes = "Primary trainer"
            ),
            Aircraft(
                make = "Cirrus",
                model = "SR22",
                registration = "N42SR",
                category = AircraftCategory.SINGLE_ENGINE_LAND,
                cruiseSpeed = 183,
                fuelCapacity = 92.0,
                fuelBurnRate = 17.0,
                homeAirport = "KPAO",
                notes = "Advanced trainer with glass cockpit"
            )
        )

        aircraft.forEach { aircraftDao.insertAircraft(it) }
    }

    /**
     * Initialize logbook with sample training sessions
     * Creates realistic progression from student pilot to intermediate level
     */
    private suspend fun initializeLogbookData(db: FlightDeckDatabase) {
        val logbookDao = db.logbookDao()

        // Define base time for entries (going back in time)
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L

        // Sample logbook entries showing progression over time
        val entries = listOf(
            // Entry 1: First training session - 30 days ago
            LogbookEntry(
                missionConfigId = null,
                missionName = "First Solo Pattern Practice",
                flightPlanId = null,
                departureAirport = "KPAO",
                arrivalAirport = "KPAO",
                route = "Traffic Pattern",
                distance = 5,
                totalTime = 45,
                simulatedInstrumentTime = 0,
                nightTime = 0,
                date = now - (30 * dayMs),
                startTime = now - (30 * dayMs),
                endTime = now - (30 * dayMs) + (45 * 60 * 1000),
                difficulty = Difficulty.BEGINNER,
                challengesCompleted = "",
                trafficDensity = TrafficDensity.LIGHT,
                weatherComplexity = WeatherComplexity.CLEAR_CALM,
                overallScore = 68f,
                communicationScore = 65f,
                navigationScore = 72f,
                weatherDecisionScore = 70f,
                emergencyHandlingScore = 0f,
                trafficManagementScore = 65f,
                totalCommunications = 8,
                correctCommunications = 6,
                frequencyChanges = 2,
                emergenciesDeclared = 0,
                goArounds = 1,
                weatherDiversions = 0,
                trafficConflicts = 0,
                equipmentFailures = 0,
                userNotes = "First time using ATC simulator. Nervous but completed the pattern.",
                aiInstructorFeedback = "Good first attempt! Focus on improving radio phraseology and confidence. Remember to include all required elements in position reports.",
                areasForImprovement = "RADIO_COMMUNICATION,SITUATIONAL_AWARENESS",
                isCompleted = true,
                wasAborted = false
            ),

            // Entry 2: Cross-country planning - 25 days ago
            LogbookEntry(
                missionConfigId = null,
                missionName = "PAO to SQL Cross-Country",
                flightPlanId = null,
                departureAirport = "KPAO",
                arrivalAirport = "KSQL",
                route = "Direct",
                distance = 12,
                totalTime = 35,
                simulatedInstrumentTime = 0,
                nightTime = 0,
                date = now - (25 * dayMs),
                startTime = now - (25 * dayMs),
                endTime = now - (25 * dayMs) + (35 * 60 * 1000),
                difficulty = Difficulty.BEGINNER,
                challengesCompleted = "",
                trafficDensity = TrafficDensity.MODERATE,
                weatherComplexity = WeatherComplexity.TYPICAL_VFR,
                overallScore = 72f,
                communicationScore = 70f,
                navigationScore = 78f,
                weatherDecisionScore = 75f,
                emergencyHandlingScore = 0f,
                trafficManagementScore = 68f,
                totalCommunications = 12,
                correctCommunications = 9,
                frequencyChanges = 3,
                emergenciesDeclared = 0,
                goArounds = 0,
                weatherDiversions = 0,
                trafficConflicts = 1,
                equipmentFailures = 0,
                userNotes = "Short cross-country. Navigation was easier than expected.",
                aiInstructorFeedback = "Excellent improvement! Your position reports are clearer now. Good checkpoint identification and timing.",
                areasForImprovement = "TRAFFIC_MANAGEMENT,RADIO_COMMUNICATION",
                isCompleted = true,
                wasAborted = false
            ),

            // Entry 3: Class D operations - 20 days ago
            LogbookEntry(
                missionConfigId = null,
                missionName = "Tower Communication Practice",
                flightPlanId = null,
                departureAirport = "KSQL",
                arrivalAirport = "KSQL",
                route = "Traffic Pattern",
                distance = 8,
                totalTime = 50,
                simulatedInstrumentTime = 0,
                nightTime = 0,
                date = now - (20 * dayMs),
                startTime = now - (20 * dayMs),
                endTime = now - (20 * dayMs) + (50 * 60 * 1000),
                difficulty = Difficulty.INTERMEDIATE,
                challengesCompleted = "RAPID_FREQUENCY_CHANGES",
                trafficDensity = TrafficDensity.BUSY,
                weatherComplexity = WeatherComplexity.TYPICAL_VFR,
                overallScore = 75f,
                communicationScore = 78f,
                navigationScore = 75f,
                weatherDecisionScore = 72f,
                emergencyHandlingScore = 0f,
                trafficManagementScore = 74f,
                totalCommunications = 15,
                correctCommunications = 13,
                frequencyChanges = 5,
                emergenciesDeclared = 0,
                goArounds = 0,
                weatherDiversions = 0,
                trafficConflicts = 2,
                equipmentFailures = 0,
                userNotes = "Busy pattern with lots of traffic. Kept up with communications well.",
                aiInstructorFeedback = "Great job handling the busy pattern! Your readbacks are getting more precise. Work on anticipating controller instructions.",
                areasForImprovement = "SITUATIONAL_AWARENESS",
                isCompleted = true,
                wasAborted = false
            ),

            // Entry 4: Weather decision making - 15 days ago
            LogbookEntry(
                missionConfigId = null,
                missionName = "Weather Diversion Challenge",
                flightPlanId = null,
                departureAirport = "KPAO",
                arrivalAirport = "KHAF",
                route = "Direct with diversion",
                distance = 18,
                totalTime = 55,
                simulatedInstrumentTime = 0,
                nightTime = 0,
                date = now - (15 * dayMs),
                startTime = now - (15 * dayMs),
                endTime = now - (15 * dayMs) + (55 * 60 * 1000),
                difficulty = Difficulty.INTERMEDIATE,
                challengesCompleted = "COASTAL_FOG_BANK,PIREP_ANALYSIS",
                trafficDensity = TrafficDensity.LIGHT,
                weatherComplexity = WeatherComplexity.CHALLENGING,
                overallScore = 82f,
                communicationScore = 80f,
                navigationScore = 83f,
                weatherDecisionScore = 88f,
                emergencyHandlingScore = 0f,
                trafficManagementScore = 75f,
                totalCommunications = 10,
                correctCommunications = 9,
                frequencyChanges = 2,
                emergenciesDeclared = 0,
                goArounds = 0,
                weatherDiversions = 1,
                trafficConflicts = 0,
                equipmentFailures = 0,
                userNotes = "Had to divert due to coastal fog. Made the right call early.",
                aiInstructorFeedback = "Excellent weather decision making! You recognized deteriorating conditions and made a timely decision. This is real-world ADM at its best.",
                areasForImprovement = "",
                isCompleted = true,
                wasAborted = false
            ),

            // Entry 5: Class C operations - 12 days ago
            LogbookEntry(
                missionConfigId = null,
                missionName = "Class C Clearance Practice",
                flightPlanId = null,
                departureAirport = "KPAO",
                arrivalAirport = "KSJC",
                route = "Direct",
                distance = 15,
                totalTime = 40,
                simulatedInstrumentTime = 0,
                nightTime = 0,
                date = now - (12 * dayMs),
                startTime = now - (12 * dayMs),
                endTime = now - (12 * dayMs) + (40 * 60 * 1000),
                difficulty = Difficulty.INTERMEDIATE,
                challengesCompleted = "BUSY_CLASS_C",
                trafficDensity = TrafficDensity.BUSY,
                weatherComplexity = WeatherComplexity.TYPICAL_VFR,
                overallScore = 85f,
                communicationScore = 87f,
                navigationScore = 82f,
                weatherDecisionScore = 80f,
                emergencyHandlingScore = 0f,
                trafficManagementScore = 88f,
                totalCommunications = 18,
                correctCommunications = 17,
                frequencyChanges = 4,
                emergenciesDeclared = 0,
                goArounds = 0,
                weatherDiversions = 0,
                trafficConflicts = 3,
                equipmentFailures = 0,
                userNotes = "First time into Class C. Approach control was very busy but manageable.",
                aiInstructorFeedback = "Outstanding! You handled the Class C environment like a pro. Your traffic awareness and communications were excellent.",
                areasForImprovement = "",
                isCompleted = true,
                wasAborted = false
            ),

            // Entry 6: Emergency procedures - 8 days ago
            LogbookEntry(
                missionConfigId = null,
                missionName = "Emergency Procedures Training",
                flightPlanId = null,
                departureAirport = "KPAO",
                arrivalAirport = "KPAO",
                route = "Local area",
                distance = 10,
                totalTime = 60,
                simulatedInstrumentTime = 0,
                nightTime = 0,
                date = now - (8 * dayMs),
                startTime = now - (8 * dayMs),
                endTime = now - (8 * dayMs) + (60 * 60 * 1000),
                difficulty = Difficulty.ADVANCED,
                challengesCompleted = "ENGINE_ROUGHNESS,ELECTRICAL_FAILURE",
                trafficDensity = TrafficDensity.MODERATE,
                weatherComplexity = WeatherComplexity.TYPICAL_VFR,
                overallScore = 78f,
                communicationScore = 82f,
                navigationScore = 75f,
                weatherDecisionScore = 75f,
                emergencyHandlingScore = 85f,
                trafficManagementScore = 72f,
                totalCommunications = 14,
                correctCommunications = 13,
                frequencyChanges = 3,
                emergenciesDeclared = 1,
                goArounds = 0,
                weatherDiversions = 0,
                trafficConflicts = 1,
                equipmentFailures = 2,
                userNotes = "Simulated engine roughness and electrical failure. Declared emergency appropriately.",
                aiInstructorFeedback = "Great emergency handling! You followed procedures correctly and communicated effectively with ATC. Your decision making under pressure was solid.",
                areasForImprovement = "SITUATIONAL_AWARENESS,DECISION_MAKING",
                isCompleted = true,
                wasAborted = false
            ),

            // Entry 7: Night operations - 5 days ago
            LogbookEntry(
                missionConfigId = null,
                missionName = "Night Pattern Work",
                flightPlanId = null,
                departureAirport = "KPAO",
                arrivalAirport = "KPAO",
                route = "Traffic Pattern",
                distance = 6,
                totalTime = 45,
                simulatedInstrumentTime = 0,
                nightTime = 45,
                date = now - (5 * dayMs),
                startTime = now - (5 * dayMs),
                endTime = now - (5 * dayMs) + (45 * 60 * 1000),
                difficulty = Difficulty.INTERMEDIATE,
                challengesCompleted = "NIGHT_OPERATIONS",
                trafficDensity = TrafficDensity.LIGHT,
                weatherComplexity = WeatherComplexity.TYPICAL_VFR,
                overallScore = 88f,
                communicationScore = 90f,
                navigationScore = 87f,
                weatherDecisionScore = 85f,
                emergencyHandlingScore = 0f,
                trafficManagementScore = 88f,
                totalCommunications = 12,
                correctCommunications = 12,
                frequencyChanges = 2,
                emergenciesDeclared = 0,
                goArounds = 0,
                weatherDiversions = 0,
                trafficConflicts = 0,
                equipmentFailures = 0,
                userNotes = "Night flying is peaceful. Communications were crisp and clear.",
                aiInstructorFeedback = "Perfect! Your communication skills are now at an advanced level. Night operations require precision, and you delivered.",
                areasForImprovement = "",
                isCompleted = true,
                wasAborted = false
            ),

            // Entry 8: Complex cross-country - 2 days ago
            LogbookEntry(
                missionConfigId = null,
                missionName = "Complex Cross-Country",
                flightPlanId = null,
                departureAirport = "KPAO",
                arrivalAirport = "KSFO",
                route = "Via KHAF, coastal route",
                distance = 32,
                totalTime = 75,
                simulatedInstrumentTime = 0,
                nightTime = 0,
                date = now - (2 * dayMs),
                startTime = now - (2 * dayMs),
                endTime = now - (2 * dayMs) + (75 * 60 * 1000),
                difficulty = Difficulty.ADVANCED,
                challengesCompleted = "BUSY_CLASS_B,WIND_SHEAR,PIREP_ANALYSIS",
                trafficDensity = TrafficDensity.CONGESTED,
                weatherComplexity = WeatherComplexity.CHALLENGING,
                overallScore = 92f,
                communicationScore = 94f,
                navigationScore = 90f,
                weatherDecisionScore = 92f,
                emergencyHandlingScore = 0f,
                trafficManagementScore = 91f,
                totalCommunications = 25,
                correctCommunications = 25,
                frequencyChanges = 7,
                emergenciesDeclared = 0,
                goArounds = 0,
                weatherDiversions = 0,
                trafficConflicts = 5,
                equipmentFailures = 0,
                userNotes = "Most challenging flight yet. Class B operations at SFO with gusty winds. Felt confident throughout.",
                aiInstructorFeedback = "Exceptional performance! You're operating at an advanced level. Your communication, situational awareness, and decision making are all excellent. You're ready for more complex scenarios.",
                areasForImprovement = "",
                isCompleted = true,
                wasAborted = false
            )
        )

        // Insert all entries
        entries.forEach { logbookDao.insertEntry(it) }

        // Initialize proficiency ratings based on the training history
        val proficiencyRatings = listOf(
            ProficiencyRating(
                skillCategory = SkillCategory.RADIO_COMMUNICATION,
                currentRating = ProficiencyLevel.ADVANCED,
                currentScore = 87f,
                previousRating = ProficiencyLevel.PROFICIENT,
                previousScore = 75f,
                ratingChangedDate = now - (5 * dayMs),
                totalSessions = 8,
                averageScore = 80f,
                bestScore = 94f,
                worstScore = 65f,
                recentScores = "82,87,90,94,90",
                trend = TrendDirection.IMPROVING,
                needsImprovement = false,
                recommendedChallenges = "",
                cfiFeedback = null
            ),
            ProficiencyRating(
                skillCategory = SkillCategory.NAVIGATION,
                currentRating = ProficiencyLevel.ADVANCED,
                currentScore = 85f,
                previousRating = ProficiencyLevel.PROFICIENT,
                previousScore = 72f,
                ratingChangedDate = now - (8 * dayMs),
                totalSessions = 8,
                averageScore = 78f,
                bestScore = 90f,
                worstScore = 72f,
                recentScores = "75,82,83,87,90",
                trend = TrendDirection.IMPROVING,
                needsImprovement = false,
                recommendedChallenges = "",
                cfiFeedback = null
            ),
            ProficiencyRating(
                skillCategory = SkillCategory.WEATHER_DECISION_MAKING,
                currentRating = ProficiencyLevel.ADVANCED,
                currentScore = 86f,
                previousRating = ProficiencyLevel.PROFICIENT,
                previousScore = 70f,
                ratingChangedDate = now - (12 * dayMs),
                totalSessions = 8,
                averageScore = 79f,
                bestScore = 92f,
                worstScore = 70f,
                recentScores = "80,88,75,85,92",
                trend = TrendDirection.IMPROVING,
                needsImprovement = false,
                recommendedChallenges = "",
                cfiFeedback = null
            ),
            ProficiencyRating(
                skillCategory = SkillCategory.TRAFFIC_MANAGEMENT,
                currentRating = ProficiencyLevel.ADVANCED,
                currentScore = 85f,
                previousRating = ProficiencyLevel.PROFICIENT,
                previousScore = 68f,
                ratingChangedDate = now - (12 * dayMs),
                totalSessions = 8,
                averageScore = 78f,
                bestScore = 91f,
                worstScore = 65f,
                recentScores = "74,88,72,88,91",
                trend = TrendDirection.IMPROVING,
                needsImprovement = false,
                recommendedChallenges = "",
                cfiFeedback = null
            ),
            ProficiencyRating(
                skillCategory = SkillCategory.EMERGENCY_PROCEDURES,
                currentRating = ProficiencyLevel.ADVANCED,
                currentScore = 85f,
                previousRating = null,
                previousScore = null,
                ratingChangedDate = now - (8 * dayMs),
                totalSessions = 1,
                averageScore = 85f,
                bestScore = 85f,
                worstScore = 85f,
                recentScores = "85",
                trend = TrendDirection.STABLE,
                needsImprovement = false,
                recommendedChallenges = "DUAL_ENGINE_FAILURE,SMOKE_IN_COCKPIT",
                cfiFeedback = null
            ),
            ProficiencyRating(
                skillCategory = SkillCategory.AIRSPACE_KNOWLEDGE,
                currentRating = ProficiencyLevel.ADVANCED,
                currentScore = 88f,
                previousRating = ProficiencyLevel.PROFICIENT,
                previousScore = 75f,
                ratingChangedDate = now - (12 * dayMs),
                totalSessions = 6,
                averageScore = 82f,
                bestScore = 92f,
                worstScore = 75f,
                recentScores = "80,85,82,88,92",
                trend = TrendDirection.IMPROVING,
                needsImprovement = false,
                recommendedChallenges = "",
                cfiFeedback = null
            ),
            ProficiencyRating(
                skillCategory = SkillCategory.CHECKLIST_DISCIPLINE,
                currentRating = ProficiencyLevel.PROFICIENT,
                currentScore = 82f,
                previousRating = ProficiencyLevel.DEVELOPING,
                previousScore = 68f,
                ratingChangedDate = now - (15 * dayMs),
                totalSessions = 8,
                averageScore = 76f,
                bestScore = 85f,
                worstScore = 68f,
                recentScores = "75,78,80,82,85",
                trend = TrendDirection.IMPROVING,
                needsImprovement = false,
                recommendedChallenges = "",
                cfiFeedback = null
            ),
            ProficiencyRating(
                skillCategory = SkillCategory.SITUATIONAL_AWARENESS,
                currentRating = ProficiencyLevel.PROFICIENT,
                currentScore = 80f,
                previousRating = ProficiencyLevel.DEVELOPING,
                previousScore = 65f,
                ratingChangedDate = now - (20 * dayMs),
                totalSessions = 8,
                averageScore = 73f,
                bestScore = 84f,
                worstScore = 65f,
                recentScores = "72,75,78,80,84",
                trend = TrendDirection.IMPROVING,
                needsImprovement = false,
                recommendedChallenges = "COCKPIT_DISTRACTION,RAPID_FREQUENCY_CHANGES",
                cfiFeedback = null
            ),
            ProficiencyRating(
                skillCategory = SkillCategory.DECISION_MAKING,
                currentRating = ProficiencyLevel.PROFICIENT,
                currentScore = 81f,
                previousRating = ProficiencyLevel.DEVELOPING,
                previousScore = 68f,
                ratingChangedDate = now - (15 * dayMs),
                totalSessions = 6,
                averageScore = 76f,
                bestScore = 88f,
                worstScore = 68f,
                recentScores = "75,80,78,81,88",
                trend = TrendDirection.IMPROVING,
                needsImprovement = false,
                recommendedChallenges = "WEATHER_DIVERSION,ALTERNATE_SELECTION",
                cfiFeedback = null
            ),
            ProficiencyRating(
                skillCategory = SkillCategory.FUEL_MANAGEMENT,
                currentRating = ProficiencyLevel.PROFICIENT,
                currentScore = 78f,
                previousRating = ProficiencyLevel.DEVELOPING,
                previousScore = 65f,
                ratingChangedDate = now - (20 * dayMs),
                totalSessions = 8,
                averageScore = 72f,
                bestScore = 82f,
                worstScore = 65f,
                recentScores = "70,72,75,78,82",
                trend = TrendDirection.IMPROVING,
                needsImprovement = false,
                recommendedChallenges = "FUEL_EMERGENCY",
                cfiFeedback = null
            )
        )

        // Insert all proficiency ratings
        proficiencyRatings.forEach { logbookDao.insertProficiencyRating(it) }

        // Calculate and insert logbook totals
        val logbookTotals = LogbookTotals(
            userId = "default_user",
            totalTime = 405, // Sum of all session times (45+35+50+55+40+60+45+75)
            simulatedInstrumentTime = 0,
            nightTime = 45,
            totalSessions = 8,
            completedSessions = 8,
            abortedSessions = 0,
            totalDistance = 106, // Sum of all distances
            uniqueAirportsVisited = 4, // KPAO, KSQL, KHAF, KSJC, KSFO
            uniqueRoutes = 7,
            averageOverallScore = 80f, // Average of all overall scores
            averageCommunicationScore = 81f,
            averageNavigationScore = 80f,
            averageWeatherScore = 80f,
            averageEmergencyScore = 85f,
            averageTrafficScore = 78f,
            perfectScores = 0,
            emergenciesHandled = 1,
            goAroundsExecuted = 1,
            weatherDiversions = 1,
            totalChallengesCompleted = 9,
            uniqueChallengesCompleted = 8,
            beginnerMissions = 2,
            intermediateMissions = 4,
            advancedMissions = 2,
            expertMissions = 0,
            lastSessionDate = now - (2 * dayMs),
            longestSessionMinutes = 75,
            currentStreak = 3, // Sessions in last 3 days
            lastUpdated = now
        )

        logbookDao.insertTotals(logbookTotals)
    }
}
