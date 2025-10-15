package com.example.flightdeck.data.knowledge

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Aviation knowledge base for RAG (Retrieval Augmented Generation)
 * Stores authoritative aviation documents for accurate AI responses
 */
@Entity(tableName = "aviation_documents")
data class AviationDocument(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: DocumentType,
    val title: String,
    val content: String,
    val source: String,
    val regulation: String? = null, // e.g., "14 CFR 91.155"
    val airport: String? = null,     // ICAO code if airport-specific
    val aircraft: String? = null,    // Aircraft type if specific
    val category: String,            // "procedures", "regulations", "phraseology"
    val keywords: String,            // Searchable keywords
    val lastUpdated: Long = System.currentTimeMillis(),
    val verified: Boolean = true     // Verified by CFI
)

enum class DocumentType {
    FAA_REGULATION,        // 14 CFR regulations
    AIM_SECTION,           // Aeronautical Information Manual
    AIRPORT_PROCEDURE,     // Airport-specific procedures
    POH_SECTION,           // Pilot Operating Handbook
    ATC_PHRASEOLOGY,       // Standard communication
    EMERGENCY_PROCEDURE,   // Emergency checklists
    WEATHER_MINIMUMS,      // VFR/IFR minimums
    AIRSPACE_RULES,        // Airspace classifications
    SAFETY_GUIDANCE        // General safety tips
}

/**
 * Sample aviation knowledge entries
 */
object AviationKnowledge {

