package com.example.flightdeck.ui.atc

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flightdeck.FlightDeckApplication
import com.example.flightdeck.R
import com.example.flightdeck.databinding.FragmentAtcBinding
import com.example.flightdeck.data.model.ATCScenario
import com.example.flightdeck.di.ViewModelFactory
import com.example.flightdeck.utils.VoiceInputManager
import com.example.flightdeck.utils.VoiceOutputManager
import com.example.flightdeck.utils.VoiceResult
import com.example.flightdeck.utils.NetworkUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.os.Bundle as SystemBundle

/**
 * ATC Simulator Fragment
 * Provides AI-powered ATC practice with voice-based radio communications
 */
class ATCFragment : Fragment() {

    private var _binding: FragmentAtcBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ATCViewModel by viewModels {
        val app = requireActivity().application as FlightDeckApplication
        ViewModelFactory(app.appContainer)
    }
    private lateinit var chatAdapter: ChatMessageAdapter
    private lateinit var trafficAdapter: TrafficAdapter

    // Voice managers
    private var voiceInputManager: VoiceInputManager? = null
    private var voiceOutputManager: VoiceOutputManager? = null
    private var isVoiceReady = false
    private var isSessionActive = false
    private var useVoiceInput = true // Enable voice by default for realistic ATC practice

