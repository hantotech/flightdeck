package com.example.flightdeck.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ATIS (Automatic Terminal Information Service) or AWOS/ASOS broadcast
 * Simulated realistic weather readbacks for training
 */
@Entity(tableName = "atis_broadcasts")
data class ATISBroadcast(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val airportIcao: String,
    val broadcastType: BroadcastType,
    val informationCode: String,      // "Alpha", "Bravo", "Charlie", etc.
    val observationTime: String,      // "1856 Zulu"
    @Embedded(prefix = "weather_")
    val weatherReport: WeatherReport,
    val activeRunway: String?,        // "Runway 31"
    val approachesInUse: String?,     // "ILS Runway 31 approach in use"
    val notams: List<String> = emptyList(),
    val remarks: String? = null,
    val frequency: Double,            // MHz (e.g., 128.05)
    val timestamp: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

enum class BroadcastType {
    ATIS,   // Automated Terminal Information Service (towered airports)
    AWOS,   // Automated Weather Observing System
    ASOS    // Automated Surface Observing System
}

/**
 * ATIS information codes (phonetic alphabet)
 * Cycles through alphabet each hour
 */
object ATISInformationCode {
    private val codes = listOf(
        "Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot",
        "Golf", "Hotel", "India", "Juliet", "Kilo", "Lima",
        "Mike", "November", "Oscar", "Papa", "Quebec", "Romeo",
        "Sierra", "Tango", "Uniform", "Victor", "Whiskey", "X-ray",
        "Yankee", "Zulu"
    )

    fun getCurrentCode(): String {
        val hourOfDay = (System.currentTimeMillis() / (1000 * 60 * 60)) % 24
        return codes[hourOfDay.toInt() % codes.size]
    }

    fun getNextCode(currentCode: String): String {
        val currentIndex = codes.indexOf(currentCode)
        return if (currentIndex >= 0) {
            codes[(currentIndex + 1) % codes.size]
        } else {
            "Alpha"
        }
    }
}

/**
 * Generate realistic ATIS readback text
 */
object ATISGenerator {

    /**
     * Generate complete ATIS broadcast message
     */
    fun generateATISReadback(
        airport: String,
        airportName: String,
        broadcast: ATISBroadcast
    ): String {
        return buildString {
            // Opening
            append("$airportName ${broadcast.broadcastType.name} information ${broadcast.informationCode}. ")
            append("Time ${broadcast.observationTime}. ")

            // Weather
            append(generateWeatherReadback(broadcast.weatherReport))

            // Temperature and dewpoint
            broadcast.weatherReport.temperature?.let { temp ->
                append("Temperature $temp, ")
            }
            broadcast.weatherReport.dewpoint?.let { dew ->
                append("dewpoint $dew. ")
            }

            // Altimeter
            broadcast.weatherReport.altimeter?.let { alt ->
                append("Altimeter ${formatAltimeter(alt)}. ")
            }

            // Active runway
            if (broadcast.activeRunway != null) {
                append("${broadcast.activeRunway} in use. ")
            }

            // Approaches
            if (broadcast.approachesInUse != null) {
                append("${broadcast.approachesInUse}. ")
            }

            // NOTAMs
            if (broadcast.notams.isNotEmpty()) {
                append("Notices to airmen: ")
                broadcast.notams.forEach { notam ->
                    append("$notam. ")
                }
            }

            // Remarks
            if (broadcast.remarks != null) {
                append("Remarks: ${broadcast.remarks}. ")
            }

            // Closing
            append("Pilots read back all runway hold short instructions. ")
            append("Advise on initial contact you have information ${broadcast.informationCode}.")
        }
    }

    /**
     * Generate AWOS/ASOS readback (simpler than ATIS)
     */
    fun generateAWOSReadback(
        airport: String,
        broadcast: ATISBroadcast
    ): String {
        return buildString {
            // Opening
            append("$airport automated weather, ")
            append("observation ${broadcast.observationTime}. ")

            // Weather
            append(generateWeatherReadback(broadcast.weatherReport))

            // Temperature and dewpoint
            broadcast.weatherReport.temperature?.let { temp ->
                append("Temperature $temp, ")
            }
            broadcast.weatherReport.dewpoint?.let { dew ->
                append("dewpoint $dew. ")
            }

            // Altimeter
            broadcast.weatherReport.altimeter?.let { alt ->
                append("Altimeter ${formatAltimeter(alt)}.")
            }
        }
    }

    /**
     * Generate weather portion of broadcast
     */
    private fun generateWeatherReadback(weather: WeatherReport): String {
        return buildString {
            // Wind
            val windSpeed = weather.windSpeed ?: 0
            if (windSpeed > 0) {
                weather.windDirection?.let { dir ->
                    append("Wind ${String.format("%03d", dir)} ")
                    append("at $windSpeed")
                    weather.windGust?.let { gust ->
                        append(", gusts $gust")
                    }
                    append(". ")
                }
            } else {
                append("Wind calm. ")
            }

            // Visibility
            weather.visibility?.let { vis ->
                if (vis >= 10.0) {
                    append("Visibility one zero or greater. ")
                } else {
                    append("Visibility ${formatVisibility(vis)}. ")
                }
            }

            // Sky conditions
            if (weather.skyConditions.isEmpty()) {
                append("Sky clear. ")
            } else {
                weather.skyConditions.forEach { condition ->
                    append("${condition.coverage.readback} ")
                    condition.altitude?.let { alt ->
                        append("${formatAltitude(alt)}. ")
                    }
                }
            }

            // Weather phenomena
            weather.weatherConditions.forEach { wx ->
                append("$wx. ")
            }
        }
    }

    private fun formatAltimeter(altimeter: Double): String {
        // Convert 30.12 to "three zero one two"
        val digits = String.format("%.2f", altimeter).replace(".", "")
        return digits.map { digitToWord(it) }.joinToString(" ")
    }

    private fun formatVisibility(visibility: Double): String {
        return when {
            visibility >= 10.0 -> "one zero or greater"
            visibility == visibility.toInt().toDouble() -> {
                "${visibility.toInt()}".map { digitToWord(it) }.joinToString(" ")
            }
            else -> {
                val whole = visibility.toInt()
                val fraction = visibility - whole
                val fractionStr = when {
                    fraction >= 0.75 -> "three quarters"
                    fraction >= 0.5 -> "one half"
                    fraction >= 0.25 -> "one quarter"
                    else -> ""
                }
                if (whole > 0) {
                    "$whole and $fractionStr"
                } else {
                    fractionStr
                }
            }
        }
    }

    private fun formatAltitude(altitude: Int): String {
        // Convert 5000 to "five thousand"
        return when {
            altitude >= 10000 -> "${altitude / 1000} thousand ${altitude % 1000}"
            altitude >= 1000 -> "${altitude / 1000} thousand"
            altitude >= 100 -> "${altitude / 100} hundred"
            else -> "$altitude"
        }.trim()
    }

    private fun digitToWord(digit: Char): String {
        return when (digit) {
            '0' -> "zero"
            '1' -> "one"
            '2' -> "two"
            '3' -> "three"
            '4' -> "four"
            '5' -> "five"
            '6' -> "six"
            '7' -> "seven"
            '8' -> "eight"
            '9' -> "nine"
            else -> ""
        }
    }
}

/**
 * Sample ATIS generator for training scenarios
 */
object SampleATISGenerator {

    fun generateSampleATIS(
        airport: Airport,
        weather: WeatherReport,
        activeRunway: String? = null
    ): ATISBroadcast {
        val informationCode = ATISInformationCode.getCurrentCode()
        val broadcastType = if (airport.towerControlled) BroadcastType.ATIS else BroadcastType.AWOS

        // Generate observation time in Zulu
        val hour = (System.currentTimeMillis() / (1000 * 60 * 60)) % 24
        val minute = (System.currentTimeMillis() / (1000 * 60)) % 60
        val observationTime = String.format("%02d%02d Zulu", hour, minute)

        // Sample NOTAMs based on airport
        val notams = mutableListOf<String>()
        if (!airport.towerControlled) {
            notams.add("Airport is uncontrolled")
        }

        return ATISBroadcast(
            airportIcao = airport.icao,
            broadcastType = broadcastType,
            informationCode = informationCode,
            observationTime = observationTime,
            weatherReport = weather,
            activeRunway = activeRunway,
            approachesInUse = if (weather.flightCategory == FlightCategory.IFR && activeRunway != null) {
                "ILS Runway $activeRunway approach in use"
            } else null,
            notams = notams,
            remarks = when (weather.flightCategory) {
                FlightCategory.LIFR -> "Low IFR conditions"
                FlightCategory.IFR -> "IFR conditions"
                FlightCategory.MVFR -> "Marginal VFR"
                FlightCategory.VFR -> null
            },
            frequency = 128.0 + (airport.icao.hashCode() % 100) / 100.0
        )
    }

    /**
     * Generate realistic ATIS with voice-like text
     */
    fun generateVoiceReadback(
        airport: Airport,
        airportName: String,
        broadcast: ATISBroadcast
    ): String {
        return when (broadcast.broadcastType) {
            BroadcastType.ATIS -> ATISGenerator.generateATISReadback(airportName, airportName, broadcast)
            BroadcastType.AWOS, BroadcastType.ASOS -> ATISGenerator.generateAWOSReadback(airport.icao, broadcast)
        }
    }
}
