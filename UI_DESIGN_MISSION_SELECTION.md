# FlightDeck Mission Selection UI Design
## Clean, Professional Aviation Aesthetic

---

## 🎨 Design Philosophy

### Core Principles
1. **Aviation Professional** - ForeFlight meets G1000 display
2. **Information First** - Clear hierarchy, no clutter
3. **Fast Access** - Pilots want quick mission starts
4. **Progressive Disclosure** - Simple by default, advanced when needed
5. **Trust & Safety** - Professional colors convey reliability

### Color Palette (Aviation Theme)

```
Primary Colors:
├─ Sky Blue:       #0A7AFF (accent, CTAs)
├─ Horizon Navy:   #1C2642 (headers, dark mode primary)
├─ Cloud White:    #FFFFFF (backgrounds, light mode)
└─ Steel Gray:     #6B7280 (secondary text)

Functional Colors:
├─ VFR Green:      #10B981 (success, VFR conditions)
├─ MVFR Yellow:    #F59E0B (warning, MVFR)
├─ IFR Red:        #EF4444 (danger, IFR, emergencies)
└─ Altitude Cyan:  #06B6D4 (info, accents)

Neutrals:
├─ Gray 50:        #F9FAFB (subtle backgrounds)
├─ Gray 100:       #F3F4F6 (card backgrounds)
├─ Gray 200:       #E5E7EB (borders)
├─ Gray 800:       #1F2937 (dark text)
└─ Gray 900:       #111827 (primary text)
```

### Typography
```
Headers:    Inter Bold / Roboto Bold
Body:       Inter Regular / Roboto Regular
Monospace:  Roboto Mono (for ICAO codes, frequencies)
```

### Spacing & Rhythm
- 8dp base unit
- 16dp standard padding
- 24dp section spacing
- 32dp screen margins

---

## 📱 Screen 1: Mission Selection (Primary Entry)

### Layout Structure
```
┌─────────────────────────────────────────────────┐
│ ← Back          New Mission          Settings ⚙️│
├─────────────────────────────────────────────────┤
│                                                 │
│  Route:  KPAO → KSNS                           │
│  Distance: 32 NM  |  Est. Time: 25 min         │
│                                                 │
│  ┌───────────────────────────────────────────┐ │
│  │ 🌤️ Weather: VFR                           │ │
│  │ Departure: Clear, 10SM, Wind 270@5        │ │
│  │ Arrival: Scattered 2500, 5SM              │ │
│  │ [View Full Brief →]                       │ │
│  └───────────────────────────────────────────┘ │
│                                                 │
│  Choose Your Mission:                           │
│                                                 │
│  ┌───────────────────────────────────────────┐ │
│  │ ⭐ BEGINNER                               │ │
│  │ First Solo                                │ │
│  │ Light traffic • Perfect weather           │ │
│  │ Relaxed pace                              │ │
│  │                                           │ │
│  │ Duration: ~25 min                         │ │
│  │ [Start Mission →]                         │ │
│  └───────────────────────────────────────────┘ │
│                                                 │
│  ┌───────────────────────────────────────────┐ │
│  │ ⭐⭐ INTERMEDIATE                          │ │
│  │ Cross-Country VFR                         │ │
│  │ Moderate traffic • Frequency changes      │ │
│  │ Realistic pace                            │ │
│  │                                           │ │
│  │ Duration: ~35 min                         │ │
│  │ [Start Mission →]                         │ │
│  └───────────────────────────────────────────┘ │
│                                                 │
│  [View More Presets ↓]                         │
│                                                 │
│  ─────────── or ───────────                    │
│                                                 │
│  [🎨 Custom Mission Builder]                   │
│                                                 │
└─────────────────────────────────────────────────┘
```

### Design Notes
- **Weather card** at top - pilots check weather first
- **Preset cards** are clean with clear difficulty indicators
- **Star rating** (⭐) instantly communicates difficulty
- **Descriptive text** tells user what to expect
- **Single CTA** per card - "Start Mission"
- **Custom builder** is de-emphasized but accessible