    // Microphone permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            initializeVoiceManagers()
        } else {
            Toast.makeText(requireContext(), "Microphone permission required for voice input", Toast.LENGTH_LONG).show()
            // Fall back to text input
            toggleInputMode()
        }
    }

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

        // Enable voice input by default for realistic ATC practice
        if (useVoiceInput) {
            binding.voiceControlsCard.visibility = View.VISIBLE
            binding.textInputCard.visibility = View.GONE
            binding.toggleInputButton.text = "Use Text Input"
            // Check for microphone permission
            checkMicrophonePermission()
        } else {
            binding.voiceControlsCard.visibility = View.GONE
            binding.textInputCard.visibility = View.VISIBLE
            // Initialize voice output for TTS even in text mode
            voiceOutputManager = VoiceOutputManager(requireContext()) { initialized ->
                isVoiceReady = initialized
                Log.d("ATCFragment", "VoiceOutputManager initialized: $initialized")
            }
        }
    }

    private fun checkMicrophonePermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                initializeVoiceManagers()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                // Show explanation and request permission
                AlertDialog.Builder(requireContext())
                    .setTitle("Microphone Permission")
                    .setMessage("Voice input requires microphone access for realistic ATC practice.")
                    .setPositiveButton("Grant") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                    .setNegativeButton("Use Text") { _, _ ->
                        toggleInputMode()
                    }
                    .show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun initializeVoiceManagers() {
        // Initialize voice input
        voiceInputManager = VoiceInputManager(requireContext())

        if (!voiceInputManager!!.isAvailable()) {
            Toast.makeText(requireContext(), "Voice recognition not available on this device", Toast.LENGTH_LONG).show()
            toggleInputMode()
            return
        }

        // Initialize voice output
        voiceOutputManager = VoiceOutputManager(requireContext()) { initialized ->
            isVoiceReady = initialized
            Log.d("ATCFragment", "VoiceOutputManager initialized: $initialized, isVoiceReady: $isVoiceReady")
            if (!initialized) {
                Toast.makeText(requireContext(), "Voice output initialization failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatMessageAdapter()
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true // Start at bottom
            }
            adapter = chatAdapter
        }

        trafficAdapter = TrafficAdapter()
        binding.trafficRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = trafficAdapter
        }
    }

    private fun setupClickListeners() {
        // Start Practice button
        binding.startPracticeButton.setOnClickListener {
            val departure = binding.departureInput.text.toString().trim().uppercase()
            val arrival = binding.arrivalInput.text.toString().trim().uppercase()

            if (departure.length != 4 || arrival.length != 4) {
                Toast.makeText(requireContext(), "Please enter valid 4-letter airport codes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startPracticeSession(departure, arrival)
        }

        // Push-to-Talk button (press and hold)
        binding.pttButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (isSessionActive) {
                        startVoiceInput()
                    } else {
                        Toast.makeText(requireContext(), "Start a practice session first", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    stopVoiceInput()
                    true
                }
                else -> false
            }
        }

        // Toggle input mode
        binding.toggleInputButton.setOnClickListener {
            toggleInputMode()
        }

        // Text send button (fallback)
        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            if (message.isNotBlank()) {
                // Check network connectivity before sending
                if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                    Toast.makeText(
                        requireContext(),
                        "No internet connection. Please check your network settings.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                viewModel.sendPilotMessage(message)
                binding.messageInput.text?.clear()
            }
        }

        // End Session button
        binding.endSessionButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("End Practice Session")
                .setMessage("Are you sure you want to end this session? You'll receive a performance summary.")
                .setPositiveButton("End Session") { _, _ ->
                    viewModel.endSession()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun startPracticeSession(departure: String, arrival: String) {
        isSessionActive = true

        // Hide flight entry, show session info
        binding.flightEntryCard.visibility = View.GONE
        binding.sessionInfoCard.visibility = View.VISIBLE

        // Update session info
        binding.sessionTitle.text = "$departure → $arrival"
        binding.sessionInfo.text = "Initializing session..."

        // Initialize practice session with ViewModel
        viewModel.startCustomSession(departure, arrival)

        Toast.makeText(requireContext(), "Practice session started: $departure to $arrival", Toast.LENGTH_SHORT).show()
    }

    private fun startVoiceInput() {
        if (!isVoiceReady) {
            Toast.makeText(requireContext(), "Voice not ready", Toast.LENGTH_SHORT).show()
            return
        }

        // Update UI
        binding.voiceStatusText.text = "Listening..."
        binding.pttButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary)

        // Start listening
        lifecycleScope.launch {
            voiceInputManager?.startListening()?.collectLatest { result ->
                handleVoiceResult(result)
            }
        }
    }

    private fun stopVoiceInput() {
        voiceInputManager?.stopListening()
        binding.voiceStatusText.text = "Press and hold to talk"
        binding.pttButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.accent)
    }

    private fun handleVoiceResult(result: VoiceResult) {
        when (result) {
            is VoiceResult.Listening -> {
                binding.voiceStatusText.text = "Listening..."
            }
            is VoiceResult.Speaking -> {
                binding.voiceStatusText.text = "Speaking..."
            }
            is VoiceResult.Processing -> {
                binding.voiceStatusText.text = "Processing..."
            }
            is VoiceResult.Partial -> {
                binding.voiceStatusText.text = "Heard: ${result.text}"
            }
            is VoiceResult.Success -> {
                binding.voiceStatusText.text = "Press and hold to talk"
                // Send the recognized text to ATC
                viewModel.sendPilotMessage(result.text)
            }
            is VoiceResult.Error -> {
                binding.voiceStatusText.text = "Error: ${result.message}"
                Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
            }
            is VoiceResult.AudioLevel -> {
                // Could use for visual feedback (e.g., volume indicator)
            }
        }
    }

    private fun toggleInputMode() {
        useVoiceInput = !useVoiceInput

        if (useVoiceInput) {
            // Show voice controls and check permissions
            binding.voiceControlsCard.visibility = View.VISIBLE
            binding.textInputCard.visibility = View.GONE
            binding.toggleInputButton.text = "Use Text Input"
            checkMicrophonePermission()
        } else {
            // Show text input
            binding.voiceControlsCard.visibility = View.GONE
            binding.textInputCard.visibility = View.VISIBLE
            // Initialize voice output for TTS if not already initialized
            if (voiceOutputManager == null) {
                voiceOutputManager = VoiceOutputManager(requireContext()) { initialized ->
                    isVoiceReady = initialized
                    Log.d("ATCFragment", "VoiceOutputManager initialized: $initialized")
                }
            }
        }
    }

    private fun observeViewModel() {
        // Current scenario
        viewModel.currentScenario.observe(viewLifecycleOwner) { scenario ->
            scenario?.let {
                binding.sessionTitle.text = it.title
                binding.sessionInfo.text = it.situation
            }
        }

        // Messages
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages) {
                // Scroll to bottom when new message added
                if (messages.isNotEmpty()) {
                    binding.messagesRecyclerView.smoothScrollToPosition(messages.size - 1)

                    // Speak ATC responses (messages from ATC, not pilot)
                    val lastMessage = messages.last()
                    Log.d("ATCFragment", "Message type: ${lastMessage.type}, isVoiceReady: $isVoiceReady")
                    if (lastMessage.type == com.example.flightdeck.ui.atc.MessageType.ATC && isVoiceReady) {
                        Log.d("ATCFragment", "Speaking: ${lastMessage.text}")
                        voiceOutputManager?.speak(lastMessage.text)
                    } else if (lastMessage.type == com.example.flightdeck.ui.atc.MessageType.ATC) {
                        Log.w("ATCFragment", "Cannot speak - isVoiceReady: $isVoiceReady")
                    }
                }
            }
        }

        // Loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.sendButton.isEnabled = !isLoading
            binding.pttButton.isEnabled = !isLoading
        }

        // Errors - show user-friendly messages
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { errorMessage ->
                // Parse error message to show user-friendly text
                val displayMessage = when {
                    errorMessage.contains("network", ignoreCase = true) ||
                    errorMessage.contains("connection", ignoreCase = true) ||
                    errorMessage.contains("timeout", ignoreCase = true) ->
                        "Connection error. Please check your internet and try again."
                    errorMessage.contains("API", ignoreCase = true) ||
                    errorMessage.contains("Gemini", ignoreCase = true) ->
                        "AI service temporarily unavailable. Please try again."
                    else -> errorMessage
                }

                Toast.makeText(requireContext(), displayMessage, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        // Session summary - navigate to summary screen when ready
        viewModel.sessionSummary.observe(viewLifecycleOwner) { summary ->
            summary?.let {
                // Navigate to SessionSummaryFragment
                val bundle = SystemBundle().apply {
                    putSerializable("sessionSummary", it)
                }
                findNavController().navigate(
                    R.id.action_practice_to_session_summary,
                    bundle
                )
                viewModel.clearSessionSummary()

                // Reset UI state
                isSessionActive = false
                binding.sessionInfoCard.visibility = View.GONE
                binding.trafficCard.visibility = View.GONE
                binding.flightEntryCard.visibility = View.VISIBLE
            }
        }

        // Active traffic
        viewModel.activeTraffic.observe(viewLifecycleOwner) { traffic ->
            if (isSessionActive) {
                if (traffic.isNotEmpty()) {
                    binding.trafficCard.visibility = View.VISIBLE
                    binding.noTrafficText.visibility = View.GONE
                    trafficAdapter.submitList(traffic)
                } else {
                    binding.trafficCard.visibility = View.VISIBLE
                    binding.noTrafficText.visibility = View.VISIBLE
                    trafficAdapter.submitList(emptyList())
                }
            } else {
                binding.trafficCard.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cleanup voice managers
        voiceInputManager?.cancel()
        voiceOutputManager?.shutdown()
        voiceInputManager = null
        voiceOutputManager = null
        _binding = null
    }
}
