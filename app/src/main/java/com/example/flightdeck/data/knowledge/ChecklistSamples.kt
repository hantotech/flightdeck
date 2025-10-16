package com.example.flightdeck.data.knowledge

import com.example.flightdeck.data.model.ChecklistCategory
import com.example.flightdeck.data.model.ChecklistItem

/**
 * Sample checklist items for common aircraft
 * Includes ATIS/AWOS checks as required by 14 CFR 91.103
 */
object ChecklistSamples {

    /**
     * Pre-flight checklist items (Cessna 172 example)
     * Includes weather briefing and ATIS check
     */
    fun getPreFlightChecklist(checklistId: Long = 1): List<ChecklistItem> {
        return listOf(
            // Weather Briefing (Required by 14 CFR 91.103)
            ChecklistItem(
                checklistId = checklistId,
                title = "Weather Briefing",
                description = "Obtain and review weather briefing including METAR, TAF, winds aloft, and any weather advisories or SIGMETs for route of flight",
                category = ChecklistCategory.PRE_FLIGHT_INTERIOR,
                sequence = 1,
                isCritical = true,
                expectedResponse = "Weather briefing obtained and favorable"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "ATIS/AWOS - Listen",
                description = "Listen to current ATIS or AWOS for departure airport. Note information code, wind, visibility, ceiling, temperature, dewpoint, altimeter setting, and active runway",
                category = ChecklistCategory.PRE_FLIGHT_INTERIOR,
                sequence = 2,
                isCritical = true,
                expectedResponse = "Information [Code] copied, altimeter [Setting]"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Altimeter - Set",
                description = "Set altimeter to current setting from ATIS/AWOS. Verify field elevation within 75 feet",
                category = ChecklistCategory.PRE_FLIGHT_INTERIOR,
                sequence = 3,
                isCritical = true,
                expectedResponse = "Altimeter set and checked"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "NOTAMs - Review",
                description = "Review all NOTAMs for departure, destination, and alternate airports. Check for runway closures, navaid outages, or other critical information",
                category = ChecklistCategory.PRE_FLIGHT_INTERIOR,
                sequence = 4,
                isCritical = true,
                expectedResponse = "NOTAMs reviewed, none affecting flight"
            ),

            // Additional pre-flight items...
            ChecklistItem(
                checklistId = checklistId,
                title = "Parking Brake - Set",
                description = "Ensure parking brake is set before starting engine",
                category = ChecklistCategory.BEFORE_ENGINE_START,
                sequence = 10,
                isCritical = true,
                expectedResponse = "Set"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Radios - Check",
                description = "Tune radios to appropriate frequencies. Ground/Tower for controlled airports, CTAF for non-towered",
                category = ChecklistCategory.BEFORE_ENGINE_START,
                sequence = 11,
                isCritical = true,
                expectedResponse = "Tuned and checked"
            )
        )
    }

    /**
     * Before landing checklist
     * Includes destination ATIS check
     */
    fun getBeforeLandingChecklist(checklistId: Long = 2): List<ChecklistItem> {
        return listOf(
            ChecklistItem(
                checklistId = checklistId,
                title = "Destination ATIS - Listen",
                description = "Listen to destination airport ATIS/AWOS at least 25 miles out. Note active runway, wind, altimeter, and any special instructions",
                category = ChecklistCategory.BEFORE_LANDING,
                sequence = 1,
                isCritical = true,
                expectedResponse = "Information [Code] copied"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Altimeter - Reset",
                description = "Reset altimeter to destination airport setting from ATIS",
                category = ChecklistCategory.BEFORE_LANDING,
                sequence = 2,
                isCritical = true,
                expectedResponse = "Altimeter set to [Setting]"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Landing Clearance - Obtain",
                description = "Contact tower and advise you have current ATIS information code",
                category = ChecklistCategory.BEFORE_LANDING,
                sequence = 3,
                isCritical = true,
                expectedResponse = "Clearance received"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Fuel Selector - Both",
                description = "Ensure fuel selector is on BOTH (for Cessna 172)",
                category = ChecklistCategory.BEFORE_LANDING,
                sequence = 4,
                isCritical = true,
                expectedResponse = "Both"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Mixture - Rich",
                description = "Advance mixture to full rich for landing (if at low altitude)",
                category = ChecklistCategory.BEFORE_LANDING,
                sequence = 5,
                isCritical = true,
                expectedResponse = "Rich"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Seat Belts - Secure",
                description = "Ensure all seat belts are fastened and secure",
                category = ChecklistCategory.BEFORE_LANDING,
                sequence = 6,
                isCritical = true,
                expectedResponse = "Secure"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Landing Lights - On",
                description = "Turn on landing lights",
                category = ChecklistCategory.BEFORE_LANDING,
                sequence = 7,
                isCritical = true,
                expectedResponse = "On"
            )
        )
    }

