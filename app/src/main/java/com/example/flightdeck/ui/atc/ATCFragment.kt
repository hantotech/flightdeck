package com.example.flightdeck.ui.atc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.flightdeck.R

/**
 * ATC Simulator Fragment (Placeholder)
 * TODO: Implement full ATC simulator with AI
 */
class ATCFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_atc, container, false)
        
        val textView = view.findViewById<TextView>(R.id.text_atc)
        textView.text = "ATC Simulator\n\nComing Soon!\n\nThis will provide AI-powered ATC practice scenarios."
        
        return view
    }
}
