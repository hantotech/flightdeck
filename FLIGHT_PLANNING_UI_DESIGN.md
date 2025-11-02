# Flight Planning UI Design

## Design Philosophy

**FlightDeck is NOT ForeFlight** - We're not building comprehensive flight planning software. Our goal:

> **"Plan just enough to practice the ATC communications for YOUR actual flight"**

**Focus:**
- ✅ Quick flight entry (departure → arrival)
- ✅ See basic route info (distance, time, fuel)
- ✅ Check weather at both ends
- ✅ Start ATC practice immediately
- ❌ No complex routing calculations
- ❌ No weight & balance
- ❌ No detailed charts
- ❌ No NavLog generation

---

## User Flow

```
Home Screen
    ↓ Tap "Plan Flight"
Flight Planning Screen
    ↓ Enter KPAO → KSQL
    ↓ Select Aircraft (Cessna 172)
    ↓ See distance, time, fuel
    ↓ Review weather (auto-loaded)
    ↓ Tap "Start Practice"
ATC Practice Screen
    ↓ Voice-based ATC with real weather
```

**Alternative Entry Points:**
1. **Quick Practice** (from Home): Enter departure/arrival → Practice immediately
2. **From Mission Selection**: Select mission → Auto-creates flight plan → Practice
3. **Saved Plans**: Recent flights list → Select → Practice again

---

## Screen 1: Flight Planning Main Screen

### Layout

```
┌──────────────────────────────────────────────┐
│ ← Back             Flight Plan          Save │
├──────────────────────────────────────────────┤
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  FLIGHT DETAILS                      │   │
│  │                                      │   │
│  │  Departure                           │   │
│  │  [KPAO] Palo Alto, CA         🔍     │   │
│  │  ↓  31nm  ·  15 min  ·  VFR          │   │
│  │  Arrival                             │   │
│  │  [KSQL] San Carlos, CA        🔍     │   │
│  │                                      │   │
│  │  Aircraft                            │   │
│  │  [Cessna 172S Skyhawk]        ▼     │   │
│  │                                      │   │
│  │  Route                               │   │
│  │  [Direct]                     ▼     │   │
│  │  • Altitude: 3,500 ft               │   │
│  │  • Speed: 110 kts                   │   │
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
│  │  💡 AI ADVICE                        │   │
│  │                                      │   │
│  │  Great VFR conditions! Wind favors   │   │
│  │  runway 31 at departure. Expect     │   │
│  │  light traffic on this short hop.   │   │
│  │  [Get More Advice]                   │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  [Start ATC Practice]                        │
│                                              │
└──────────────────────────────────────────────┘
```

### Key Features

**✅ Auto-Calculation**
- Distance calculated from coordinates (Haversine formula)
- Time = Distance / Cruise Speed
- Fuel = (Time * Burn Rate) + 50% reserve
- Updates live as you change inputs

**✅ Weather Integration**
- Auto-loads METAR for both airports
- Shows flight category (VFR/MVFR/IFR/LIFR)
- Color-coded: 🟢 VFR, 🟡 MVFR, 🔴 IFR, 🔴 LIFR
- Tap "View METAR" for full text

**✅ AI Advice**
- Optional AI-powered flight planning advice
- Analyzes weather, route, aircraft
- Gives brief summary
- Tap "Get More Advice" for detailed analysis

**✅ Simplicity**
- No complex routing (Direct or Airway)
- Default altitude/speed from aircraft profile
- Single-page form (no multi-step wizard)

---

## Screen 2: Airport Search

**Triggered by tapping search icon 🔍 next to airport field**

```
┌──────────────────────────────────────────────┐
│ ← Cancel      Select Airport                 │
├──────────────────────────────────────────────┤
│  🔍 Search airports...                       │
├──────────────────────────────────────────────┤
│                                              │
│  RECENT                                      │
│  🛫 KPAO - Palo Alto Airport                 │
│     Class E • Towered • Palo Alto, CA        │
│                                              │
│  🛫 KSQL - San Carlos Airport                │
│     Class D • Towered • San Carlos, CA       │
│                                              │
│  ────────────────────────────────            │
│                                              │
│  NEARBY (within 50nm)                        │
│  🛫 KSFO - San Francisco Intl      13nm      │
│     Class B • Towered • San Francisco, CA    │
│                                              │
│  🛫 KSJC - San Jose Intl          18nm      │
│     Class C • Towered • San Jose, CA         │
│                                              │
│  🔒 KRHV - Reid-Hillview          22nm      │
│     Class D • Towered • San Jose, CA         │
│     [Premium]                                │
│                                              │
│  🛫 KHAF - Half Moon Bay          25nm      │
│     Class G • Non-towered • Half Moon Bay    │
│                                              │
└──────────────────────────────────────────────┘
```

**Features:**
- Search by ICAO, IATA, or name
- Recent airports at top
- Nearby airports (uses device location if available)
- Premium airports show 🔒 indicator
- Tap premium airport → Upgrade prompt

---

## Screen 3: Saved Flight Plans

