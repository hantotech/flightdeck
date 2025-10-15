package com.example.flightdeck.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.flightdeck.data.local.dao.*
import com.example.flightdeck.data.model.*

@Database(
    entities = [
        Aircraft::class,
        ChecklistItem::class,
        ChecklistSession::class,
        ChecklistItemCompletion::class,
        FlightPlan::class,
        Waypoint::class,
        ATCScenario::class,
        ATCExchange::class,
        ATCPracticeSession::class,
        ATCResponse::class,
        PerformanceReport::class,
        Achievement::class,
        UserProgress::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlightDeckDatabase : RoomDatabase() {

    abstract fun aircraftDao(): AircraftDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun flightPlanDao(): FlightPlanDao
    abstract fun atcDao(): ATCDao
    abstract fun performanceDao(): PerformanceDao

    companion object {
        @Volatile
        private var INSTANCE: FlightDeckDatabase? = null

        fun getDatabase(context: Context): FlightDeckDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlightDeckDatabase::class.java,
                    "flightdeck_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
