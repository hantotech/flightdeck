package com.example.flightdeck.data.local.dao

import androidx.room.*
import com.example.flightdeck.data.model.Aircraft
import kotlinx.coroutines.flow.Flow

@Dao
interface AircraftDao {

    @Query("SELECT * FROM aircraft ORDER BY registration")
    fun getAllAircraft(): Flow<List<Aircraft>>

    @Query("SELECT * FROM aircraft WHERE id = :id")
    suspend fun getAircraftById(id: Long): Aircraft?

    @Query("SELECT * FROM aircraft WHERE registration = :registration")
    suspend fun getAircraftByRegistration(registration: String): Aircraft?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAircraft(aircraft: Aircraft): Long

    @Update
    suspend fun updateAircraft(aircraft: Aircraft)

    @Delete
    suspend fun deleteAircraft(aircraft: Aircraft)

    @Query("DELETE FROM aircraft WHERE id = :id")
    suspend fun deleteAircraftById(id: Long)
}
