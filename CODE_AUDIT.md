# FlightDeck Code Audit - Path A (Voice ATC Focus)

**Date**: November 2, 2025
**Purpose**: Identify code to keep/delete for focused voice ATC MVP

---

## 🎯 Audit Summary

**Total Files**: 85 Kotlin files
**Keep**: ~45 files (53%)
**Delete**: ~40 files (47%)

---

## ✅ KEEP - Core to Voice ATC MVP

### **Data Models** (6 of 11 files)

**KEEP:**
1. ✅ `Airport.kt` - Airport data for ATC scenarios
2. ✅ `ATCScenario.kt` - Core ATC practice scenarios
3. ✅ `ATIS.kt` - Weather broadcasts, part of ATC comm
4. ✅ `Logbook.kt` - Simple practice history tracking
5. ✅ `Traffic.kt` - Realistic traffic for scenarios
6. ✅ `Weather.kt` - Weather data for scenario realism

**DELETE:**
- ❌ `Aircraft.kt` - Not needed for MVP
- ❌ `ChecklistItem.kt` - Cutting interactive checklists
- ❌ `FlightPlan.kt` - Cutting flight planning tool
- ❌ `MissionConfig.kt` - Cutting 26 challenge modules
- ❌ `Performance.kt` - Not needed for MVP

---

### **DAOs** (6 of 11 files)

**KEEP:**
1. ✅ `AirportDao.kt` - Airport database access
2. ✅ `ATCDao.kt` - ATC scenarios and sessions
3. ✅ `ATISDao.kt` - Weather broadcast data
4. ✅ `LogbookDao.kt` - Practice history
5. ✅ `TrafficDao.kt` - Traffic simulation
6. ✅ `KnowledgeDao.kt` - FAA regulations (RAG system)

**DELETE:**
- ❌ `AircraftDao.kt` - Not needed
- ❌ `ChecklistDao.kt` - Cutting checklists
- ❌ `FlightPlanDao.kt` - Cutting flight planning
- ❌ `MissionConfigDao.kt` - Cutting missions
- ❌ `PerformanceDao.kt` - Not needed

---

### **Repositories** (5 of 8 files)

**KEEP:**
1. ✅ `AirportRepository.kt` - Airport data management
2. ✅ `ATCRepository.kt` - **CORE** - ATC practice logic
3. ✅ `ATISRepository.kt` - Weather broadcasts
4. ✅ `LogbookRepository.kt` - Practice history
5. ✅ `TrafficSimulator.kt` - Realistic traffic generation

**DELETE:**
- ❌ `ChecklistRepository.kt` - Cutting checklists
- ❌ `FlightPlanRepository.kt` - Cutting flight planning
- ❌ `MissionConfigRepository.kt` - Cutting missions

---

### **AI Services** (3 of 7+ files)

**KEEP (Simple, single-model approach):**
1. ✅ `AIService.kt` - Basic AI service
2. ✅ `AnthropicApiService.kt` - Claude API interface
3. ✅ `AnthropicModels.kt` - Request/response models

