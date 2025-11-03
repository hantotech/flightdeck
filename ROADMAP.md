# FlightDeck Development Roadmap

**Goal**: Ship voice-based ATC practice MVP in 30 days

**Strategy**: Focus ruthlessly on voice ATC core, ship fast, iterate based on real user feedback

---

## 📊 Current Status (Week 0)

### ✅ What's Built
- Backend architecture (MVVM, repositories, Room database)
- AI integration (Claude + Gemini APIs working)
- Airport database (30 free airports + infrastructure for 20K premium)
- Weather integration (METAR/TAF API)
- Basic UI structure (Home, Weather, Settings screens)
- Digital logbook backend (data models, DAOs, repository)

### 🚧 What's Missing
- **Voice input/output** - Critical path item
- **ATC practice UI** - Need voice-focused interface
- **Session flow** - Start practice → talk → get feedback → end
- **Premium purchase** - Google Play Billing integration

---

## 🎯 30-Day MVP Plan

### **Week 1: Voice Core (Nov 4-10)**

**Goal**: Get voice input + output working with basic ATC scenario

#### Day 1-2: Voice Input
- [ ] Implement Android SpeechRecognizer
- [ ] Add RECORD_AUDIO permission
- [ ] Create VoiceInputManager service
- [ ] Test: App can hear and transcribe pilot speech
- [ ] Handle aviation terminology (manual corrections if needed)

#### Day 3-4: Voice Output
- [ ] Implement Android TextToSpeech
- [ ] Create VoiceOutputManager service
- [ ] Configure voice parameters (pitch, speed for controller voice)
- [ ] Test: App can speak ATC responses clearly

#### Day 5: Integration
- [ ] Connect voice input → AI service → voice output
- [ ] Test simple flow: Speak → AI responds → Hear response
- [ ] Fix critical bugs

#### Weekend: Testing
- [ ] Test with real ATC phrases
- [ ] Measure voice recognition accuracy
- [ ] Iterate on TTS voice parameters

**Deliverable**: Working voice conversation with AI ATC (even if UI is ugly)

---

### **Week 2: ATC Practice UI (Nov 11-17)**

**Goal**: Build minimal but functional practice interface

#### Day 6-7: Practice Screen UI
- [ ] Create ATCPracticeFragment
- [ ] Add flight entry form (departure ICAO, arrival ICAO)
- [ ] Add big Push-to-Talk button (aviation-styled)
- [ ] Add chat transcript (shows your message + ATC response)
- [ ] Add voice status indicator (listening / processing / speaking)

#### Day 8-9: Session Flow
- [ ] Implement "Start Practice" button
- [ ] Load airport data for selected airports
- [ ] Fetch weather for scenario realism
- [ ] Generate initial ATC prompt ("Palo Alto Ground listening...")
- [ ] Implement PTT button (hold to talk, release to process)
- [ ] Show AI response in transcript + speak it
- [ ] Add "End Session" button

#### Day 10: Scenario System
- [ ] Create 3 basic scenarios:
  - Ground clearance
  - Takeoff clearance
  - Landing clearance
- [ ] Let user select scenario before starting
- [ ] AI uses scenario context for responses

#### Weekend: Polish
- [ ] Improve UI spacing and colors
- [ ] Add loading states
- [ ] Add error handling (no internet, API failure)
- [ ] Test end-to-end flow 10+ times

**Deliverable**: Can practice a complete ground-to-takeoff scenario with voice

---

### **Week 3: Feedback & History (Nov 18-24)**

**Goal**: Give users feedback and track their practice

#### Day 11-12: Session Evaluation
- [ ] After session ends, show feedback screen
- [ ] Display:
  - Overall score (0-100)
  - What you did well
  - What to improve
  - Specific phraseology suggestions
- [ ] Store session in database (ATCPracticeSession table)

#### Day 13-14: Practice History
- [ ] Create PracticeHistoryFragment
- [ ] Show list of past sessions with:
  - Date/time
  - Airports practiced
  - Scenario type
  - Score
- [ ] Tap session to see full transcript
- [ ] Add to bottom navigation (4 tabs: Home, Practice, History, Settings)

#### Day 15: Simple Analytics
- [ ] Show basic stats on Home screen:
  - Total practice time
  - Total sessions
  - Average score
  - Sessions this week
- [ ] Add "Continue Practicing" quick action

#### Weekend: Testing
- [ ] Complete 5-10 practice sessions yourself
- [ ] Test history and analytics display correctly
- [ ] Fix any bugs found

**Deliverable**: Working practice system with feedback and history

---

### **Week 4: Premium & Beta (Nov 25-Dec 1)**

**Goal**: Add premium upgrade, prepare for beta testing

#### Day 16-17: Premium Purchase Flow
- [ ] Integrate Google Play Billing Library
- [ ] Create PremiumUpgradeDialog
- [ ] Show dialog when:
  - User hits 50 session limit (free tier)
  - User tries to select airport outside free 30
- [ ] Implement purchase flow
- [ ] Store premium status in DataStore
- [ ] Unlock features after purchase

#### Day 18: Premium Features
- [ ] Gate advanced scenarios (approach, departure, emergency)
- [ ] Gate non-free airports
- [ ] Add "export history" button (premium only)
- [ ] Export to CSV format

