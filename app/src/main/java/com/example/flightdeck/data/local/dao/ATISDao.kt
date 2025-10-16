package com.example.flightdeck.data.local.dao

import androidx.room.*
import com.example.flightdeck.data.model.ATISBroadcast
import com.example.flightdeck.data.model.BroadcastType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ATIS/AWOS broadcasts
 */
@Dao
interface ATISDao {

    @Query("SELECT * FROM atis_broadcasts WHERE airportIcao = :icao AND isActive = 1 ORDER BY timestamp DESC LIMIT 1")
    suspend fun getCurrentATIS(icao: String): ATISBroadcast?

    @Query("SELECT * FROM atis_broadcasts WHERE airportIcao = :icao AND isActive = 1 ORDER BY timestamp DESC LIMIT 1")
    fun observeCurrentATIS(icao: String): Flow<ATISBroadcast?>

    @Query("SELECT * FROM atis_broadcasts WHERE airportIcao = :icao ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getATISHistory(icao: String, limit: Int = 10): List<ATISBroadcast>

    @Query("SELECT * FROM atis_broadcasts WHERE broadcastType = :type AND isActive = 1 ORDER BY timestamp DESC")
    suspend fun getActiveBroadcastsByType(type: BroadcastType): List<ATISBroadcast>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertATIS(atis: ATISBroadcast): Long

    @Update
    suspend fun updateATIS(atis: ATISBroadcast)

    @Query("UPDATE atis_broadcasts SET isActive = 0 WHERE airportIcao = :icao")
    suspend fun deactivateAllATIS(icao: String)

    @Query("UPDATE atis_broadcasts SET isActive = 0 WHERE timestamp < :timestamp")
    suspend fun deactivateOldATIS(timestamp: Long)

    @Delete
    suspend fun deleteATIS(atis: ATISBroadcast)

    @Query("DELETE FROM atis_broadcasts WHERE airportIcao = :icao")
    suspend fun deleteAllATISForAirport(icao: String)

    /**
     * Transaction: Deactivate old ATIS and insert new one
     */
    @Transaction
    suspend fun updateCurrentATIS(icao: String, newATIS: ATISBroadcast): Long {
        deactivateAllATIS(icao)
        return insertATIS(newATIS)
    }
}
