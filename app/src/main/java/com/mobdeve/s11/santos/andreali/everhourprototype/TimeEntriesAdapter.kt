package com.mobdeve.s11.santos.andreali.everhourprototype

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.EntryCardBinding

class TimeEntriesAdapter(
    private var timeEntries: MutableList<TimeEntry>
) : RecyclerView.Adapter<TimeEntriesAdapter.TimeEntryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeEntryViewHolder {
        val binding = EntryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeEntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeEntryViewHolder, position: Int) {
        val timeEntry = timeEntries[position]
        holder.bind(timeEntry)
    }

    override fun getItemCount(): Int = timeEntries.size

    inner class TimeEntryViewHolder(private val binding: EntryCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(timeEntry: TimeEntry) {
            binding.tvEntryName.text = timeEntry.name
//            binding.tvEntryTime.text = timeEntry.time
            // Bind other fields as necessary
        }
    }

    fun updateTimeEntries(newTimeEntries: List<TimeEntry>) {
        timeEntries.clear()
        timeEntries.addAll(newTimeEntries)
        notifyDataSetChanged()
    }
}
