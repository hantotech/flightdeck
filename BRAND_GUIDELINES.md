# FlightDeck Brand Guidelines

**Version 1.0 | Updated: October 2025**

Comprehensive design system and brand standards for FlightDeck - AI-Powered Pilot Training App

---

## 🎯 Brand Identity

### Mission
Provide student pilots and aviation enthusiasts with accessible, AI-powered training tools to practice communication, procedures, and decision-making skills.

### Brand Personality
- **Professional** - Aviation-grade accuracy and reliability
- **Approachable** - Friendly interface that reduces training anxiety
- **Educational** - Clear feedback and constructive guidance
- **Tech-Forward** - Modern AI integration without overwhelming users

### Brand Values
1. **Safety First** - Accurate information grounded in FAA regulations
2. **Accessibility** - Training available 24/7, anywhere
3. **Progress** - Celebrate improvement and achievement
4. **Community** - Support the aviation training community

---

## 🎨 Color System

### Primary Palette

#### Aviation Blue (Primary Brand Color)
```
Primary:       #1976D2  (rgb 25, 118, 210)  - Main brand color, CTAs, headers
Primary Dark:  #004BA0  (rgb 0, 75, 160)    - Status bar, hover states
Primary Light: #63A4FF  (rgb 99, 164, 255)  - Backgrounds, accents
```

**Usage**: Navigation bars, primary buttons, active states, important headers, pilot rank cards

**Psychology**: Trust, professionalism, sky/aviation association, calm focus

#### Safety Orange (Accent Color)
```
Accent:        #FF6F00  (rgb 255, 111, 0)   - Important actions, alerts
Accent Light:  #FFA040  (rgb 255, 160, 64)  - Hover states, highlights
```

**Usage**: Call-to-action buttons, important notifications, achievement badges, warnings that need attention (non-critical)

**Psychology**: Energy, attention, safety equipment association, action-oriented

### Neutral Palette

#### Backgrounds & Surfaces
```
Background:    #F5F5F5  (rgb 245, 245, 245) - App background
Surface:       #FFFFFF  (rgb 255, 255, 255) - Cards, sheets, dialogs
Surface Variant: #E0E0E0 (rgb 224, 224, 224) - Dividers, disabled states
```

#### Text Colors
```
Text Primary:     #212121  (87% opacity)  - Main content text
Text Secondary:   #757575  (60% opacity)  - Supporting text, labels
Text Hint:        #BDBDBD  (38% opacity)  - Placeholder text
Text on Primary:  #FFFFFF  (100% opacity) - Text on blue backgrounds
Text on Accent:   #FFFFFF  (100% opacity) - Text on orange backgrounds
```

### Semantic Colors

#### Status Indicators
```
Success:  #388E3C  (rgb 56, 142, 60)   - Correct answers, completed items
Error:    #D32F2F  (rgb 211, 47, 47)   - Errors, failed attempts
Warning:  #FFA000  (rgb 255, 160, 0)    - Cautions, important notices
Info:     #1976D2  (rgb 25, 118, 210)   - Informational messages
```

#### Flight Category Colors (FAA Standard)
```
VFR (Visual):           #4CAF50  (Green)  - Clear weather
MVFR (Marginal Visual): #2196F3  (Blue)   - Reduced visibility
IFR (Instrument):       #FF9800  (Orange) - Low visibility
LIFR (Low Instrument):  #F44336  (Red)    - Very low visibility
```

**Usage**: Weather displays, ATIS viewer, flight planning

### Color Usage Rules

1. **Primary Blue** should dominate (~60% of colored UI elements)
2. **Safety Orange** should be used sparingly (~10%) for emphasis
3. **Neutrals** provide breathing room (~30%)
4. **Semantic colors** only for their specific purposes
5. **Maintain 4.5:1 contrast ratio** minimum for WCAG AA compliance
6. **Never use red/green alone** for critical info (colorblind users)

### Color Combinations

#### Approved Pairings
- ✅ Aviation Blue + White (high contrast, professional)
- ✅ Aviation Blue + Safety Orange (brand identity)
- ✅ Light Gray Background + White Cards (depth, hierarchy)
- ✅ Dark Blue Header + Light Blue Background (aviation gradient)

