package com.example.flightdeck.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.flightdeck.MainActivity
import com.example.flightdeck.databinding.ActivitySplashBinding
import com.example.flightdeck.ui.onboarding.OnboardingActivity

/**
 * Splash Screen Activity
 * Shows FlightDeck branding while app initializes
 * Determines if user should see onboarding or go straight to main app
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    companion object {
        private const val SPLASH_DELAY = 2000L // 2 seconds
        private const val PREFS_NAME = "FlightDeckPrefs"
        private const val KEY_FIRST_LAUNCH = "isFirstLaunch"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar for full-screen splash
        supportActionBar?.hide()

        // Check if first launch
        val isFirstLaunch = checkFirstLaunch()

        // Delay and navigate
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen(isFirstLaunch)
        }, SPLASH_DELAY)
    }

    /**
     * Check if this is the first time the app is launched
     */
    private fun checkFirstLaunch(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    /**
     * Mark that the app has been launched before
     */
    private fun markFirstLaunchComplete() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    /**
     * Navigate to onboarding (first launch) or main app (returning user)
     */
    private fun navigateToNextScreen(isFirstLaunch: Boolean) {
        val intent = if (isFirstLaunch) {
            // First time user - show onboarding
            Intent(this, OnboardingActivity::class.java)
        } else {
            // Returning user - go straight to main app
            Intent(this, MainActivity::class.java)
        }

        startActivity(intent)
        finish() // Close splash screen

        // Optional: Add transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