**Accessible from: Home → "Saved Plans" or Planning screen → "Recent"**

```
┌──────────────────────────────────────────────┐
│ ← Back         Saved Flight Plans     +New   │
├──────────────────────────────────────────────┤
│                                              │
│  THIS WEEK                                   │
│  ┌────────────────────────────────────────┐ │
│  │ KPAO → KSQL                            │ │
│  │ 31nm · 15min · Cessna 172              │ │
│  │ VFR · Last flown 2 days ago            │ │
│  │ [Practice Again]  [Edit]  [Delete]     │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  ┌────────────────────────────────────────┐ │
│  │ KSFO → KSJC                            │ │
│  │ 28nm · 12min · Piper Archer            │ │
│  │ VFR · Last flown 4 days ago            │ │
│  │ [Practice Again]  [Edit]  [Delete]     │ │
│  └────────────────────────────────────────┘ │
│                                              │
│  LAST MONTH                                  │
│  ┌────────────────────────────────────────┐ │
│  │ KPAO → KHAF                            │ │
│  │ 25nm · 13min · Cessna 172              │ │
│  │ VFR · Last flown 18 days ago           │ │
│  │ [Practice Again]  [Edit]  [Delete]     │ │
│  └────────────────────────────────────────┘ │
│                                              │
└──────────────────────────────────────────────┘
```

**Actions:**
- **Practice Again**: Loads plan → Refreshes weather → Start practice
- **Edit**: Opens planning screen with pre-filled data
- **Delete**: Removes from saved plans

---

## Screen 4: Mission → Flight Plan Integration

**When user selects a mission from Mission Selection screen:**

```
┌──────────────────────────────────────────────┐
│ ← Back      Mission: Cross-Country VFR      │
├──────────────────────────────────────────────┤
│                                              │
│  ⭐⭐ Intermediate • 35 minutes               │
│  Moderate traffic • Typical VFR              │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  SET UP YOUR FLIGHT                  │   │
│  │                                      │   │
│  │  This mission practices cross-country│   │
│  │  communication with frequency changes│   │
│  │  and traffic conflicts.              │   │
│  │                                      │   │
│  │  Departure                           │   │
│  │  [Select Airport]            🔍      │   │
│  │  ↓                                   │   │
│  │  Arrival                             │   │
│  │  [Select Airport]            🔍      │   │
│  │                                      │   │
│  │  Aircraft                            │   │
│  │  [Cessna 172S]               ▼      │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  💡 TIP: Select airports 20-50nm apart      │
│  for best cross-country experience.          │
│                                              │
│  [Create Flight Plan]                        │
│                                              │
└──────────────────────────────────────────────┘

      ↓ After creating plan

┌──────────────────────────────────────────────┐
│ ← Back      Mission: Cross-Country VFR      │
├──────────────────────────────────────────────┤
│  KPAO → KSQL · 31nm · 15 min · Cessna 172   │
│  VFR Conditions                              │
├──────────────────────────────────────────────┤
│                                              │
│  ✓ Flight plan created                       │
│  ✓ Weather loaded                            │
│  ✓ Mission parameters applied                │
│                                              │
│  MISSION FEATURES:                           │
│  • Moderate traffic (3-5 aircraft)          │
│  • 2 frequency changes expected              │
│  • Traffic conflicts possible                │
│  • AI-powered realistic ATC                  │
│                                              │
│  [Start Mission]                             │
│                                              │
└──────────────────────────────────────────────┘
```

---

## Component Breakdown

### 1. Airport Input Field

```kotlin
// Component: AirportInputField
┌────────────────────────────────────┐
│ Label: Departure                   │
│ ┌──────────────────────────────┐   │
│ │ KPAO  Palo Alto, CA      🔍  │   │
│ └──────────────────────────────┘   │
│ Class E • Towered • Elev 4ft       │
└────────────────────────────────────┘
```

**Features:**
- Autocomplete dropdown as you type
- Shows airport details below
- Search icon opens full airport search
- Recent airports suggested
- Premium indicator if locked

### 2. Weather Card

```kotlin
// Component: WeatherSummaryCard
┌────────────────────────────────────┐
│ 🛫 KPAO (Departure)         VFR    │
│ Clear skies • Wind 310@8kt         │
│ Temp 72°F • Altim 30.12           │
│ [View Full METAR]                  │
└────────────────────────────────────┘
```

**Features:**
- Flight category badge (color-coded)
- Simplified weather summary
- Tap to expand full METAR text
- Auto-refresh every 5 minutes

### 3. Flight Metrics Card

```kotlin
// Component: FlightMetricsCard
┌────────────────────────────────────┐
│ FLIGHT METRICS           Auto ✓    │
│                                    │
│ Distance: 31 nautical miles        │
│ Est. Time: 15 minutes              │
│ Fuel: 5.2 gallons (w/ reserve)    │
└────────────────────────────────────┘
```

**Features:**
- Auto-calculates on input change
- Shows reserve fuel (50% added)
- Simplified presentation
- No complex fuel planning

