package com.example.flightdeck.ui.mission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flightdeck.FlightDeckApplication
import com.example.flightdeck.R
import com.example.flightdeck.data.model.Difficulty
import com.example.flightdeck.data.model.MissionConfig
import com.example.flightdeck.databinding.FragmentMissionSelectionBinding
import com.example.flightdeck.di.ViewModelFactory

/**
 * Mission Selection Fragment
 * Allows users to choose from preset missions or create custom training scenarios
 */
class MissionSelectionFragment : Fragment() {

    private var _binding: FragmentMissionSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MissionSelectionViewModel by viewModels {
        ViewModelFactory((requireActivity().application as FlightDeckApplication).appContainer)
    }

    private lateinit var presetsAdapter: MissionAdapter
    private lateinit var recentlyFlownAdapter: MissionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissionSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupDifficultyFilters()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        // Presets adapter
        presetsAdapter = MissionAdapter { mission ->
            onMissionSelected(mission)
        }

        binding.presetsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = presetsAdapter
        }

        // Recently flown adapter
        recentlyFlownAdapter = MissionAdapter { mission ->
            onMissionSelected(mission)
        }

        binding.recentlyFlownRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentlyFlownAdapter
        }
    }

    private fun setupDifficultyFilters() {
        binding.difficultyFilterChips.setOnCheckedStateChangeListener { _, checkedIds ->
            val difficulty = when (checkedIds.firstOrNull()) {
                R.id.chipBeginner -> Difficulty.BEGINNER
                R.id.chipIntermediate -> Difficulty.INTERMEDIATE
                R.id.chipAdvanced -> Difficulty.ADVANCED
                R.id.chipExpert -> Difficulty.EXPERT
                else -> null // "All" selected
            }
            viewModel.filterByDifficulty(difficulty)
        }
    }

    private fun setupClickListeners() {
        binding.createCustomButton.setOnClickListener {
            Toast.makeText(requireContext(), "Custom mission builder coming soon!", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to custom mission builder in future phase
        }
    }

    private fun observeViewModel() {
        // Preset missions
        viewModel.presetMissions.observe(viewLifecycleOwner) { missions ->
            presetsAdapter.submitList(missions)
        }

        // Recently flown missions
        viewModel.recentlyFlownMissions.observe(viewLifecycleOwner) { missions ->
            if (missions.isNotEmpty()) {
                binding.recentlyFlownHeader.visibility = View.VISIBLE
                binding.recentlyFlownRecyclerView.visibility = View.VISIBLE
                recentlyFlownAdapter.submitList(missions)
            } else {
                binding.recentlyFlownHeader.visibility = View.GONE
                binding.recentlyFlownRecyclerView.visibility = View.GONE
            }
        }

        // Loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun onMissionSelected(mission: MissionConfig) {
        // TODO: Navigate to practice screen with selected mission
        // For now, just show a toast
        Toast.makeText(
            requireContext(),
            "Starting: ${mission.name} (${mission.estimatedDifficultyStars}⭐)",
            Toast.LENGTH_SHORT
        ).show()

        // Navigate to practice tab
        findNavController().navigate(R.id.navigation_practice)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
