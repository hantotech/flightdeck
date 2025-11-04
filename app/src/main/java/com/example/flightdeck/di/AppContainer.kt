package com.example.flightdeck.di

import android.content.Context
import com.example.flightdeck.data.local.FlightDeckDatabase
import com.example.flightdeck.data.repository.*
import com.example.flightdeck.data.remote.weather.AviationWeatherService

/**
 * Application-level dependency container
 * Provides singleton instances of repositories and services
 */
class AppContainer(context: Context) {

    // Database
    val database: FlightDeckDatabase by lazy {
        FlightDeckDatabase.getDatabase(context)
    }

    // Repositories (Voice ATC Focus)
    val logbookRepository: LogbookRepository by lazy {
        LogbookRepository(database.logbookDao())
    }

    val airportRepository: AirportRepository by lazy {
        AirportRepository(database.airportDao())
    }

    val weatherService: AviationWeatherService by lazy {
        AviationWeatherService()
    }

    val atisRepository: ATISRepository by lazy {
        ATISRepository(
            atisDao = database.atisDao(),
            airportRepository = airportRepository,
            weatherService = weatherService
        )
    }

    val trafficSimulator: TrafficSimulator by lazy {
        TrafficSimulator(
            trafficDao = database.trafficDao(),
            airportRepository = airportRepository
        )
    }

    val atcRepository: ATCRepository by lazy {
        ATCRepository(
            atcDao = database.atcDao(),
            airportRepository = airportRepository,
            trafficSimulator = trafficSimulator
        )
    }
}