### Card Design Details
```
Mission Card:
├─ Background: White (#FFFFFF) with subtle shadow
├─ Border: 1px solid Gray 200 (#E5E7EB)
├─ Padding: 20dp
├─ Corner Radius: 12dp
├─ Hover: Subtle elevation increase
└─ Active: Sky Blue border (#0A7AFF)

CTA Button:
├─ Full width within card
├─ Height: 48dp
├─ Background: Sky Blue (#0A7AFF)
├─ Text: White, Bold, 16sp
├─ Corner Radius: 8dp
└─ Icon: → arrow for forward action
```

---

## 📱 Screen 2: Expanded Presets View

### Layout
```
┌─────────────────────────────────────────────────┐
│ ← Back          Mission Presets                 │
├─────────────────────────────────────────────────┤
│                                                 │
│  Tabs: [All] [Beginner] [Intermediate]         │
│        [Advanced] [Expert]                      │
│                                                 │
│  ╔═══════════════════════════════════════════╗ │
│  ║ BEGINNER MISSIONS                         ║ │
│  ╚═══════════════════════════════════════════╝ │
│                                                 │
│  ┌─────────────┐  ┌─────────────┐             │
│  │ ⭐          │  │ ⭐          │             │
│  │ First Solo  │  │ Pattern     │             │
│  │             │  │ Practice    │             │
│  │ Light      │  │ Moderate    │             │
│  │ traffic    │  │ traffic     │             │
│  │             │  │             │             │
│  │ 25 min     │  │ 30 min      │             │
│  │ [Start]    │  │ [Start]     │             │
│  └─────────────┘  └─────────────┘             │
│                                                 │
│  ╔═══════════════════════════════════════════╗ │
│  ║ INTERMEDIATE MISSIONS                     ║ │
│  ╚═══════════════════════════════════════════╝ │
│                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────┐│
│  │ ⭐⭐        │  │ ⭐⭐⭐      │  │ ⭐⭐⭐   ││
│  │ Cross-      │  │ Busy        │  │ Weather  ││
│  │ Country VFR │  │ Airspace    │  │ Challenge││
│  │             │  │             │  │          ││
│  │ Frequency   │  │ Traffic +   │  │ MVFR +   ││
│  │ changes     │  │ Go-arounds  │  │ Crosswind││
│  │             │  │             │  │          ││
│  │ 35 min     │  │ 40 min      │  │ 38 min   ││
│  │ [Start]    │  │ [Start]     │  │ [Start]  ││
│  └─────────────┘  └─────────────┘  └──────────┘│
│                                                 │
└─────────────────────────────────────────────────┘
```

### Design Notes
- **Tab filter** for quick access by difficulty
- **Grid layout** shows multiple missions
- **Compact cards** with essential info
- **Section headers** use subtle dividers
- **Scrollable** vertically

---

## 📱 Screen 3: Custom Mission Builder (Advanced)

### Layout - Step 1: Base Configuration
```
┌─────────────────────────────────────────────────┐
│ ← Back          Custom Mission          [Save]  │
├─────────────────────────────────────────────────┤
│                                                 │
│  Step 1 of 2: Base Configuration               │
│  ━━━━━━━━━━━━━━━━━━━━━                        │
│                                                 │
│  Mission Name                                   │
│  ┌───────────────────────────────────────────┐ │
│  │ My Custom Mission                         │ │
│  └───────────────────────────────────────────┘ │
│                                                 │
│  Base Difficulty                                │
│  ┌────┐ ┌────┐ ┌────┐ ┌────┐                  │
│  │ ⭐ │ │⭐⭐│ │⭐⭐⭐│ │⭐⭐⭐⭐│              │
│  │    │ │ ●  │ │    │ │    │  ← selected      │
│  └────┘ └────┘ └────┘ └────┘                  │
│  Beginner Interm. Advanced Expert              │
│                                                 │
│  ┌───────────────────────────────────────────┐ │
│  │ 📊 Mission Parameters                     │ │
│  ├───────────────────────────────────────────┤ │
│  │                                           │ │
│  │ Traffic Density                           │ │
│  │ ○─────●──────○ Moderate                   │ │
│  │ Light   Busy   Congested                  │ │
│  │                                           │ │
│  │ Weather Complexity                        │ │
│  │ ○────●───────○ Typical VFR                │ │
│  │ Clear  MVFR   Dynamic                     │ │
│  │                                           │ │
│  │ Communication Pace                        │ │
│  │ ○────●───────○ Moderate                   │ │
│  │ Relaxed Busy  Rapid                       │ │
│  │                                           │ │
│  └───────────────────────────────────────────┘ │
│                                                 │
│  [Continue to Challenges →]                    │
│                                                 │
└─────────────────────────────────────────────────┘
```

