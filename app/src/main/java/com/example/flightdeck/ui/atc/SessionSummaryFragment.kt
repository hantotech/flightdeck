package com.example.flightdeck.ui.atc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flightdeck.databinding.FragmentSessionSummaryBinding
import com.example.flightdeck.data.model.SessionSummary
import java.text.SimpleDateFormat
import java.util.*

/**
 * Shows summary and feedback for a completed ATC practice session
 */
class SessionSummaryFragment : Fragment() {

    private var _binding: FragmentSessionSummaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var transcriptAdapter: ChatMessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()

        // Get session summary from arguments (passed from ATCFragment)
        val sessionSummary = arguments?.getSerializable("sessionSummary") as? SessionSummary
        if (sessionSummary != null) {
            displaySummary(sessionSummary)
        }
    }

    private fun setupRecyclerView() {
        transcriptAdapter = ChatMessageAdapter()
        binding.transcriptRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transcriptAdapter
        }
    }

    private fun setupClickListeners() {
        binding.practiceAgainButton.setOnClickListener {
            // Navigate back to practice screen and start new session
            findNavController().popBackStack()
        }

        binding.backToHomeButton.setOnClickListener {
            // Navigate to home screen
            findNavController().popBackStack()
        }
    }

    private fun displaySummary(summary: SessionSummary) {
        // Session info
        binding.sessionTitleText.text = summary.sessionTitle

        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val date = dateFormat.format(Date(summary.endTime))
        binding.sessionDateText.text = "$date • ${summary.durationMinutes} minutes"

        // Score
        binding.scoreText.text = "${summary.overallScore.toInt()}%"
        binding.scoreDescriptionText.text = summary.scoreDescription

        // Strengths
        val strengthsText = summary.strengths.joinToString("\n") { "• $it" }
        binding.strengthsText.text = strengthsText

        // Improvements
        val improvementsText = summary.improvements.joinToString("\n") { "• $it" }
        binding.improvementsText.text = improvementsText

        // Transcript
        transcriptAdapter.submitList(summary.transcript)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