#### Avoid
- ❌ Orange + Red (too aggressive)
- ❌ Blue + Green (reduces brand clarity)
- ❌ Low contrast combinations (accessibility)

---

## ✍️ Typography System

### Font Family

**Primary Font**: **Roboto** (Android system default)
- ✅ Excellent readability on screens
- ✅ Professional appearance
- ✅ Wide character support
- ✅ Optimized for Material Design

**Fallback**: System Default Sans-Serif

### Type Scale

#### Display Text
```
Display Large:  57sp  Bold      - Splash screen, major announcements
Display Medium: 45sp  Bold      - Rare, very important screens
Display Small:  36sp  Bold      - Welcome screens
```

#### Headline Text
```
Headline Large:  32sp  Bold      - Screen titles (rare)
Headline Medium: 28sp  Bold      - Section headers
Headline Small:  24sp  Bold      - Card headers, important titles
```

#### Title Text
```
Title Large:   22sp  Medium    - App bar titles
Title Medium:  16sp  Medium    - List item titles, card titles
Title Small:   14sp  Medium    - Compact titles, dense lists
```

#### Body Text
```
Body Large:    16sp  Regular   - Main content, readable paragraphs
Body Medium:   14sp  Regular   - Default body text
Body Small:    12sp  Regular   - Captions, metadata
```

#### Label Text
```
Label Large:   14sp  Medium    - Button text, emphasized labels
Label Medium:  12sp  Medium    - Form labels, tabs
Label Small:   11sp  Medium    - Dense UI, overlines
```

### Type Usage Guidelines

1. **Headlines**: Bold weight, sentence case
2. **Body Text**: Regular weight, 1.5x line height for readability
3. **Labels**: Medium weight, all caps for emphasis (sparingly)
4. **Links**: Primary color, no underline (unless in paragraph)
5. **Emphasis**: Medium/Bold weight, NOT all caps for long text

### Aviation-Specific Typography

- **Airport Codes**: 16sp, Bold, all caps (e.g., "KPAO")
- **Aircraft IDs**: 14sp, Medium, format "N12345"
- **Frequencies**: 14sp, Monospace, format "118.6"
- **Scores/Ratings**: 24sp, Bold for number, 14sp Medium for label

---

## 📐 Spacing & Layout System

### 8pt Grid System

All spacing uses multiples of 8dp for consistency and alignment.

```
4dp   - Minimal spacing (icons, tight layouts)
8dp   - Base unit (compact spacing)
12dp  - Slight separation
16dp  - Standard spacing (default margin/padding)
24dp  - Section spacing
32dp  - Large section spacing
48dp  - Screen margins (tablets)
64dp  - Extra large spacing
```

### Standard Dimensions

```xml
<!-- Margins -->
<dimen name="margin_tiny">4dp</dimen>
<dimen name="margin_small">8dp</dimen>
<dimen name="margin_medium">12dp</dimen>
<dimen name="margin_standard">16dp</dimen>
<dimen name="margin_large">24dp</dimen>
<dimen name="margin_xlarge">32dp</dimen>
<dimen name="margin_xxlarge">48dp</dimen>

<!-- Padding -->
<dimen name="padding_small">8dp</dimen>
<dimen name="padding_medium">12dp</dimen>
<dimen name="padding_standard">16dp</dimen>
<dimen name="padding_large">24dp</dimen>

<!-- Card/Element Sizes -->
<dimen name="card_corner_radius">8dp</dimen>
<dimen name="card_elevation">2dp</dimen>
<dimen name="button_height">48dp</dimen>
<dimen name="input_height">56dp</dimen>
<dimen name="touch_target_min">48dp</dimen>

<!-- Icon Sizes -->
<dimen name="icon_tiny">16dp</dimen>
<dimen name="icon_small">24dp</dimen>
<dimen name="icon_medium">32dp</dimen>
<dimen name="icon_large">48dp</dimen>
<dimen name="icon_xlarge">64dp</dimen>
```

### Layout Principles

