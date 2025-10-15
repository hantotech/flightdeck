package com.example.flightdeck.data.knowledge

/**
 * AIM (Aeronautical Information Manual) Content
 * Official FAA guide for pilots
 *
 * Source: https://www.faa.gov/air_traffic/publications/atpubs/aim_html/
 */
object AIMContent {

    /**
     * Essential AIM chapters for pilot communication and procedures
     */
    fun getEssentialAIMSections(): List<AviationDocument> {
        return listOf(

            // ===== CHAPTER 2: AERONAUTICAL LIGHTING =====

            AviationDocument(
                type = DocumentType.AIM_SECTION,
                title = "AIM 2-1-2 - Visual Glideslope Indicators",
                content = """
                    AIM 2-1-2 Visual Glideslope Indicators

                    VASI (Visual Approach Slope Indicator):
                    - Two-bar VASI: Red/White = On glidepath
                    - "Red over white, you're all right"
                    - "White over white, you're high as a kite"
                    - "Red over red, you're dead"

                    PAPI (Precision Approach Path Indicator):
                    - Four lights on left side of runway
                    - 4 white = Too high
                    - 3 white, 1 red = Slightly high
                    - 2 white, 2 red = On glidepath (3° typically)
                    - 1 white, 3 red = Slightly low
                    - 4 red = Too low

                    MEMORY AID: More white = higher, more red = lower
                """.trimIndent(),
                source = "AIM 2-1-2",
                category = "procedures",
                keywords = "VASI, PAPI, glideslope, visual approach, landing aids",
                verified = true
            ),

            // ===== CHAPTER 3: AIRSPACE =====

            AviationDocument(
                type = DocumentType.AIM_SECTION,
                title = "AIM 3-2-1 - General Airspace Classification",
                content = """
                    AIM 3-2-1 General

                    Airspace Classes Summary:

                    Class A: 18,000 MSL to FL600
                    - IFR only, clearance required
                    - Transponder with Mode C required

                    Class B: Busy terminal areas (major airports)
                    - ATC clearance required to enter
                    - Transponder with Mode C required
                    - Two-way radio communication required
                    - VFR: 3 SM visibility, clear of clouds

                    Class C: Moderate traffic airports
                    - Two-way radio communication required
                    - Transponder with Mode C required
                    - VFR: 3 SM visibility, 500/1000/2000 cloud clearance

                    Class D: Towered airports
                    - Two-way radio communication required
                    - No transponder required (unless in Mode C veil)
                    - VFR: 3 SM visibility, 500/1000/2000 cloud clearance

                    Class E: Controlled airspace not A, B, C, or D
                    - No ATC clearance required for VFR
                    - Transponder with Mode C above 10,000 MSL
                    - Various visibility and cloud clearance requirements

                    Class G: Uncontrolled airspace
                    - No ATC services
                    - Relaxed VFR minimums in some areas

                    MEMORY AID: "A-B-C-D" = decreasing complexity and requirements
                """.trimIndent(),
                source = "AIM 3-2-1",
                category = "regulations",
                keywords = "airspace, Class A B C D E G, requirements, VFR, IFR",
                verified = true
            ),

            // ===== CHAPTER 4: AIR TRAFFIC CONTROL =====

            AviationDocument(
                type = DocumentType.ATC_PHRASEOLOGY,
                title = "AIM 4-2-1 - General Radio Communications",
                content = """
                    AIM 4-2-1 General

                    Initial Contact Format:
                    1. Name of facility being called
                    2. Your full aircraft identification
                    3. Your position or location
                    4. Your request or intention
                    5. ATIS information received

                    Example: "Palo Alto Ground, Cessna November One Two Three Four Five,
                    at the ramp, ready to taxi with information Alpha"

                    Subsequent Contacts:
                    - Use abbreviated callsign after initial contact
                    - Example: "N12345, turn right heading zero niner zero"

                    Read Backs Required (CRITICAL):
                    - Runway assignments
                    - Taxi instructions
                    - Hold short instructions
                    - Clearance into/out of Class B, C, D airspace
                    - Frequency changes
                    - Heading and altitude assignments
                    - Altimeter settings
                    - Transponder codes

                    Hear Back: Listen for your callsign in ATC's response to ensure they
                    heard you correctly.
                """.trimIndent(),
                source = "AIM 4-2-1",
                category = "phraseology",
                keywords = "radio communication, initial contact, read back, ATC, phraseology",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.ATC_PHRASEOLOGY,
                title = "AIM 4-2-3 - Contact Procedures",
                content = """
                    AIM 4-2-3 Contact Procedures

                    Initial Contact:
                    When contacting a facility for the first time, or when changing frequencies,
                    state the following:

                    1. Facility name
                    2. Your full aircraft call sign
                    3. Type of aircraft (optional on initial, helpful for clarity)
                    4. Location or altitude
                    5. Request or intentions
                    6. ATIS code (if applicable)

                    Example Contacts:

                    Ground Control: "San Francisco Ground, Cessna Three One Five Tango Mike,
                    at gate 20, taxi with information Bravo"

                    Tower: "San Francisco Tower, Cessna Three One Five Tango Mike, ready for
                    takeoff, runway two eight right"

                    Approach: "NorCal Approach, Cessna Three One Five Tango Mike, two thousand
                    five hundred, VFR to Palo Alto"

                    Clearance Delivery (IFR): "San Francisco Clearance, Cessna Three One Five
                    Tango Mike, IFR to Palo Alto, request clearance"

                    Communication Established:
                    When ATC responds with your callsign, two-way communication is established.
                    You may then proceed with your request or comply with instructions.
                """.trimIndent(),
                source = "AIM 4-2-3",
                category = "phraseology",
                keywords = "contact procedures, initial call, ground, tower, approach, clearance delivery",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.ATC_PHRASEOLOGY,
                title = "AIM 4-3-3 - Clearance for Takeoff",
                content = """
                    AIM 4-3-3 Clearance for Takeoff

                    CRITICAL SAFETY DISTINCTION:

                    "CLEARED FOR TAKEOFF" - Authorization to depart
                    - This is the ONLY phraseology that authorizes takeoff
                    - Example: "Cessna 345TM, Runway 31, cleared for takeoff"

                    "LINE UP AND WAIT" (LUAW) - NOT a takeoff clearance
                    - Authorization to taxi onto the runway and hold in position
                    - Must wait for explicit "cleared for takeoff"
                    - Example: "Cessna 345TM, Runway 31, line up and wait"

                    "POSITION AND HOLD" - Old terminology (no longer used)
                    - Changed to "line up and wait" in 2010
                    - If you hear this, query ATC for clarification

                    Read Back Requirements:
                    ALWAYS read back:
                    - Runway assignment
                    - Takeoff clearance or line up and wait instruction

                    Correct Read Back Examples:
                    - "Runway 31, cleared for takeoff, Cessna 345TM"
                    - "Runway 31, line up and wait, Cessna 345TM"

                    NEVER begin takeoff roll until you hear the words "CLEARED FOR TAKEOFF"

                    If unclear or didn't hear clearance: "Say again" or "Verify cleared for takeoff"
                """.trimIndent(),
                source = "AIM 4-3-3",
                category = "phraseology",
                keywords = "takeoff clearance, line up and wait, LUAW, runway, cleared for takeoff",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.ATC_PHRASEOLOGY,
                title = "AIM 4-3-14 - Communications with Tower (Pattern)",
                content = """
                    AIM 4-3-14 Communications with Tower when operating in the traffic pattern

                    Position Reports (When Requested):
                    Pilots should report their position in the traffic pattern when:
                    1. Entering downwind
                    2. Turning base
                    3. Turning final (if not in sight)

                    Standard Position Report Format:
                    "[Facility], [Callsign], [Position], [Runway]"

                    Examples:
                    - "Palo Alto Tower, Cessna 345TM, entering left downwind, runway 31"
                    - "Palo Alto Tower, Cessna 345TM, left base, runway 31"
                    - "Palo Alto Tower, Cessna 345TM, final, runway 31"

                    Landing Clearance:
                    Tower will issue: "Cessna 345TM, Runway 31, cleared to land"

                    Proper Read Back: "Cleared to land, Runway 31, Cessna 345TM"

                    Go-Around:
                    If you need to go around: "Cessna 345TM, going around"
                    Tower will respond with instructions

                    Touch-and-Go:
                    Request: "Request touch-and-go" or "Request the option"
                    Clearance: "Cessna 345TM, Runway 31, cleared touch-and-go"
                    or "Cessna 345TM, Runway 31, cleared for the option"

                    "The Option" gives you flexibility to make a full stop landing, touch-and-go,
                    stop-and-go, or low approach.
                """.trimIndent(),
                source = "AIM 4-3-14",
                category = "phraseology",
                keywords = "traffic pattern, position reports, landing clearance, touch and go, downwind, base, final",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.ATC_PHRASEOLOGY,
                title = "AIM 4-3-18 - Taxiing",
                content = """
                    AIM 4-3-18 Taxiing

                    Taxi Clearance Format:
                    ATC will issue: "[Callsign], taxi to [location] via [taxiway(s)], [hold short if applicable]"

                    Example: "Cessna 345TM, taxi to Runway 31 via Alpha, hold short of Runway 13"

                    Required Read Back:
                    ALWAYS read back:
                    - Runway or location
                    - Taxi route
                    - Any hold short instructions

                    Example Read Back: "Taxi to Runway 31 via Alpha, hold short Runway 13, Cessna 345TM"

                    Runway Crossing:
                    If your taxi route crosses a runway, ATC will explicitly state:
                    "Cessna 345TM, cross Runway 13, taxi to Runway 31 via Alpha"

                    You MUST read back: "Cross Runway 13, Cessna 345TM"

                    CRITICAL SAFETY:
                    - NEVER cross a runway without explicit clearance
                    - NEVER enter a runway without explicit clearance
                    - If uncertain of taxi route: "Request progressive taxi"
                    - If lost: STOP and call Ground: "Ground, Cessna 345TM, request location"

                    Progressive Taxi:
                    Requests step-by-step taxi instructions:
                    "Ground, Cessna 345TM, unfamiliar with airport, request progressive taxi to Runway 31"
                """.trimIndent(),
                source = "AIM 4-3-18",
                category = "phraseology",
                keywords = "taxiing, taxi clearance, runway crossing, hold short, progressive taxi, ground control",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.AIM_SECTION,
                title = "AIM 5-2-4 - Instrument Departure Procedures",
                content = """
                    AIM 5-2-4 Instrument Departure Procedures (IDP)

                    Standard Instrument Departure (SID):
                    - Pre-planned IFR departure route
                    - Reduces radio congestion
                    - Provides obstacle clearance
                    - Must be specifically assigned by ATC

                    Departure Procedures (DP):
                    Two types:
                    1. Obstacle Departure Procedures (ODP) - For obstacle clearance
                    2. Standard Instrument Departures (SID) - ATC routing

                    VFR Departure:
                    At towered airports, may request:
                    - "Straight out departure" - Continue runway heading
                    - "Left/Right departure" - Turn after takeoff
                    - "Departure to the [direction]" - Specific direction

                    Example: "Request straight out departure" or
                    "Request departure to the west"

                    ATC will respond with approval or alternate instructions.

                    IMPORTANT: At busy Class B or C airports, ATC may assign a specific
                    departure heading even for VFR. Comply and read back.
                """.trimIndent(),
                source = "AIM 5-2-4",
                category = "procedures",
                keywords = "departure, SID, ODP, instrument departure, VFR departure, straight out",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.AIM_SECTION,
                title = "AIM 5-4-5 - Missed Approach",
                content = """
                    AIM 5-4-5 Missed Approach

                    When to Execute Missed Approach:
                    1. Instructed by ATC
                    2. Runway environment not in sight at Decision Altitude/Height (IFR)
                    3. Unsafe landing conditions
                    4. Any time pilot determines landing cannot be made safely

                    VFR Equivalent - Go-Around:
                    Common reasons:
                    - Runway occupied
                    - Approach unstabilized (too fast, too high)
                    - Poor alignment with runway
                    - Strong winds or windshear
                    - Traffic conflict

                    Go-Around Communication:
                    Pilot: "Cessna 345TM, going around"
                    Tower: "Cessna 345TM, roger, fly runway heading, climb and maintain [altitude]"

                    Go-Around Procedure (VFR):
                    1. Apply full power
                    2. Carb heat OFF (if applicable)
                    3. Flaps to takeoff setting (gradual)
                    4. Pitch for Vy (best rate of climb)
                    5. Positive rate of climb - retract remaining flaps
                    6. Follow ATC instructions

                    CRITICAL: Never hesitate to go around if landing doesn't look right.
                    It's always better to go around than force a bad landing.
                """.trimIndent(),
                source = "AIM 5-4-5",
                category = "procedures",
                keywords = "go around, missed approach, landing, safety, unstabilized approach",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.AIM_SECTION,
                title = "AIM 6-1-2 - Emergency Condition",
                content = """
                    AIM 6-1-2 Emergency Condition

                    Declaring an Emergency:
                    Use: "MAYDAY, MAYDAY, MAYDAY" (distress - immediate assistance needed)
                    or "PAN-PAN, PAN-PAN, PAN-PAN" (urgency - need assistance but not immediate danger)

                    MAYDAY Format:
                    1. "MAYDAY, MAYDAY, MAYDAY"
                    2. Callsign (three times if time permits)
                    3. Nature of emergency
                    4. Intentions
                    5. Position, altitude, heading
                    6. Souls on board, fuel remaining (time)

                    Example:
                    "MAYDAY, MAYDAY, MAYDAY, Cessna Three Four Five Tango Mike, Cessna Three Four
                    Five Tango Mike, Cessna Three Four Five Tango Mike, engine failure, attempting
                    forced landing, 5 miles west of Palo Alto Airport, 2,000 feet, heading west,
                    two souls on board, estimating 20 minutes fuel"

                    Transponder: Squawk 7700 for emergency

                    Radio Frequency:
                    - Stay on current frequency if in contact with ATC
                    - If no contact, try 121.5 MHz (emergency frequency)

                    PIC Authority:
                    14 CFR 91.3(b): In an emergency, PIC may deviate from any regulation to the
                    extent required to meet that emergency.

                    Post-Emergency:
                    May be required to submit written report to FAA (14 CFR 91.3(c))
                """.trimIndent(),
                source = "AIM 6-1-2",
                category = "emergency",
                keywords = "emergency, MAYDAY, PAN-PAN, 7700, squawk, distress, engine failure",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.AIM_SECTION,
                title = "AIM 6-3-1 - Distress and Urgency Communications",
                content = """
                    AIM 6-3-1 Distress and Urgency Communications

                    Distress (MAYDAY):
                    - Immediate danger to aircraft or persons
                    - Examples: Engine failure, fire, structural failure

                    Urgency (PAN-PAN):
                    - Concern for safety but not immediate danger
                    - Examples: Low fuel, weather diversion, passenger medical issue

                    Emergency Frequencies:
                    - 121.5 MHz - Civil emergency (VHF)
                    - 243.0 MHz - Military emergency (UHF)
                    - Monitored by ATC, FSS, and military facilities

                    After Initial MAYDAY/PAN-PAN:
                    ATC will:
                    1. Acknowledge emergency
                    2. Clear frequency for your transmissions
                    3. Provide vectors, weather, or other assistance
                    4. Alert emergency services if needed
                    5. Coordinate with other facilities

                    Special Scenarios:

                    Lost Communications:
                    - Squawk 7600
                    - Follow lost comm procedures (91.185 for IFR)

                    Radio Failure:
                    - Squawk 7600
                    - Fly expected route and altitude
                    - Watch for light gun signals at towered airport

                    Unlawful Interference (Hijacking):
                    - Squawk 7500
                    - Follow hijacker instructions for safety
                    - Attempt discrete communication with ATC if possible
                """.trimIndent(),
                source = "AIM 6-3-1",
                category = "emergency",
                keywords = "distress, urgency, 121.5, emergency frequency, lost communications, 7600, 7500, hijacking",
                verified = true
            )
        )
    }
}
