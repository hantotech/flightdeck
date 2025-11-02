# Flight Planning UI - REVISED Design (Final)

## ✅ Decisions Made

Based on user feedback, here's the finalized design:

1. **Navigation**: Home screen quick action ✅
2. **AI Advice**: Phase 1 MVP ✅
3. **Favorite Routes**: Yes, with save/load ✅
4. **Waypoints**: Phase 1 MVP ✅
5. **Premium Tier**: Waypoints 3+ airports behind paywall ✅

---

## 💎 Premium Tier Strategy: Waypoints

### Free Tier (Included)
```
┌────────────────────────────────┐
│ Direct Flights Only            │
│                                │
│ 🛫 KPAO (Departure)            │
│      ↓  Direct Route           │
│ 🛬 KSQL (Arrival)              │
│                                │
│ ✅ Perfect for:                │
│ • Pattern work                 │
│ • Short cross-countries        │
│ • Local flights                │
│ • 90% of student pilot needs   │
└────────────────────────────────┘
```

**What You Get:**
- 2 airports: Departure → Arrival
- Direct routing only
- All other features (weather, AI advice, metrics)

### Premium Tier ($29.99 one-time)
```
┌────────────────────────────────┐
│ Multi-Waypoint Routes          │
│                                │
│ 🛫 KPAO (Departure)            │
│      ↓                         │
│ 📍 VPSUN (VFR waypoint)        │
│      ↓                         │
│ 📍 KHAF (Stop/fuel)            │
│      ↓                         │
│ 🛬 KSFO (Arrival)              │
│                                │
│ ✅ Perfect for:                │
│ • Complex cross-countries      │
│ • IFR practice                 │
│ • Multi-leg flights            │
│ • Checkride preparation        │
└────────────────────────────────┘
```

**What You Get:**
- Unlimited waypoints (3+ airports)
- VFR checkpoints, VORs, Fixes
- Fuel stops along route
- Complex routing practice
- 20,000+ airports (from airport premium)

### Why This Works

**Value Proposition:**
- ✅ Free tier covers direct flights (most common)
- ✅ Premium unlocks complex training scenarios
- ✅ Aligns with "serious pilot" audience
- ✅ Clear upgrade trigger: "Plan a 3+ leg flight"

**Conversion Triggers:**
1. Student planning long cross-country (requires fuel stop)
2. Pilot practicing IFR routing (waypoints required)
3. CFI wanting complex scenarios for students

**Bundled Value:**
- Premium already includes 20,000+ airports
- Adding waypoints strengthens the bundle
- Single $29.99 purchase unlocks BOTH

---

## Updated Main Screen Design

### Free Tier UI

```
┌──────────────────────────────────────────────┐
│ ← Back             Plan Flight          Save │
├──────────────────────────────────────────────┤
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  ROUTE                               │   │
│  │                                      │   │
│  │  Departure                           │   │
│  │  [KPAO] Palo Alto, CA         🔍     │   │
│  │  ↓  Direct  ·  31nm  ·  15 min       │   │
│  │  Arrival                             │   │
│  │  [KSQL] San Carlos, CA        🔍     │   │
│  │                                      │   │
│  │  🔒 Want to add waypoints?           │   │
│  │     [Unlock Premium - $29.99]        │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  FLIGHT DETAILS                      │   │
│  │                                      │   │
│  │  Aircraft                            │   │
│  │  [Cessna 172S Skyhawk]        ▼     │   │
│  │                                      │   │
│  │  Altitude: [3,500 ft]         ▼     │   │
│  │  Speed: [110 kts]                    │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  FLIGHT METRICS             Auto ✓   │   │
│  │                                      │   │
│  │  Distance: 31 nautical miles         │   │
│  │  Est. Time: 15 minutes               │   │
│  │  Fuel: 5.2 gallons (w/ reserve)     │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  WEATHER CONDITIONS        Updated 2m│   │
│  │                                      │   │
│  │  🛫 KPAO (Departure)           VFR   │   │
│  │     Clear skies • Wind 310@8kt       │   │
│  │     Temp 72°F • Altim 30.12         │   │
│  │     [View METAR]                     │   │
│  │                                      │   │
│  │  🛬 KSQL (Arrival)             VFR   │   │
│  │     Few clouds • Wind 300@6kt        │   │
│  │     Temp 70°F • Altim 30.14         │   │
│  │     [View METAR]                     │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  💡 AI FLIGHT ADVICE                 │   │
│  │                                      │   │
│  │  Excellent VFR conditions for this   │   │
│  │  short hop. Wind favors runway 31 at │   │
│  │  departure and runway 30 at arrival. │   │
│  │  Expect light traffic. Perfect for   │   │
│  │  pattern work or touch-and-go.       │   │
│  │                                      │   │
│  │  [Get Detailed Analysis]             │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  [Start ATC Practice]                        │
│                                              │
└──────────────────────────────────────────────┘
```