#### Day 19: Final Polish
- [ ] Update Home screen with better UI
- [ ] Add onboarding tutorial (3 screens explaining how to use)
- [ ] Improve Settings screen (API keys, premium status, about)
- [ ] Final bug fixing pass

#### Day 20: Beta Preparation
- [ ] Create beta testing plan
- [ ] Write beta tester instructions
- [ ] Set up Google Play Console beta track
- [ ] Create feedback form

#### Weekend: Beta Launch
- [ ] Upload beta to Google Play Console
- [ ] Recruit 20 student pilots for testing (local flight school, /r/flying, pilot forums)
- [ ] Send beta invites
- [ ] Monitor feedback closely

**Deliverable**: Beta version live with 20+ testers

---

## 📅 Post-MVP (Week 5+)

### Based on Beta Feedback

**Week 5: Iterate on Feedback**
- Fix critical bugs reported by beta testers
- Improve voice recognition based on real usage
- Add most-requested features (if small)
- Improve AI prompts for more realistic ATC

**Week 6: Launch Preparation**
- Create marketing materials (screenshots, video demo)
- Write compelling Play Store description
- Set up support email
- Create simple landing page
- Prepare for public launch

**Week 7: Public Launch**
- Submit to Google Play Store (public release)
- Post to:
  - /r/flying
  - /r/aviation
  - Student Pilot forums
  - AOPA, EAA forums
- Reach out to flight schools
- Monitor reviews and respond

---

## 🎯 Feature Backlog (Post-Launch)

### Version 1.1 (Month 2)
- [ ] Improve voice recognition accuracy
- [ ] Add more scenario types (VFR flight following, Class B transitions)
- [ ] Multi-aircraft traffic (hear other planes on frequency)
- [ ] Session replay with audio

### Version 1.2 (Month 3)
- [ ] CFI dashboard (track student progress)
- [ ] Flight school partnerships
- [ ] Custom scenarios (CFI can create specific practice)
- [ ] Offline mode (pre-downloaded scenarios)

### Version 2.0 (Month 6)
- [ ] Instrument rating scenarios (IFR clearances, approaches)
- [ ] Real-time suggestions during practice (not just after)
- [ ] Voice profiles (different controller personalities)
- [ ] iOS version (if Android proves successful)

---

## 🚫 What We're NOT Building

**Out of Scope for MVP:**
- ❌ Interactive checklists UI
- ❌ Flight planning tools
- ❌ Weight & balance calculators
- ❌ Charts or map displays
- ❌ Performance calculations
- ❌ Multiple AI model routing (just use Claude Sonnet or Gemini Pro)
- ❌ Complex mission configuration system
- ❌ 26 challenge modules
- ❌ Proficiency tracking algorithms (keep it simple - just session scores)

**Why**: These dilute focus from the core value prop (voice ATC practice)

---

## 🎯 Success Metrics

### Week 1 Success
- ✅ Can speak to app and get voice response
- ✅ Voice recognition accuracy >70%
- ✅ No crashes during voice interaction

### Week 2 Success
- ✅ Can complete KPAO ground clearance scenario end-to-end
- ✅ AI responses are realistic and helpful
- ✅ PTT interface feels natural

### Week 3 Success
- ✅ Session feedback is useful
- ✅ Practice history displays correctly
- ✅ I can see my improvement over multiple sessions

### Week 4 Success
- ✅ Premium purchase flow works
- ✅ 20 beta testers signed up
- ✅ Average rating from beta testers: 4.0+
- ✅ At least 10 beta testers complete 5+ sessions

### Week 5-6 Success
- ✅ Critical bugs fixed
- ✅ App approved for Google Play Store
- ✅ First 10 paying users

---

## 🔄 Daily Workflow

**Every day:**
1. Pick 1-2 tasks from roadmap
2. Build, test, commit
3. Update this roadmap with ✅ when complete
4. If blocked, pivot to unblocked task

**Every Friday:**
- Review week's progress
- Test everything built this week
- Update roadmap for next week
- Share progress update

**Don't:**
- Don't add "cool features" not on this roadmap
- Don't optimize prematurely
- Don't perfect the UI before functionality works
- Don't build features "because they're easy" - only build if on roadmap

---

## 🎉 Definition of MVP Done

MVP is complete when:

1. ✅ User can speak radio calls and hear AI ATC responses
2. ✅ User can practice ground clearance, takeoff, and landing
3. ✅ User receives feedback after each session
4. ✅ User can view practice history
5. ✅ User can upgrade to premium ($19.99)
6. ✅ App doesn't crash during normal use
7. ✅ 20 beta testers have tested it
8. ✅ Average beta rating is 4.0+

**Then ship it.** Don't wait for perfection.

---

## 💡 Philosophy

**"Ship fast, iterate based on real users, not assumptions."**

- MVP in 30 days means ruthless prioritization
- Voice ATC is the ONLY thing that matters for MVP
- Everything else is nice-to-have
- Better to ship something working in 30 days than something perfect in 6 months
- Beta testers will tell us what actually matters

**Focus. Ship. Learn. Iterate.**

---

*Last Updated: November 2, 2025*
*Next Review: Weekly on Fridays*
