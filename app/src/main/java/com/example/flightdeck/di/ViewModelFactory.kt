package com.example.flightdeck.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flightdeck.ui.logbook.LogbookOverviewViewModel
import com.example.flightdeck.ui.atc.ATCViewModel

/**
 * Factory for creating ViewModels with dependency injection
 * Focused on voice ATC practice ViewModels
 */
class ViewModelFactory(private val appContainer: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LogbookOverviewViewModel::class.java) -> {
                LogbookOverviewViewModel(appContainer.logbookRepository) as T
            }
            modelClass.isAssignableFrom(ATCViewModel::class.java) -> {
                ATCViewModel(appContainer.atcRepository) as T
            }
            // Add other ViewModels as needed for voice ATC features
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