### Premium Tier UI

```
┌──────────────────────────────────────────────┐
│ ← Back             Plan Flight          Save │
├──────────────────────────────────────────────┤
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  ROUTE                               │   │
│  │                                      │   │
│  │  Departure                           │   │
│  │  [KPAO] Palo Alto, CA         🔍     │   │
│  │  ↓  12nm  ·  6 min                   │   │
│  │  Waypoint 1                   [×]    │   │
│  │  [KHAF] Half Moon Bay         🔍     │   │
│  │  ↓  25nm  ·  13 min                  │   │
│  │  Waypoint 2                   [×]    │   │
│  │  [KSFO] San Francisco Intl    🔍     │   │
│  │  ↓  15nm  ·  8 min                   │   │
│  │  Arrival                             │   │
│  │  [KSJC] San Jose Intl         🔍     │   │
│  │                                      │   │
│  │  Total: 52nm  ·  27 minutes          │   │
│  │                                      │   │
│  │  [+ Add Waypoint]             💎     │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  FLIGHT DETAILS                      │   │
│  │  ... (same as free tier)             │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  FLIGHT METRICS             Auto ✓   │   │
│  │                                      │   │
│  │  Total Distance: 52 nautical miles   │   │
│  │  Total Time: 27 minutes              │   │
│  │  Fuel: 8.4 gallons (w/ reserve)     │   │
│  │  Legs: 3                             │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  WEATHER CONDITIONS        Updated 2m│   │
│  │                                      │   │
│  │  🛫 KPAO                        VFR   │   │
│  │  📍 KHAF                        VFR   │   │
│  │  📍 KSFO                        VFR   │   │
│  │  🛬 KSJC                        VFR   │   │
│  │                                      │   │
│  │  [View All METARs]                   │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  💡 AI FLIGHT ADVICE                 │   │
│  │                                      │   │
│  │  Complex multi-leg flight through    │   │
│  │  Class B airspace. Be prepared for   │   │
│  │  4 frequency changes. KSFO requires  │   │
│  │  Class B clearance. Allow extra time │   │
│  │  for taxi at KSFO (busy airport).    │   │
│  │                                      │   │
│  │  [Get Detailed Analysis]             │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  [Start ATC Practice]                        │
│                                              │
└──────────────────────────────────────────────┘
```

---

## Premium Upgrade Prompts

### Scenario 1: User Tries to Add 3rd Airport

```
┌────────────────────────────────────────┐
│ 🔒 Premium Feature                     │
├────────────────────────────────────────┤
│                                        │
│ Multi-Waypoint Routes                  │
│                                        │
│ Unlock premium to add waypoints and    │
│ practice complex cross-country flights.│
│                                        │
│ ✓ Unlimited waypoints                  │
│ ✓ 20,000+ US/Canada airports          │
│ ✓ Complex routing practice             │
│ ✓ IFR procedure training               │
│ ✓ One-time payment, lifetime access    │
│                                        │
│ [Unlock Premium - $29.99]   [Cancel]   │
│                                        │
└────────────────────────────────────────┘
```

### Scenario 2: Info Banner (Soft Sell)

```
┌────────────────────────────────────────┐
│ 💡 Planning a long cross-country?      │
│                                        │
│ Premium unlocks waypoints for complex  │
│ routes with fuel stops and VFR checks. │
│                                        │
│ [Learn More]                      [×]  │
└────────────────────────────────────────┘
```

---

## Home Screen Integration

### Updated Home Screen with "Plan Flight" Quick Action

```
┌──────────────────────────────────────────────┐
│                                              │
│  Good afternoon, Pilot! ☀️                   │
│  Rank: Private Pilot                         │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  QUICK ACTIONS                       │   │
│  │                                      │   │
│  │  ┌────────────┐  ┌────────────┐     │   │
│  │  │ 🎯         │  │ ✈️         │     │   │
│  │  │ Start      │  │ Plan       │     │   │
│  │  │ Training   │  │ Flight     │ ← NEW│
│  │  └────────────┘  └────────────┘     │   │
│  │                                      │   │
│  │  ┌────────────┐  ┌────────────┐     │   │
│  │  │ 🌤️         │  │ 📊         │     │   │
│  │  │ Check      │  │ View       │     │   │
│  │  │ Weather    │  │ Logbook    │     │   │
│  │  └────────────┘  └────────────┘     │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  RECENT ACTIVITY                     │   │
│  │                                      │   │
│  │  Yesterday • Cross-Country VFR       │   │
│  │  KPAO → KSQL • Score: 92            │   │
│  │                                      │   │
│  │  2 days ago • Pattern Practice       │   │
│  │  KPAO Local • Score: 88             │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  THIS WEEK                           │   │
│  │                                      │   │
│  │  🔥 3 day streak                     │   │
│  │  ⏱ 105 minutes practiced             │   │
│  │  📚 5 sessions completed             │   │
│  └──────────────────────────────────────┘   │
│                                              │
└──────────────────────────────────────────────┘
```

