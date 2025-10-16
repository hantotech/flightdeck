package com.example.flightdeck.data.local.dao

import androidx.room.*
import com.example.flightdeck.data.model.Airport
import com.example.flightdeck.data.model.AirportType
import com.example.flightdeck.data.model.Frequency
import com.example.flightdeck.data.model.FrequencyType
import com.example.flightdeck.data.model.Runway
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Airport operations
 */
@Dao
interface AirportDao {

    // ========== AIRPORT QUERIES ==========

    @Query("SELECT * FROM airports WHERE icao = :icao")
    suspend fun getAirportByIcao(icao: String): Airport?

    @Query("SELECT * FROM airports WHERE iata = :iata")
    suspend fun getAirportByIata(iata: String): Airport?

    @Query("""
        SELECT * FROM airports
        WHERE icao LIKE '%' || :query || '%'
           OR iata LIKE '%' || :query || '%'
           OR name LIKE '%' || :query || '%'
           OR city LIKE '%' || :query || '%'
        ORDER BY name
        LIMIT :limit
    """)
    suspend fun searchAirports(query: String, limit: Int = 20): List<Airport>

    @Query("SELECT * FROM airports WHERE type IN (:types) ORDER BY name")
    suspend fun getAirportsByType(types: List<AirportType>): List<Airport>

    @Query("""
        SELECT * FROM airports
        WHERE towerControlled = 1
        ORDER BY name
    """)
    suspend fun getToweredAirports(): List<Airport>

    @Query("""
        SELECT * FROM airports
        WHERE country = :country
        ORDER BY name
    """)
    suspend fun getAirportsByCountry(country: String): List<Airport>

    @Query("""
        SELECT * FROM airports
        WHERE state = :state
        ORDER BY name
    """)
    suspend fun getAirportsByState(state: String): List<Airport>

    /**
     * Find airports within a radius (simplified box search)
     * For precise distance, use Haversine formula in repository
     */
    @Query("""
        SELECT * FROM airports
        WHERE latitude BETWEEN :minLat AND :maxLat
          AND longitude BETWEEN :minLon AND :maxLon
        ORDER BY name
        LIMIT :limit
    """)
    suspend fun getAirportsNearby(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double,
        limit: Int = 50
    ): List<Airport>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAirport(airport: Airport)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAirports(airports: List<Airport>)

    @Update
    suspend fun updateAirport(airport: Airport)

    @Delete
    suspend fun deleteAirport(airport: Airport)

    @Query("DELETE FROM airports")
    suspend fun deleteAllAirports()

    @Query("SELECT COUNT(*) FROM airports")
    suspend fun getAirportCount(): Int

    // ========== RUNWAY QUERIES ==========

    @Query("SELECT * FROM runways WHERE airportIcao = :icao ORDER BY identifier")
    suspend fun getRunwaysForAirport(icao: String): List<Runway>

    @Query("SELECT * FROM runways WHERE airportIcao = :icao ORDER BY identifier")
    fun observeRunwaysForAirport(icao: String): Flow<List<Runway>>

    @Query("""
        SELECT * FROM runways
        WHERE airportIcao = :icao
          AND length >= :minLength
        ORDER BY length DESC
    """)
    suspend fun getRunwaysLongerThan(icao: String, minLength: Int): List<Runway>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunway(runway: Runway)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunways(runways: List<Runway>)

    @Delete
    suspend fun deleteRunway(runway: Runway)

    @Query("DELETE FROM runways WHERE airportIcao = :icao")
    suspend fun deleteRunwaysForAirport(icao: String)

    // ========== FREQUENCY QUERIES ==========

    @Query("SELECT * FROM frequencies WHERE airportIcao = :icao ORDER BY type")
    suspend fun getFrequenciesForAirport(icao: String): List<Frequency>

    @Query("SELECT * FROM frequencies WHERE airportIcao = :icao ORDER BY type")
    fun observeFrequenciesForAirport(icao: String): Flow<List<Frequency>>

    @Query("""
        SELECT * FROM frequencies
        WHERE airportIcao = :icao AND type = :type
        LIMIT 1
    """)
    suspend fun getFrequencyByType(icao: String, type: FrequencyType): Frequency?

    @Query("""
        SELECT * FROM frequencies
        WHERE airportIcao = :icao AND type IN (:types)
        ORDER BY type
    """)
    suspend fun getFrequenciesByTypes(icao: String, types: List<FrequencyType>): List<Frequency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFrequency(frequency: Frequency)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFrequencies(frequencies: List<Frequency>)

    @Delete
    suspend fun deleteFrequency(frequency: Frequency)

    @Query("DELETE FROM frequencies WHERE airportIcao = :icao")
    suspend fun deleteFrequenciesForAirport(icao: String)

    // ========== COMBINED QUERIES ==========

    /**
     * Get complete airport data with runways and frequencies
     */
    @Transaction
    suspend fun getCompleteAirportData(icao: String): CompleteAirportData? {
        val airport = getAirportByIcao(icao) ?: return null
        val runways = getRunwaysForAirport(icao)
        val frequencies = getFrequenciesForAirport(icao)
        return CompleteAirportData(airport, runways, frequencies)
    }
}

/**
 * Complete airport data including runways and frequencies
 */
data class CompleteAirportData(
    val airport: Airport,
    val runways: List<Runway>,
    val frequencies: List<Frequency>
)
