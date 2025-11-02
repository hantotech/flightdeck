# Airport Database: Free vs Premium Tier Strategy

## Overview

FlightDeck uses a **two-tier airport system** to balance user value with monetization:

- **Free Tier**: 30 curated major airports across US/Canada
- **Premium Tier**: 20,000+ airports from OurAirports database

---

## 🆓 Free Tier (30 Airports)

### Coverage

**United States (27 airports):**
- **California** (5): KPAO, KSFO, KSJC, KSQL, KLAX
- **Pacific Northwest** (3): KSEA (Seattle), KPDX (Portland), KBFI (Boeing Field)
- **Southwest** (3): KPHX (Phoenix), KLAS (Las Vegas), KDEN (Denver)
- **Texas** (4): KDFW, KIAH (Houston), KAUS (Austin), KSAT (San Antonio)
- **Midwest** (4): KORD (Chicago), KMSP (Minneapolis), KDTW (Detroit), KCLE (Cleveland)
- **Southeast** (4): KATL (Atlanta), KMIA (Miami), KMCO (Orlando), KTPA (Tampa)
- **Northeast** (4): KJFK (New York), KBOS (Boston), KDCA (DC), KPHL (Philadelphia)

**Canada (3 airports):**
- CYYZ (Toronto Pearson)
- CYVR (Vancouver)
- CYUL (Montreal)

### Value Proposition

✅ **Complete training experience** - All major airports for ATC practice
✅ **Class B, C, D coverage** - Practice complex airspace operations
✅ **Geographic diversity** - Every US region represented
✅ **Zero paywall friction** - Users can practice immediately
✅ **Real-world relevance** - 90% of students train at these or nearby airports

---

## 💎 Premium Tier (20,000+ Airports)

### What's Included

**OurAirports Database (Public Domain):**
- 20,000+ US & Canada airports
- Small GA airports (Class D, E, G)
- Rural airstrips
- Heliports
- Seaplane bases
- Complete runway data (length, width, surface, lighting)
- Complete frequency data (Tower, Ground, CTAF, ATIS, AWOS/ASOS)
- Coordinates, elevation, airspace class

### Premium Use Cases

**"Practice YOUR Flight":**
- Student practicing solo cross-country from THEIR home airport
- Pilot visiting small rural airports
- Mountain flying practice (e.g., KASE Aspen)
- Backcountry strips
- Unusual operations (grass strips, short fields)

**Target Audience:**
- Serious student pilots (pre-checkride)
- Licensed pilots maintaining currency
- Pilots planning specific cross-country routes
- CFIs wanting comprehensive training scenarios

### Pricing

**One-Time Purchase: $29.99**
- Unlocks all 20,000+ airports permanently
- No subscription required
- Aligns with app positioning (not ForeFlight competitor)
- Fair value: ~$0.0015 per airport

---

## 📊 Monetization Analysis

### Free Tier Conversion Strategy

**User Journey:**
1. **Download** → 30 free airports immediately available
2. **Practice** → Complete training at major airports (no complaints)
3. **"My Airport"** → User searches for KXYZ (their local GA airport)
4. **Discovery** → "This airport requires Premium"
5. **Decision** → "I want to practice MY actual flights" → Purchase

**Conversion Triggers:**
- Searching for hometown airport (not in free list)
- Planning specific cross-country (intermediate airports)
- CFI wanting to train students at their local field
- Pilot moving to new area

### Competitive Advantage

**vs ForeFlight ($299/year):**
- FlightDeck: $29.99 one-time (100x cheaper)
- ForeFlight: Full flight planning + database
- FlightDeck: Focused on ATC practice only

**vs ARSim (competitors):**
- Most simulators have limited airports
- FlightDeck free tier beats most paid simulators
- Premium tier is comprehensive + affordable

### Revenue Projections

**Conservative Estimates:**
- 10,000 downloads (Year 1)
- 5% premium conversion = 500 buyers
- Revenue: $14,995
- Sustainable for indie developer

**Optimistic Estimates:**
- 50,000 downloads (Year 2)
- 8% premium conversion = 4,000 buyers
- Revenue: $119,960
- Justifies continued development

---

## 🔧 Technical Implementation

### Database Schema

```kotlin
@Entity(tableName = "airports")
data class Airport(
    val icao: String,
    val name: String,
    val city: String,
    val state: String?,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Int,
    val airspaceClass: AirspaceClass?,
    val towerControlled: Boolean,
    val type: AirportType,
    val isPremium: Boolean = false, // ← KEY FIELD
    ...
)
```

### Free Tier Initialization

**File**: `DatabaseInitializer.kt`
- Hardcoded list of 30 airports
- `isPremium = false` for all
- Runs on first app launch

### Premium Tier Initialization

**File**: `OurAirportsLoader.kt` (to be created)
- Reads CSV files from `assets/ourairports/`
- Parses airports.csv, runways.csv, frequencies.csv
- Marks all as `isPremium = true`
- Triggered on premium purchase

### Data Source

