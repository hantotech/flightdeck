package com.example.flightdeck.ui.atc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flightdeck.R
import com.example.flightdeck.databinding.FragmentAtcBinding
import com.example.flightdeck.data.model.ATCScenario
import kotlinx.coroutines.launch

/**
 * ATC Simulator Fragment
 * Provides AI-powered ATC practice with realistic radio communications
 */
class ATCFragment : Fragment() {

    private var _binding: FragmentAtcBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ATCViewModel by viewModels()
    private lateinit var chatAdapter: ChatMessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAtcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatMessageAdapter()
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true // Start at bottom
            }
            adapter = chatAdapter
        }
    }

    private fun setupClickListeners() {
        // Send button
        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            if (message.isNotBlank()) {
                viewModel.sendPilotMessage(message)
                binding.messageInput.text?.clear()
            }
        }

        // Change scenario button
        binding.changeScenarioButton.setOnClickListener {
            showScenarioSelector()
        }
    }

    private fun observeViewModel() {
        // Current scenario
        viewModel.currentScenario.observe(viewLifecycleOwner) { scenario ->
            scenario?.let {
                binding.scenarioTitle.text = it.title
                binding.scenarioSituation.text = it.situation
                binding.airportInfo.text = "${it.airport} • ${it.scenarioType.name.replace('_', ' ')}"
            }
        }

        // Messages
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages) {
                // Scroll to bottom when new message added
                if (messages.isNotEmpty()) {
                    binding.messagesRecyclerView.smoothScrollToPosition(messages.size - 1)
                }
            }
        }

        // Loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.sendButton.isEnabled = !isLoading
        }

        // Errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun showScenarioSelector() {
        lifecycleScope.launch {
            try {
                val scenarios = viewModel.getAllScenarios()
                if (scenarios.isEmpty()) {
                    Toast.makeText(requireContext(), "No scenarios available", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val scenarioTitles = scenarios.map { it.title }.toTypedArray()

                AlertDialog.Builder(requireContext())
                    .setTitle("Select Scenario")
                    .setItems(scenarioTitles) { _, which ->
                        viewModel.selectScenario(scenarios[which])
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading scenarios: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
