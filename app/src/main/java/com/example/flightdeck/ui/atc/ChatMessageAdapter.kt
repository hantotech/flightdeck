package com.example.flightdeck.ui.atc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flightdeck.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying chat messages in ATC simulator
 */
class ChatMessageAdapter : ListAdapter<ChatMessage, ChatMessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pilotLayout: LinearLayout = itemView.findViewById(R.id.pilotMessageLayout)
        private val pilotText: TextView = itemView.findViewById(R.id.pilotMessageText)
        private val pilotTime: TextView = itemView.findViewById(R.id.pilotMessageTime)

        private val atcLayout: LinearLayout = itemView.findViewById(R.id.atcMessageLayout)
        private val atcText: TextView = itemView.findViewById(R.id.atcMessageText)
        private val atcTime: TextView = itemView.findViewById(R.id.atcMessageTime)

        private val systemLayout: LinearLayout = itemView.findViewById(R.id.systemMessageLayout)
        private val systemText: TextView = itemView.findViewById(R.id.systemMessageText)

        private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        fun bind(message: ChatMessage) {
            // Hide all layouts first
            pilotLayout.visibility = View.GONE
            atcLayout.visibility = View.GONE
            systemLayout.visibility = View.GONE

            // Show appropriate layout based on message type
            when (message.type) {
                MessageType.PILOT -> {
                    pilotLayout.visibility = View.VISIBLE
                    pilotText.text = message.text
                    pilotTime.text = timeFormat.format(Date(message.timestamp))
                }
                MessageType.ATC -> {
                    atcLayout.visibility = View.VISIBLE
                    atcText.text = message.text
                    atcTime.text = timeFormat.format(Date(message.timestamp))
                }
                MessageType.SYSTEM -> {
                    systemLayout.visibility = View.VISIBLE
                    systemText.text = message.text
                }
            }
        }
    }

    private class MessageDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
