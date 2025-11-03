package com.example.flightdeck.data.local.dao

import androidx.room.*
import com.example.flightdeck.data.model.ATCScenario
import com.example.flightdeck.data.model.ATCScenarioType
import com.example.flightdeck.data.model.Difficulty
import com.example.flightdeck.data.model.ATCExchange
import com.example.flightdeck.data.model.ATCPracticeSession
import com.example.flightdeck.data.model.ATCResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface ATCDao {

    // ATC Scenarios
    @Query("SELECT * FROM atc_scenarios ORDER BY difficulty, title")
    fun getAllScenarios(): Flow<List<ATCScenario>>

    @Query("SELECT * FROM atc_scenarios WHERE id = :id")
    suspend fun getScenarioById(id: Long): ATCScenario?

    @Query("SELECT * FROM atc_scenarios WHERE scenarioType = :type")
    fun getScenariosByType(type: ATCScenarioType): Flow<List<ATCScenario>>

    @Query("SELECT * FROM atc_scenarios WHERE difficulty = :difficulty")
    fun getScenariosByDifficulty(difficulty: Difficulty): Flow<List<ATCScenario>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenario(scenario: ATCScenario): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenarios(scenarios: List<ATCScenario>)

    // ATC Exchanges
    @Query("SELECT * FROM atc_exchanges WHERE scenarioId = :scenarioId ORDER BY sequence")
    fun getScenarioExchanges(scenarioId: Long): Flow<List<ATCExchange>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchange(exchange: ATCExchange): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchanges(exchanges: List<ATCExchange>)

    // Practice Sessions
    @Query("SELECT * FROM atc_practice_sessions ORDER BY startTime DESC")
    fun getAllPracticeSessions(): Flow<List<ATCPracticeSession>>

    @Query("SELECT * FROM atc_practice_sessions WHERE id = :sessionId")
    suspend fun getPracticeSessionById(sessionId: Long): ATCPracticeSession?

    @Query("SELECT * FROM atc_practice_sessions WHERE scenarioId = :scenarioId ORDER BY startTime DESC")
    fun getSessionsByScenario(scenarioId: Long): Flow<List<ATCPracticeSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPracticeSession(session: ATCPracticeSession): Long

    @Update
    suspend fun updatePracticeSession(session: ATCPracticeSession)

    // Responses
    @Query("SELECT * FROM atc_responses WHERE sessionId = :sessionId ORDER BY timestamp")
    fun getSessionResponses(sessionId: Long): Flow<List<ATCResponse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponse(response: ATCResponse): Long

    @Query("DELETE FROM atc_responses WHERE sessionId = :sessionId")
    suspend fun deleteSessionResponses(sessionId: Long)
}
