package com.example.flightdeck.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.flightdeck.MainActivity
import com.example.flightdeck.R
import com.example.flightdeck.databinding.ActivityOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Onboarding Activity
 * 3-screen carousel introducing new users to FlightDeck features
 * Shows: Welcome, ATC Practice, Progress Tracking
 */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter

    companion object {
        private const val PREFS_NAME = "FlightDeckPrefs"
        private const val KEY_FIRST_LAUNCH = "isFirstLaunch"
        private const val TOTAL_PAGES = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide action bar for full-screen onboarding
        supportActionBar?.hide()

        setupViewPager()
        setupButtons()
    }

    /**
     * Setup ViewPager2 with onboarding pages
     */
    private fun setupViewPager() {
        onboardingAdapter = OnboardingAdapter(this)
        binding.viewPager.adapter = onboardingAdapter

        // Setup TabLayout dots indicator
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        // Listen for page changes to update button visibility
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtons(position)
            }
        })
    }

    /**
     * Setup navigation buttons (Skip, Back, Next, Get Started)
     */
    private fun setupButtons() {
        binding.skipButton.setOnClickListener {
            completeOnboarding()
        }

        binding.backButton.setOnClickListener {
            val currentPage = binding.viewPager.currentItem
            if (currentPage > 0) {
                binding.viewPager.currentItem = currentPage - 1
            }
        }

        binding.nextButton.setOnClickListener {
            val currentPage = binding.viewPager.currentItem
            if (currentPage < TOTAL_PAGES - 1) {
                binding.viewPager.currentItem = currentPage + 1
            }
        }

        binding.getStartedButton.setOnClickListener {
            completeOnboarding()
        }

        // Initial button state (page 0)
        updateButtons(0)
    }

    /**
     * Update button visibility based on current page
     */
    private fun updateButtons(position: Int) {
        when (position) {
            0 -> {
                // First page: Show Skip, hide Back, show Next, hide Get Started
                binding.skipButton.visibility = View.VISIBLE
                binding.backButton.visibility = View.INVISIBLE
                binding.nextButton.visibility = View.VISIBLE
                binding.getStartedButton.visibility = View.GONE
            }
            TOTAL_PAGES - 1 -> {
                // Last page: Hide Skip, show Back, hide Next, show Get Started
                binding.skipButton.visibility = View.GONE
                binding.backButton.visibility = View.VISIBLE
                binding.nextButton.visibility = View.GONE
                binding.getStartedButton.visibility = View.VISIBLE
            }
            else -> {
                // Middle pages: Hide Skip, show Back, show Next, hide Get Started
                binding.skipButton.visibility = View.GONE
                binding.backButton.visibility = View.VISIBLE
                binding.nextButton.visibility = View.VISIBLE
                binding.getStartedButton.visibility = View.GONE
            }
        }
    }

    /**
     * Mark onboarding as complete and navigate to main app
     */
    private fun completeOnboarding() {
        // Mark that user has completed onboarding
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()

        // Navigate to main app
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

        // Optional: Add transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