1. **Content padding**: 16dp minimum from screen edges
2. **Card padding**: 16dp internal padding
3. **List item height**: 56dp minimum (touch targets)
4. **Button spacing**: 8dp between horizontal buttons
5. **Section spacing**: 24dp between distinct sections
6. **Bottom navigation height**: 56dp

---

## 🎨 Component Styles

### Cards

```
Material Design 3 Card
- Corner radius: 8dp
- Elevation: 2dp (resting), 4dp (raised), 8dp (hover)
- Background: surface (#FFFFFF)
- Padding: 16dp
- Margin: 8dp from other cards
```

**Usage**: Logbook entries, quick actions, session details, weather cards

### Buttons

#### Primary Button
```
- Background: accent (#FF6F00)
- Text: white, 14sp, Medium weight, all caps
- Height: 48dp
- Corner radius: 4dp
- Padding: 16dp horizontal, 12dp vertical
```

**Usage**: Main call-to-action (Start Training, Submit, Continue)

#### Secondary Button
```
- Background: transparent
- Stroke: 1dp, primary color
- Text: primary color, 14sp, Medium weight
- Height: 48dp
- Corner radius: 4dp
```

**Usage**: Alternative actions (Cancel, Skip, Back)

#### Text Button
```
- Background: transparent
- Text: primary color, 14sp, Medium weight
- Padding: 8dp horizontal
```

**Usage**: Tertiary actions (View All, Learn More)

### Input Fields

```
Material Design Outlined TextField
- Height: 56dp
- Border: 1dp, text_hint color
- Border (focused): 2dp, primary color
- Corner radius: 4dp
- Label: 12sp, floating
- Text: 16sp
- Padding: 16dp horizontal
```

### Bottom Navigation

```
- Height: 56dp
- Background: surface (#FFFFFF)
- Elevation: 8dp
- Icons: 24dp, primary when selected
- Labels: 12sp, primary when selected, text_secondary when not
- 5 items maximum
```

---

## 🖼️ Iconography

### Icon Style

- **Style**: Material Design Icons (filled for selected, outlined for unselected)
- **Weight**: Regular (400)
- **Optical size**: 24dp standard
- **Color**: Inherit from parent or primary/accent

### Emoji Icons for Features

Use sparingly and consistently:

```
🎯 - Training/Missions
📡 - ATC Communication
🌤️ - Weather
📊 - Logbook/Analytics
⚙️ - Settings
🎓 - Learning/Education
✈️ - Aircraft/Flight
📍 - Location/Airport
🏆 - Achievements
⭐ - Rating/Quality
🔥 - Streaks
⏱️ - Time
```

**Rules**:
- Use for quick recognition in cards/headers
- Not for navigation icons (use Material icons)
- Consistent emoji across platforms where possible

### Aviation-Specific Icons

- **Runway**: Use simple line drawing
- **Aircraft**: Side view silhouette
- **Radio Tower**: Simplified tower icon
- **Weather**: Cloud, sun, rain combinations

---

## 📱 Screen Patterns

### Standard Screen Layout

```
┌─────────────────────────────┐
│  Top App Bar (56dp)         │ ← Primary color background
├─────────────────────────────┤
│  Content Area               │
│  - 16dp padding edges       │
│  - ScrollView for long      │
│  - Cards with 8dp margins   │
│                             │
│                             │
│                             │
├─────────────────────────────┤
│  Bottom Navigation (56dp)   │ ← Surface color
└─────────────────────────────┘
```

### Card Grid (Quick Actions)

```
2x2 Grid:
- Column weight: 1:1
- Row spacing: 8dp
- Card height: 120dp
- Content centered
- Icon 36sp emoji or 48dp Material icon
- Label 14sp, bold, 2 lines max
```

---

## ✨ Motion & Animation

### Timing

```
Fast:    100-150ms  - Icon changes, color changes
Medium:  200-300ms  - Screen transitions, card reveals
Slow:    400-500ms  - Large movements, page transitions
```

### Easing

- **Standard**: Cubic bezier (0.4, 0.0, 0.2, 1) - Default
- **Deceleration**: Cubic bezier (0.0, 0.0, 0.2, 1) - Enter screen
- **Acceleration**: Cubic bezier (0.4, 0.0, 1, 1) - Exit screen

### Recommended Animations

