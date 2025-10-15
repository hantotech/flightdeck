package com.example.flightdeck.data.knowledge

/**
 * FAR (Federal Aviation Regulations) - 14 CFR
 * Official regulations from the FAA
 *
 * Source: https://www.ecfr.gov/current/title-14
 */
object FARContent {

    /**
     * Key regulations for pilot training
     */
    fun getEssentialRegulations(): List<AviationDocument> {
        return listOf(

            // ===== PART 61: CERTIFICATION - PILOTS =====

            AviationDocument(
                type = DocumentType.FAA_REGULATION,
                title = "14 CFR 61.3 - Requirement for Certificates",
                content = """
                    14 CFR § 61.3 - Requirement for certificates, ratings, and authorizations.

                    (a) Required pilot certificate. A person may not act as pilot in command or in any other
                    capacity as a required pilot flight crewmember of a civil aircraft unless that person:

                    (1) Has a valid pilot certificate or special purpose pilot authorization issued under
                    this part and is in that person's physical possession or readily accessible in the
                    aircraft when exercising the privileges of that pilot certificate;

                    (2) Has a photo identification that is in that person's physical possession or readily
                    accessible in the aircraft when exercising the privileges of that pilot certificate.

                    (b) Required pilot certificate for operating a foreign-registered aircraft within the
                    United States. No person may serve as a required pilot flight crewmember of a civil
                    aircraft of foreign registry within the United States, unless that person's pilot certificate
                    issued under this part has been placed in that person's physical possession or has been
                    made readily accessible in the aircraft.
                """.trimIndent(),
                source = "14 CFR § 61.3",
                regulation = "14 CFR 61.3",
                category = "regulations",
                keywords = "pilot certificate, requirements, identification, physical possession",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.FAA_REGULATION,
                title = "14 CFR 61.51 - Pilot Logbooks",
                content = """
                    14 CFR § 61.51 - Pilot logbooks.

                    (a) Training time and aeronautical experience. Each person must document and record
                    the following time in a manner acceptable to the Administrator:

                    (1) Training and aeronautical experience used to meet the requirements for a certificate,
                    rating, or flight review of this part.

                    (2) The aeronautical experience required for meeting the recent flight experience
                    requirements of this part.

                    (b) Logbook entries. For each flight or lesson logged, the following must be entered:
                    (1) General: Date, total flight time or lesson time, location (for flight)
                    (2) Type of pilot experience or training: Solo, PIC, SIC, dual received, etc.
                    (3) Conditions of flight: Day or night, actual instrument, simulated instrument
                """.trimIndent(),
                source = "14 CFR § 61.51",
                regulation = "14 CFR 61.51",
                category = "regulations",
                keywords = "logbook, flight time, training time, documentation, aeronautical experience",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.FAA_REGULATION,
                title = "14 CFR 61.56 - Flight Review",
                content = """
                    14 CFR § 61.56 - Flight review.

                    (a) Except as provided in paragraphs (b) and (f) of this section, a flight review consists of a
                    minimum of 1 hour of flight training and 1 hour of ground training.

                    (c) A person who has, within the period specified in paragraph (a) of this section, passed any
                    of the following need not accomplish the flight review required by this section:

                    (1) A pilot proficiency check or practical test conducted by an examiner, an approved pilot
                    check airman, or a U.S. Armed Force, for a pilot certificate, rating, or operating privilege.

                    No person may act as pilot in command of an aircraft unless that person has accomplished a
                    flight review within the preceding 24 calendar months.
                """.trimIndent(),
                source = "14 CFR § 61.56",
                regulation = "14 CFR 61.56",
                category = "regulations",
                keywords = "flight review, BFR, currency, 24 months, proficiency",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.FAA_REGULATION,
                title = "14 CFR 61.57 - Recent Flight Experience",
                content = """
                    14 CFR § 61.57 - Recent flight experience: Pilot in command.

                    (a) General experience. Except as provided in paragraph (e) of this section, no person may
                    act as a pilot in command of an aircraft carrying passengers unless that person has made at
                    least three takeoffs and three landings within the preceding 90 days, and:

                    (1) The person acted as the sole manipulator of the flight controls; and
                    (2) The required takeoffs and landings were performed in an aircraft of the same category,
                    class, and type (if a type rating is required).

                    (b) Night takeoff and landing experience. No person may act as pilot in command of an
                    aircraft carrying passengers during the period beginning 1 hour after sunset and ending 1 hour
                    before sunrise, unless within the preceding 90 days that person has made at least three takeoffs
                    and three landings to a full stop during that period in an aircraft of the same category, class,
                    and type.
                """.trimIndent(),
                source = "14 CFR § 61.57",
                regulation = "14 CFR 61.57",
                category = "regulations",
                keywords = "currency, 90 days, three takeoffs landings, night currency, passengers",
                verified = true
            ),

            // ===== PART 91: GENERAL OPERATING RULES =====

            AviationDocument(
                type = DocumentType.FAA_REGULATION,
                title = "14 CFR 91.3 - Responsibility and Authority of PIC",
                content = """
                    14 CFR § 91.3 - Responsibility and authority of the pilot in command.

                    (a) The pilot in command of an aircraft is directly responsible for, and is the final
                    authority as to, the operation of that aircraft.

                    (b) In an in-flight emergency requiring immediate action, the pilot in command may deviate
                    from any rule of this part to the extent required to meet that emergency.

                    (c) Each pilot in command who deviates from a rule under paragraph (b) of this section shall,
                    upon the request of the Administrator, send a written report of that deviation to the Administrator.

                    KEY POINT: The PIC has final authority and responsibility for the safe operation of the aircraft.
                    This includes go/no-go decisions, weather evaluation, and passenger safety.
                """.trimIndent(),
                source = "14 CFR § 91.3",
                regulation = "14 CFR 91.3",
                category = "regulations",
                keywords = "PIC, pilot in command, responsibility, authority, emergency deviation",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.FAA_REGULATION,
                title = "14 CFR 91.103 - Preflight Action",
                content = """
                    14 CFR § 91.103 - Preflight action.

                    Each pilot in command shall, before beginning a flight, become familiar with all available
                    information concerning that flight. This information must include:

                    (a) For a flight under IFR or a flight not in the vicinity of an airport, weather reports
                    and forecasts, fuel requirements, alternatives available if the planned flight cannot be
                    completed, and any known traffic delays of which the pilot in command has been advised by ATC.

                    (b) For any flight, runway lengths at airports of intended use, and the following takeoff
                    and landing distance information:
                    (1) For civil aircraft for which an approved Airplane or Rotorcraft Flight Manual containing
                    takeoff and landing distance data is required, the takeoff and landing distance data contained therein.

                    CRITICAL: This regulation requires pilots to review weather, NOTAMs, fuel requirements,
                    alternates, runway lengths, and performance data before EVERY flight.
                """.trimIndent(),
                source = "14 CFR § 91.103",
                regulation = "14 CFR 91.103",
                category = "regulations",
                keywords = "preflight, weather briefing, fuel requirements, NOTAMs, runway length, alternates",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.FAA_REGULATION,
                title = "14 CFR 91.151 - Fuel Requirements for VFR Flight (Day)",
                content = """
                    14 CFR § 91.151 - Fuel requirements for flight in VFR conditions.

                    (a) No person may begin a flight in an airplane under VFR conditions unless (considering
                    wind and forecast weather conditions) there is enough fuel to fly to the first point of
                    intended landing and, assuming normal cruising speed:

                    (1) During the day, to fly after that for at least 30 minutes; or
                    (2) At night, to fly after that for at least 45 minutes.

                    MEMORY AID:
                    - Day VFR: Destination + 30 minutes reserve
                    - Night VFR: Destination + 45 minutes reserve

                    This is MINIMUM - recommended practice is 1 hour reserve for day VFR.
                """.trimIndent(),
                source = "14 CFR § 91.151",
                regulation = "14 CFR 91.151",
                category = "regulations",
                keywords = "fuel requirements, VFR, 30 minutes, 45 minutes, reserves, day, night",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.FAA_REGULATION,
                title = "14 CFR 91.155 - Basic VFR Weather Minimums",
                content = """
                    14 CFR § 91.155 - Basic VFR weather minimums.

                    Class A: Not applicable (IFR only)

                    Class B:
                    - Visibility: 3 statute miles
                    - Cloud clearance: Clear of clouds

                    Class C:
                    - Visibility: 3 statute miles
                    - Cloud clearance: 500 feet below, 1,000 feet above, 2,000 feet horizontal

                    Class D:
                    - Visibility: 3 statute miles
                    - Cloud clearance: 500 feet below, 1,000 feet above, 2,000 feet horizontal

                    Class E (below 10,000 MSL):
                    - Visibility: 3 statute miles
                    - Cloud clearance: 500 feet below, 1,000 feet above, 2,000 feet horizontal

                    Class E (at or above 10,000 MSL):
                    - Visibility: 5 statute miles
                    - Cloud clearance: 1,000 feet below, 1,000 feet above, 1 statute mile horizontal

                    Class G (1,200 AGL or less, Day):
                    - Visibility: 1 statute mile
                    - Cloud clearance: Clear of clouds

                    Class G (1,200 AGL or less, Night):
                    - Visibility: 3 statute miles
                    - Cloud clearance: 500 feet below, 1,000 feet above, 2,000 feet horizontal

                    MEMORY AID (Class B/C/D/E): "3-152" = 3 miles, 1,000 above, 500 below, 2,000 horizontal
                """.trimIndent(),
                source = "14 CFR § 91.155",
                regulation = "14 CFR 91.155",
                category = "regulations",
                keywords = "VFR minimums, visibility, cloud clearance, weather minimums, airspace",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.FAA_REGULATION,
                title = "14 CFR 91.209 - Aircraft Lights",
                content = """
                    14 CFR § 91.209 - Aircraft lights.

                    No person may:
                    (a) During the period from sunset to sunrise (or, in Alaska, during the period a prominent
                    unlighted object cannot be seen from a distance of 3 statute miles or the sun is more than
                    6 degrees below the horizon):

                    (1) Operate an aircraft unless it has lighted position lights;

                    (b) Park or move an aircraft in, or in dangerous proximity to, a night flight operations
                    area of an airport unless the aircraft:
                    (1) Is clearly illuminated;
                    (2) Has lighted position lights; or
                    (3) Is in an area that is marked by obstruction lights;

                    (c) Operate an aircraft that is equipped with an anticollision light system, unless it has
                    lighted anticollision lights. However, the anticollision lights need not be lighted when the
                    pilot-in-command determines that, because of operating conditions, it would be in the interest
                    of safety to turn the lights off.

                    PRACTICAL: All exterior lights (position, beacon, strobes) must be on from sunset to sunrise.
                """.trimIndent(),
                source = "14 CFR § 91.209",
                regulation = "14 CFR 91.209",
                category = "regulations",
                keywords = "aircraft lights, position lights, anticollision, beacon, night operations",
                verified = true
            ),

            AviationDocument(
                type = DocumentType.FAA_REGULATION,
                title = "14 CFR 91.215 - ADS-B Out Equipment",
                content = """
                    14 CFR § 91.215 - ADS-B Out equipment and use.

                    (a) All aircraft. For operations after January 1, 2020, unless otherwise authorized by ATC,
                    no person may operate an aircraft in the airspace described in paragraphs (b) and (c) of this
                    section unless the aircraft has equipment installed that meets the performance requirements
                    in § 91.227.

                    (b) ADS-B Out required airspace:
                    (1) Class A, B, and C airspace;
                    (2) Class E airspace at and above 10,000 feet MSL, excluding airspace at and below 2,500 feet AGL;
                    (3) Class E airspace over the Gulf of Mexico from the coastline out to 12 nautical miles;
                    (4) Within 30 nautical miles of certain busy airports (Mode C veil).

                    PRACTICAL: If you fly in Class B or C airspace, or above 10,000 MSL (except below 2,500 AGL),
                    you need ADS-B Out.
                """.trimIndent(),
                source = "14 CFR § 91.215",
                regulation = "14 CFR 91.215",
                category = "regulations",
                keywords = "ADS-B, transponder, Mode C, Class B, Class C, equipment requirements",
                verified = true
            )
        )
    }
}
