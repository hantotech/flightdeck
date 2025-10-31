package com.example.flightdeck.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flightdeck.ui.logbook.LogbookOverviewViewModel

/**
 * Factory for creating ViewModels with dependency injection
 */
class ViewModelFactory(private val appContainer: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LogbookOverviewViewModel::class.java) -> {
                LogbookOverviewViewModel(appContainer.logbookRepository) as T
            }
            // TODO: Add other ViewModels here as they're created
            // modelClass.isAssignableFrom(MissionSelectionViewModel::class.java) -> {
            //     MissionSelectionViewModel(appContainer.missionConfigRepository) as T
            // }
            // modelClass.isAssignableFrom(ATCViewModel::class.java) -> {
            //     ATCViewModel(appContainer.atcRepository) as T
            // }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
