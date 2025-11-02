package com.example.flightdeck.ui.flightplanning

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.flightdeck.R
import com.example.flightdeck.data.model.Aircraft
import com.example.flightdeck.databinding.FragmentFlightPlanningBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

/**
 * Flight Planning Fragment
 * Allows users to plan flights with departure/arrival selection,
 * aircraft selection, weather info, and AI advice
 */
class FlightPlanningFragment : Fragment() {

    private var _binding: FragmentFlightPlanningBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FlightPlanningViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlightPlanningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAirportInputs()
        setupAircraftSelection()
        setupClickListeners()
        observeViewModel()
    }

    /**
     * Setup departure and arrival airport inputs with text watchers
     */
    private fun setupAirportInputs() {
        // Departure airport input
        binding.departureInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val icao = s?.toString()?.uppercase()
                if (icao?.length == 4) {
                    viewModel.setDepartureAirport(icao)
                }
            }
        })

        // Arrival airport input
        binding.arrivalInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val icao = s?.toString()?.uppercase()
                if (icao?.length == 4) {
                    viewModel.setArrivalAirport(icao)
                }
            }
        })
    }

    /**
     * Setup aircraft selection dropdown
     */
    private fun setupAircraftSelection() {
        viewModel.availableAircraft.observe(viewLifecycleOwner) { aircraftList ->
            if (aircraftList.isNotEmpty()) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    aircraftList.map { "${it.make} ${it.model}" }
                )
                binding.aircraftSpinner.setAdapter(adapter)

                // Set default selection
                binding.aircraftSpinner.setText(
                    "${aircraftList[0].make} ${aircraftList[0].model}",
                    false
                )

                // Handle selection
                binding.aircraftSpinner.setOnItemClickListener { _, _, position, _ ->
                    viewModel.setSelectedAircraft(aircraftList[position])
                }
            }
        }
    }

    /**
     * Setup button click listeners
     */
    private fun setupClickListeners() {
        // Unlock waypoints button (premium)
        binding.unlockWaypointsButton.setOnClickListener {
            showPremiumUpgradeDialog()
        }

        // Save as favorite button
        binding.saveAsFavoriteButton.setOnClickListener {
            showSaveFavoriteDialog()
        }

        // Start ATC Practice button
        binding.startAtcButton.setOnClickListener {
            val flightPlanId = viewModel.createFlightPlanAndStartATC()
            if (flightPlanId != null) {
                // Navigate to ATC practice with flight plan
                // TODO: Pass flightPlanId to ATC screen
                findNavController().navigate(R.id.navigation_practice)
            }
        }

        // Detailed AI advice button
        binding.detailedAdviceButton.setOnClickListener {
            showDetailedAIAdviceDialog()
        }
    }

    /**
     * Observe ViewModel LiveData
     */
    private fun observeViewModel() {
        // Departure airport
        viewModel.departureAirport.observe(viewLifecycleOwner) { airportData ->
            if (airportData != null) {
                binding.departureInput.setText(airportData.airport.icao)
                updateRouteSummary()
            }
        }

        // Arrival airport
        viewModel.arrivalAirport.observe(viewLifecycleOwner) { airportData ->
            if (airportData != null) {
                binding.arrivalInput.setText(airportData.airport.icao)
                updateRouteSummary()
            }
        }

        // Departure METAR
        viewModel.departureMetar.observe(viewLifecycleOwner) { weather ->
            if (weather != null) {
                binding.departureMetarText.text = weather.rawMetar
                binding.weatherCard.isVisible = true
            }
        }

        // Arrival METAR
        viewModel.arrivalMetar.observe(viewLifecycleOwner) { weather ->
            if (weather != null) {
                binding.arrivalMetarText.text = weather.rawMetar
                binding.weatherCard.isVisible = true
            }
        }

        // Flight metrics
        viewModel.flightMetrics.observe(viewLifecycleOwner) { metrics ->
            if (metrics != null) {
                binding.distanceText.text = "${String.format("%.0f", metrics.distance)} nm"
                binding.flightTimeText.text = "${metrics.estimatedFlightTime} min"
                binding.fuelText.text = "${String.format("%.1f", metrics.fuelRequired)} gal"
                binding.metricsCard.isVisible = true
                binding.saveAsFavoriteButton.isVisible = true
                binding.startAtcButton.isVisible = true

                updateRouteSummary()
            }
        }

        // AI advice
        viewModel.aiAdvice.observe(viewLifecycleOwner) { advice ->
            if (advice != null) {
                // Show brief summary (first 100 chars)
                val briefAdvice = if (advice.length > 100) {
                    advice.substring(0, 100) + "..."
                } else {
                    advice
                }
                binding.aiAdviceText.text = briefAdvice
                binding.aiAdviceCard.isVisible = true
            }
        }

        // Premium status
        viewModel.isPremiumUser.observe(viewLifecycleOwner) { isPremium ->
            if (isPremium) {
                // Show waypoint controls for premium users
                binding.waypointPremiumBanner.isVisible = false
                binding.waypointsContainer.isVisible = true
                binding.addWaypointButton.isVisible = true
            } else {
                // Show premium lock banner for free users
                binding.waypointPremiumBanner.isVisible = true
                binding.waypointsContainer.isVisible = false
                binding.addWaypointButton.isVisible = false
            }
        }

        // Error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        // Loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: Show/hide loading indicator
        }
    }

    /**
     * Update route summary text
     */
    private fun updateRouteSummary() {
        val departure = viewModel.departureAirport.value
        val arrival = viewModel.arrivalAirport.value
        val metrics = viewModel.flightMetrics.value

        if (departure != null && arrival != null && metrics != null) {
            val summary = "Direct · ${String.format("%.0f", metrics.distance)}nm · ${metrics.estimatedFlightTime}min"
            binding.routeSummaryText.text = summary
            binding.routeSummaryLayout.isVisible = true
            binding.waypointsSection.isVisible = true
        } else {
            binding.routeSummaryLayout.isVisible = false
            binding.waypointsSection.isVisible = false
        }
    }

    /**
     * Show premium upgrade dialog
     */
    private fun showPremiumUpgradeDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🔒 Premium Feature")
            .setMessage(
                "Multi-Waypoint Routes\n\n" +
                        "✓ Unlimited waypoints\n" +
                        "✓ 20,000+ US/Canada airports\n" +
                        "✓ Complex routing practice\n" +
                        "✓ One-time payment, lifetime access\n\n" +
                        "Unlock premium for \$29.99"
            )
            .setPositiveButton("Unlock Premium") { _, _ ->
                // TODO: Launch in-app purchase flow
                Toast.makeText(requireContext(), "In-app purchase coming soon!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Show save favorite dialog
     */
    private fun showSaveFavoriteDialog() {
        val input = TextInputEditText(requireContext())
        input.hint = "Route name (optional)"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("⭐ Save as Favorite")
            .setMessage("Save this route to your favorites for quick access")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val customName = input.text?.toString()?.takeIf { it.isNotBlank() }
                viewModel.saveAsFavorite(customName)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Show detailed AI advice dialog
     */
    private fun showDetailedAIAdviceDialog() {
        val fullAdvice = viewModel.aiAdvice.value ?: "No advice available"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("💡 Detailed Flight Analysis")
            .setMessage(fullAdvice)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