- **Screen transitions**: Slide + fade (300ms)
- **Card appearance**: Fade in + slight scale (200ms)
- **Button press**: Ripple effect (Material default)
- **Loading**: Circular progress indicator (primary color)
- **Success/Error**: Slide in from top with icon (300ms)

---

## 🗣️ Voice & Tone

### Writing Style

- **Concise**: Short sentences, clear instructions
- **Supportive**: Encouraging without being condescending
- **Professional**: Aviation terminology when appropriate
- **Friendly**: Conversational but not casual

### Examples

#### ✅ Good
- "Good morning, Pilot! Ready for today's training?"
- "Great job! Your readback was clear and accurate."
- "3-day streak! Keep up the excellent work."
- "Review the ATIS before departure"

#### ❌ Avoid
- "Hey there! Time to fly!" (too casual)
- "You did it wrong. Try again." (too harsh)
- "OMG amazing!!! 🎉🎉🎉" (too enthusiastic)
- "Peruse meteorological data" (too formal)

### Button Labels

- Use **action verbs**: "Start Training", "View Details", "Save Entry"
- Be **specific**: Not "OK", use "Continue" or "Confirm"
- Keep **short**: 1-3 words ideal

### Error Messages

- **State the problem**: "Unable to load weather data"
- **Explain why**: "Check your internet connection"
- **Offer solution**: "Tap to retry"

---

## ♿ Accessibility

### Contrast Requirements

- **Normal text**: 4.5:1 minimum
- **Large text (18sp+)**: 3:1 minimum
- **UI components**: 3:1 minimum

### Touch Targets

- **Minimum**: 48dp x 48dp
- **Ideal**: 56dp x 56dp for important actions

### Screen Reader Support

- All interactive elements have `contentDescription`
- Image buttons describe action, not appearance
- Group related content with proper headings

### Color Independence

- Never use color alone to convey information
- Add icons, text labels, or patterns
- Example: VFR/MVFR/IFR shows color + text label + icon

---

## 📦 Asset Delivery

### Image Formats

- **Icons**: Vector (XML) preferred
- **Photos**: WebP or JPEG
- **Transparency**: PNG
- **Illustrations**: SVG → Vector XML

### Density Buckets

Provide assets for:
- `mdpi` (1x)
- `hdpi` (1.5x)
- `xhdpi` (2x)
- `xxhdpi` (3x)
- `xxxhdpi` (4x)

Or use vector drawables (preferred).

---

## 🎯 Implementation Checklist

### Phase 1: Core Branding ✅
- [x] Define color palette
- [ ] Update themes.xml to use aviation colors
- [ ] Create comprehensive dimens.xml
- [ ] Document typography scale
- [ ] Establish spacing system

### Phase 2: Components
- [ ] Create reusable button styles
- [ ] Standardize card appearances
- [ ] Define input field styles
- [ ] Create icon library
- [ ] Build component Storybook/samples

### Phase 3: Patterns
- [ ] Document screen layouts
- [ ] Create navigation patterns
- [ ] Define data display patterns
- [ ] Establish empty states
- [ ] Design error states

### Phase 4: Polish
- [ ] Add loading animations
- [ ] Implement transitions
- [ ] Audit accessibility
- [ ] Test on multiple devices
- [ ] User testing with pilots

---

## 📚 Resources

### Design Tools
- **Figma**: FlightDeck Design System (future)
- **Material Design**: https://m3.material.io/
- **Android Guidelines**: https://developer.android.com/design

### Aviation References
- **FAA Regulations**: https://www.faa.gov/
- **ICAO Standards**: Color coding, terminology
- **Pilot Operating Handbooks**: Real-world reference

### Inspiration
- **ForeFlight**: Professional aviation app design
- **Garmin Pilot**: Glass cockpit aesthetic
- **Skyvector**: Map and planning UI

---

## 🔄 Version History

- **v1.0** (Oct 2025): Initial brand guidelines established
  - Core color palette defined
  - Typography system documented
  - Spacing grid established
  - Component styles outlined

---

**Questions or suggestions?** Open an issue on GitHub or contact the design team.

**These guidelines are living documents** - update as the app evolves and user feedback is received.