**Tap "Plan Flight" → Opens Flight Planning Screen**

---

## Favorite Routes / Saved Plans

### Screen: My Saved Routes

```
┌──────────────────────────────────────────────┐
│ ← Back         My Saved Routes         +New  │
├──────────────────────────────────────────────┤
│                                              │
│  FAVORITES ⭐                                 │
│  ┌────────────────────────────────────────┐ │
│  │ ⭐ KPAO → KSQL                         │ │
│  │ "Quick Bay Area Hop"                   │ │
│  │ 31nm · 15min · Cessna 172 · Direct    │ │
│  │ Flown 8 times · Avg score: 89         │ │
│  │                                        │ │
│  │ [Practice]  [Edit]  [⋮]               │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ ⭐ KPAO → KHAF → KSFO                  │ │
│  │ "Coastal Route" 💎 Premium             │ │
│  │ 52nm · 27min · Cessna 172              │ │
│  │ Flown 3 times · Avg score: 91         │ │
│  │                                        │ │
│  │ [Practice]  [Edit]  [⋮]               │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  RECENT                                      │
│  ┌────────────────────────────────────────┐ │
│  │ KSFO → KSJC                            │ │
│  │ 28nm · 12min · Piper Archer            │ │
│  │ Last flown: 2 days ago                 │ │
│  │                                        │ │
│  │ [Practice]  [Save as Favorite]  [⋮]   │ │
│  └────────────────────────────────────────┘ │
│                                              │
└──────────────────────────────────────────────┘
```

### Features

**⭐ Favorites**
- Star your most-used routes
- Give them custom names ("Quick Bay Area Hop")
- Shows usage stats (times flown, avg score)
- Quick "Practice" button

**📊 Route Statistics**
- Times flown
- Average score
- Last flown date
- Total distance/time

**💎 Premium Indicator**
- Routes with 3+ airports show "Premium" badge
- Can still view details if premium user
- Locked if free user tries to practice

**Actions Menu [⋮]**
- Edit route
- Duplicate route
- Delete route
- Share route (export)

---

## AI Advice Integration

### AI Advice Card (Always Visible in Phase 1)

```
┌────────────────────────────────────────┐
│ 💡 AI FLIGHT ADVICE                    │
│                                        │
│ [Brief 2-3 sentence summary]           │
│                                        │
│ [Get Detailed Analysis]                │
└────────────────────────────────────────┘
```

**Brief Summary Examples:**

**Good Conditions:**
> "Excellent VFR conditions for this short hop. Wind favors runway 31 at departure and runway 30 at arrival. Expect light traffic."

**Weather Warning:**
> "MVFR conditions at arrival. Visibility 4SM in mist. Consider delaying departure or filing alternate. Monitor ATIS closely."

**Complex Route:**
> "Multi-leg flight through Class B airspace. Be prepared for 4 frequency changes. KSFO requires Class B clearance."

**IFR Practice:**
> "Good IMC practice scenario. Expect vectors for ILS 31L. Be ready for hold instructions if traffic is busy."

### Detailed AI Analysis Modal

**Tap "Get Detailed Analysis" → Opens Full-Screen Modal**

```
┌──────────────────────────────────────────────┐
│ ← Back         Flight Analysis               │
├──────────────────────────────────────────────┤
│                                              │
│  KPAO → KSQL                                 │
│  31nm · 15 minutes · Cessna 172              │
│                                              │
│  ──────────────────────────────              │
│                                              │
│  WEATHER ANALYSIS                            │
│                                              │
│  Departure (KPAO): VFR                       │
│  • Wind: 310° at 8 knots (light crosswind)  │
│  • Visibility: 10+ SM (excellent)            │
│  • Ceiling: Clear skies                      │
│  • Altimeter: 30.12 (high pressure)          │
│                                              │
│  ✅ No significant weather concerns           │
│                                              │
│  Arrival (KSQL): VFR                         │
│  • Wind: 300° at 6 knots (light, favoring)  │
│  • Visibility: 10+ SM (excellent)            │
│  • Ceiling: Few clouds at 3,000 ft           │
│  • Altimeter: 30.14 (stable)                 │
│                                              │
│  ✅ Perfect conditions for arrival            │
│                                              │
│  ──────────────────────────────              │
│                                              │
│  ROUTE CONSIDERATIONS                        │
│                                              │
│  • Direct route avoids controlled airspace   │
│  • Stay south of KSFO Class B (3,000 ft)    │
│  • Monitor San Francisco Approach 120.5      │
│  • VFR flight following recommended          │
│                                              │
│  ──────────────────────────────              │
│                                              │
│  ATC COMMUNICATIONS                          │
│                                              │
│  Expected Frequencies:                       │
│  1. KPAO Tower: 118.6                        │
│  2. Bay Approach (optional): 120.5           │
│  3. KSQL Tower: 119.0                        │
│                                              │
│  Expect 2-3 frequency changes if using       │
│  flight following.                           │
│                                              │
│  ──────────────────────────────              │
│                                              │
│  FUEL & PERFORMANCE                          │
│                                              │
│  • Fuel required: 3.5 gallons                │
│  • Fuel reserve: 1.7 gallons (50%)          │
│  • Total: 5.2 gallons                        │
│  • Aircraft capacity: 56 gallons ✅          │
│                                              │
│  Plenty of fuel for this short flight.      │
│                                              │
│  ──────────────────────────────              │
│                                              │
│  RECOMMENDATIONS                             │
│                                              │
│  ✅ Good conditions, proceed as planned       │
│  • Request flight following for practice     │
│  • Monitor KSFO Class B boundaries           │
│  • Expect runway 31 at departure             │
│  • Expect runway 30 at arrival               │
│  • Light traffic expected mid-afternoon      │
│                                              │
└──────────────────────────────────────────────┘
```

