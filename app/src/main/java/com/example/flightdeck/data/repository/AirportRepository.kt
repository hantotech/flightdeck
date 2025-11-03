package com.example.flightdeck.data.repository

import com.example.flightdeck.data.local.dao.AirportDao
import com.example.flightdeck.data.local.dao.CompleteAirportData
import com.example.flightdeck.data.model.Airport
import com.example.flightdeck.data.model.AirportType
import com.example.flightdeck.data.model.Runway
import com.example.flightdeck.data.model.Frequency
import com.example.flightdeck.data.model.FrequencyType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.math.*

/**
 * Repository for airport data with caching and online lookup
 */
class AirportRepository(
    private val airportDao: AirportDao
) {

    // ========== AIRPORT OPERATIONS ==========

    /**
     * Get airport by ICAO code with caching
     * Returns cached data if available, otherwise looks up online
     */
    suspend fun getAirportByIcao(icao: String): Result<CompleteAirportData> = withContext(Dispatchers.IO) {
        try {
            // Try cache first
            val cachedData = airportDao.getCompleteAirportData(icao.uppercase())
            if (cachedData != null) {
                return@withContext Result.success(cachedData)
            }

            // TODO: Implement online lookup if not cached
            // For now, return error if not in database
            Result.failure(Exception("Airport $icao not found in database. Online lookup not yet implemented."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Search airports by query (ICAO, name, city)
     */
    suspend fun searchAirports(query: String, limit: Int = 20): List<Airport> = withContext(Dispatchers.IO) {
        airportDao.searchAirports(query, limit)
    }

    /**
     * Get airports by type (small, medium, large)
     */
    suspend fun getAirportsByType(types: List<AirportType>): List<Airport> = withContext(Dispatchers.IO) {
        airportDao.getAirportsByType(types)
    }

    /**
     * Get all towered airports
     */
    suspend fun getToweredAirports(): List<Airport> = withContext(Dispatchers.IO) {
        airportDao.getToweredAirports()
    }

    /**
     * Get airports in a state
     */
    suspend fun getAirportsByState(state: String): List<Airport> = withContext(Dispatchers.IO) {
        airportDao.getAirportsByState(state)
    }

    /**
     * Find airports near coordinates using Haversine formula
     */
    suspend fun getAirportsNearby(
        latitude: Double,
        longitude: Double,
        radiusMiles: Double = 50.0,
        limit: Int = 50
    ): List<AirportWithDistance> = withContext(Dispatchers.IO) {
        // Calculate approximate bounding box (1 degree ≈ 69 miles)
        val degreeRadius = radiusMiles / 69.0
        val minLat = latitude - degreeRadius
        val maxLat = latitude + degreeRadius
        val minLon = longitude - degreeRadius
        val maxLon = longitude + degreeRadius

        val candidates = airportDao.getAirportsNearby(minLat, maxLat, minLon, maxLon, limit * 2)

        // Calculate actual distances and filter
        candidates.mapNotNull { airport ->
            val distance = calculateDistance(latitude, longitude, airport.latitude, airport.longitude)
            if (distance <= radiusMiles) {
                AirportWithDistance(airport, distance)
            } else null
        }
            .sortedBy { it.distanceMiles }
            .take(limit)
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusMiles = 3959.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusMiles * c
    }

    // ========== RUNWAY OPERATIONS ==========

    /**
     * Get runways for airport
     */
    suspend fun getRunwaysForAirport(icao: String): List<Runway> = withContext(Dispatchers.IO) {
        airportDao.getRunwaysForAirport(icao.uppercase())
    }

    /**
     * Observe runways for real-time updates
     */
    fun observeRunwaysForAirport(icao: String): Flow<List<Runway>> {
        return airportDao.observeRunwaysForAirport(icao.uppercase())
    }

    /**
     * Get longest runway at airport
     */
    suspend fun getLongestRunway(icao: String): Runway? = withContext(Dispatchers.IO) {
        airportDao.getRunwaysForAirport(icao.uppercase())
            .maxByOrNull { it.length }
    }

    /**
     * Check if airport has runway meeting minimum length
     */
    suspend fun hasRunwayLongerThan(icao: String, minLength: Int): Boolean = withContext(Dispatchers.IO) {
        val longest = getLongestRunway(icao)
        longest?.length?.let { it >= minLength } ?: false
    }

    // ========== FREQUENCY OPERATIONS ==========

    /**
     * Get all frequencies for airport
     */
    suspend fun getFrequenciesForAirport(icao: String): List<Frequency> = withContext(Dispatchers.IO) {
        airportDao.getFrequenciesForAirport(icao.uppercase())
    }

    /**
     * Observe frequencies for real-time updates
     */
    fun observeFrequenciesForAirport(icao: String): Flow<List<Frequency>> {
        return airportDao.observeFrequenciesForAirport(icao.uppercase())
    }

    /**
     * Get specific frequency type (e.g., TOWER, GROUND, ATIS)
     */
    suspend fun getFrequency(icao: String, type: FrequencyType): Frequency? = withContext(Dispatchers.IO) {
        airportDao.getFrequencyByType(icao.uppercase(), type)
    }

    /**
     * Get communication frequencies (Tower, Ground, Clearance)
     */
    suspend fun getCommunicationFrequencies(icao: String): List<Frequency> = withContext(Dispatchers.IO) {
        val types = listOf(
            FrequencyType.TOWER,
            FrequencyType.GROUND,
            FrequencyType.CLEARANCE_DELIVERY,
            FrequencyType.APPROACH,
            FrequencyType.DEPARTURE
        )
        airportDao.getFrequenciesByTypes(icao.uppercase(), types)
    }

    /**
     * Get ATIS/AWOS/ASOS frequency
     */
    suspend fun getWeatherFrequency(icao: String): Frequency? = withContext(Dispatchers.IO) {
        val types = listOf(FrequencyType.ATIS, FrequencyType.AWOS, FrequencyType.ASOS)
        airportDao.getFrequenciesByTypes(icao.uppercase(), types).firstOrNull()
    }

    /**
     * Get CTAF/UNICOM for non-towered airports
     */
    suspend fun getCTAF(icao: String): Frequency? = withContext(Dispatchers.IO) {
        airportDao.getFrequencyByType(icao.uppercase(), FrequencyType.CTAF)
            ?: airportDao.getFrequencyByType(icao.uppercase(), FrequencyType.UNICOM)
    }

    // ========== BULK OPERATIONS ==========

    /**
     * Bulk insert airports (for initial database population)
     */
    suspend fun insertAirports(airports: List<Airport>) = withContext(Dispatchers.IO) {
        airportDao.insertAirports(airports)
    }

    /**
     * Bulk insert runways
     */
    suspend fun insertRunways(runways: List<Runway>) = withContext(Dispatchers.IO) {
        airportDao.insertRunways(runways)
    }

    /**
     * Bulk insert frequencies
     */
    suspend fun insertFrequencies(frequencies: List<Frequency>) = withContext(Dispatchers.IO) {
        airportDao.insertFrequencies(frequencies)
    }

    /**
     * Get database statistics
     */
    suspend fun getDatabaseStats(): AirportDatabaseStats = withContext(Dispatchers.IO) {
        AirportDatabaseStats(
            totalAirports = airportDao.getAirportCount(),
            toweredAirports = airportDao.getToweredAirports().size,
            // Add more stats as needed
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: AirportRepository? = null

        fun getInstance(airportDao: AirportDao): AirportRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AirportRepository(airportDao).also { INSTANCE = it }
            }
        }
    }
}

/**
 * Airport with calculated distance from a point
 */
data class AirportWithDistance(
    val airport: Airport,
    val distanceMiles: Double
)

/**
 * Airport database statistics
 */
data class AirportDatabaseStats(
    val totalAirports: Int,
    val toweredAirports: Int
)