### Design Notes - Step 1
- **Progress indicator** shows step 1 of 2
- **Large difficulty selector** - tap to select
- **Sliders** for fine-tuning parameters
- **Current value** shown next to slider
- **Clean card** for parameters
- **Single CTA** to continue

---

### Layout - Step 2: Add Challenges (Optional)
```
┌─────────────────────────────────────────────────┐
│ ← Back          Custom Mission          [Save]  │
├─────────────────────────────────────────────────┤
│                                                 │
│  Step 2 of 2: Add Challenges (Optional)        │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━              │
│                                                 │
│  💡 Tip: Add specific scenarios to practice    │
│                                                 │
│  ┌───────────────────────────────────────────┐ │
│  │ Selected: 2 challenges                    │ │
│  │ • Go-Around Practice ⭐⭐ [✕]            │ │
│  │ • Engine Roughness ⭐⭐⭐⭐ [✕]          │ │
│  └───────────────────────────────────────────┘ │
│                                                 │
│  💬 Communication Challenges                    │
│  ┌──────────────────────────────────┐          │
│  │ Rapid Frequency Changes ⭐⭐    │  [+]      │
│  │ Quick handoffs every 2-3 min    │          │
│  └──────────────────────────────────┘          │
│  ┌──────────────────────────────────┐          │
│  │ Complex Clearances ⭐⭐⭐        │  [+]      │
│  │ Long, detailed instructions     │          │
│  └──────────────────────────────────┘          │
│                                                 │
│  ✈️ Traffic Challenges                         │
│  ┌──────────────────────────────────┐          │
│  │ ✓ Go-Around Practice ⭐⭐       │  [✓]      │
│  │   Tower sends you around        │  Selected │
│  └──────────────────────────────────┘          │
│  ┌──────────────────────────────────┐          │
│  │ Traffic Conflicts ⭐⭐          │  [+]      │
│  │ Multiple traffic advisories     │          │
│  └──────────────────────────────────┘          │
│                                                 │
│  🚨 Emergency Scenarios (Pick ONE only)        │
│  ┌──────────────────────────────────┐          │
│  │ ✓ Engine Roughness ⭐⭐⭐⭐     │  [✓]      │
│  │   Partial power loss scenario   │  Selected │
│  └──────────────────────────────────┘          │
│  ┌──────────────────────────────────┐          │
│  │ 🔒 Electrical Failure ⭐⭐⭐⭐⭐│  Locked   │
│  │   Complete 10 missions first    │          │
│  └──────────────────────────────────┘          │
│                                                 │
│  [View All Challenges (12 more) ↓]            │
│                                                 │
│  ─────────────────────────────────────────     │
│                                                 │
│  Estimated Difficulty: ⭐⭐⭐⭐                  │
│  Estimated Duration: 42 minutes                 │
│                                                 │
│  [Create Mission →]                            │
│                                                 │
└─────────────────────────────────────────────────┘
```

### Design Notes - Step 2
- **Selected challenges** shown at top - easy to remove
- **Challenges grouped by category** with emoji icons
- **Expandable sections** to reduce clutter
- **Challenge cards** show:
  - Name + difficulty stars
  - Brief description
  - Add button [+] or checkmark [✓]
- **Locked challenges** grayed out with lock icon 🔒
- **Emergency note** warns only one allowed
- **Estimated stats** update in real-time
- **Create button** prominent at bottom

---

## 🎨 Component Design Specs

### Challenge Card (Collapsed)
```
┌────────────────────────────────────────┐
│ Go-Around Practice ⭐⭐              │ [+]
│ Tower sends you around                 │
└────────────────────────────────────────┘

Specs:
- Height: 64dp
- Padding: 16dp
- Background: White
- Border: 1px Gray 200
- Border Radius: 8dp
- Font: Inter Regular 14sp
- Stars: 16sp yellow (#F59E0B)
```

