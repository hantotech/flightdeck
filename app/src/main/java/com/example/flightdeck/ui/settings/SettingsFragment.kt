package com.example.flightdeck.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flightdeck.R
import com.example.flightdeck.ui.onboarding.OnboardingActivity
import com.google.android.material.button.MaterialButton

/**
 * Settings Fragment
 * Provides app settings, help, and tutorial access
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        setupClickListeners(view)

        return view
    }

    private fun setupClickListeners(view: View) {
        // Show Tutorial button
        view.findViewById<MaterialButton>(R.id.showTutorialButton)?.setOnClickListener {
            // Launch onboarding/tutorial activity
            val intent = Intent(requireContext(), OnboardingActivity::class.java)
            startActivity(intent)
        }
    }
}
