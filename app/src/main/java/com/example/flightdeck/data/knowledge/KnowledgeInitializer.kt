package com.example.flightdeck.data.knowledge

import com.example.flightdeck.data.local.dao.KnowledgeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Initializes the aviation knowledge base with FAR/AIM content
 * This should be run once on first app launch
 */
class KnowledgeInitializer(
    private val knowledgeDao: KnowledgeDao
) {

    /**
     * Initialize database with all FAR/AIM content
     * This populates the RAG system with authoritative sources
     */
    suspend fun initializeKnowledgeBase() = withContext(Dispatchers.IO) {
        val allDocuments = mutableListOf<AviationDocument>()

        // Add sample aviation knowledge
        allDocuments.addAll(AviationKnowledge.getSampleDocuments())

        // Add FAR regulations
        allDocuments.addAll(FARContent.getEssentialRegulations())

        // Add AIM sections
        allDocuments.addAll(AIMContent.getEssentialAIMSections())

        // Insert all documents
        knowledgeDao.insertDocuments(allDocuments)
    }

    /**
     * Check if knowledge base has been initialized
     */
    suspend fun isInitialized(): Boolean = withContext(Dispatchers.IO) {
        // Check if we have any verified documents
        val count = knowledgeDao.searchDocuments("", limit = 1).size
        count > 0
    }

    /**
     * Get statistics about knowledge base
     */
    suspend fun getKnowledgeBaseStats(): KnowledgeStats = withContext(Dispatchers.IO) {
        val farDocs = knowledgeDao.searchByKeyword("14 CFR").size
        val aimDocs = knowledgeDao.searchByKeyword("AIM").size
        val proceduralDocs = knowledgeDao.searchByKeyword("procedure").size
        val phraseologyDocs = knowledgeDao.searchByKeyword("phraseology").size

        KnowledgeStats(
            totalDocuments = farDocs + aimDocs,
            farRegulations = farDocs,
            aimSections = aimDocs,
            proceduralDocs = proceduralDocs,
            phraseologyDocs = phraseologyDocs
        )
    }

    /**
     * Update a specific document (for corrections or updates)
     */
    suspend fun updateDocument(document: AviationDocument) = withContext(Dispatchers.IO) {
        knowledgeDao.updateDocument(document)
    }

    /**
     * Add custom document (for flight school specific procedures)
     */
    suspend fun addCustomDocument(
        title: String,
        content: String,
        source: String,
        category: String,
        keywords: String,
        aircraftType: String? = null,
        airport: String? = null,
        verifiedByCFI: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val document = AviationDocument(
            type = DocumentType.AIRPORT_PROCEDURE,
            title = title,
            content = content,
            source = source,
            category = category,
            keywords = keywords,
            aircraft = aircraftType,
            airport = airport,
            verified = verifiedByCFI
        )

        knowledgeDao.insertDocument(document)
    }

    companion object {
        @Volatile
        private var INSTANCE: KnowledgeInitializer? = null

        fun getInstance(knowledgeDao: KnowledgeDao): KnowledgeInitializer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: KnowledgeInitializer(knowledgeDao).also { INSTANCE = it }
            }
        }
    }
}

/**
 * Statistics about the knowledge base
 */
data class KnowledgeStats(
    val totalDocuments: Int,
    val farRegulations: Int,
    val aimSections: Int,
    val proceduralDocs: Int,
    val phraseologyDocs: Int
)
