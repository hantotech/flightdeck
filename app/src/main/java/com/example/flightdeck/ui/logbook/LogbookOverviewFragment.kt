package com.example.flightdeck.ui.logbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.flightdeck.FlightDeckApplication
import com.example.flightdeck.R
import com.example.flightdeck.databinding.FragmentLogbookOverviewBinding
import com.example.flightdeck.di.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

/**
 * Logbook Overview Fragment
 * Displays user's flight training logbook with proficiency tracking
 */
class LogbookOverviewFragment : Fragment() {

    private var _binding: FragmentLogbookOverviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LogbookOverviewViewModel by viewModels {
        ViewModelFactory((requireActivity().application as FlightDeckApplication).appContainer)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogbookOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Setup click listeners
        binding.viewAllEntriesButton.setOnClickListener {
            // TODO: Navigate to all entries screen
            Snackbar.make(binding.root, "All Entries - Coming Soon", Snackbar.LENGTH_SHORT).show()
        }

        binding.viewAllSkillsButton.setOnClickListener {
            // TODO: Navigate to proficiency detail screen
            Snackbar.make(binding.root, "All Skills - Coming Soon", Snackbar.LENGTH_SHORT).show()
        }

        binding.exportButton.setOnClickListener {
            // TODO: Show export dialog
            Snackbar.make(binding.root, "Export - Coming Soon", Snackbar.LENGTH_SHORT).show()
        }

        // Pull to refresh
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeViewModel() {
        // Loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }

        // Error state
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        // Pilot rank
        viewModel.pilotRank.observe(viewLifecycleOwner) { rank ->
            binding.rankTitle.text = rank?.displayName ?: "Student Pilot"
            binding.rankIcon.text = rank?.icon ?: "🎓"
        }

        // Logbook totals
        viewModel.totals.observe(viewLifecycleOwner) { totals ->
            totals?.let {
                binding.totalTimeValue.text = viewModel.getFormattedFlightTime()
                binding.totalSessionsValue.text = it.totalSessions.toString()
                binding.airportsVisitedValue.text = it.uniqueAirportsVisited.toString()
                binding.currentStreakValue.text = "${it.currentStreak} days"
            }
        }

        // Average proficiency
        viewModel.proficiencyRatings.observe(viewLifecycleOwner) {
            val avgScore = viewModel.getAverageProficiencyScore()
            binding.avgProficiency.text = "$avgScore%"
            binding.starRating.text = getStarString(viewModel.getStarRating())
        }

        // Top proficiencies
        viewModel.topProficiencies.observe(viewLifecycleOwner) { proficiencies ->
            // TODO: Display top proficiencies in RecyclerView or list
            // For now, showing count
            binding.proficienciesCount.text = "${proficiencies.size} skills tracked"
        }

        // Recent entries
        viewModel.recentEntries.observe(viewLifecycleOwner) { entries ->
            // TODO: Display recent entries in RecyclerView
            // For now, showing count
            binding.recentEntriesCount.text = "${entries.size} recent sessions"
        }
    }

    private fun getStarString(stars: Int): String {
        return when (stars) {
            5 -> "⭐⭐⭐⭐⭐"
            4 -> "⭐⭐⭐⭐"
            3 -> "⭐⭐⭐"
            2 -> "⭐⭐"
            1 -> "⭐"
            else -> ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
