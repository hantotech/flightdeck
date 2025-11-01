package com.example.flightdeck.ui.atc

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flightdeck.R
import com.example.flightdeck.databinding.FragmentAtcBinding
import com.example.flightdeck.data.model.ATCScenario
import com.example.flightdeck.utils.VoiceInputManager
import com.example.flightdeck.utils.VoiceOutputManager
import com.example.flightdeck.utils.VoiceResult
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ATC Simulator Fragment
 * Provides AI-powered ATC practice with voice-based radio communications
 */
class ATCFragment : Fragment() {

    private var _binding: FragmentAtcBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ATCViewModel by viewModels()
    private lateinit var chatAdapter: ChatMessageAdapter

    // Voice managers
    private var voiceInputManager: VoiceInputManager? = null
    private var voiceOutputManager: VoiceOutputManager? = null
    private var isVoiceReady = false
    private var isSessionActive = false
    private var useVoiceInput = true

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
        checkMicrophonePermission()
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
                viewModel.sendPilotMessage(message)
                binding.messageInput.text?.clear()
            }
        }
    }

    private fun startPracticeSession(departure: String, arrival: String) {
        isSessionActive = true

        // Hide flight entry, show session info
        binding.flightEntryCard.visibility = View.GONE
        binding.sessionInfoCard.visibility = View.VISIBLE

        // Update session info
        binding.sessionTitle.text = "$departure → $arrival"
        binding.sessionInfo.text = "Session starting..."

        // TODO: Call ViewModel to initialize practice session with AI
        // For now, just show a welcome message
        Toast.makeText(requireContext(), "Practice session started: $departure to $arrival", Toast.LENGTH_SHORT).show()

        // Send initial ATC greeting (simulated)
        val greeting = "$departure Ground, frequency 121.9 active."
        voiceOutputManager?.speak(greeting)
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
            // Show voice controls
            binding.voiceControlsCard.visibility = View.VISIBLE
            binding.textInputCard.visibility = View.GONE
            binding.toggleInputButton.text = "Use Text Input"
        } else {
            // Show text input
            binding.voiceControlsCard.visibility = View.GONE
            binding.textInputCard.visibility = View.VISIBLE
            binding.toggleInputButton.text = "Use Voice Input"
            // Move toggle button to text input card
            binding.toggleInputButton.visibility = View.GONE // Temporarily hide, would need to restructure layout
        }
    }

    private fun observeViewModel() {
        // Messages
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages) {
                // Scroll to bottom when new message added
                if (messages.isNotEmpty()) {
                    binding.messagesRecyclerView.smoothScrollToPosition(messages.size - 1)

                    // Speak ATC responses (messages from ATC, not pilot)
                    val lastMessage = messages.last()
                    if (lastMessage.type == com.example.flightdeck.ui.atc.MessageType.ATC && isVoiceReady) {
                        voiceOutputManager?.speak(lastMessage.text)
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

        // Errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
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
