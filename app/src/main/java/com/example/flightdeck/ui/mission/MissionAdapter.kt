package com.example.flightdeck.ui.mission

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flightdeck.R
import com.example.flightdeck.data.model.MissionConfig
import com.example.flightdeck.data.model.getChallengesList
import com.google.android.material.card.MaterialCardView

/**
 * Adapter for displaying mission cards in RecyclerView
 */
class MissionAdapter(
    private val onMissionClick: (MissionConfig) -> Unit
) : ListAdapter<MissionConfig, MissionAdapter.MissionViewHolder>(MissionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mission_card, parent, false)
        return MissionViewHolder(view, onMissionClick)
    }

    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MissionViewHolder(
        itemView: View,
        private val onMissionClick: (MissionConfig) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val missionCard: MaterialCardView = itemView.findViewById(R.id.missionCard)
        private val missionName: TextView = itemView.findViewById(R.id.missionName)
        private val difficultyStars: TextView = itemView.findViewById(R.id.difficultyStars)
        private val difficultyLabel: TextView = itemView.findViewById(R.id.difficultyLabel)
        private val durationText: TextView = itemView.findViewById(R.id.durationText)
        private val timesFlownText: TextView = itemView.findViewById(R.id.timesFlownText)
        private val missionParams: TextView = itemView.findViewById(R.id.missionParams)
        private val challengesText: TextView = itemView.findViewById(R.id.challengesText)

        fun bind(mission: MissionConfig) {
            // Mission name
            missionName.text = mission.name

            // Difficulty stars
            val stars = "⭐".repeat(mission.estimatedDifficultyStars)
            difficultyStars.text = stars

            // Difficulty label
            difficultyLabel.text = mission.baseDifficulty.name

            // Duration
            durationText.text = "⏱ ${mission.estimatedDurationMinutes} min"

            // Times flown (show only if flown before)
            if (mission.timesFlown > 0) {
                timesFlownText.visibility = View.VISIBLE
                timesFlownText.text = "✓ Flown ${mission.timesFlown}x"
            } else {
                timesFlownText.visibility = View.GONE
            }

            // Mission parameters
            val params = buildString {
                append(mission.trafficDensity.displayName.lowercase().replaceFirstChar { it.uppercase() })
                append(" traffic • ")
                append(mission.weatherComplexity.displayName)
                append(" • ")
                append(mission.communicationPace.displayName)
                append(" pace")
            }
            missionParams.text = params

            // Challenges (if any)
            val challenges = mission.getChallengesList()
            if (challenges.isNotEmpty()) {
                challengesText.visibility = View.VISIBLE
                val challengeText = challenges.take(2).joinToString(" • ") {
                    "${it.category.icon} ${it.displayName}"
                }
                val moreText = if (challenges.size > 2) " +${challenges.size - 2} more" else ""
                challengesText.text = challengeText + moreText
            } else {
                challengesText.visibility = View.GONE
            }

            // Click listener
            missionCard.setOnClickListener {
                onMissionClick(mission)
            }
        }
    }

    class MissionDiffCallback : DiffUtil.ItemCallback<MissionConfig>() {
        override fun areItemsTheSame(oldItem: MissionConfig, newItem: MissionConfig): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MissionConfig, newItem: MissionConfig): Boolean {
            return oldItem == newItem
        }
    }
}
