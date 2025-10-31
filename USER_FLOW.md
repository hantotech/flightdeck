# FlightDeck User Flow & App Structure

**Complete navigation map from first launch to all major features**

---

## 📱 App Structure Overview

```
FlightDeck App Launch
│
├─ FIRST TIME USERS
│  ├─ 1. Splash Screen (2 sec)
│  ├─ 2. Onboarding (3 screens)
│  ├─ 3. Account Setup (optional)
│  └─ 4. Main App
│
└─ RETURNING USERS
   ├─ 1. Splash Screen (2 sec)
   └─ 2. Main App (skip onboarding)
```

---

## 🚀 First Launch Experience

### **Screen 1: Splash Screen**
**Duration**: 2 seconds
**Purpose**: Branding, initialization

```
┌─────────────────────────────┐
│                             │
│                             │
│          ✈️                 │
│      FlightDeck             │
│                             │
│   AI-Powered Pilot          │
│      Training               │
│                             │
│      Loading...             │
│                             │
└─────────────────────────────┘
```

**What Happens**:
- Initialize database
- Check if first launch
- Load user preferences
- If first launch → Onboarding
- If returning → Home

---

### **Screen 2-4: Onboarding Carousel**
**Purpose**: Introduce key features

#### **Onboarding Page 1: Welcome**
```
┌─────────────────────────────┐
│         [Skip]              │
│                             │
│      ✈️  FlightDeck         │
│                             │
│  Your Personal Flight       │
│    Training Assistant       │
│                             │
│   • Practice ATC            │
│   • Track Progress          │
│   • Train Anywhere          │
│                             │
│                             │
│      ● ○ ○                  │
│         [Next →]            │
└─────────────────────────────┘
```

#### **Onboarding Page 2: Features**
```
┌─────────────────────────────┐
│         [Skip]              │
│                             │
│        📡                   │
│                             │
│  Practice ATC Communication │
│                             │
│  • Realistic scenarios      │
│  • AI-powered controllers   │
│  • Instant feedback         │
│  • Track proficiency        │
│                             │
│      ○ ● ○                  │
│    [← Back]  [Next →]       │
└─────────────────────────────┘
```

#### **Onboarding Page 3: Get Started**
```
┌─────────────────────────────┐
│                             │
│        🎓                   │
│                             │
│   Track Your Progress       │
│                             │
│  • Digital logbook          │
│  • 10 skill categories      │
│  • Earn pilot ranks         │
│  • Share achievements       │
│                             │
│      ○ ○ ●                  │
│    [← Back]  [Get Started]  │
└─────────────────────────────┘
```

**Tapping "Get Started"** → Account Setup or Home

---

### **Screen 5: Account Setup (Optional)**
**Purpose**: Create profile or continue as guest

```
┌─────────────────────────────┐
│   ← Welcome to FlightDeck   │
├─────────────────────────────┤
│                             │
│  Set up your pilot profile  │
│  to sync progress across    │
│  devices                    │
│                             │
│  Call Sign (Optional)       │
│  ┌─────────────────────────┐│
│  │ N12345                  ││
│  └─────────────────────────┘│
│                             │
│  Email (Optional)           │
│  ┌─────────────────────────┐│
│  │ pilot@example.com       ││
│  └─────────────────────────┘│
│                             │
│  [Continue as Guest]        │
│                             │
│  [Create Account]           │
│                             │
└─────────────────────────────┘
```

**Options**:
- **Continue as Guest** → Home (local data only)
- **Create Account** → Home (with cloud sync)

---

## 🏠 Main App Structure

### **Bottom Navigation (5 Tabs)**

```
┌─────────────────────────────┐
│         Main Content        │
│         (Fragment)          │
│                             │
├─────────────────────────────┤
│  🏠    🎯    🌤️   📊   ⚙️   │
│ Home Practice Weather Log  Set│
└─────────────────────────────┘
```

**Tabs**:
1. **🏠 Home** - Dashboard & Quick Actions
2. **🎯 Practice** - Training Missions & ATC
3. **🌤️ Weather** - ATIS & Airport Info
4. **📊 Logbook** - Progress & Proficiency
5. **⚙️ Settings** - Configuration

---

## 🏠 Tab 1: Home Dashboard

