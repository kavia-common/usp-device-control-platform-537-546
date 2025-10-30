package org.example.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.util.LogEntry

/**
 PUBLIC_INTERFACE
 Adapter for displaying log entries.
 */
class LogsAdapter : ListAdapter<LogEntry, LogsAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<LogEntry>() {
        override fun areItemsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean =
            oldItem == newItem
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.log_time)
        val level: TextView = itemView.findViewById(R.id.log_level)
        val message: TextView = itemView.findViewById(R.id.log_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log_entry, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.time.text = item.time
        holder.level.text = item.level
        holder.message.text = item.message
    }
}
