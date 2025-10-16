# FlightDeck UI Design Specification

## 🎨 App Structure

### Navigation Architecture
```
MainActivity (Bottom Navigation)
├─ Home Tab
│  └─ Quick Actions Dashboard
├─ Practice Tab
│  ├─ ATC Simulator
│  ├─ Checklists
│  └─ Flight Planning
├─ Weather Tab
│  ├─ ATIS Viewer
│  └─ Airport Search
└─ Profile Tab
   ├─ Settings
   ├─ Progress
   └─ API Configuration
```

---

## 📱 Screen Designs

### 1. Home Screen (Dashboard)
**Purpose**: Quick access to all features

**Components**:
- Welcome message with user info
- Quick action cards:
  - 🎙️ Get ATIS
  - 📡 Practice ATC
  - ✅ Start Checklist
  - 🗺️ Plan Flight
- Recent activity
- Progress summary

**Layout**:
```
┌─────────────────────────────┐
│ FlightDeck        [Settings]│
├─────────────────────────────┤
│ Good afternoon, Pilot!      │
│ Ready to practice?          │
├─────────────────────────────┤
│ ┌──────┐ ┌──────┐          │
│ │ 🎙️   │ │ 📡   │          │
│ │ ATIS │ │ ATC  │          │
│ └──────┘ └──────┘          │
│ ┌──────┐ ┌──────┐          │
│ │ ✅   │ │ 🗺️   │          │
│ │Check │ │Plan  │          │
│ └──────┘ └──────┘          │
├─────────────────────────────┤
│ Recent Activity             │
│ • ATC Practice - KPAO       │
│ • Checklist - C172          │
└─────────────────────────────┘
```

---

### 2. ATIS Viewer Screen
**Purpose**: Get weather broadcasts for airports

**Features**:
- Airport search/selection
- Current ATIS display
- Formatted readback
- Copy button
- Listen button (text-to-speech future)

**Layout**:
```
┌─────────────────────────────┐
│ ← ATIS / AWOS               │
├─────────────────────────────┤
│ Airport: [KPAO      ] [🔍]  │
│                             │
│ ┌─────────────────────────┐ │
│ │ Palo Alto Airport       │ │
│ │ Information Alpha       │ │
│ ├─────────────────────────┤ │
│ │ Time: 1856Z             │ │
│ │ Wind: 090° at 8kt       │ │
│ │ Visibility: 10 SM       │ │
│ │ Ceiling: BKN 25000      │ │
│ │ Temp: 23°C Dew: 14°C    │ │
│ │ Altimeter: 30.12        │ │
│ │ Runway: 31 in use       │ │
│ └─────────────────────────┘ │
│                             │
│ [View Full Broadcast]       │
│ [Copy to Clipboard]         │
└─────────────────────────────┘
```

---

### 3. ATC Simulator Screen
**Purpose**: Practice radio communications

**Features**:
- Chat interface
- Scenario selection
- AI-powered ATC responses
- Traffic awareness
- Evaluation/scoring

**Layout**:
```
┌─────────────────────────────┐
│ ← ATC Simulator             │
├─────────────────────────────┤
│ Scenario: Ground Clearance  │
│ Airport: KPAO               │
│ Info: Alpha | Rwy: 31       │
├─────────────────────────────┤
│                             │
│ 📡 ATC:                     │
│ "Skyhawk 12345, Palo Alto  │
│  Ground, taxi runway 31    │
│  via Alpha"                │
│                             │
│ 👨‍✈️ You:                     │
│ "Palo Alto Ground,         │
│  Skyhawk 12345, ready to   │
│  taxi, information Alpha"  │
│                             │
│ ℹ️ Traffic:                  │
│ • Citation 3-mile final    │
│                             │
├─────────────────────────────┤
│ [Type message...]      [▶]  │
└─────────────────────────────┘
```

---

### 4. Checklist Screen
**Purpose**: Interactive pre-flight checklists

**Features**:
- Checklist category selection
- Item-by-item progression
- ATIS integration
- AI guidance
- Completion tracking

**Layout**:
```
┌─────────────────────────────┐
│ ← Pre-Flight Checklist      │
├─────────────────────────────┤
│ Aircraft: Cessna 172        │
│ Progress: 3/12 ▓▓▓░░░░░░░  │
├─────────────────────────────┤
│                             │
│ ☑ Weather Briefing          │
│ ☑ ATIS - Listen             │
│   Info: Alpha | Alt: 30.12  │
│                             │
│ ▶ Altimeter - Set           │
│   Set altimeter to current  │
│   ATIS setting (30.12)      │
│                             │
│   Current: [    ]           │
│   [✓ Completed]             │
│                             │
│ ☐ NOTAMs - Review           │
│ ☐ Fuel Quantity - Check     │
│                             │
└─────────────────────────────┘
```

---

### 5. Airport Search Screen
**Purpose**: Find and view airport information

**Features**:
- Search by ICAO/name/city
- Nearby airports
- Airport details
- Runway information
- Frequencies

