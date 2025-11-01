# Feature Priority & Hiding Strategy

**Strategy**: Hide incomplete features (not delete), focus on voice + core ATC practice

---

## 🎯 Phase 1: Core MVP (Week 1-2) - SHOW THESE

### What Users See:

**Bottom Navigation (4 tabs):**
1. 🏠 **Home** - Dashboard with quick actions ✅ (Already built)
2. 🎤 **Practice** - Voice-based ATC practice (BUILD THIS NOW)
3. 🌤️ **Weather** - ATIS viewer ✅ (Already built)
4. 📊 **Logbook** - Digital logbook ✅ (Already built)

**~~ Settings tab hidden for now ~~** (will unhide in Phase 2)

### Features to SHOW:

**Home Tab:**
- ✅ Greeting and rank display
- ✅ Quick action cards (Keep: ATC Practice, Check Weather, View Logbook)
- ❌ HIDE: "Start Training" card (mission selection not ready)
- ✅ Recent activity (from logbook)
- ✅ Weekly progress stats

**Practice Tab (NEW - Voice ATC):**
- ✅ Simple flight entry (2 fields: departure, arrival)
- ✅ Push-to-Talk button
- ✅ Chat transcript
- ✅ Voice status indicator
- ✅ Basic session summary

**Weather Tab:**
- ✅ Keep as-is (already working)

**Logbook Tab:**
- ✅ Keep as-is (already working)

---

## 🔒 Phase 2: Features to HIDE (Not Delete)

### Backend Exists, UI Hidden:

**Mission Configuration:**
- Code: ✅ All data models, DAOs, repository exist
- UI: ❌ Hidden - No mission selection screen
- Access: Disabled in navigation
- **How to hide**: Don't show "Start Training" button, don't create MissionSelectionFragment
- **When to unhide**: Phase 2 (after voice works)

**Interactive Checklists:**
- Code: ✅ Backend complete (ChecklistRepository, AI integration)
- UI: ❌ Not built yet
- Access: No navigation item
- **How to hide**: Just don't create the UI yet
- **When to unhide**: Phase 3 (after mission selection)

**Flight Planning:**
- Code: ✅ Backend complete (FlightPlanRepository, weather integration)
- UI: ❌ Not built yet
- Access: No navigation item
- **How to hide**: Don't create FlightPlanFragment
- **When to unhide**: Phase 4 (advanced feature)

**Settings:**
- Code: ✅ Basic settings screen exists
- UI: ✅ Built but basic
- Access: ❌ Remove from bottom navigation for now
- **How to hide**: Comment out from navigation XML
- **When to unhide**: Phase 2 (after voice works)

**Advanced Features:**
- Proficiency detail screens
- Analytics dashboard
- Session detail views
- Export functionality

**How to hide**: Don't create navigation paths, don't show buttons

---

## 🔨 Implementation: Hiding Features

### Step 1: Simplify Bottom Navigation

**File**: `res/menu/bottom_nav_menu.xml`

**BEFORE (5 tabs):**
```xml
<menu>
    <item id="@+id/navigation_home" icon="@drawable/ic_home" title="Home"/>
    <item id="@+id/navigation_practice" icon="@drawable/ic_dashboard" title="Practice"/>
    <item id="@+id/navigation_weather" icon="@drawable/ic_notifications" title="Weather"/>
    <item id="@+id/navigation_logbook" icon="@drawable/ic_logbook" title="Logbook"/>
    <item id="@+id/navigation_settings" icon="@drawable/ic_settings" title="Settings"/>
</menu>
```

**AFTER (4 tabs - Settings hidden):**
```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/navigation_home"
        android:icon="@drawable/ic_home_black_24dp"
        android:title="@string/title_home" />
    <item
        android:id="@+id/navigation_practice"
        android:icon="@drawable/ic_dashboard_black_24dp"
        android:title="@string/title_practice" />
    <item
        android:id="@+id/navigation_weather"
        android:icon="@drawable/ic_notifications_black_24dp"
        android:title="@string/title_weather" />
    <item
        android:id="@+id/navigation_logbook"
        android:icon="@drawable/ic_logbook"
        android:title="@string/title_logbook" />
    <!-- Settings hidden for Phase 1 -->
    <!--
    <item
        android:id="@+id/navigation_settings"
        android:icon="@drawable/ic_settings"
        android:title="@string/title_settings" />
    -->
</menu>
```

### Step 2: Hide "Start Training" Card in Home

**File**: `res/layout/fragment_home.xml`

**Find the Start Training card and set visibility to GONE:**

```xml
<!-- Start Training Button -->
<com.google.android.material.card.MaterialCardView
    android:id="@+id/startTrainingCard"
    android:layout_width="0dp"
    android:layout_height="120dp"
    android:visibility="gone"
    <!-- Hidden until mission selection is ready -->
    ...
```

**OR** better yet, just remove it from the GridLayout temporarily and keep it in a comment:

```xml
<!-- Quick Actions Grid (2x2 becomes 1x3) -->
<GridLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="24dp"
    android:columnCount="2"
    android:rowCount="2">

    <!-- Start Training - Hidden for Phase 1
    <com.google.android.material.card.MaterialCardView
        android:id="@+id/startTrainingCard"
        ... all the code ...
    </com.google.android.material.card.MaterialCardView>
    -->

    <!-- ATC Practice Button - SHOW -->
    <com.google.android.material.card.MaterialCardView
        android:id="@+id/atcCard"
        ...
```

