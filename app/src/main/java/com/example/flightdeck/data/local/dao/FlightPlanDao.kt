package com.example.flightdeck.data.local.dao

import androidx.room.*
import com.example.flightdeck.data.model.FlightPlan
import com.example.flightdeck.data.model.Waypoint
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightPlanDao {

    // Flight Plans
    @Query("SELECT * FROM flight_plans ORDER BY createdAt DESC")
    fun getAllFlightPlans(): Flow<List<FlightPlan>>

    @Query("SELECT * FROM flight_plans WHERE id = :id")
    suspend fun getFlightPlanById(id: Long): FlightPlan?

    @Query("SELECT * FROM flight_plans WHERE aircraftId = :aircraftId ORDER BY createdAt DESC")
    fun getFlightPlansByAircraft(aircraftId: Long): Flow<List<FlightPlan>>

    @Query("SELECT * FROM flight_plans WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveFlightPlan(): FlightPlan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlightPlan(flightPlan: FlightPlan): Long

    @Update
    suspend fun updateFlightPlan(flightPlan: FlightPlan)

    @Delete
    suspend fun deleteFlightPlan(flightPlan: FlightPlan)

    @Query("DELETE FROM flight_plans WHERE id = :id")
    suspend fun deleteFlightPlanById(id: Long)

    // Waypoints
    @Query("SELECT * FROM waypoints WHERE flightPlanId = :flightPlanId ORDER BY sequence")
    fun getWaypoints(flightPlanId: Long): Flow<List<Waypoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaypoint(waypoint: Waypoint): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaypoints(waypoints: List<Waypoint>)

    @Update
    suspend fun updateWaypoint(waypoint: Waypoint)

    @Delete
    suspend fun deleteWaypoint(waypoint: Waypoint)

    @Query("DELETE FROM waypoints WHERE flightPlanId = :flightPlanId")
    suspend fun deleteFlightPlanWaypoints(flightPlanId: Long)
}