### **Home Screen (Logged In)**
```
┌─────────────────────────────┐
│  FlightDeck        [Profile]│
├─────────────────────────────┤
│  ☀️ Good afternoon, Pilot!  │
│  Ready for today's training?│
│                             │
│  🎓 Your Rank: Private Pilot│
│  ⭐⭐⭐⭐ (83% avg)          │
│                             │
├─────────────────────────────┤
│  Quick Actions              │
│  ┌──────────┐ ┌──────────┐ │
│  │    🎯    │ │    📡    │ │
│  │  Start   │ │   ATC    │ │
│  │ Training │ │ Practice │ │
│  └──────────┘ └──────────┘ │
│  ┌──────────┐ ┌──────────┐ │
│  │    🌤️    │ │    📊    │ │
│  │  Check   │ │   View   │ │
│  │ Weather  │ │ Logbook  │ │
│  └──────────┘ └──────────┘ │
├─────────────────────────────┤
│  Recent Activity            │
│  ┌─────────────────────────┐│
│  │ 📍 KPAO → KSQL          ││
│  │ Cross-country (92%)     ││
│  │ 2 days ago              ││
│  └─────────────────────────┘│
│  ┌─────────────────────────┐│
│  │ 📡 Pattern Work - KPAO  ││
│  │ Beginner (88%)          ││
│  │ 5 days ago              ││
│  └─────────────────────────┘│
│                             │
│  [View All Sessions]        │
├─────────────────────────────┤
│  This Week's Progress       │
│  🔥 3-day streak!           │
│  ⏱️ 2h 15m flight time      │
│  ✅ 4 sessions completed    │
│  📈 +8% proficiency         │
└─────────────────────────────┘
```

**Quick Actions Lead To**:
- 🎯 **Start Training** → Mission Selection
- 📡 **ATC Practice** → ATC Simulator
- 🌤️ **Check Weather** → ATIS Viewer
- 📊 **View Logbook** → Logbook Tab

---

## 🎯 Tab 2: Practice

### **Practice Hub**
```
┌─────────────────────────────┐
│  ← Practice                 │
├─────────────────────────────┤
│  Choose Your Training       │
│                             │
│  ┌─────────────────────────┐│
│  │  🎯 Training Missions   ││
│  │  Build custom scenarios ││
│  │  with challenges        ││
│  │          [Start →]      ││
│  └─────────────────────────┘│
│                             │
│  ┌─────────────────────────┐│
│  │  📡 ATC Simulator       ││
│  │  Practice radio comms   ││
│  │  with AI controller     ││
│  │          [Start →]      ││
│  └─────────────────────────┘│
│                             │
│  ┌─────────────────────────┐│
│  │  ✅ Interactive Checklist││
│  │  Pre-flight procedures  ││
│  │  with AI guidance       ││
│  │    [Coming Soon]        ││
│  └─────────────────────────┘│
│                             │
│  ┌─────────────────────────┐│
│  │  🗺️ Flight Planning     ││
│  │  Plan routes with       ││
│  │  real-time weather      ││
│  │    [Coming Soon]        ││
│  └─────────────────────────┘│
└─────────────────────────────┘
```

---

### **Practice → Mission Selection**
```
┌─────────────────────────────┐
│  ← Training Missions        │
├─────────────────────────────┤
│  [Presets]  [Custom]        │
├─────────────────────────────┤
│  Preset Missions            │
│                             │
│  ┌─────────────────────────┐│
│  │ 🎓 First Solo           ││
│  │ Pattern practice        ││
│  │ Difficulty: ⭐          ││
│  │         [Start Mission] ││
│  └─────────────────────────┘│
│                             │
│  ┌─────────────────────────┐│
│  │ 🌤️ Weather Challenge    ││
│  │ Cross-country with fog  ││
│  │ Difficulty: ⭐⭐⭐       ││
│  │         [Start Mission] ││
│  └─────────────────────────┘│
│                             │
│  ┌─────────────────────────┐│
│  │ 🚨 Emergency Procedures ││
│  │ Engine failure handling ││
│  │ Difficulty: ⭐⭐⭐⭐     ││
│  │         [Start Mission] ││
│  └─────────────────────────┘│
│                             │
│  [View All 10 Presets]      │
└─────────────────────────────┘
```

---

