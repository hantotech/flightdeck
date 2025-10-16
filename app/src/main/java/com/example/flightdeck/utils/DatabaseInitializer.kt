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
    }

    private suspend fun isInitialized(db: FlightDeckDatabase): Boolean {
        return db.airportDao().getAirportCount() > 0
    }

    /**
     * Initialize airports with common US training airports
     */
    private suspend fun initializeAirports(db: FlightDeckDatabase) {
        val airportDao = db.airportDao()

        val airports = listOf(
            // California airports
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
                type = AirportType.SMALL_AIRPORT
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
                type = AirportType.LARGE_AIRPORT
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
                type = AirportType.LARGE_AIRPORT
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
                type = AirportType.SMALL_AIRPORT
            ),
            Airport(
                icao = "KHAF",
                iata = "HAF",
                name = "Half Moon Bay Airport",
                city = "Half Moon Bay",
                state = "CA",
                country = "USA",
                latitude = 37.513,
                longitude = -122.500,
                elevation = 66,
                airspaceClass = AirspaceClass.G,
                towerControlled = false,
                type = AirportType.SMALL_AIRPORT
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
}