**Layout**:
```
┌─────────────────────────────┐
│ ← Airport Database          │
├─────────────────────────────┤
│ Search: [Palo Alto    ] [🔍]│
│                             │
│ [My Location] [Nearby]      │
├─────────────────────────────┤
│ Results (3):                │
│                             │
│ ┌─────────────────────────┐ │
│ │ 🗼 KPAO - Palo Alto     │ │
│ │ 0.0 mi | Class E | 4'   │ │
│ │ Rwy 13/31: 2443'        │ │
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ 🗼 KNUQ - Moffett Fed   │ │
│ │ 3.2 mi | Class D | 32'  │ │
│ └─────────────────────────┘ │
└─────────────────────────────┘
```

---

### 6. Settings Screen
**Purpose**: Configure app and API keys

**Features**:
- API key configuration
- User tier selection
- Preferences
- About

**Layout**:
```
┌─────────────────────────────┐
│ ← Settings                  │
├─────────────────────────────┤
│ AI Configuration            │
│ ┌─────────────────────────┐ │
│ │ Claude API Key          │ │
│ │ [sk-ant-...    ] [Edit] │ │
│ │                         │ │
│ │ Gemini API Key          │ │
│ │ [AIzaSy...     ] [Edit] │ │
│ │                         │ │
│ │ User Tier: Basic ▼      │ │
│ └─────────────────────────┘ │
│                             │
│ Preferences                 │
│ ┌─────────────────────────┐ │
│ │ ☑ Enable notifications  │ │
│ │ ☑ Auto-refresh ATIS     │ │
│ │ ☐ Voice readback (TTS)  │ │
│ └─────────────────────────┘ │
│                             │
│ About                       │
│ Version 1.0.0 (Beta)        │
└─────────────────────────────┘
```

---

## 🎨 Design System

### Colors (Aviation Theme)
```kotlin
object FlightDeckColors {
    val Primary = Color(0xFF1976D2)        // Aviation Blue
    val PrimaryDark = Color(0xFF004BA0)
    val Accent = Color(0xFFFF6F00)         // Safety Orange
    val Background = Color(0xFFF5F5F5)
    val Surface = Color(0xFFFFFFFF)
    val Error = Color(0xFFD32F2F)
    val Success = Color(0xFF388E3C)
    val Warning = Color(0xFFFFA000)

    // Status Colors
    val VFR = Color(0xFF4CAF50)            // Green
    val MVFR = Color(0xFF2196F3)           // Blue
    val IFR = Color(0xFFFF9800)            // Orange
    val LIFR = Color(0xFFF44336)           // Red
}
```

### Typography
```kotlin
object FlightDeckTypography {
    val H1 = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    val H2 = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    )
    val Body = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    )
    val Monospace = TextStyle(
        fontFamily = FontFamily.Monospace,  // For ATIS/METAR
        fontSize = 14.sp
    )
}
```

### Component Styles

**Cards**:
- Rounded corners: 8dp
- Elevation: 2dp
- Padding: 16dp
- Background: Surface

**Buttons**:
- Primary: Filled with Primary color
- Secondary: Outlined
- Height: 48dp
- Corner radius: 4dp

**Icons**:
- Material Icons
- Aviation-themed custom icons
- Size: 24dp standard

---

## 🔄 User Flows

### Flow 1: Get ATIS and Start Checklist
```
Home → Tap "ATIS" Card
     → Select Airport (KPAO)
     → View ATIS (Info: Alpha, Alt: 30.12)
     → Tap "Start Checklist"
     → Pre-Flight Checklist opens
     → ATIS info pre-filled
     → Complete checklist items
     → Mark complete
```

### Flow 2: Practice ATC Communication
```
Home → Tap "ATC" Card
     → Select Scenario (Ground Clearance)
     → View airport/ATIS info
     → Type pilot message
     → AI responds with ATC
     → View traffic advisories
     → Continue conversation
     → Get evaluation/score
```

### Flow 3: Search Airport
```
Weather Tab → Airport Search
            → Type "San Francisco"
            → View results (KSFO, etc.)
            → Tap airport
            → View details (runways, frequencies)
            → Tap "Get ATIS"
            → View current weather
```

---

## 📊 Data Flow

```
UI (Fragment)
    ↓
ViewModel (LiveData/Flow)
    ↓
Repository (Business Logic)
    ↓
DAO/API (Data Sources)
    ↓
Database/Network
```

**Example: ATIS Viewer**
```
ATISFragment
    ↓ observes
ATISViewModel
    ↓ calls
ATISRepository
    ↓ uses
ATISDao + WeatherService + AirportRepository
    ↓ accesses
Room Database + Aviation Weather API
```

---

## 🎯 MVP Features (Phase 1)

**Must Have**:
1. ✅ ATIS Viewer (complete workflow)
2. ✅ Airport Search
3. ✅ ATC Simulator (basic chat)
4. ✅ Settings (API keys)
5. ✅ Home dashboard

**Should Have** (Phase 2):
- Interactive checklists
- Progress tracking
- Saved airports
- History

**Nice to Have** (Phase 3):
- Text-to-speech ATIS
- Voice input for ATC
- Flight planning
- Multi-aircraft support

---

## 🚀 Implementation Order

1. **Navigation & Home** (Foundation)
2. **ATIS Viewer** (Easiest, shows value)
3. **Airport Search** (Enables ATIS)
4. **Settings** (API configuration)
5. **ATC Simulator** (Core feature)
6. **Checklists** (Integration)
7. **Polish & Testing**

---

This spec provides a complete blueprint for building FlightDeck's UI. Ready to start implementation!
