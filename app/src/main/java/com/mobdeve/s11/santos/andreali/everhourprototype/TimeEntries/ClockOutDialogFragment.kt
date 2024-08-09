package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ClockOutDialogFragment : DialogFragment() {

    private lateinit var database: DatabaseReference

    private lateinit var timeEntryId: String
    private var elapsedTime: Long = 0
    private lateinit var projectId: String
    private lateinit var workspaceId: String
    private lateinit var entryName: String
    private lateinit var projectName: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.entry_logout_ol, null)

        builder.setView(view)

        // Retrieve arguments inside onCreateDialog
        arguments?.let {
            timeEntryId = it.getString("TIME_ENTRY_ID").orEmpty()
            elapsedTime = it.getLong("ELAPSED_TIME", 0)
            projectId = it.getString("PROJECT_ID").orEmpty()
            workspaceId = it.getString("WORKSPACE_ID").orEmpty()
            entryName = it.getString("ENTRY_NAME").orEmpty()
            projectName = it.getString("PROJECT_NAME").orEmpty() // Retrieve projectName
        } ?: run {
            Toast.makeText(context, "Missing required arguments", Toast.LENGTH_SHORT).show()
            dismiss()
            return builder.create()
        }

        // Validate that the necessary information is present
        if (projectName.isEmpty()) {
            Toast.makeText(context, "Project Name missing", Toast.LENGTH_SHORT).show()
            dismiss()
            return builder.create()
        }

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Initialize views
        val btnDelete = view.findViewById<Button>(R.id.btnLogOut)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        // Set button click listeners
        btnDelete.setOnClickListener {
            // Additional validation if needed
            if (projectId.isEmpty() || workspaceId.isEmpty() || entryName.isEmpty()) {
                Toast.makeText(context, "Required data missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            (activity as EntryTimerActivity).stopTimer() // Stop timer in the activity
            updateTimeEntry() // Update the existing time entry
            dismiss()
            navigateToTimeEntriesActivity()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        return builder.create()
    }

    private fun updateTimeEntry() {
        // Ensure that timeEntryId is not null or empty
        if (timeEntryId.isNotEmpty()) {
            val timeEntryRef = database.child("workspaces").child(workspaceId)
                .child("projects").child(projectId).child("time_entries").child(timeEntryId)

            // Format elapsedTime to "hh:mm:ss"
            val hours = (elapsedTime / 1000) / 3600
            val minutes = (elapsedTime / 1000 % 3600) / 60
            val seconds = (elapsedTime / 1000 % 60)
            val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)

            // Update existing time entry
            timeEntryRef.child("timeElapsed").setValue(timeFormatted)
        }
    }

    private fun navigateToTimeEntriesActivity() {
        val intent = Intent(activity, TimeEntriesActivity::class.java).apply {
            putExtra("PROJECT_ID", projectId)
            putExtra("WORKSPACE_ID", workspaceId)
            putExtra("PROJECT_NAME", projectName)
        }
        startActivity(intent)
        Toast.makeText(context, "Clocked out successfully", Toast.LENGTH_SHORT).show()
    }
}