    /**
     * Before takeoff checklist
     * References ATIS for runway and wind check
     */
    fun getBeforeTakeoffChecklist(checklistId: Long = 3): List<ChecklistItem> {
        return listOf(
            ChecklistItem(
                checklistId = checklistId,
                title = "ATIS Information - Confirm Current",
                description = "Verify you have the most current ATIS information code before calling for takeoff clearance",
                category = ChecklistCategory.BEFORE_TAKEOFF,
                sequence = 1,
                isCritical = true,
                expectedResponse = "Current ATIS confirmed"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Wind Check",
                description = "Verify winds are within aircraft crosswind limits. Reference ATIS or tower report",
                category = ChecklistCategory.BEFORE_TAKEOFF,
                sequence = 2,
                isCritical = true,
                expectedResponse = "Winds within limits"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Takeoff Briefing",
                description = "Brief planned departure runway, initial heading, abort procedures, and emergency return plan",
                category = ChecklistCategory.BEFORE_TAKEOFF,
                sequence = 3,
                isCritical = true,
                expectedResponse = "Briefing complete"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Flight Controls - Free and Correct",
                description = "Check that flight controls move freely in all directions and in the correct sense",
                category = ChecklistCategory.BEFORE_TAKEOFF,
                sequence = 4,
                isCritical = true,
                expectedResponse = "Free and correct"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Trim - Set",
                description = "Set elevator trim to takeoff position",
                category = ChecklistCategory.BEFORE_TAKEOFF,
                sequence = 5,
                isCritical = true,
                expectedResponse = "Set for takeoff"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Flaps - Set",
                description = "Set flaps to takeoff setting (0-10° for Cessna 172)",
                category = ChecklistCategory.BEFORE_TAKEOFF,
                sequence = 6,
                isCritical = true,
                expectedResponse = "Set for takeoff"
            )
        )
    }

    /**
     * Emergency checklist
     * Includes communicating with ATC
     */
    fun getEmergencyChecklist(checklistId: Long = 4): List<ChecklistItem> {
        return listOf(
            ChecklistItem(
                checklistId = checklistId,
                title = "Airspeed - Best Glide",
                description = "Immediately establish best glide speed for maximum range (68 KIAS for C172)",
                category = ChecklistCategory.EMERGENCY,
                sequence = 1,
                isCritical = true,
                expectedResponse = "Best glide established"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Landing Site - Select",
                description = "Select suitable emergency landing area within gliding distance",
                category = ChecklistCategory.EMERGENCY,
                sequence = 2,
                isCritical = true,
                expectedResponse = "Site selected"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Emergency - Declare on 121.5",
                description = "If unable to restart, squawk 7700 and declare MAYDAY MAYDAY MAYDAY on 121.5 MHz. State aircraft type, position, souls on board, and intentions",
                category = ChecklistCategory.EMERGENCY,
                sequence = 3,
                isCritical = true,
                expectedResponse = "Emergency declared"
            ),

            ChecklistItem(
                checklistId = checklistId,
                title = "Restart - Attempt",
                description = "Attempt engine restart per POH procedures if time and altitude permit",
                category = ChecklistCategory.EMERGENCY,
                sequence = 4,
                isCritical = true,
                expectedResponse = "Restart attempted"
            )
        )
    }

    /**
     * All checklist items combined
     */
    fun getAllChecklistItems(): List<ChecklistItem> {
        return getPreFlightChecklist(1) +
                getBeforeTakeoffChecklist(2) +
                getBeforeLandingChecklist(3) +
                getEmergencyChecklist(4)
    }
}
