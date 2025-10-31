package com.example.flightdeck.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Adapter for onboarding ViewPager2
 * Manages the 3 onboarding pages
 */
class OnboardingAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingPage1Fragment()
            1 -> OnboardingPage2Fragment()
            2 -> OnboardingPage3Fragment()
            else -> OnboardingPage1Fragment()
        }
    }
}