### **Practice → ATC Simulator**
```
┌─────────────────────────────┐
│  ← ATC Communication        │
├─────────────────────────────┤
│  📍 KPAO - Palo Alto        │
│  🌤️ Info: Alpha | VFR       │
│  🛫 Rwy 31 in use           │
├─────────────────────────────┤
│  Conversation History       │
│                             │
│  👨‍✈️ You:                    │
│  "Palo Alto Ground,        │
│   Skyhawk 12345, ready     │
│   to taxi with Alpha"      │
│                             │
│  📡 ATC:                    │
│  "Skyhawk 12345, Palo      │
│   Alto Ground, taxi to     │
│   runway 31 via Alpha"     │
│                             │
│  👨‍✈️ You:                    │
│  "Taxi runway 31 via       │
│   Alpha, Skyhawk 345"      │
│                             │
│  ✅ Excellent readback!     │
│                             │
├─────────────────────────────┤
│  ℹ️ Current Traffic:         │
│  • Citation on 3-mile final │
├─────────────────────────────┤
│  💬 [Type message...]  [▶] │
│  🎤 [Voice input future]    │
└─────────────────────────────┘
```

**After Session**:
```
┌─────────────────────────────┐
│  Session Complete! 🎉       │
├─────────────────────────────┤
│  Overall Score: 88%         │
│  ⭐⭐⭐⭐                     │
│                             │
│  Communication: 92%  ✅     │
│  Phraseology: 85%    ✅     │
│  Readback: 88%       ✅     │
│                             │
│  Feedback:                  │
│  Great job! Your readbacks  │
│  were clear and accurate.   │
│  Work on including all      │
│  required elements in your  │
│  initial call-up.           │
│                             │
│  [Save to Logbook]          │
│  [Practice Again]           │
│  [Share Score]              │
└─────────────────────────────┘
```

---

## 🌤️ Tab 3: Weather

### **Weather Hub**
```
┌─────────────────────────────┐
│  Weather & Airports         │
├─────────────────────────────┤
│  Quick Airport Access       │
│  ┌─────────────────────────┐│
│  │ 📍 KPAO - Palo Alto     ││
│  │ VFR • Alt: 30.12        ││
│  │ Wind: 090° at 8kt       ││
│  │         [View ATIS →]   ││
│  └─────────────────────────┘│
│                             │
│  ┌─────────────────────────┐│
│  │ 📍 KSFO - San Francisco ││
│  │ VFR • Alt: 30.14        ││
│  │ Wind: 280° at 12kt G20  ││
│  │         [View ATIS →]   ││
│  └─────────────────────────┘│
│                             │
│  [+ Add Favorite Airport]   │
│                             │
├─────────────────────────────┤
│  Airport Search             │
│  ┌─────────────────────────┐│
│  │ Search: [KSJC    ] 🔍  ││
│  └─────────────────────────┘│
│                             │
│  [📍 Nearby Airports]        │
│  [🗺️ Browse All]            │
└─────────────────────────────┘
```

---

### **Weather → ATIS Viewer**
```
┌─────────────────────────────┐
│  ← ATIS / AWOS              │
├─────────────────────────────┤
│  KPAO - Palo Alto Airport   │
│  Updated: 2 min ago 🔄      │
│                             │
│  ┌─────────────────────────┐│
│  │ Information Alpha       ││
│  ├─────────────────────────┤│
│  │ Time: 1856 Zulu         ││
│  │ Wind: 090° at 8 knots   ││
│  │ Visibility: 10 statute  ││
│  │ Sky: Broken 25,000      ││
│  │ Temperature: 23°C       ││
│  │ Dew Point: 14°C         ││
│  │ Altimeter: 30.12        ││
│  │ Runway: 31 in use       ││
│  │                         ││
│  │ Advise on contact you   ││
│  │ have information Alpha  ││
│  └─────────────────────────┘│
│                             │
│  Flight Category: 🟢 VFR    │
│                             │
│  [🔊 Listen to ATIS]        │
│  [📋 Copy Broadcast]        │
│  [✅ Start Pre-Flight]      │
└─────────────────────────────┘
```

---

## 📊 Tab 4: Logbook