---

## Premium Feature Summary

### What's Free

✅ **Direct Flights (A → B)**
- Departure + Arrival (2 airports)
- 30 major airports included
- Weather + AI advice
- Favorite routes
- All flight metrics
- ATC practice

### What's Premium ($29.99)

💎 **Multi-Waypoint Routes (A → B → C → D...)**
- Unlimited waypoints (3+ airports)
- 20,000+ US/Canada airports
- Complex cross-country training
- IFR routing practice
- Fuel stop planning
- VFR checkpoint routing

### Bundled Value

**Premium unlocks BOTH:**
1. 20,000+ airports (vs 30 free)
2. Multi-waypoint routes (vs 2 airports)

**Single purchase: $29.99 lifetime**

---

## Implementation Plan - Phase 1 MVP

### What to Build Now

1. **Flight Planning Screen** ✈️
   - Departure/arrival inputs with search
   - Aircraft selection
   - Altitude/speed inputs
   - Auto-calculated metrics (distance, time, fuel)
   - Weather display (METAR for both airports)
   - AI advice card (brief summary + detailed analysis)
   - Premium waypoint upsell banner (if free user)
   - [+ Add Waypoint] button (locked for free, functional for premium)
   - Save as favorite
   - [Start ATC Practice] button

2. **Home Screen Quick Action** 🏠
   - Add "Plan Flight" card to quick actions grid
   - Navigation to flight planning screen

3. **Saved Routes Screen** ⭐
   - List of favorite routes
   - Recent routes
   - Usage statistics
   - Quick "Practice" button
   - Edit/delete actions

4. **Airport Search** 🔍
   - Search by ICAO, IATA, name
   - Recent airports
   - Nearby airports
   - Premium indicator (🔒)
   - Upgrade prompt for premium airports

5. **Premium Prompts** 💎
   - Waypoint upgrade modal
   - Premium airport upgrade modal
   - Info banners (soft sell)

6. **Backend Integration** 🔌
   - FlightPlanRepository (already exists)
   - Weather service integration
   - AI advice service integration
   - Save/load favorite routes
   - Premium status check

---

## Database Schema Updates

### Add to FlightPlan Entity

```kotlin
@Entity(tableName = "flight_plans")
data class FlightPlan(
    // ... existing fields ...
    val isFavorite: Boolean = false,      // ← NEW
    val customName: String? = null,        // ← NEW ("Quick Bay Area Hop")
    val timesFlown: Int = 0,              // ← NEW
    val averageScore: Float? = null,       // ← NEW
    val lastFlownDate: Long? = null        // ← NEW
)
```

### Waypoint Already Supports Multi-Leg

```kotlin
@Entity(tableName = "waypoints")
data class Waypoint(
    val flightPlanId: Long,
    val sequence: Int,              // Order in route (1, 2, 3...)
    val identifier: String,
    val waypointType: WaypointType  // AIRPORT, VOR, FIX, GPS
)
```

✅ Backend already supports this!

---

## Next Steps

### Ready to Build?

I have a complete spec now. Want me to:

1. ✅ **Start building Phase 1 MVP** (flight planning screen, home integration, saved routes)
2. ⏸️ **Refine the design more** (any changes?)
3. 📝 **Create detailed component specs** (each component broken down)

**Estimated Implementation Time:**
- Flight planning screen: 3-4 hours
- Home screen integration: 30 minutes
- Saved routes screen: 2-3 hours
- Premium prompts: 1 hour
- **Total: ~7-9 hours of work**

Let me know if the design looks good and I'll start building! 🚀
