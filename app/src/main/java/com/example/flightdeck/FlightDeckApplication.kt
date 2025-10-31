package com.example.flightdeck

import android.app.Application
import com.example.flightdeck.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.example.flightdeck.utils.DatabaseInitializer

/**
 * FlightDeck Application class
 * Initializes database and app-wide resources
 */
class FlightDeckApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Dependency container
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        // Initialize dependency container
        appContainer = AppContainer(applicationContext)

        // Initialize database with sample data
        applicationScope.launch {
            DatabaseInitializer.initialize(applicationContext)
        }
    }
}