### Challenge Card (Expanded - on tap)
```
┌────────────────────────────────────────┐
│ Go-Around Practice ⭐⭐              │ [+]
│ Tower sends you around                 │
├────────────────────────────────────────┤
│ 💡 What to Expect:                     │
│ • Last-minute go-around instructions   │
│ • Immediate action required            │
│ • Re-sequencing in pattern             │
│                                        │
│ ✈️ Tips:                                │
│ • React immediately to call            │
│ • Full power, positive climb           │
│ • Follow standard procedures           │
│                                        │
│ [Learn More] [Add Challenge →]         │
└────────────────────────────────────────┘

Specs:
- Animated expansion
- Additional padding: 16dp
- Info text: Gray 600, 12sp
- Bullet points with icons
- Two CTAs at bottom
```

### Difficulty Stars
```
⭐         = 1 star (Beginner)
⭐⭐       = 2 stars (Easy)
⭐⭐⭐     = 3 stars (Intermediate)
⭐⭐⭐⭐   = 4 stars (Advanced)
⭐⭐⭐⭐⭐ = 5 stars (Expert)

Color: #F59E0B (yellow)
Size: 16sp inline, 24sp for selectors
```

### Slider Component
```
Traffic Density
○─────●──────○  Moderate
│     │      │
Light Busy   Congested

Specs:
- Track: Gray 200, 4dp height
- Active Track: Sky Blue, 4dp
- Thumb: Sky Blue, 24dp circle
- Labels: Gray 600, 12sp
- Current value: Gray 900, 14sp Bold
```

---

## 📐 Interaction Patterns

### Mission Card Tap
```
User taps card →
  Brief scale animation (95% → 100%) →
    Navigate to mission brief screen →
      Show full details + Start button
```

### Challenge Selection
```
User taps [+] →
  Check if emergency (if yes, check limit) →
    If valid: Checkmark appears [✓] →
      Update selected list at top →
        Recalculate difficulty →
          Update bottom stats

    If invalid: Show toast:
      "⚠️ Only one emergency allowed"
```

### Slider Interaction
```
User drags slider →
  Update value label in real-time →
    Update difficulty calculation →
      Show updated stars
```

---

## 🚀 Animation & Transitions

### Screen Transitions
- **Slide from right** for forward navigation
- **Slide to right** for back navigation
- **Fade** for modal overlays
- **Duration**: 300ms
- **Easing**: Ease-out

### Card Interactions
- **Hover** (on tablets): Subtle elevation increase
- **Tap**: Brief scale down to 95%
- **Select**: Scale up to 102% then settle

### Challenge Add/Remove
- **Add**: Slide in from right with fade
- **Remove**: Fade out with slide left
- **Duration**: 200ms

---

## 💡 Empty States

### No Recent Missions
```
┌─────────────────────────────────────┐
│                                     │
│         ✈️                          │
│                                     │
│    No Missions Yet                  │
│    Start your first mission to      │
│    begin tracking your progress     │
│                                     │
│    [Start a Mission →]              │
│                                     │
└─────────────────────────────────────┘
```

### No Custom Missions
```
┌─────────────────────────────────────┐
│                                     │
│         🎨                          │
│                                     │
│    No Custom Missions               │
│    Create a custom mission to       │
│    practice specific scenarios      │
│                                     │
│    [Create Custom Mission →]        │
│                                     │
└─────────────────────────────────────┘
```

---

## 📱 Responsive Considerations

### Phone (< 600dp width)
- Stack preset cards vertically
- Full-width buttons
- Collapsed challenge descriptions by default

### Tablet (> 600dp width)
- 2-column grid for preset cards
- Side-by-side comparison possible
- Expanded challenge info visible without tap

---

## ♿ Accessibility

- **Minimum touch target**: 48dp × 48dp
- **Color contrast**: WCAG AA compliant
- **Text size**: Scalable with system settings
- **Screen reader**: Full content descriptions
- **Focus indicators**: Clear blue outline
- **Keyboard navigation**: Tab order logical

---

## 🎯 Key Takeaways

1. **Simple first**: Most users see presets, one tap to start
2. **Power available**: Advanced users can customize extensively
3. **Aviation aesthetic**: Professional, trustworthy, clean
4. **Information hierarchy**: Weather → Difficulty → Details → Action
5. **Progressive disclosure**: Complexity hidden until needed
6. **Fast mission start**: 2 taps maximum from route selection to flying

---

*This design balances simplicity for beginners with power for advanced users, all while maintaining a professional aviation aesthetic that builds trust and confidence.*
