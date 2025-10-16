package com.example.flightdeck.ui.weather

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.flightdeck.R
import com.example.flightdeck.databinding.FragmentAtisBinding

/**
 * Fragment for ATIS/AWOS viewer
 */
class ATISFragment : Fragment() {

    private var _binding: FragmentAtisBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ATISViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAtisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Search button
        binding.getAtisButton.setOnClickListener {
            val icao = binding.airportCodeInput.text?.toString() ?: ""
            viewModel.getATIS(icao)
        }

        // Enter key on keyboard
        binding.airportCodeInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val icao = binding.airportCodeInput.text?.toString() ?: ""
                viewModel.getATIS(icao)
                true
            } else {
                false
            }
        }

        // Copy button
        binding.copyButton.setOnClickListener {
            copyATISToClipboard()
        }

        // Refresh button
        binding.refreshButton.setOnClickListener {
            viewModel.refreshATIS()
        }
    }

    private fun observeViewModel() {
        // UI State
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ATISUiState.Initial -> showInitial()
                is ATISUiState.Loading -> showLoading()
                is ATISUiState.Success -> showSuccess()
                is ATISUiState.Error -> showError(state.message)
            }
        }

        // Current Airport
        viewModel.currentAirport.observe(viewLifecycleOwner) { airport ->
            airport?.let {
                binding.airportName.text = it.name
                binding.airportInfo.text = "${it.icao} • Class ${it.airspaceClass ?: "G"} • " +
                        if (it.towerControlled) "Towered" else "Non-towered"
            }
        }

        // Current ATIS
        viewModel.currentATIS.observe(viewLifecycleOwner) { atis ->
            atis?.let {
                binding.informationCode.text = "Information ${it.informationCode}"
                binding.timeValue.text = it.observationTime
                binding.windValue.text = "${it.weatherReport.windDirection}° at ${it.weatherReport.windSpeed}kt"
                binding.visibilityValue.text = "${it.weatherReport.visibility} SM"
                binding.temperatureValue.text = "${it.weatherReport.temperature}°C / ${it.weatherReport.dewpoint}°C"
                binding.altimeterValue.text = "${it.weatherReport.altimeter}"
                binding.runwayValue.text = it.activeRunway ?: "N/A"
            }
        }

        // ATIS Readback
        viewModel.atisReadback.observe(viewLifecycleOwner) { readback ->
            binding.fullBroadcast.text = readback ?: ""
        }
    }

    private fun showInitial() {
        binding.loadingIndicator.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.atisScrollView.visibility = View.GONE
    }

    private fun showLoading() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE
        binding.atisScrollView.visibility = View.GONE
    }

    private fun showSuccess() {
        binding.loadingIndicator.visibility = View.GONE
        binding.errorText.visibility = View.GONE
        binding.atisScrollView.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        binding.loadingIndicator.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = message
        binding.atisScrollView.visibility = View.GONE
    }

    private fun copyATISToClipboard() {
        val readback = viewModel.atisReadback.value ?: return
        val informationCode = viewModel.currentATIS.value?.informationCode ?: "Unknown"

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("ATIS Information $informationCode", readback)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(
            requireContext(),
            "ATIS copied to clipboard",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
