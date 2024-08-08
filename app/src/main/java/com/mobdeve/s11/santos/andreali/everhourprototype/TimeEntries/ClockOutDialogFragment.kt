package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.s11.santos.andreali.everhourprototype.R
import com.mobdeve.s11.santos.andreali.everhourprototype.TimeEntriesActivity

class ClockOutDialogFragment : DialogFragment() {

    private lateinit var database: DatabaseReference

    private var timeEntryId: String? = null
    private var elapsedTime: Long = 0
    private var projectId: String? = null
    private var workspaceId: String? = null
    private var entryName: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.entry_logout_ol, null)

        builder.setView(view)

        // Retrieve arguments
        timeEntryId = arguments?.getString("TIME_ENTRY_ID")
        elapsedTime = arguments?.getLong("ELAPSED_TIME", 0) ?: 0
        projectId = arguments?.getString("PROJECT_ID")
        workspaceId = arguments?.getString("WORKSPACE_ID")
        entryName = arguments?.getString("ENTRY_NAME")

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Initialize views
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        // Set button click listeners
        btnDelete.setOnClickListener {
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
        // Ensure that timeEntryId is not null
        timeEntryId?.let { id ->
            val timeEntryRef = database.child("time_entries").child(id)

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
        }
        startActivity(intent)
    }
}
