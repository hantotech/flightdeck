@echo off
REM FlightDeck Automated Demo Script (Windows)
REM This script automates UI interactions for a canned demo

setlocal
set ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe

echo ========================================
echo   FlightDeck - Automated Demo
echo ========================================
echo.

REM Check if device is connected
echo Checking for connected device...
"%ADB%" devices | findstr "device" >nul
if %errorlevel% neq 0 (
    echo Error: No device connected. Please start emulator.
    pause
    exit /b 1
)
echo Device connected
echo.

REM Create screenshots directory
if not exist demo\screenshots mkdir demo\screenshots

echo ============================================
echo DEMO START
echo ============================================
echo.

REM Step 1: Launch app
echo [1/13] Launching FlightDeck...
"%ADB%" shell am start -n com.example.flightdeck/.MainActivity
timeout /t 3 /nobreak >nul
"%ADB%" exec-out screencap -p > demo\screenshots\01_home_dashboard.png
echo Screenshot saved: 01_home_dashboard.png
pause

REM Step 2: View home dashboard
echo.
echo [2/13] Home Dashboard
echo   - Welcome message
echo   - Quick action cards
echo   - Database stats
pause

REM Step 3: Navigate to ATIS
echo.
echo [3/13] Navigating to ATIS Weather Viewer...
"%ADB%" shell input tap 960 2720
timeout /t 2 /nobreak >nul
"%ADB%" exec-out screencap -p > demo\screenshots\02_atis_viewer.png
echo Screenshot saved: 02_atis_viewer.png
pause

REM Step 4: Search KPAO
echo.
echo [4/13] Searching for KPAO weather...
"%ADB%" shell input tap 640 500
timeout /t 1 /nobreak >nul
"%ADB%" shell input text "KPAO"
timeout /t 1 /nobreak >nul
"%ADB%" shell input tap 640 650
timeout /t 3 /nobreak >nul
"%ADB%" exec-out screencap -p > demo\screenshots\03_atis_kpao.png
echo Screenshot saved: 03_atis_kpao.png
pause

REM Step 5: Navigate to ATC Simulator
echo.
echo [5/13] Navigating to ATC Simulator...
"%ADB%" shell input tap 426 2720
timeout /t 2 /nobreak >nul
"%ADB%" exec-out screencap -p > demo\screenshots\04_atc_simulator.png
echo Screenshot saved: 04_atc_simulator.png
pause

REM Step 6: View scenario
echo.
echo [6/13] Current Scenario: Ground Clearance - VFR
echo   - Scenario details displayed
echo   - Ready to practice radio calls
pause

REM Step 7: Type pilot message
echo.
echo [7/13] Typing pilot message...
"%ADB%" shell input tap 640 2500
timeout /t 1 /nobreak >nul
"%ADB%" shell input text "Palo%%sAlto%%sGround,%%sCessna%%s12345,%%sat%%sthe%%sramp%%swith%%sinformation%%sAlpha,%%sready%%sto%%staxi%%sfor%%srunway%%s31,%%sVFR%%sto%%sthe%%ssouth."
timeout /t 1 /nobreak >nul
"%ADB%" exec-out screencap -p > demo\screenshots\05_pilot_message_typed.png
echo Screenshot saved: 05_pilot_message_typed.png
pause

REM Step 8: Send and wait for AI
echo.
echo [8/13] Sending message and waiting for AI response...
"%ADB%" shell input tap 1100 2500
timeout /t 1 /nobreak >nul
"%ADB%" exec-out screencap -p > demo\screenshots\06_sending_message.png
echo Waiting for AI to respond...
timeout /t 5 /nobreak >nul
"%ADB%" exec-out screencap -p > demo\screenshots\07_atc_response.png
echo Screenshot saved: 07_atc_response.png
pause

REM Step 9: View conversation
echo.
echo [9/13] Conversation History
echo   - Pilot message (blue, right)
echo   - ATC response (white, left)
echo   - Timestamps displayed
pause

REM Step 10: Show scenario selector
echo.
echo [10/13] Opening scenario selector...
"%ADB%" shell input tap 1050 350
timeout /t 2 /nobreak >nul
"%ADB%" exec-out screencap -p > demo\screenshots\08_scenario_selector.png
echo Screenshot saved: 08_scenario_selector.png
pause

REM Step 11: View scenarios
echo.
echo [11/13] Available Scenarios
echo   - 5 training scenarios
echo   - Beginner to Advanced
echo   - Various ATC situations
pause

REM Select emergency scenario
echo Selecting Emergency Declaration scenario...
"%ADB%" shell input tap 640 1200
timeout /t 2 /nobreak >nul
"%ADB%" exec-out screencap -p > demo\screenshots\09_emergency_scenario.png
echo Screenshot saved: 09_emergency_scenario.png
pause

REM Step 12: Emergency scenario
echo.
echo [12/13] Emergency Scenario Loaded
echo   - Engine trouble at 3,000 feet
echo   - Advanced training scenario
pause

REM Step 13: Return home
echo.
echo [13/13] Returning to Home...
"%ADB%" shell input tap 160 2720
timeout /t 2 /nobreak >nul
"%ADB%" exec-out screencap -p > demo\screenshots\10_home_final.png
echo Screenshot saved: 10_home_final.png
pause

echo.
echo ========================================
echo   Demo Complete!
echo ========================================
echo.
echo Screenshots saved to: demo\screenshots\
dir /b demo\screenshots\*.png | find /c ".png"
echo.

echo Reset app for another demo? (Press Ctrl+C to skip)
pause
"%ADB%" shell pm clear com.example.flightdeck
echo App reset complete
echo.

echo Demo script finished!
pause