### 4. AI Advice Card (Optional)

```kotlin
// Component: AIAdviceCard
┌────────────────────────────────────┐
│ 💡 AI ADVICE                       │
│                                    │
│ Great VFR conditions! Wind favors  │
│ runway 31 at departure.            │
│ [Get More Advice]                  │
└────────────────────────────────────┘
```

**Features:**
- Optional (can be hidden)
- Brief 2-3 sentence summary
- Tap for detailed AI analysis
- Uses EnhancedAIService backend

---

## Navigation Integration

### Bottom Navigation (5 tabs)

```
┌────────────────────────────────────────┐
│ 🏠 Home | 🎯 Practice | 🌤️ Weather     │
│ 📊 Logbook | ⚙️ Settings               │
└────────────────────────────────────────┘
```

**Where does Flight Planning fit?**

**Option 1: In Practice Tab (Recommended)**
```
Practice Tab
├── Quick Practice (departure/arrival → go)
├── Mission Selection
│   └── Creates flight plan automatically
└── Saved Flight Plans
```

**Option 2: Dedicated Tab (If you want 6 tabs)**
```
Add 6th tab: ✈️ Plan
- But this dilutes focus
- Most users just want Quick Practice
```

**Option 3: Home Quick Action (Recommended)**
```
Home Screen
├── Start Training (→ Mission Selection)
├── Quick Practice (→ Flight Planning)
├── Check Weather
└── View Logbook
```

### Recommended: **Home Quick Action + Practice Tab Integration**

---

## Responsive Design

### Mobile (Phone)

- **Single column** layout
- Cards stack vertically
- Collapsible sections (weather, AI advice)
- Large touch targets (48dp minimum)
- Bottom sheet for airport search

### Tablet (Optional)

- **Two-column** layout
  - Left: Flight inputs
  - Right: Weather + Metrics + AI
- More screen real estate
- Side panel for saved plans

---

## Data Flow

```
┌─────────────────────┐
│ Flight Planning UI  │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│ FlightPlanViewModel │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────────────────────┐
│ FlightPlanRepository                │
│ • createFlightPlan()                │
│ • calculateFlightMetrics()          │
│ • getWeatherBriefing()              │
│ • getFlightPlanningAdvice() [AI]   │
└──────────┬──────────────────────────┘
           │
           ├─→ FlightPlanDao (Room DB)
           ├─→ AviationWeatherService (Weather API)
           └─→ EnhancedAIService (AI advice)
```

---

## Key Design Decisions

### 1. **Keep It Simple**
- ❌ No complex routing (VOR airways, SIDs/STARs)
- ❌ No weight & balance calculations
- ❌ No performance charts
- ✅ Just enough to set up ATC practice

### 2. **Weather is Auto-Loaded**
- Automatically fetches METAR on airport selection
- No manual "Get Weather" button needed
- Always shows current conditions

### 3. **AI Advice is Optional**
- Not forced on users
- Available if they want it
- Brief and actionable

### 4. **Mobile-First**
- Designed for phone screens
- Touch-friendly
- Fast entry (minimal typing)

### 5. **Integration with Missions**
- Missions auto-create flight plans
- Users can override departure/arrival
- Seamless flow

---

## Implementation Priority

### Phase 1: Basic Flight Planning ✅ (This is what to build now)
- [ ] Flight planning screen (single page)
- [ ] Airport search with free/premium filtering
- [ ] Auto-calculate distance, time, fuel
- [ ] Weather integration (METAR display)
- [ ] Save flight plan
- [ ] "Start Practice" button → ATC Practice screen

### Phase 2: Enhanced Features
- [ ] AI advice integration
- [ ] Waypoints support (optional)
- [ ] Alternate airport selection
- [ ] Route options (Direct vs Airway)

### Phase 3: Advanced
- [ ] Saved plans management
- [ ] Recent flights list
- [ ] Quick re-fly
- [ ] Share flight plan (export)

---

## Sample Screens (Mockup Descriptions)

### Screen 1: Empty State
```
Big airplane icon
"Plan Your Flight"
Subtitle: "Enter departure and arrival to get started"
[Get Started]
```

### Screen 2: Quick Entry
```
Two large input fields:
- FROM [  ]  🔍
- TO   [  ]  🔍
Everything else auto-fills
[Start Practice]
```

### Screen 3: Full Details
```
Complete form with:
- Airports
- Aircraft
- Weather
- Metrics
- AI advice
[Start Practice]
```

---

## Next Steps

1. **Review this design doc** - Does it fit your vision?
2. **Choose navigation approach** - Home quick action vs dedicated tab?
3. **Build Phase 1** - Basic flight planning screen
4. **Integrate with ATC practice** - Pass flight plan to practice screen
5. **Test with users** - Get feedback on simplicity

**Questions to answer:**
- Where should "Plan Flight" live? (Home quick action vs Practice tab?)
- Do you want AI advice in MVP or Phase 2?
- Should waypoints be Phase 1 or Phase 2?
- How important is "Saved Plans" management?