    fun getSampleDocuments(): List<AviationDocument> {
        return listOf(
            // ATC Phraseology
            AviationDocument(
                type = DocumentType.ATC_PHRASEOLOGY,
                title = "Initial Contact with Ground Control",
                content = """
                    Standard format for initial call to ground control:
                    "[Facility] [Service], [Aircraft Call Sign], [Location], [Request] with [ATIS]"

                    Example: "Palo Alto Ground, Skyhawk N12345, at the ramp, ready to taxi with information Alpha"

                    Required elements:
                    1. Facility name and service (e.g., "Palo Alto Ground")
                    2. Full aircraft call sign on first contact
                    3. Current location on airport
                    4. Request (taxi, departure, etc.)
                    5. ATIS information letter received
                """.trimIndent(),
                source = "AIM 4-2-3, Pilot/Controller Glossary",
                category = "phraseology",
                keywords = "ground control, initial contact, taxi, ATIS",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.ATC_PHRASEOLOGY,
                title = "Takeoff Clearance Communication",
                content = """
                    When cleared for takeoff, read back:
                    1. Aircraft call sign
                    2. Runway assignment
                    3. Takeoff clearance

                    Example: "Runway 31, cleared for takeoff, Skyhawk N12345"

                    NEVER begin takeoff roll until you hear "CLEARED FOR TAKEOFF"
                    "Line up and wait" is NOT a takeoff clearance.
                """.trimIndent(),
                source = "AIM 4-3-3",
                category = "phraseology",
                keywords = "takeoff clearance, runway, cleared for takeoff",
                verified = true
            ),

            // VFR Weather Minimums
            AviationDocument(
                type = DocumentType.WEATHER_MINIMUMS,
                title = "VFR Weather Minimums - Class D Airspace",
                content = """
                    VFR weather minimums for Class D airspace (towered airports):
                    - Visibility: 3 statute miles
                    - Cloud clearance: 500 feet below, 1,000 feet above, 2,000 feet horizontal
                    - Flight visibility required

                    If weather is below these minimums, you cannot legally operate VFR.
                    You must either:
                    1. Wait for weather to improve
                    2. File and fly IFR (if qualified)
                    3. Request special VFR clearance
                """.trimIndent(),
                source = "14 CFR 91.155(c)",
                regulation = "14 CFR 91.155",
                category = "regulations",
                keywords = "VFR minimums, weather, visibility, cloud clearance, Class D",
                verified = true
            ),

            // Emergency Procedures
            AviationDocument(
                type = DocumentType.EMERGENCY_PROCEDURE,
                title = "Engine Failure After Takeoff",
                content = """
                    Immediate actions for engine failure after takeoff:

                    1. PITCH for best glide speed (68 KIAS for C172)
                    2. Pick a landing spot AHEAD - do NOT turn back
                    3. Attempt engine restart if altitude permits
                    4. Declare emergency on current frequency: "MAYDAY MAYDAY MAYDAY"
                    5. Transmit position and intentions
                    6. Squawk 7700

                    CRITICAL: Below 500 AGL, land straight ahead. Attempting to return to
                    runway often results in stall/spin accident.
                """.trimIndent(),
                source = "C172 POH Section 3, Emergency Procedures",
                aircraft = "C172",
                category = "emergency",
                keywords = "engine failure, emergency, takeoff, forced landing",
                verified = true
            ),

            // Takeoff Procedure
            AviationDocument(
                type = DocumentType.POH_SECTION,
                title = "Normal Takeoff - Cessna 172",
                content = """
                    Normal takeoff procedure for Cessna 172:

                    Before Takeoff:
                    1. Mixture: RICH (below 3,000 ft density altitude)
                    2. Fuel selector: BOTH
                    3. Flaps: 0° to 10° (short field use 10°)
                    4. Trim: Set for takeoff
                    5. Lights: ON

                    Takeoff:
                    1. Throttle: FULL (verify 2,300-2,400 RPM)
                    2. Check engine instruments: green
                    3. Rotate: 55-60 KIAS
                    4. Climb speed: 70-80 KIAS
                    5. Retract flaps: At safe altitude if used

                    NEVER lean mixture for takeoff - always RICH for maximum power.
                """.trimIndent(),
                source = "Cessna 172S POH Section 4.2",
                aircraft = "C172",
                category = "procedures",
                keywords = "takeoff, normal takeoff, mixture, C172, Cessna",
                verified = true
            ),

            // Pattern Procedures
            AviationDocument(
                type = DocumentType.AIRPORT_PROCEDURE,
                title = "Traffic Pattern - Palo Alto (KPAO)",
                content = """
                    Traffic pattern procedures for KPAO:

                    Runway 31 (Left traffic):
                    - Pattern altitude: 800 feet MSL
                    - Upwind: Maintain runway heading to 400 AGL before turning
                    - Crosswind: Turn left 90° at 400-500 AGL
                    - Downwind: Parallel runway at 800 MSL, 1/2 mile offset
                    - Base: Turn when runway is 45° behind wing
                    - Final: Aligned with runway centerline

                    Noise abatement: Avoid overflying residential areas.
                    Report positions: "Entering downwind", "Turning base", "Final runway 31"
                """.trimIndent(),
                source = "KPAO Airport/Facility Directory",
                airport = "KPAO",
                category = "procedures",
                keywords = "traffic pattern, KPAO, Palo Alto, pattern altitude",
                verified = true
            ),

            // Airspace
            AviationDocument(
                type = DocumentType.AIRSPACE_RULES,
                title = "Class D Airspace Requirements",
                content = """
                    To operate in Class D airspace (towered airport):

                    Requirements:
                    1. Two-way radio communication established before entering
                    2. Weather: VFR minimums (3 SM visibility, cloud clearances)
                    3. ADS-B Out if equipped (after 2020)

                    Communication:
                    - Contact tower before entering airspace
                    - Acknowledgment of call sign establishes communication
                    - Follow all ATC instructions

                    Typical Class D extends from surface to 2,500 AGL, radius 4 NM.
                    Verify specific dimensions on sectional chart.
                """.trimIndent(),
                source = "14 CFR 91.129, AIM 3-2-5",
                regulation = "14 CFR 91.129",
                category = "regulations",
                keywords = "Class D, airspace, tower, communication required",
                verified = true
            )
        )
    }
}
