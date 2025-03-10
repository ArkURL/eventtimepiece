package com.arkurl.eventtimepiece.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arkurl.eventtimepiece.R
import com.arkurl.eventtimepiece.data.local.model.Event
import java.util.Locale

class EventAdapter(
    private val onItemClick: (Event) -> Unit,
    private val onAddEventListener: (Event) -> Unit,
    private val onEditEventListener: (Event) -> Unit,
    private val onDeleteEventListener: (Event) -> Unit
    ): ListAdapter<Event, EventAdapter.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val eventTitle: TextView = view.findViewById(R.id.event_title)
        val eventDescription: TextView = view.findViewById(R.id.event_description)
        val eventTime: TextView = view.findViewById(R.id.text_time_cost)

        val eventAddButton: Button = view.findViewById<Button>(R.id.button_add)
        val eventEditButton: Button = view.findViewById<Button>(R.id.button_edit)
        val eventDeleteButton: Button = view.findViewById<Button>(R.id.button_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = getItem(position)
        holder.eventTitle.text = event.name
        holder.eventDescription.text = event.description
        holder.eventTime.text = formatTimeDuration(event.timeCost)

        holder.itemView.setOnClickListener {
            onItemClick(event)
        }

        holder.eventAddButton.setOnClickListener {
            onAddEventListener(event)
        }

        holder.eventEditButton.setOnClickListener {
            onEditEventListener(event)
        }

        holder.eventDeleteButton.setOnClickListener {
            onDeleteEventListener(event)
        }
    }

    private fun formatTimeDuration(time: Long): String {
        val hours = time / 3600
        val minutes = (time % 3600) / 60
        val seconds = time % 60

        return when {
            hours > 0 -> String.format(Locale.getDefault(), "%02d h %02d m %02d s", hours, minutes, seconds)
            minutes > 0 -> String.format(Locale.getDefault(), "%02d m %02d s", minutes, seconds)
            else -> String.format(Locale.getDefault(), "%02d s", seconds)
        }
    }

    companion object {
        private var DIFF_CALLBACK = object : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem == newItem
            }
        }
    }
}