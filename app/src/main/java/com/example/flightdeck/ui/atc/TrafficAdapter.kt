package com.example.flightdeck.ui.atc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flightdeck.R
import com.example.flightdeck.data.model.SimulatedTraffic
import com.example.flightdeck.data.model.TrafficPosition

/**
 * Adapter for displaying active traffic during ATC practice
 */
class TrafficAdapter : ListAdapter<SimulatedTraffic, TrafficAdapter.TrafficViewHolder>(TrafficDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrafficViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_traffic, parent, false)
        return TrafficViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrafficViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TrafficViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val callsignText: TextView = itemView.findViewById(R.id.trafficCallsignText)
        private val positionText: TextView = itemView.findViewById(R.id.trafficPositionText)

        fun bind(traffic: SimulatedTraffic) {
            callsignText.text = traffic.aircraftCallsign
            positionText.text = formatPosition(traffic.currentPosition)
        }

        private fun formatPosition(position: TrafficPosition): String {
            return when (position) {
                TrafficPosition.ON_GROUND_RAMP -> "at ramp"
                TrafficPosition.ON_GROUND_TAXIWAY -> "taxiing"
                TrafficPosition.ON_GROUND_RUNWAY_HOLDING -> "holding short"
                TrafficPosition.ON_GROUND_RUNWAY_ACTIVE -> "on runway"
                TrafficPosition.DEPARTING -> "departing"
                TrafficPosition.IN_PATTERN_DOWNWIND -> "downwind"
                TrafficPosition.IN_PATTERN_BASE -> "base"
                TrafficPosition.IN_PATTERN_FINAL -> "on final"
                TrafficPosition.APPROACHING -> "inbound"
                TrafficPosition.OVERFLYING -> "overflying"
                TrafficPosition.LANDED_ROLLOUT -> "clearing runway"
            }
        }
    }

    private class TrafficDiffCallback : DiffUtil.ItemCallback<SimulatedTraffic>() {
        override fun areItemsTheSame(oldItem: SimulatedTraffic, newItem: SimulatedTraffic): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SimulatedTraffic, newItem: SimulatedTraffic): Boolean {
            return oldItem == newItem
        }
    }
}
