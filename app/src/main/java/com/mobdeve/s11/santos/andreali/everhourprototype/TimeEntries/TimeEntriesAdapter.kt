package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.EntryCardBinding

class TimeEntriesAdapter(
    private val timeEntries: MutableList<TimeEntry>,
    private val fragmentManager: FragmentManager,
    private val projectId: String,
    private val projectName: String,
    private val workspaceId: String,
    private val context: Context
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
            binding.tvTime.text = timeEntry.timeElapsed
            binding.tvRate.text = timeEntry.rate.toString()

            binding.ivDots.setOnClickListener {
                showOptionsDialog(timeEntry)
            }

            binding.ibRecord.setOnClickListener {
                navigateToEntryTimerActivity(timeEntry)
            }
        }

        private fun showOptionsDialog(timeEntry: TimeEntry) {
            val optionsDialog = AlertDialog.Builder(binding.root.context)
                .setItems(arrayOf("Update", "Delete")) { _, which ->
                    when (which) {
                        0 -> showUpdateDialog(timeEntry)
                        1 -> showDeleteDialog(timeEntry)
                    }
                }
                .create()
            optionsDialog.show()
        }

        private fun showUpdateDialog(timeEntry: TimeEntry) {
            val updateDialog = TimeEntryUpdateDialogFragment(timeEntry) { updatedTimeEntry ->
                updateTimeEntryInFirebase(updatedTimeEntry)
            }
            updateDialog.show(fragmentManager, "TimeEntryUpdateDialog")
        }

        private fun showDeleteDialog(timeEntry: TimeEntry) {
            val deleteDialog = TimeEntryDeleteDialogFragment(timeEntry) {
                deleteTimeEntryFromFirebase(timeEntry.timeEntryID)
            }
            deleteDialog.show(fragmentManager, "TimeEntryDeleteDialog")
        }

        private fun navigateToEntryTimerActivity(timeEntry: TimeEntry) {
            val intent = Intent(context, EntryTimerActivity::class.java).apply {
                putExtra("TIME_ENTRY_ID", timeEntry.timeEntryID)
                putExtra("PROJECT_ID", projectId)
                putExtra("WORKSPACE_ID", workspaceId)
                putExtra("PROJECT_NAME", projectName)
                putExtra("ENTRY_NAME", timeEntry.name)
                putExtra("TIME_ELAPSED", timeEntry.timeElapsed)
            }
            context.startActivity(intent)
        }

        private fun updateTimeEntryInFirebase(timeEntry: TimeEntry) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("workspaces")
                .child(workspaceId).child("projects").child(projectId)
                .child("time_entries").child(timeEntry.timeEntryID)
            databaseReference.setValue(timeEntry).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context, "Time entry updated successfully", Toast.LENGTH_SHORT).show()
                    notifyItemChanged(adapterPosition)
                } else {
                    Toast.makeText(binding.root.context, "Failed to update time entry", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun deleteTimeEntryFromFirebase(timeEntryID: String) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("workspaces")
                .child(workspaceId).child("projects").child(projectId)
                .child("time_entries").child(timeEntryID)
            databaseReference.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(binding.root.context, "Time entry deleted successfully", Toast.LENGTH_SHORT).show()
                    timeEntries.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                } else {
                    Toast.makeText(binding.root.context, "Failed to delete time entry", Toast.LENGTH_SHORT).show()
                    Log.e("DeleteTimeEntry", "Delete failed: ${task.exception?.message}")
                }
            }
        }
    }

    fun updateTimeEntries(newTimeEntries: List<TimeEntry>) {
        timeEntries.clear()
        timeEntries.addAll(newTimeEntries)
        notifyDataSetChanged()
    }
}