### **Logbook Overview** (Current Implementation)
```
┌─────────────────────────────┐
│  Digital Logbook   [Export] │
├─────────────────────────────┤
│  🎓 Your Rank               │
│  Private Pilot              │
│  ⭐⭐⭐⭐ (83%)              │
│                             │
│  Quick Stats                │
│  ┌───────┐ ┌───────┐       │
│  │ 6:45  │ │   8   │       │
│  │ Hours │ │Session│       │
│  └───────┘ └───────┘       │
│  ┌───────┐ ┌───────┐       │
│  │   4   │ │ 🔥 3  │       │
│  │Airport│ │ Streak│       │
│  └───────┘ └───────┘       │
│                             │
│  Proficiency Overview       │
│  📡 Radio: 87% ████▓░      │
│  🧭 Nav: 85%   ████▓░      │
│  🌤️ Weather: 86% ████▓░    │
│  [View All 10 Skills]       │
│                             │
│  Recent Sessions            │
│  ┌─────────────────────────┐│
│  │ KPAO → KSFO             ││
│  │ Complex XC • 92% ⭐⭐⭐⭐││
│  │ 2 days ago              ││
│  └─────────────────────────┘│
│  [View All Entries]         │
└─────────────────────────────┘
```

---

## ⚙️ Tab 5: Settings

### **Settings Menu**
```
┌─────────────────────────────┐
│  ← Settings                 │
├─────────────────────────────┤
│  Profile                    │
│  ┌─────────────────────────┐│
│  │ 👤 Call Sign: N12345    ││
│  │ 🎓 Rank: Private Pilot  ││
│  │ ⭐ Avg Score: 83%       ││
│  │         [Edit Profile]  ││
│  └─────────────────────────┘│
│                             │
│  AI Configuration           │
│  ┌─────────────────────────┐│
│  │ 🤖 Claude API           ││
│  │ Status: ✅ Connected    ││
│  │         [Manage Keys]   ││
│  └─────────────────────────┘│
│                             │
│  Preferences                │
│  ☑ Notifications enabled    │
│  ☑ Auto-refresh weather     │
│  ☐ Voice feedback (TTS)     │
│  ☐ Dark mode               │
│                             │
│  Data & Storage             │
│  [📤 Export Logbook]        │
│  [🔄 Sync Settings]         │
│  [🗑️ Clear Cache]           │
│                             │
│  About                      │
│  [ℹ️ Help & Tutorial]       │
│  [📖 User Manual]           │
│  [⚖️ Privacy Policy]         │
│  [📧 Contact Support]       │
│                             │
│  Version 1.0.0 (Beta)       │
│  [Sign Out]                 │
└─────────────────────────────┘
```

---

## 🔄 Complete User Journeys

### **Journey 1: New User First Training Session**

1. **Launch App** → Splash Screen
2. **Onboarding** (3 screens) → Learn about features
3. **Account Setup** → Choose "Continue as Guest"
4. **Home Screen** → See welcome message
5. **Tap "Start Training"** → Mission Selection
6. **Select "First Solo"** → Mission details
7. **Tap "Start Mission"** → ATC Simulator opens
8. **Complete ATC Practice** → Get scored (88%)
9. **Tap "Save to Logbook"** → Entry saved
10. **Navigate to Logbook Tab** → See first entry!

**Duration**: ~10 minutes including 5 min practice

---

### **Journey 2: Check Weather Before Flight**

1. **Open App** → Home Screen
2. **Tap Weather Tab** → Weather Hub
3. **Search "KPAO"** → Airport results
4. **Tap KPAO** → ATIS details
5. **Review weather** → Info Alpha, VFR
6. **Tap "Copy Broadcast"** → Copied to clipboard
7. **Optionally**: Tap "Start Pre-Flight" → Checklist

**Duration**: ~2 minutes

---

### **Journey 3: Track Progress Over Time**

1. **Complete multiple training sessions** (over days/weeks)
2. **Navigate to Logbook Tab** → Overview
3. **See rank progression** → Student → Private Pilot
4. **Tap "View All Skills"** → Proficiency detail
5. **See improvement trends** → 📈 All improving
6. **Tap individual session** → Session detail
7. **Review AI feedback** → Areas to improve
8. **Return to Home** → See streak & stats

**Duration**: Ongoing

---

## 🎨 Navigation Patterns