**OurAirports** (https://ourairports.com/data/)
- Public Domain data
- Free to use commercially
- Regular updates
- CSV format (easy parsing)

**CSV Files Needed:**
1. `airports.csv` (~60K airports worldwide)
2. `runways.csv` (runway details)
3. `airport-frequencies.csv` (communication frequencies)

**Filtering:**
- Filter to US (country = "US") + Canada (country = "CA")
- Exclude closed airports
- Result: ~20,000 active airports

---

## 🎨 UI/UX Strategy

### Airport Search

**Free Airports:**
```
🛫 KPAO - Palo Alto Airport
   Class E • Towered • Palo Alto, CA
```

**Premium Airports (locked):**
```
🔒 KRHV - Reid-Hillview Airport
   Class D • Towered • San Jose, CA
   [Unlock Premium to access]
```

### Upgrade Prompt

**Modal shown when selecting premium airport:**
```
┌─────────────────────────────────────────┐
│ 🔒 Premium Airport                      │
│                                         │
│ KRHV - Reid-Hillview Airport            │
│                                         │
│ Unlock 20,000+ airports across the US   │
│ and Canada for $29.99 (one-time).       │
│                                         │
│ ✓ All US & Canada airports             │
│ ✓ Complete runway & frequency data      │
│ ✓ Practice YOUR actual flights          │
│ ✓ Lifetime access, no subscription      │
│                                         │
│  [Unlock Premium - $29.99]   [Cancel]   │
└─────────────────────────────────────────┘
```

### Settings Screen

**Premium Status:**
```
┌─────────────────────────────────────────┐
│ Airport Database                         │
│                                         │
│ Free: 30 airports ✓                     │
│ Premium: 20,000+ airports 🔒             │
│                                         │
│ [Upgrade to Premium - $29.99]           │
└─────────────────────────────────────────┘
```

---

## ✅ Benefits of This Strategy

### For Free Users
- ✅ Complete training experience at major airports
- ✅ No artificial limitations on functionality
- ✅ Can complete entire course with free tier
- ✅ Professional experience (not "trial version")

### For Premium Users
- ✅ Access to their actual home airport
- ✅ Practice specific cross-country routes
- ✅ Complete US/Canada coverage
- ✅ Fair one-time price (not subscription fatigue)

### For Business
- ✅ High conversion trigger ("MY airport")
- ✅ Sustainable revenue model
- ✅ No ongoing API costs (offline data)
- ✅ Competitive advantage vs limited competitors
- ✅ Clear differentiation from ForeFlight

---

## 🚀 Rollout Plan

### Phase 1: Free Tier Only (Current)
- ✅ 30 airports initialized
- ✅ `isPremium` field added to schema
- ✅ Database version 6
- ⏳ No premium indicators in UI yet

### Phase 2: Premium Preparation (Next)
- Create `OurAirportsLoader.kt`
- Add CSV files to `assets/ourairports/`
- Implement CSV parsing logic
- Test premium airport loading

### Phase 3: Premium Purchase Flow
- Add in-app purchase integration (Google Play Billing)
- Implement premium status check
- Add upgrade prompts to airport search
- Show 🔒 indicator on premium airports

### Phase 4: Premium Rollout
- Enable premium purchases
- Monitor conversion rate
- Gather user feedback
- Iterate on messaging

---

## 📝 Key Decisions

### Why One-Time vs Subscription?

**One-Time Purchase ($29.99):**
- ✅ Aligns with "not competing with ForeFlight" positioning
- ✅ Lower barrier to conversion
- ✅ No churn/renewal friction
- ✅ Feels fair for "data unlock"
- ✅ Sustainable for indie dev

**Subscription ($9.99/month) - Rejected:**
- ❌ Subscription fatigue (users already pay ForeFlight)
- ❌ Competes with ForeFlight pricing
- ❌ Doesn't fit "practice app" positioning
- ❌ Requires constant justification of value

### Why 30 Free Airports?

**Too Few (10 airports):**
- ❌ Feels restrictive
- ❌ Can't practice all airspace classes
- ❌ Missing major regions
- ❌ Users feel "locked out"

**30 Airports (Sweet Spot):**
- ✅ Complete training experience
- ✅ All US regions covered
- ✅ Feels generous, not restrictive
- ✅ Still strong conversion trigger ("my airport")

**Too Many (100 airports):**
- ❌ Weakens premium value proposition
- ❌ Most GA airports covered
- ❌ Lower conversion rate

### Why OurAirports vs FAA API?

**OurAirports CSV:**
- ✅ Free, public domain
- ✅ Works offline (critical for aviation)
- ✅ One-time load (fast app performance)
- ✅ No API limits or costs
- ✅ Comprehensive worldwide data

**FAA API:**
- ❌ US only (no Canada)
- ❌ Requires internet
- ❌ API rate limits
- ❌ Less comprehensive data

---

## 🎯 Success Metrics

**Key Performance Indicators:**
1. **Free user satisfaction**: Can complete training with free tier
2. **Premium conversion rate**: Target 5-8%
3. **Upgrade trigger**: Users search for non-free airport
4. **Revenue per user**: $29.99 × conversion rate
5. **User feedback**: "Fair pricing" sentiment

**Next Steps:**
1. Monitor free tier usage patterns
2. Identify most-searched premium airports
3. Optimize upgrade messaging
4. Gather user testimonials
5. Iterate on conversion flow