**DELETE (Complexity we don't need):**
- ❌ `AIOrchestrator.kt` - 4-model routing system (over-engineered)
- ❌ `AIProvider.kt` - Multi-provider abstraction
- ❌ `EnhancedAIService.kt` - If this is multi-model routing
- ❌ `EnhancedAIServiceWithRAG.kt` - Keep RAG, but simplify if possible
- ❌ `providers/ClaudeProvider.kt` - Multi-model routing
- ❌ `providers/GeminiProvider.kt` - Multi-model routing

**Decision**: Use ONE AI model (Claude Sonnet or Gemini Pro). Kill the complex routing.

---

### **Utils** (4 of 7 files)

**KEEP:**
1. ✅ `VoiceInputManager.kt` - **CRITICAL** - Voice input (FULLY IMPLEMENTED!)
2. ✅ `VoiceOutputManager.kt` - **CRITICAL** - Voice output (FULLY IMPLEMENTED!)
3. ✅ `DatabaseInitializer.kt` - Database setup
4. ✅ `OurAirportsLoader.kt` - 20K airport database loader

**DELETE:**
- ❌ `ChallengeValidator.kt` - Mission system validation
- ❌ `ProficiencyCalculator.kt` - Complex proficiency tracking
- ❌ `EmailValidator.kt` - Not needed for MVP

---

### **UI Components** (15 of 26 files)

**KEEP (Core UI for voice ATC):**

**ATC Practice (4 files) - CORE:**
1. ✅ `ui/atc/ATCFragment.kt` - Main ATC practice screen
2. ✅ `ui/atc/ATCViewModel.kt` - ATC logic
3. ✅ `ui/atc/ChatMessage.kt` - Chat message model
4. ✅ `ui/atc/ChatMessageAdapter.kt` - Display chat transcript

**Home/Dashboard (2 files):**
5. ✅ `ui/home/HomeFragment.kt` - Home screen
6. ✅ `ui/home/HomeViewModel.kt` - Home logic

**Weather/ATIS (2 files):**
7. ✅ `ui/weather/ATISFragment.kt` - ATIS viewer (part of ATC)
8. ✅ `ui/weather/ATISViewModel.kt` - ATIS logic

**Logbook (2 files) - Simple history:**
9. ✅ `ui/logbook/LogbookOverviewFragment.kt` - Practice history
10. ✅ `ui/logbook/LogbookOverviewViewModel.kt` - History logic

**Settings (1 file):**
11. ✅ `ui/settings/SettingsFragment.kt` - App settings

**Onboarding (5 files):**
12. ✅ `ui/onboarding/OnboardingActivity.kt`
13. ✅ `ui/onboarding/OnboardingAdapter.kt`
14. ✅ `ui/onboarding/OnboardingPage1Fragment.kt`
15. ✅ `ui/onboarding/OnboardingPage2Fragment.kt`
16. ✅ `ui/onboarding/OnboardingPage3Fragment.kt`

**Other:**
17. ✅ `ui/splash/SplashActivity.kt` - Splash screen

**DELETE (Out of scope for MVP):**
- ❌ `ui/flightplanning/FlightPlanningFragment.kt` - Cutting flight planning
- ❌ `ui/flightplanning/FlightPlanningViewModel.kt`
- ❌ `ui/mission/MissionSelectionFragment.kt` - Cutting mission selection
- ❌ `ui/mission/MissionSelectionViewModel.kt`
- ❌ `ui/mission/MissionAdapter.kt`
- ❌ `ui/dashboard/DashboardFragment.kt` - Check if duplicate of home
- ❌ `ui/dashboard/DashboardViewModel.kt`
- ❌ `ui/notifications/NotificationsFragment.kt` - Not needed
- ❌ `ui/notifications/NotificationsViewModel.kt`

---

### **Root Files** (2 files)

**KEEP:**
1. ✅ `FlightDeckApplication.kt` - App initialization
2. ✅ `MainActivity.kt` - Main activity

---

## 🎉 CRITICAL DISCOVERY: Voice is Already Built!

### **VoiceInputManager.kt** (155 lines)
**Status**: ✅ FULLY IMPLEMENTED

**Features:**
- Android SpeechRecognizer integration
- Flow-based API (modern Kotlin)
- Partial results support
- Audio level monitoring
- Comprehensive error handling
- Aviation-specific configurations (longer timeouts for radio calls)
- States: Listening, Speaking, Processing, Success, Error

**Quality**: Production-ready, well-architected

### **VoiceOutputManager.kt** (171 lines)
**Status**: ✅ FULLY IMPLEMENTED

**Features:**
- Android TextToSpeech integration
- **Aviation-specific pronunciation**:
  - N-numbers: "N12345" → "November One Two Three Four Five"
  - Runways: "runway 31" → "runway three one"
  - Altimeter: "30.12" → "three zero point one two"
- Progress callbacks
- Proper cleanup and lifecycle management

**Quality**: Production-ready, aviation-aware

### **Impact on Roadmap:**
🚀 **Week 1 (Voice Core) is basically DONE!**

This moves us from "30 days to MVP" to potentially **"15-20 days to MVP"** since voice infrastructure is complete.

---

## 🗑️ DELETION PLAN

### **Phase 1: Delete Entire Feature Directories**

```bash
# Flight Planning (2 files)
rm -rf app/src/main/java/com/example/flightdeck/ui/flightplanning/

# Mission Selection (3 files)
rm -rf app/src/main/java/com/example/flightdeck/ui/mission/

# Dashboard if duplicate (2 files) - AUDIT FIRST
# Check if dashboard is different from home before deleting
# rm -rf app/src/main/java/com/example/flightdeck/ui/dashboard/

# Notifications (2 files)
rm -rf app/src/main/java/com/example/flightdeck/ui/notifications/
```

### **Phase 2: Delete Individual Model Files**

```bash
cd app/src/main/java/com/example/flightdeck/data/model/
rm Aircraft.kt ChecklistItem.kt FlightPlan.kt MissionConfig.kt Performance.kt
```

### **Phase 3: Delete DAO Files**

```bash
cd app/src/main/java/com/example/flightdeck/data/local/dao/
rm AircraftDao.kt ChecklistDao.kt FlightPlanDao.kt MissionConfigDao.kt PerformanceDao.kt
```

### **Phase 4: Delete Repository Files**

```bash
cd app/src/main/java/com/example/flightdeck/data/repository/
rm ChecklistRepository.kt FlightPlanRepository.kt MissionConfigRepository.kt
```

### **Phase 5: Delete AI Complexity**

```bash
cd app/src/main/java/com/example/flightdeck/data/remote/ai/
rm AIOrchestrator.kt AIProvider.kt
rm -rf providers/  # Delete multi-model providers
# Keep: AIService.kt, AnthropicApiService.kt, AnthropicModels.kt
```

### **Phase 6: Delete Util Files**

```bash
cd app/src/main/java/com/example/flightdeck/utils/
rm ChallengeValidator.kt ProficiencyCalculator.kt EmailValidator.kt
```

---

## 🔍 Files to AUDIT Before Deleting

Need to check these files before deletion:

1. **`ui/dashboard/` vs `ui/home/`**
   - Are these duplicates or different?
   - If different, which one do we keep?

2. **`EnhancedAIService.kt` vs `EnhancedAIServiceWithRAG.kt`**
   - Is RAG system needed for FAA regulations?
   - If yes, simplify and keep basic RAG
   - If no, delete both and use simple AIService.kt

3. **`KnowledgeDao.kt`**
   - What does this store?
   - Is it for RAG/FAA regulations?
   - Might be useful for accurate ATC feedback

---

## 📊 Database Schema Impact

### **Tables to KEEP** (for voice ATC MVP)

1. ✅ `airports` - Airport data
2. ✅ `runways` - Runway information
3. ✅ `frequencies` - ATC frequencies
4. ✅ `atc_scenarios` - Practice scenarios
5. ✅ `atc_practice_sessions` - User sessions
6. ✅ `atc_exchanges` - Conversation history
7. ✅ `logbook_entries` - Simple practice history
8. ✅ `traffic_aircraft` - Traffic simulation
9. ✅ `weather_data` - Weather for scenarios
10. ✅ `atis_broadcasts` - ATIS information
11. ✅ `knowledge_base` - FAA regulations (if using RAG)

### **Tables to DELETE**

- ❌ `aircraft` - Not needed for MVP
- ❌ `checklist_items` - Cutting checklists
- ❌ `checklist_sessions` - Cutting checklists
- ❌ `checklist_completions` - Cutting checklists
- ❌ `flight_plans` - Cutting flight planning
- ❌ `waypoints` - Cutting flight planning
- ❌ `mission_configs` - Cutting missions
- ❌ `performance_reports` - Not needed
- ❌ `achievements` - Not needed for MVP
- ❌ `logbook_totals` - Simplify logbook
- ❌ `proficiency_ratings` - Cutting complex proficiency tracking

**Estimated reduction**: ~50% fewer tables (23 → ~11 tables)

---

## 🎯 What's Missing for MVP

Based on audit, here's what we still need to build:

### **Week 1: Voice Integration (MOSTLY DONE!)**
- [x] VoiceInputManager - COMPLETE
- [x] VoiceOutputManager - COMPLETE
- [ ] **Connect to ATCFragment** - Add PTT button, wire up voice managers
- [ ] **Test voice flow** - Speak → AI → Hear response

### **Week 2: ATC Practice UI**
- [ ] Simplify ATCFragment for voice focus
- [ ] Add Push-to-Talk button
- [ ] Add voice status indicators
- [ ] Simple flight entry (departure/arrival ICAO)
- [ ] Session flow (start → practice → end)

### **Week 3: Feedback & History**
- [ ] Post-session evaluation screen
- [ ] Simplify logbook UI (just list of sessions)
- [ ] Basic analytics on home screen

### **Week 4: Premium & Beta**
- [ ] Google Play Billing integration
- [ ] Premium upgrade dialog
- [ ] Beta testing preparation

---

## 🚀 Revised Timeline

**Original estimate**: 30 days to MVP
**New estimate**: **15-20 days to MVP**

**Why faster:**
- Voice infrastructure complete (saves Week 1)
- ATC repository exists (saves time)
- Airport database ready (saves time)
- Weather integration done (saves time)

**What accelerated us:**
- VoiceInputManager and VoiceOutputManager are production-ready
- Backend architecture is solid
- Just need to wire up UI and delete cruft

---

## 📋 Next Steps

1. **Audit Dashboard vs Home** - Determine if duplicate
2. **Delete Phase 1-6** - Remove ~40 files we don't need
3. **Simplify Database Schema** - Remove ~12 unused tables
4. **Update AppContainer** - Remove references to deleted repositories
5. **Test build** - Ensure app still compiles after deletions
6. **Connect voice to UI** - Wire VoiceManagers to ATCFragment

---

## 💡 Key Takeaways

### **Good News:**
✅ Voice infrastructure is DONE and looks excellent
✅ Backend architecture is solid and well-organized
✅ Airport/Weather/Traffic systems are built
✅ MVP timeline cut in half (30 days → 15-20 days)

### **Action Required:**
⚠️ Delete ~40 files that distract from voice ATC focus
⚠️ Simplify database schema (remove ~12 tables)
⚠️ Kill AI routing complexity (use ONE model)
⚠️ Wire voice managers to UI (main remaining work)

### **Philosophy:**
"We have more built than we thought, but less of what we need finished. Delete the distractions, finish the core, ship it."

---

*Last Updated: November 2, 2025*
*Next: Execute deletion plan, then wire up voice UI*