### Step 3: Update HomeFragment Click Handlers

**File**: `ui/home/HomeFragment.kt`

**Comment out the hidden card's click listener:**

```kotlin
private fun setupClickListeners() {
    // Start Training - Hidden for Phase 1
    // binding.startTrainingCard.setOnClickListener {
    //     findNavController().navigate(R.id.navigation_practice)
    // }

    // ATC Practice - Navigate to Practice tab
    binding.atcCard.setOnClickListener {
        findNavController().navigate(R.id.navigation_practice)
    }

    // Check Weather - Navigate to Weather tab
    binding.weatherCard.setOnClickListener {
        findNavController().navigate(R.id.navigation_weather)
    }

    // View Logbook - Navigate to Logbook tab
    binding.logbookCard.setOnClickListener {
        findNavController().navigate(R.id.navigation_logbook)
    }
}
```

---

## 🎤 What to BUILD Now: Voice ATC Practice

### New Simple Practice Screen

**File**: `res/layout/fragment_atc_practice.xml` (Simplified)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background">

    <!-- Flight Entry Section (Top) -->
    <com.google.android.material.card.MaterialCardView
        android:id="@+id/flightEntryCard"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"
        app:cardElevation="2dp"
        app:cardCornerRadius="8dp"
        app:layout_constraintTop_toTopOf="parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="16dp">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Practice Flight"
                android:textSize="18sp"
                android:textStyle="bold"
                android:textColor="@color/text_primary"
                android:layout_marginBottom="16dp" />

            <!-- Departure Airport -->
            <com.google.android.material.textfield.TextInputLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="Departure (e.g., KPAO)"
                style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox">

                <com.google.android.material.textfield.TextInputEditText
                    android:id="@+id/departureInput"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:inputType="textCapCharacters"
                    android:maxLength="4" />
            </com.google.android.material.textfield.TextInputLayout>

            <!-- Arrival Airport -->
            <com.google.android.material.textfield.TextInputLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:hint="Arrival (e.g., KSQL)"
                style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox">

                <com.google.android.material.textfield.TextInputEditText
                    android:id="@+id/arrivalInput"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:inputType="textCapCharacters"
                    android:maxLength="4" />
            </com.google.android.material.textfield.TextInputLayout>

            <!-- Start Practice Button -->
            <com.google.android.material.button.MaterialButton
                android:id="@+id/startPracticeButton"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="16dp"
                android:text="Start Practice"
                android:backgroundTint="@color/accent" />

        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

    <!-- Chat/Transcript Area (Middle) -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/chatRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_margin="16dp"
        app:layout_constraintTop_toBottomOf="@id/flightEntryCard"
        app:layout_constraintBottom_toTopOf="@id/controlsCard" />

    <!-- Voice Controls (Bottom) -->
    <com.google.android.material.card.MaterialCardView
        android:id="@+id/controlsCard"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"
        app:cardElevation="4dp"
        app:cardCornerRadius="8dp"
        app:layout_constraintBottom_toBottomOf="parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="16dp"
            android:gravity="center">

            <!-- Status Text -->
            <TextView
                android:id="@+id/statusText"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Press and hold to talk"
                android:textSize="14sp"
                android:textColor="@color/text_secondary"
                android:layout_marginBottom="16dp" />

            <!-- Push-to-Talk Button -->
            <com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
                android:id="@+id/pttButton"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Push to Talk"
                android:textColor="@color/text_on_accent"
                app:icon="@android:drawable/ic_btn_speak_now"
                app:iconTint="@color/text_on_accent"
                app:backgroundTint="@color/accent" />

        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

</androidx.constraintlayout.widget.ConstraintLayout>
```

---

## 📋 Phase Rollout Plan

### **Phase 1 (Weeks 1-2): Voice MVP**
- ✅ Hide: Start Training card, Settings tab
- ✅ Build: Voice input/output
- ✅ Build: Simple practice screen
- ✅ Show: Home, Practice (voice), Weather, Logbook

### **Phase 2 (Weeks 3-4): Mission Selection**
- ✅ Unhide: "Start Training" card
- ✅ Build: Mission selection UI
- ✅ Unhide: Settings tab
- ✅ Show: All 5 bottom nav tabs

### **Phase 3 (Month 2): Checklists**
- ✅ Build: Interactive checklist UI
- ✅ Add: Checklist navigation item (or in Practice tab)
- ✅ Connect: Existing checklist backend

### **Phase 4 (Month 3): Flight Planning**
- ✅ Build: Flight planning UI
- ✅ Connect: Existing flight plan backend
- ✅ Unhide: Advanced features

---

## ✅ Benefits of This Approach

1. **No code deletion** - All backend work preserved
2. **Focused development** - Ship voice fast
3. **Clean UI** - Users see only working features
4. **Easy to unhide** - Just uncomment code
5. **Professional** - No half-baked features visible

---

## 🎯 Next Immediate Steps

1. **Update bottom navigation** - Remove settings tab (comment out)
2. **Update Home layout** - Hide Start Training card (comment out)
3. **Update HomeFragment** - Comment out hidden card listeners
4. **Create voice managers** - VoiceInputManager, VoiceOutputManager
5. **Update ATC practice screen** - Add voice controls
6. **Test voice** - Get basic PTT working

**Start with voice. Everything else can wait.**
