package com.example.flightdeck.data.local.dao

import androidx.room.*
import com.example.flightdeck.data.knowledge.AviationDocument
import com.example.flightdeck.data.knowledge.DocumentType
import kotlinx.coroutines.flow.Flow

@Dao
interface KnowledgeDao {

    @Query("SELECT * FROM aviation_documents WHERE verified = 1")
    fun getAllVerifiedDocuments(): Flow<List<AviationDocument>>

    @Query("SELECT * FROM aviation_documents WHERE type = :type AND verified = 1")
    fun getDocumentsByType(type: DocumentType): Flow<List<AviationDocument>>

    @Query("""
        SELECT * FROM aviation_documents
        WHERE keywords LIKE '%' || :keyword || '%'
        AND verified = 1
        ORDER BY lastUpdated DESC
    """)
    suspend fun searchByKeyword(keyword: String): List<AviationDocument>

    @Query("""
        SELECT * FROM aviation_documents
        WHERE (keywords LIKE '%' || :query || '%'
           OR title LIKE '%' || :query || '%'
           OR content LIKE '%' || :query || '%')
        AND verified = 1
        ORDER BY lastUpdated DESC
        LIMIT :limit
    """)
    suspend fun searchDocuments(query: String, limit: Int = 5): List<AviationDocument>

    @Query("""
        SELECT * FROM aviation_documents
        WHERE airport = :airport
        AND verified = 1
    """)
    suspend fun getAirportSpecificDocs(airport: String): List<AviationDocument>

    @Query("""
        SELECT * FROM aviation_documents
        WHERE aircraft = :aircraft
        AND verified = 1
    """)
    suspend fun getAircraftSpecificDocs(aircraft: String): List<AviationDocument>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: AviationDocument): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<AviationDocument>)

    @Update
    suspend fun updateDocument(document: AviationDocument)

    @Delete
    suspend fun deleteDocument(document: AviationDocument)

    @Query("DELETE FROM aviation_documents WHERE verified = 0")
    suspend fun deleteUnverifiedDocuments()
}
