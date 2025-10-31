#!/bin/bash
# FlightDeck Automated Demo Script
# This script automates UI interactions for a canned demo

# Set ADB path (adjust if needed)
ADB="$HOME/AppData/Local/Android/Sdk/platform-tools/adb.exe"

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  FlightDeck - Automated Demo${NC}"
echo -e "${BLUE}========================================${NC}\n"

# Function to pause and show message
pause() {
    echo -e "\n${YELLOW}$1${NC}"
    read -p "Press Enter to continue..."
}

# Function to take screenshot
screenshot() {
    local name=$1
    echo -e "${GREEN}📸 Taking screenshot: $name${NC}"
    "$ADB" exec-out screencap -p > "demo/screenshots/${name}.png"
}

# Check if device is connected
echo -e "${GREEN}Checking for connected device...${NC}"
"$ADB" devices | grep -q "device$"
if [ $? -ne 0 ]; then
    echo -e "${YELLOW}Error: No device connected. Please start emulator.${NC}"
    exit 1
fi

# Create screenshots directory
mkdir -p demo/screenshots

echo -e "${GREEN}✓ Device connected${NC}\n"

# ============================================
# DEMO START
# ============================================

pause "🎬 DEMO STEP 1: Launch FlightDeck"
"$ADB" shell am start -n com.example.flightdeck/.MainActivity
sleep 3
screenshot "01_home_dashboard"

pause "📱 DEMO STEP 2: View Home Dashboard\n\n- Welcome message\n- Quick action cards\n- Database stats (5 airports, 5 scenarios)"

pause "🌤️ DEMO STEP 3: Navigate to ATIS Weather Viewer"
# Tap Weather tab (bottom navigation - 3rd item)
"$ADB" shell input tap 960 2720
sleep 2
screenshot "02_atis_viewer"

pause "📡 DEMO STEP 4: Search for KPAO weather\n\n- Enter ICAO code 'KPAO'\n- Show current ATIS information"
# Tap search input field (approximate coordinates)
"$ADB" shell input tap 640 500
sleep 1
# Type KPAO
"$ADB" shell input text "KPAO"
sleep 1
# Tap Get ATIS button
"$ADB" shell input tap 640 650
sleep 3
screenshot "03_atis_kpao"

pause "📻 DEMO STEP 5: Navigate to ATC Simulator"
# Tap Practice tab (bottom navigation - 2nd item)
"$ADB" shell input tap 426 2720
sleep 2
screenshot "04_atc_simulator"

pause "🎯 DEMO STEP 6: View Current Scenario\n\n- Ground Clearance - VFR\n- Scenario details\n- Ready to practice"

pause "✈️ DEMO STEP 7: Send pilot message\n\n- Type: 'Palo Alto Ground, Cessna 12345, at the ramp with information Alpha, ready to taxi for runway 31, VFR to the south.'"
# Tap message input field
"$ADB" shell input tap 640 2500
sleep 1
# Type pilot message
"$ADB" shell input text "Palo%sAlto%sGround,%sCessna%s12345,%sat%sthe%sramp%swith%sinformation%sAlpha,%sready%sto%staxi%sfor%srunway%s31,%sVFR%sto%sthe%ssouth."
sleep 1
screenshot "05_pilot_message_typed"

pause "📤 DEMO STEP 8: Send message and wait for AI response"
# Tap Send button
"$ADB" shell input tap 1100 2500
sleep 1
screenshot "06_sending_message"
echo -e "${YELLOW}⏳ Waiting for AI response...${NC}"
sleep 5
screenshot "07_atc_response"

pause "💬 DEMO STEP 9: View conversation history\n\n- Pilot message (blue, right)\n- ATC response (white, left)\n- Timestamps shown"

pause "🔄 DEMO STEP 10: Show scenario selection"
# Tap Change button
"$ADB" shell input tap 1050 350
sleep 2
screenshot "08_scenario_selector"

pause "📋 DEMO STEP 11: View available scenarios\n\n- 5 training scenarios\n- Difficulty levels\n- Various ATC situations"
# Tap to select Emergency Declaration (5th item)
"$ADB" shell input tap 640 1200
sleep 2
screenshot "09_emergency_scenario"

pause "🚨 DEMO STEP 12: Emergency Scenario Loaded\n\n- More complex situation\n- Engine trouble at 3,000 feet\n- Demonstrates advanced training"

pause "🏠 DEMO STEP 13: Return to Home"
# Tap Home tab
"$ADB" shell input tap 160 2720
sleep 2
screenshot "10_home_final"

# ============================================
# DEMO COMPLETE
# ============================================

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}  ✓ Demo Complete!${NC}"
echo -e "${GREEN}========================================${NC}\n"

echo -e "${BLUE}Screenshots saved to: demo/screenshots/${NC}"
echo -e "${BLUE}Total screenshots: $(ls demo/screenshots/*.png 2>/dev/null | wc -l)${NC}\n"

pause "🔄 Reset app for another demo? (This will clear all data)"
"$ADB" shell pm clear com.example.flightdeck
echo -e "${GREEN}✓ App reset complete${NC}"

echo -e "\n${YELLOW}Demo script finished!${NC}\n"
