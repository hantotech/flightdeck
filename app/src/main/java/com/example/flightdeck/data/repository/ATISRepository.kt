package com.example.flightdeck.data.repository

import com.example.flightdeck.data.local.dao.ATISDao
import com.example.flightdeck.data.model.ATISBroadcast
import com.example.flightdeck.data.model.BroadcastType
import com.example.flightdeck.data.model.ATISGenerator
import com.example.flightdeck.data.model.SampleATISGenerator
import com.example.flightdeck.data.model.Runway
import com.example.flightdeck.data.model.ATISInformationCode
import com.example.flightdeck.data.model.WeatherReport
import com.example.flightdeck.data.remote.weather.AviationWeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository for ATIS/AWOS broadcasts
 * Integrates with weather service and airport data
 */
class ATISRepository(
    private val atisDao: ATISDao,
    private val airportRepository: AirportRepository,
    private val weatherService: AviationWeatherService
) {

    /**
     * Get current ATIS/AWOS for airport
     * Generates new broadcast if none exists or if outdated
     */
    suspend fun getCurrentATIS(icao: String): Result<ATISBroadcast> = withContext(Dispatchers.IO) {
        try {
            // Check for existing current ATIS
            val existing = atisDao.getCurrentATIS(icao.uppercase())

            // If exists and less than 1 hour old, return it
            if (existing != null && !isATISOutdated(existing)) {
                return@withContext Result.success(existing)
            }

            // Generate new ATIS
            generateNewATIS(icao.uppercase())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Observe current ATIS with real-time updates
     */
    fun observeCurrentATIS(icao: String): Flow<ATISBroadcast?> {
        return atisDao.observeCurrentATIS(icao.uppercase())
    }

    /**
     * Get ATIS readback text for pilot to listen to
     */
    suspend fun getATISReadback(icao: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val atis = getCurrentATIS(icao).getOrThrow()
            val airportData = airportRepository.getAirportByIcao(icao).getOrThrow()

            val readback = when (atis.broadcastType) {
                BroadcastType.ATIS -> ATISGenerator.generateATISReadback(
                    airport = icao,
                    airportName = airportData.airport.name,
                    broadcast = atis
                )
                BroadcastType.AWOS, BroadcastType.ASOS -> ATISGenerator.generateAWOSReadback(
                    airport = icao,
                    broadcast = atis
                )
            }

            Result.success(readback)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get ATIS information code (for pilot readback)
     * Example: "Information Alpha"
     */
    suspend fun getATISInformationCode(icao: String): String? {
        return atisDao.getCurrentATIS(icao.uppercase())?.informationCode
    }

    /**
     * Generate new ATIS broadcast with current weather
     */
    suspend fun generateNewATIS(
        icao: String,
        activeRunway: String? = null
    ): Result<ATISBroadcast> = withContext(Dispatchers.IO) {
        try {
            // Get airport data
            val airportData = airportRepository.getAirportByIcao(icao).getOrThrow()

            // Get current weather
            val weather = weatherService.getMetar(icao).getOrThrow()

            // Determine active runway if not provided
            val runway = activeRunway ?: weather.windDirection?.let { windDir ->
                determineActiveRunway(airportData.runways, windDir)
            }

            // Generate ATIS
            val atis = SampleATISGenerator.generateSampleATIS(
                airport = airportData.airport,
                weather = weather,
                activeRunway = runway
            )

            // Save to database (deactivates old ATIS)
            atisDao.updateCurrentATIS(icao, atis)

            Result.success(atis)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Determine active runway based on wind
     * Pilots use the runway that is most aligned with wind direction
     */
    private fun determineActiveRunway(runways: List<Runway>, windDirection: Int): String? {
        if (runways.isEmpty()) return null

        // Find runway with heading closest to wind direction
        val bestRunway = runways.minByOrNull { runway ->
            val leHeading = runway.leHeading ?: 0
            val heHeading = runway.heHeading ?: 180

            val leDiff = minOf(
                Math.abs(leHeading - windDirection),
                360 - Math.abs(leHeading - windDirection)
            )
            val heDiff = minOf(
                Math.abs(heHeading - windDirection),
                360 - Math.abs(heHeading - windDirection)
            )

            minOf(leDiff, heDiff)
        }

        // Determine which end to use
        return bestRunway?.let { runway ->
            val leHeading = runway.leHeading ?: 0
            val heHeading = runway.heHeading ?: 180

            val leDiff = minOf(
                Math.abs(leHeading - windDirection),
                360 - Math.abs(leHeading - windDirection)
            )
            val heDiff = minOf(
                Math.abs(heHeading - windDirection),
                360 - Math.abs(heHeading - windDirection)
            )

            if (leDiff < heDiff) runway.leIdentifier else runway.heIdentifier
        }
    }

    /**
     * Check if ATIS is outdated (>1 hour old)
     */
    private fun isATISOutdated(atis: ATISBroadcast): Boolean {
        val oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000)
        return atis.timestamp < oneHourAgo
    }

    /**
     * Force refresh ATIS (useful for testing or training scenarios)
     */
    suspend fun refreshATIS(icao: String): Result<ATISBroadcast> = withContext(Dispatchers.IO) {
        try {
            // Deactivate old ATIS
            atisDao.deactivateAllATIS(icao.uppercase())

            // Generate new ATIS with next information code
            val oldCode = atisDao.getATISHistory(icao, 1).firstOrNull()?.informationCode
            val newCode = if (oldCode != null) {
                ATISInformationCode.getNextCode(oldCode)
            } else {
                ATISInformationCode.getCurrentCode()
            }

            generateNewATIS(icao)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get ATIS history for airport
     */
    suspend fun getATISHistory(icao: String, limit: Int = 10): List<ATISBroadcast> = withContext(Dispatchers.IO) {
        atisDao.getATISHistory(icao.uppercase(), limit)
    }

    /**
     * Cleanup old ATIS broadcasts (older than 24 hours)
     */
    suspend fun cleanupOldATIS() = withContext(Dispatchers.IO) {
        val oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
        atisDao.deactivateOldATIS(oneDayAgo)
    }

    /**
     * Get ATIS frequency for airport
     */
    suspend fun getATISFrequency(icao: String): Double? = withContext(Dispatchers.IO) {
        airportRepository.getWeatherFrequency(icao)?.frequency
    }

    companion object {
        @Volatile
        private var INSTANCE: ATISRepository? = null

        fun getInstance(
            atisDao: ATISDao,
            airportRepository: AirportRepository,
            weatherService: AviationWeatherService
        ): ATISRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ATISRepository(atisDao, airportRepository, weatherService).also {
                    INSTANCE = it
                }
            }
        }
    }
}

/**
 * Extension function to format ATIS for checklist display
 */
fun ATISBroadcast.toChecklistSummary(): String {
    return buildString {
        append("Information $informationCode - ")
        append("${weatherReport.windDirection}°/${weatherReport.windSpeed}kt, ")
        append("${weatherReport.visibility}SM vis, ")
        append("${weatherReport.temperature}°C, ")
        append("${weatherReport.altimeter} in")
        if (activeRunway != null) {
            append(", Runway $activeRunway")
        }
    }
}
