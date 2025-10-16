package com.example.flightdeck.data.local.dao

import androidx.room.*
import com.example.flightdeck.data.model.SimulatedTraffic
import com.example.flightdeck.data.model.TrafficPosition
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Traffic operations
 */
@Dao
interface TrafficDao {

    @Query("SELECT * FROM simulated_traffic WHERE id = :id")
    suspend fun getTrafficById(id: Long): SimulatedTraffic?

    @Query("SELECT * FROM simulated_traffic WHERE sessionId = :sessionId AND isActive = 1")
    suspend fun getActiveTrafficForSession(sessionId: Long): List<SimulatedTraffic>

    @Query("SELECT * FROM simulated_traffic WHERE sessionId = :sessionId AND isActive = 1")
    fun observeActiveTrafficForSession(sessionId: Long): Flow<List<SimulatedTraffic>>

    @Query("""
        SELECT * FROM simulated_traffic
        WHERE airportIcao = :icao AND isActive = 1
        ORDER BY lastUpdate DESC
    """)
    suspend fun getActiveTrafficAtAirport(icao: String): List<SimulatedTraffic>

    @Query("""
        SELECT * FROM simulated_traffic
        WHERE sessionId = :sessionId
          AND isActive = 1
          AND currentPosition = :position
    """)
    suspend fun getTrafficByPosition(sessionId: Long, position: TrafficPosition): List<SimulatedTraffic>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraffic(traffic: SimulatedTraffic): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTraffic(traffic: List<SimulatedTraffic>): List<Long>

    @Update
    suspend fun updateTraffic(traffic: SimulatedTraffic)

    @Query("UPDATE simulated_traffic SET isActive = 0 WHERE id = :id")
    suspend fun deactivateTraffic(id: Long)

    @Query("UPDATE simulated_traffic SET isActive = 0 WHERE sessionId = :sessionId")
    suspend fun deactivateAllTrafficForSession(sessionId: Long)

    @Delete
    suspend fun deleteTraffic(traffic: SimulatedTraffic)

    @Query("DELETE FROM simulated_traffic WHERE sessionId = :sessionId")
    suspend fun deleteTrafficForSession(sessionId: Long)

    @Query("DELETE FROM simulated_traffic WHERE isActive = 0 AND lastUpdate < :timestamp")
    suspend fun deleteOldInactiveTraffic(timestamp: Long)

    @Query("SELECT COUNT(*) FROM simulated_traffic WHERE sessionId = :sessionId AND isActive = 1")
    suspend fun getActiveTrafficCount(sessionId: Long): Int
}
