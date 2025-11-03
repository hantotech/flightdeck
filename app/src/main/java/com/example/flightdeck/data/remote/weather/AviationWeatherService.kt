package com.example.flightdeck.data.remote.weather

import com.example.flightdeck.data.model.WeatherReport
import com.example.flightdeck.data.model.ForecastReport
import com.example.flightdeck.data.model.FlightCategory
import com.example.flightdeck.data.model.SkyCondition
import com.example.flightdeck.data.model.SkyCoverage
import com.example.flightdeck.data.model.PeriodForecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Service for fetching aviation weather data (METAR/TAF)
 * Uses Aviation Weather Center API (aviationweather.gov)
 */
class AviationWeatherService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    /**
     * Fetch METAR weather report for an airport
     */
    suspend fun getMetar(icaoCode: String): Result<WeatherReport> = withContext(Dispatchers.IO) {
        try {
            val url = "https://aviationweather.gov/api/data/metar?ids=$icaoCode&format=json"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Weather fetch failed: ${response.code}"))
                }

                val body = response.body?.string() ?: ""
                val weatherReport = parseMetarJson(icaoCode, body)
                Result.success(weatherReport)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetch TAF forecast for an airport
     */
    suspend fun getTaf(icaoCode: String): Result<ForecastReport> = withContext(Dispatchers.IO) {
        try {
            val url = "https://aviationweather.gov/api/data/taf?ids=$icaoCode&format=json"

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("TAF fetch failed: ${response.code}"))
                }

                val body = response.body?.string() ?: ""
                val forecast = parseTafJson(icaoCode, body)
                Result.success(forecast)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get weather briefing combining METAR and TAF
     */
    suspend fun getWeatherBriefing(icaoCode: String): Result<WeatherBriefing> =
        withContext(Dispatchers.IO) {
            try {
                val metarResult = getMetar(icaoCode)
                val tafResult = getTaf(icaoCode)

                val metar = metarResult.getOrNull()
                val taf = tafResult.getOrNull()

                if (metar == null) {
                    return@withContext Result.failure(
                        Exception("Unable to fetch current weather")
                    )
                }

                Result.success(
                    WeatherBriefing(
                        airport = icaoCode,
                        currentWeather = metar,
                        forecast = taf,
                        briefingTime = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun parseMetarJson(icao: String, json: String): WeatherReport {
        // Simplified parser - in production, use proper JSON parsing with Gson/Moshi
        // For now, return sample data
        return WeatherReport(
            airport = icao,
            observationTime = "Current",
            rawMetar = "METAR $icao 121856Z 09008KT 10SM FEW050 BKN250 23/14 A3012",
            temperature = 23,
            dewpoint = 14,
            windDirection = 90,
            windSpeed = 8,
            windGust = null,
            visibility = 10.0,
            altimeter = 30.12,
            flightCategory = FlightCategory.VFR,
            skyConditions = listOf(
                SkyCondition(SkyCoverage.FEW, 5000),
                SkyCondition(SkyCoverage.BKN, 25000)
            ),
            weatherConditions = emptyList()
        )
    }

    private fun parseTafJson(icao: String, json: String): ForecastReport {
        // Simplified parser - in production, use proper JSON parsing
        return ForecastReport(
            airport = icao,
            issueTime = "Current",
            validPeriod = "12 hours",
            rawTaf = "TAF $icao 121720Z 1218/1318 09008KT P6SM FEW050 BKN250",
            forecasts = listOf(
                PeriodForecast(
                    timeFrom = "18:00Z",
                    timeTo = "06:00Z",
                    windDirection = 90,
                    windSpeed = 8,
                    visibility = 10.0,
                    skyConditions = listOf(
                        SkyCondition(SkyCoverage.FEW, 5000)
                    )
                )
            )
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: AviationWeatherService? = null

        fun getInstance(): AviationWeatherService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AviationWeatherService().also { INSTANCE = it }
            }
        }
    }
}

/**
 * Combined weather briefing
 */
data class WeatherBriefing(
    val airport: String,
    val currentWeather: WeatherReport,
    val forecast: ForecastReport?,
    val briefingTime: Long
)
