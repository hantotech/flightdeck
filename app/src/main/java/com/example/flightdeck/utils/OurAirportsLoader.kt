package com.example.flightdeck.utils

import android.content.Context
import com.example.flightdeck.data.local.FlightDeckDatabase
import com.example.flightdeck.data.model.Airport
import com.example.flightdeck.data.model.AirportType
import com.example.flightdeck.data.model.AirspaceClass
import com.example.flightdeck.data.model.Runway
import com.example.flightdeck.data.model.SurfaceType
import com.example.flightdeck.data.model.Frequency
import com.example.flightdeck.data.model.FrequencyType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Loads OurAirports CSV data for premium tier
 *
 * Data Source: https://ourairports.com/data/
 * License: Public Domain
 *
 * CSV Files Required (place in assets/ourairports/):
 * - airports.csv (~60K airports worldwide)
 * - runways.csv (runway details)
 * - airport-frequencies.csv (communication frequencies)
 *
 * This loader:
 * 1. Reads CSV files from assets
 * 2. Filters to US + Canada only
 * 3. Excludes closed airports
 * 4. Marks all as isPremium = true
 * 5. Bulk inserts into database
 *
 * Expected result: ~20,000 US/Canada airports
 */
object OurAirportsLoader {

    /**
     * Load premium airports from OurAirports CSV
     * Call this when user purchases premium tier
     */
    suspend fun loadPremiumAirports(context: Context): Result<PremiumLoadResult> = withContext(Dispatchers.IO) {
        try {
            val db = FlightDeckDatabase.getDatabase(context)

            // Check if already loaded
            val existingPremiumCount = db.airportDao().getPremiumAirportCount()
            if (existingPremiumCount > 0) {
                return@withContext Result.success(
                    PremiumLoadResult(
                        airportsLoaded = existingPremiumCount,
                        runwaysLoaded = 0,
                        frequenciesLoaded = 0,
                        alreadyLoaded = true
                    )
                )
            }

            // Load CSV files
            val airports = loadAirportsCSV(context)
            val runways = loadRunwaysCSV(context)
            val frequencies = loadFrequenciesCSV(context)

            // Bulk insert
            db.airportDao().insertAirports(airports)
            db.airportDao().insertRunways(runways)
            db.airportDao().insertFrequencies(frequencies)

            Result.success(
                PremiumLoadResult(
                    airportsLoaded = airports.size,
                    runwaysLoaded = runways.size,
                    frequenciesLoaded = frequencies.size,
                    alreadyLoaded = false
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Load airports from CSV
     * Format: id,ident,type,name,latitude_deg,longitude_deg,elevation_ft,continent,iso_country,iso_region,municipality,scheduled_service,gps_code,iata_code,local_code,home_link,wikipedia_link,keywords
     */
    private fun loadAirportsCSV(context: Context): List<Airport> {
        val airports = mutableListOf<Airport>()

        try {
            val inputStream = context.assets.open("ourairports/airports.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Skip header
            reader.readLine()

            reader.forEachLine { line ->
                try {
                    val airport = parseAirportLine(line)
                    if (airport != null) {
                        airports.add(airport)
                    }
                } catch (e: Exception) {
                    // Skip malformed lines
                }
            }

            reader.close()
        } catch (e: Exception) {
            // CSV file not found (expected during development)
            // TODO: Add proper error handling for production
        }

        return airports
    }

    /**
     * Parse a single airport CSV line
     */
    private fun parseAirportLine(line: String): Airport? {
        val fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())

        // Filter: US and Canada only
        val country = fields.getOrNull(8)?.trim('"') ?: return null
        if (country != "US" && country != "CA") return null

        // Filter: Exclude closed airports
        val type = fields.getOrNull(2)?.trim('"') ?: return null
        if (type == "closed") return null

        // Extract fields
        val icao = fields.getOrNull(1)?.trim('"') ?: return null
        if (icao.length != 4) return null // Must be 4-letter ICAO

        val name = fields.getOrNull(3)?.trim('"') ?: return null
        val latitude = fields.getOrNull(4)?.toDoubleOrNull() ?: return null
        val longitude = fields.getOrNull(5)?.toDoubleOrNull() ?: return null
        val elevation = fields.getOrNull(6)?.toIntOrNull() ?: 0
        val municipality = fields.getOrNull(10)?.trim('"') ?: ""
        val iataCode = fields.getOrNull(13)?.trim('"')?.takeIf { it.isNotEmpty() }
        val region = fields.getOrNull(9)?.trim('"')?.split("-")?.getOrNull(1) // Extract state from "US-CA"

        // Determine airport type
        val airportType = when (type) {
            "large_airport" -> AirportType.LARGE_AIRPORT
            "medium_airport" -> AirportType.MEDIUM_AIRPORT
            "small_airport" -> AirportType.SMALL_AIRPORT
            "heliport" -> AirportType.HELIPORT
            "seaplane_base" -> AirportType.SEAPLANE_BASE
            else -> AirportType.SMALL_AIRPORT
        }

        // Assume not towered for most GA airports (can be overridden with frequency data)
        val towerControlled = airportType == AirportType.LARGE_AIRPORT || airportType == AirportType.MEDIUM_AIRPORT

        // Default to Class E for small airports
        val airspaceClass = when (airportType) {
            AirportType.LARGE_AIRPORT -> AirspaceClass.B
            AirportType.MEDIUM_AIRPORT -> AirspaceClass.C
            else -> AirspaceClass.E
        }

        return Airport(
            icao = icao,
            iata = iataCode,
            name = name,
            city = municipality,
            state = region,
            country = if (country == "US") "USA" else "Canada",
            latitude = latitude,
            longitude = longitude,
            elevation = elevation,
            airspaceClass = airspaceClass,
            towerControlled = towerControlled,
            type = airportType,
            isPremium = true // Mark as premium
        )
    }

    /**
     * Load runways from CSV
     * Format: id,airport_ref,airport_ident,length_ft,width_ft,surface,lighted,closed,le_ident,le_latitude_deg,le_longitude_deg,le_elevation_ft,le_heading_degT,he_ident,he_latitude_deg,he_longitude_deg,he_elevation_ft,he_heading_degT
     */
    private fun loadRunwaysCSV(context: Context): List<Runway> {
        val runways = mutableListOf<Runway>()

        try {
            val inputStream = context.assets.open("ourairports/runways.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Skip header
            reader.readLine()

            reader.forEachLine { line ->
                try {
                    val runway = parseRunwayLine(line)
                    if (runway != null) {
                        runways.add(runway)
                    }
                } catch (e: Exception) {
                    // Skip malformed lines
                }
            }

            reader.close()
        } catch (e: Exception) {
            // CSV file not found (expected during development)
        }

        return runways
    }

    /**
     * Parse a single runway CSV line
     */
    private fun parseRunwayLine(line: String): Runway? {
        val fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())

        val airportIcao = fields.getOrNull(2)?.trim('"') ?: return null
        if (airportIcao.length != 4) return null

        val lengthFt = fields.getOrNull(3)?.toIntOrNull() ?: return null
        val widthFt = fields.getOrNull(4)?.toIntOrNull() ?: return null
        val surfaceStr = fields.getOrNull(5)?.trim('"')?.lowercase() ?: "unknown"
        val lighted = fields.getOrNull(6)?.trim('"') == "1"
        val closed = fields.getOrNull(7)?.trim('"') == "1"

        if (closed) return null // Skip closed runways

        val leIdent = fields.getOrNull(8)?.trim('"') ?: return null
        val heIdent = fields.getOrNull(13)?.trim('"') ?: return null

        val surface = when {
            surfaceStr.contains("asph") -> SurfaceType.ASPHALT
            surfaceStr.contains("conc") -> SurfaceType.CONCRETE
            surfaceStr.contains("grass") -> SurfaceType.GRASS
            surfaceStr.contains("dirt") -> SurfaceType.DIRT
            surfaceStr.contains("gravel") -> SurfaceType.GRAVEL
            surfaceStr.contains("turf") -> SurfaceType.TURF
            surfaceStr.contains("water") -> SurfaceType.WATER
            else -> SurfaceType.UNKNOWN
        }

        return Runway(
            airportIcao = airportIcao,
            identifier = "$leIdent/$heIdent",
            length = lengthFt,
            width = widthFt,
            surface = surface,
            lighted = lighted,
            closedMarking = false,
            leIdentifier = leIdent,
            leLatitude = fields.getOrNull(9)?.toDoubleOrNull(),
            leLongitude = fields.getOrNull(10)?.toDoubleOrNull(),
            leElevation = fields.getOrNull(11)?.toIntOrNull(),
            leHeading = fields.getOrNull(12)?.toIntOrNull(),
            heIdentifier = heIdent,
            heLatitude = fields.getOrNull(14)?.toDoubleOrNull(),
            heLongitude = fields.getOrNull(15)?.toDoubleOrNull(),
            heElevation = fields.getOrNull(16)?.toIntOrNull(),
            heHeading = fields.getOrNull(17)?.toIntOrNull()
        )
    }

    /**
     * Load frequencies from CSV
     * Format: id,airport_ref,airport_ident,type,description,frequency_mhz
     */
    private fun loadFrequenciesCSV(context: Context): List<Frequency> {
        val frequencies = mutableListOf<Frequency>()

        try {
            val inputStream = context.assets.open("ourairports/airport-frequencies.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Skip header
            reader.readLine()

            reader.forEachLine { line ->
                try {
                    val frequency = parseFrequencyLine(line)
                    if (frequency != null) {
                        frequencies.add(frequency)
                    }
                } catch (e: Exception) {
                    // Skip malformed lines
                }
            }

            reader.close()
        } catch (e: Exception) {
            // CSV file not found (expected during development)
        }

        return frequencies
    }

    /**
     * Parse a single frequency CSV line
     */
    private fun parseFrequencyLine(line: String): Frequency? {
        val fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())

        val airportIcao = fields.getOrNull(2)?.trim('"') ?: return null
        if (airportIcao.length != 4) return null

        val typeStr = fields.getOrNull(3)?.trim('"')?.uppercase() ?: return null
        val description = fields.getOrNull(4)?.trim('"') ?: ""
        val frequencyMhz = fields.getOrNull(5)?.toDoubleOrNull() ?: return null

        val frequencyType = when {
            typeStr.contains("TWR") || typeStr.contains("TOWER") -> FrequencyType.TOWER
            typeStr.contains("GND") || typeStr.contains("GROUND") -> FrequencyType.GROUND
            typeStr.contains("CLNC") || typeStr.contains("CLEARANCE") -> FrequencyType.CLEARANCE_DELIVERY
            typeStr.contains("APP") || typeStr.contains("APPROACH") -> FrequencyType.APPROACH
            typeStr.contains("DEP") || typeStr.contains("DEPARTURE") -> FrequencyType.DEPARTURE
            typeStr.contains("ATIS") -> FrequencyType.ATIS
            typeStr.contains("UNICOM") -> FrequencyType.UNICOM
            typeStr.contains("CTAF") -> FrequencyType.CTAF
            typeStr.contains("MULTICOM") -> FrequencyType.MULTICOM
            typeStr.contains("FSS") -> FrequencyType.FSS
            typeStr.contains("AWOS") -> FrequencyType.AWOS
            typeStr.contains("ASOS") -> FrequencyType.ASOS
            else -> return null // Skip unknown types
        }

        return Frequency(
            airportIcao = airportIcao,
            type = frequencyType,
            description = description,
            frequency = frequencyMhz
        )
    }
}

/**
 * Result of premium airport loading
 */
data class PremiumLoadResult(
    val airportsLoaded: Int,
    val runwaysLoaded: Int,
    val frequenciesLoaded: Int,
    val alreadyLoaded: Boolean
)

/**
 * Extension function to count premium airports
 */
suspend fun com.example.flightdeck.data.local.dao.AirportDao.getPremiumAirportCount(): Int {
    // TODO: Add this query to AirportDao:
    // @Query("SELECT COUNT(*) FROM airports WHERE isPremium = 1")
    // suspend fun getPremiumAirportCount(): Int
    return 0 // Placeholder
}
