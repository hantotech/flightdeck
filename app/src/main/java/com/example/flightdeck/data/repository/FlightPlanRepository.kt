package com.example.flightdeck.data.repository

import com.example.flightdeck.data.local.dao.FlightPlanDao
import com.example.flightdeck.data.model.FlightPlan
import com.example.flightdeck.data.model.Waypoint
import com.example.flightdeck.data.remote.ai.EnhancedAIService
import com.example.flightdeck.data.remote.weather.AviationWeatherService
import com.example.flightdeck.data.remote.weather.WeatherBriefing
import kotlinx.coroutines.flow.Flow

/**
 * Repository for flight planning with AI assistance using smart routing
 */
class FlightPlanRepository(
    private val flightPlanDao: FlightPlanDao,
    private val aiService: EnhancedAIService = EnhancedAIService.getInstance(),
    private val weatherService: AviationWeatherService = AviationWeatherService.getInstance()
) {

    // Flight Plans
    fun getAllFlightPlans(): Flow<List<FlightPlan>> = flightPlanDao.getAllFlightPlans()

    fun getFlightPlansByAircraft(aircraftId: Long): Flow<List<FlightPlan>> =
        flightPlanDao.getFlightPlansByAircraft(aircraftId)

    suspend fun getFlightPlanById(id: Long): FlightPlan? =
        flightPlanDao.getFlightPlanById(id)

    suspend fun getActiveFlightPlan(): FlightPlan? =
        flightPlanDao.getActiveFlightPlan()

    suspend fun createFlightPlan(flightPlan: FlightPlan): Long {
        return flightPlanDao.insertFlightPlan(flightPlan)
    }

    suspend fun updateFlightPlan(flightPlan: FlightPlan) {
        flightPlanDao.updateFlightPlan(flightPlan)
    }

    suspend fun deleteFlightPlan(id: Long) {
        flightPlanDao.deleteFlightPlanById(id)
    }

    // Waypoints
    fun getWaypoints(flightPlanId: Long): Flow<List<Waypoint>> =
        flightPlanDao.getWaypoints(flightPlanId)

    suspend fun addWaypoint(waypoint: Waypoint): Long {
        return flightPlanDao.insertWaypoint(waypoint)
    }

    // Weather Integration
    suspend fun getWeatherBriefing(icaoCode: String): Result<WeatherBriefing> {
        return weatherService.getWeatherBriefing(icaoCode)
    }

    // AI Flight Planning Assistance
    suspend fun getFlightPlanningAdvice(
        departure: String,
        arrival: String,
        aircraft: String
    ): Result<String> {
        // Get weather for departure and arrival
        val departureWeather = weatherService.getMetar(departure).getOrNull()
        val arrivalWeather = weatherService.getMetar(arrival).getOrNull()

        val weatherSummary = buildString {
            if (departureWeather != null) {
                append("Departure ($departure): ${departureWeather.flightCategory}, ")
                append("Wind ${departureWeather.windDirection}°/${departureWeather.windSpeed}kt, ")
                append("Vis ${departureWeather.visibility}SM. ")
            }
            if (arrivalWeather != null) {
                append("Arrival ($arrival): ${arrivalWeather.flightCategory}, ")
                append("Wind ${arrivalWeather.windDirection}°/${arrivalWeather.windSpeed}kt.")
            }
        }

        return aiService.getFlightPlanningAdvice(
            departure = departure,
            arrival = arrival,
            aircraft = aircraft,
            weatherConditions = weatherSummary.ifEmpty { null }
        )
    }

    /**
     * Calculate estimated flight time and fuel requirements
     */
    fun calculateFlightMetrics(
        distance: Double, // nautical miles
        cruiseSpeed: Int, // knots
        fuelBurnRate: Double // gallons per hour
    ): FlightMetrics {
        val flightTimeHours = distance / cruiseSpeed
        val flightTimeMinutes = (flightTimeHours * 60).toInt()
        val fuelRequired = (flightTimeHours * fuelBurnRate) * 1.5 // 50% reserve

        return FlightMetrics(
            estimatedFlightTime = flightTimeMinutes,
            fuelRequired = fuelRequired,
            distance = distance
        )
    }
}

data class FlightMetrics(
    val estimatedFlightTime: Int, // minutes
    val fuelRequired: Double, // gallons
    val distance: Double // nautical miles
)
