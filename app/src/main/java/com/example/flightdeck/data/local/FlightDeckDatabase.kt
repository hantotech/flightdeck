package com.example.flightdeck.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.flightdeck.data.local.dao.*
import com.example.flightdeck.data.model.ATCScenario
import com.example.flightdeck.data.model.ATCExchange
import com.example.flightdeck.data.model.ATCPracticeSession
import com.example.flightdeck.data.model.ATCResponse
import com.example.flightdeck.data.model.Airport
import com.example.flightdeck.data.model.Runway
import com.example.flightdeck.data.model.Frequency
import com.example.flightdeck.data.model.SimulatedTraffic
import com.example.flightdeck.data.model.ATISBroadcast
import com.example.flightdeck.data.model.LogbookEntry
import com.example.flightdeck.data.model.ProficiencyRating
import com.example.flightdeck.data.model.LogbookTotals
import com.example.flightdeck.data.model.Converters
import com.example.flightdeck.data.knowledge.AviationDocument

@Database(
    entities = [
        // ATC Practice (Core)
        ATCScenario::class,
        ATCExchange::class,
        ATCPracticeSession::class,
        ATCResponse::class,

        // Airports & Traffic
        Airport::class,
        Runway::class,
        Frequency::class,
        SimulatedTraffic::class,

        // Weather (ATIS)
        ATISBroadcast::class,

        // Logbook
        LogbookEntry::class,
        ProficiencyRating::class,
        LogbookTotals::class,

        // Knowledge Base (FAA Regulations)
        AviationDocument::class
    ],
    version = 8, // Simplified for voice ATC focus
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlightDeckDatabase : RoomDatabase() {

    // Core DAOs for voice ATC practice
    abstract fun atcDao(): ATCDao
    abstract fun airportDao(): AirportDao
    abstract fun trafficDao(): TrafficDao
    abstract fun atisDao(): ATISDao
    abstract fun logbookDao(): LogbookDao
    abstract fun knowledgeDao(): KnowledgeDao

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
