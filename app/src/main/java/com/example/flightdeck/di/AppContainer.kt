package com.example.flightdeck.di

import android.content.Context
import com.example.flightdeck.data.local.FlightDeckDatabase
import com.example.flightdeck.data.repository.*

/**
 * Application-level dependency container
 * Provides singleton instances of repositories and services
 */
class AppContainer(context: Context) {

    // Database
    val database: FlightDeckDatabase by lazy {
        FlightDeckDatabase.getDatabase(context)
    }

    // Repositories
    val logbookRepository: LogbookRepository by lazy {
        LogbookRepository(database.logbookDao())
    }

    val missionConfigRepository: MissionConfigRepository by lazy {
        MissionConfigRepository(database.missionConfigDao())
    }

    val airportRepository: AirportRepository by lazy {
        AirportRepository(database.airportDao())
    }

    val trafficSimulator: TrafficSimulator by lazy {
        TrafficSimulator(
            trafficDao = database.trafficDao(),
            airportRepository = airportRepository
        )
    }

    val checklistRepository: ChecklistRepository by lazy {
        ChecklistRepository(checklistDao = database.checklistDao())
    }

    val atcRepository: ATCRepository by lazy {
        ATCRepository(
            atcDao = database.atcDao(),
            airportRepository = airportRepository,
            trafficSimulator = trafficSimulator
        )
    }

    val flightPlanRepository: FlightPlanRepository by lazy {
        FlightPlanRepository(flightPlanDao = database.flightPlanDao())
    }

    // TODO: Add other repositories as needed
    // - WeatherRepository
    // - AircraftRepository
    // - PerformanceRepository
    // - KnowledgeRepository
}
