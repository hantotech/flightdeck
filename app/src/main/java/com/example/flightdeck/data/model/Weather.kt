package com.example.flightdeck.data.model

/**
 * Represents weather information for an airport (METAR data)
 */
data class WeatherReport(
    val airport: String,
    val observationTime: String,
    val rawMetar: String,
    val temperature: Int?, // Celsius
    val dewpoint: Int?, // Celsius
    val windDirection: Int?, // degrees
    val windSpeed: Int?, // knots
    val windGust: Int?, // knots
    val visibility: Double?, // statute miles
    val altimeter: Double?, // inHg
    val flightCategory: FlightCategory,
    val skyConditions: List<SkyCondition>,
    val weatherConditions: List<String> = emptyList()
)

enum class FlightCategory {
    VFR,    // Visual Flight Rules - Good conditions
    MVFR,   // Marginal VFR - Marginal conditions
    IFR,    // Instrument Flight Rules - Poor conditions
    LIFR    // Low IFR - Very poor conditions
}

data class SkyCondition(
    val coverage: SkyCoverage,
    val altitude: Int? // feet AGL
)

enum class SkyCoverage {
    SKC,  // Sky Clear
    FEW,  // Few clouds (1/8 to 2/8)
    SCT,  // Scattered (3/8 to 4/8)
    BKN,  // Broken (5/8 to 7/8)
    OVC,  // Overcast (8/8)
    OVX   // Obscured
}

/**
 * Terminal Aerodrome Forecast (TAF)
 */
data class ForecastReport(
    val airport: String,
    val issueTime: String,
    val validPeriod: String,
    val rawTaf: String,
    val forecasts: List<PeriodForecast>
)

data class PeriodForecast(
    val timeFrom: String,
    val timeTo: String,
    val windDirection: Int?,
    val windSpeed: Int?,
    val visibility: Double?,
    val skyConditions: List<SkyCondition>,
    val changeIndicator: String? = null // TEMPO, BECMG, FM, PROB
)
