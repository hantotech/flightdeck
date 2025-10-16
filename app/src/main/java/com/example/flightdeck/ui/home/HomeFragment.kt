package com.example.flightdeck.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.flightdeck.R
import com.example.flightdeck.databinding.FragmentHomeBinding

/**
 * Home/Dashboard Fragment
 * Shows quick actions and app status
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

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        // ATIS Card - Navigate to Weather tab
        binding.atisCard.setOnClickListener {
            findNavController().navigate(R.id.navigation_weather)
        }

        // ATC Practice Card - Navigate to Practice tab
        binding.atcCard.setOnClickListener {
            findNavController().navigate(R.id.navigation_practice)
        }

        // Airport Card - Navigate to Weather tab (can search from there)
        binding.airportCard.setOnClickListener {
            findNavController().navigate(R.id.navigation_weather)
        }

        // Knowledge Card - TODO: Navigate to knowledge screen when implemented
        binding.knowledgeCard.setOnClickListener {
            // Placeholder - will implement knowledge screen later
        }
    }

    private fun observeViewModel() {
        // Database status
        viewModel.databaseStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                DatabaseStatus.LOADING -> {
                    binding.databaseStatus.text = getString(R.string.status_loading)
                    binding.databaseStatus.setTextColor(resources.getColor(R.color.mvfr, null))
                }
                DatabaseStatus.READY -> {
                    binding.databaseStatus.text = getString(R.string.status_ready)
                    binding.databaseStatus.setTextColor(resources.getColor(R.color.vfr, null))
                }
                DatabaseStatus.ERROR -> {
                    binding.databaseStatus.text = getString(R.string.status_error)
                    binding.databaseStatus.setTextColor(resources.getColor(R.color.lifr, null))
                }
            }
        }

        // Airport count
        viewModel.airportCount.observe(viewLifecycleOwner) { count ->
            binding.airportCount.text = "$count airports"
        }

        // Scenario count
        viewModel.scenarioCount.observe(viewLifecycleOwner) { count ->
            binding.scenarioCount.text = "$count scenarios"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
