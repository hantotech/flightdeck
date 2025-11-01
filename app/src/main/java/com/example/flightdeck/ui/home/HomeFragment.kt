package com.example.flightdeck.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.flightdeck.FlightDeckApplication
import com.example.flightdeck.R
import com.example.flightdeck.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Home/Dashboard Fragment
 * Shows quick actions, recent activity, and weekly progress
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGreeting()
        setupClickListeners()
        loadLogbookData()
    }

    /**
     * Setup time-of-day greeting
     */
    private fun setupGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Good morning, Pilot!"
            in 12..17 -> "Good afternoon, Pilot!"
            else -> "Good evening, Pilot!"
        }
        binding.greetingText.text = greeting
    }

    /**
     * Setup click listeners for quick action cards
     */
    private fun setupClickListeners() {
        // Start Training - HIDDEN for Phase 1 (Mission selection not ready)
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

    /**
     * Load and display logbook data (rank, recent sessions, weekly stats)
     */
    private fun loadLogbookData() {
        val app = requireActivity().application as FlightDeckApplication
        val logbookRepo = app.appContainer.logbookRepository

        lifecycleScope.launch {
            // Load pilot rank
            val rank = logbookRepo.getPilotRank()
            binding.rankText.text = rank.displayName
            binding.rankStars.text = getStarsForRank(rank.ordinal + 1)

            // Load recent sessions (limit to 2 most recent)
            val recentSessions = logbookRepo.getAllEntries().take(2)
            displayRecentSessions(recentSessions)

            // Load weekly statistics
            val now = System.currentTimeMillis()
            val weekAgo = now - (7 * 24 * 60 * 60 * 1000L) // 7 days ago
            val weeklySessions = logbookRepo.getAllEntries().filter { entry ->
                entry.date >= weekAgo
            }

            // Calculate weekly stats
            val totalMinutes: Int = weeklySessions.sumOf { entry -> entry.totalTime }
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            val sessionCount = weeklySessions.size
            val currentStreak = logbookRepo.getTotals().currentStreak

            // Calculate proficiency change
            val avgScore = if (weeklySessions.isNotEmpty()) {
                weeklySessions.map { entry -> entry.overallScore }.average().toInt()
            } else {
                0
            }
            val proficiencyChange = if (weeklySessions.size >= 2) {
                val firstScore = weeklySessions.last().overallScore
                val lastScore = weeklySessions.first().overallScore
                lastScore - firstScore
            } else {
                0f
            }

            // Update UI
            binding.streakText.text = "$currentStreak ${if (currentStreak == 1) "day" else "days"}"
            binding.weeklyTimeText.text = "${hours}h ${minutes}m"
            binding.weeklySessionsText.text = "$sessionCount"

            val changeText = if (proficiencyChange > 0) "+${proficiencyChange.toInt()}%" else "${proficiencyChange.toInt()}%"
            binding.proficiencyChangeText.text = changeText
            binding.proficiencyChangeText.setTextColor(
                resources.getColor(
                    if (proficiencyChange >= 0) R.color.vfr else R.color.lifr,
                    null
                )
            )
        }
    }

    /**
     * Display recent session cards
     */
    private fun displayRecentSessions(sessions: List<com.example.flightdeck.data.model.LogbookEntry>) {
        if (sessions.isEmpty()) {
            binding.noActivityText.visibility = View.VISIBLE
            return
        }

        binding.noActivityText.visibility = View.GONE

        // Clear container
        val container = binding.recentActivityContainer
        container.removeAllViews()

        // Add simple cards for each recent session
        sessions.forEach { entry ->
            // Create a MaterialCardView programmatically
            val cardView = com.google.android.material.card.MaterialCardView(requireContext()).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 16
                }
                radius = 8f * resources.displayMetrics.density
                cardElevation = 2f * resources.displayMetrics.density
            }

            // Create content layout
            val contentLayout = android.widget.LinearLayout(requireContext()).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
            }

            // Route text
            val routeText = android.widget.TextView(requireContext()).apply {
                text = "📍 ${entry.departureAirport} → ${entry.arrivalAirport}"
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(resources.getColor(R.color.text_primary, null))
            }

            // Mission name and score
            val detailText = android.widget.TextView(requireContext()).apply {
                text = "${entry.missionName} (${entry.overallScore.toInt()}%)"
                textSize = 14f
                setTextColor(resources.getColor(R.color.text_secondary, null))
                setPadding(0, 4, 0, 0)
            }

            // Time ago
            val timeText = android.widget.TextView(requireContext()).apply {
                val daysAgo = ((System.currentTimeMillis() - entry.date) / (24 * 60 * 60 * 1000)).toInt()
                text = if (daysAgo == 0) "Today" else if (daysAgo == 1) "Yesterday" else "$daysAgo days ago"
                textSize = 12f
                setTextColor(resources.getColor(R.color.text_secondary, null))
                setPadding(0, 4, 0, 0)
            }

            contentLayout.addView(routeText)
            contentLayout.addView(detailText)
            contentLayout.addView(timeText)
            cardView.addView(contentLayout)
            container.addView(cardView)
        }
    }

    /**
     * Get star rating based on rank level
     */
    private fun getStarsForRank(level: Int): String {
        val fullStars = level
        return "⭐".repeat(fullStars)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