### **Primary Navigation**: Bottom Tabs
- Always visible
- Current tab highlighted
- Badge notifications on tabs

### **Secondary Navigation**:
- **Top App Bar** with back button
- **FAB** (Floating Action Button) for primary actions
- **Drawer** (future) for additional options

### **Deep Linking**:
```
flightdeck://atis?airport=KPAO
flightdeck://mission/start?preset=first-solo
flightdeck://logbook/session/123
```

---

## 🔐 Authentication Options

### **Option 1: Guest Mode** (Current)
- Local data only
- No cloud sync
- No account required
- Data lost if app uninstalled

### **Option 2: Email/Password** (Future)
- Cloud sync
- Multi-device support
- Data backup
- Social features

### **Option 3: Social Login** (Future)
- Google Sign-In
- Apple Sign-In
- Quick onboarding

---

## 📊 Information Architecture

```
FlightDeck
│
├─ Onboarding
│  ├─ Welcome
│  ├─ Features
│  └─ Get Started
│
├─ Account (Optional)
│  ├─ Guest Mode
│  └─ Create Account
│
├─ Main App (Bottom Nav)
│  │
│  ├─ 🏠 Home
│  │  ├─ Quick Actions
│  │  ├─ Recent Activity
│  │  └─ Progress Summary
│  │
│  ├─ 🎯 Practice
│  │  ├─ Training Missions
│  │  │  ├─ Preset Missions (10)
│  │  │  └─ Custom Mission Builder
│  │  │
│  │  ├─ ATC Simulator
│  │  │  ├─ Scenario Selection
│  │  │  ├─ Chat Interface
│  │  │  └─ Session Results
│  │  │
│  │  ├─ Interactive Checklists
│  │  └─ Flight Planning
│  │
│  ├─ 🌤️ Weather
│  │  ├─ Favorite Airports
│  │  ├─ Airport Search
│  │  └─ ATIS Viewer
│  │
│  ├─ 📊 Logbook
│  │  ├─ Overview
│  │  │  ├─ Rank & Stats
│  │  │  ├─ Proficiency Overview
│  │  │  └─ Recent Entries
│  │  │
│  │  ├─ All Entries
│  │  ├─ Session Detail
│  │  ├─ Proficiency Detail
│  │  └─ Analytics Dashboard
│  │
│  └─ ⚙️ Settings
│     ├─ Profile
│     ├─ AI Configuration
│     ├─ Preferences
│     ├─ Data & Storage
│     └─ About
│
└─ Dialogs & Overlays
   ├─ Session Complete
   ├─ Mission Selection
   ├─ API Key Setup
   └─ Export Options
```

---

## 🎯 Implementation Priority

### **Phase 1: Core Structure** (This Week)
1. ✅ Remove/simplify current navigation
2. ✅ Create proper Home screen
3. ✅ Implement bottom navigation correctly
4. ✅ Add splash screen
5. ✅ Basic onboarding (optional)

### **Phase 2: Key Features** (Week 2)
1. Refine Practice hub
2. Complete ATIS viewer
3. Enhance Logbook display
4. Polish Settings

### **Phase 3: Enhancement** (Week 3-4)
1. Add onboarding carousel
2. Implement account setup
3. Add deep linking
4. Polish transitions

---

## 📝 Current vs Ideal State

### **Current State**
```
App Launch
  → MainActivity
    → Bottom Nav with 5 tabs
      → Logbook shows sample data
      → Other tabs incomplete
```

### **Ideal State**
```
App Launch
  → Splash Screen
    → Onboarding (first time)
      → Account Setup (optional)
        → Home Dashboard
          → Quick Actions
            → Feature screens
              → Back to Home
```

---

## 🚀 Next Steps

**Immediate Actions**:
1. **Restructure Home screen** to match design
2. **Add splash screen** with branding
3. **Create onboarding** (simple 1-screen version first)
4. **Refine bottom navigation** icons and labels
5. **Test complete user flow** end-to-end

**Files to Create**:
- `ui/splash/SplashActivity.kt`
- `ui/onboarding/OnboardingActivity.kt`
- `ui/home/HomeFragment.kt` (redesign current)
- `res/layout/activity_splash.xml`
- `res/layout/activity_onboarding.xml`

---

This flow provides a complete, professional app experience that matches modern mobile app standards! 🎯
