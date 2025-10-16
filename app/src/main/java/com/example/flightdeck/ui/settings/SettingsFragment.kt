package com.example.flightdeck.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.flightdeck.R

/**
 * Settings Fragment (Placeholder)
 * TODO: Implement API key configuration and preferences
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        
        val textView = view.findViewById<TextView>(R.id.text_settings)
        textView.text = "Settings\n\nComing Soon!\n\nThis will provide API key configuration and user preferences."
        
        return view
    }
}
