package com.mobdeve.s11.santos.andreali.everhourprototype

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.EntryDeleteOlBinding

class TimeEntryDeleteDialogFragment(
    private val timeEntry: TimeEntry,
    private val onDeleteSuccess: () -> Unit
) : DialogFragment() {

    private var _binding: EntryDeleteOlBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EntryDeleteOlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDelete.setOnClickListener {
            deleteTimeEntry()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun deleteTimeEntry() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("time_entries")
            .child(timeEntry.projectID).child(timeEntry.timeEntryID)

        databaseReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Time entry deleted successfully", Toast.LENGTH_SHORT).show()
                onDeleteSuccess()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Failed to delete time entry", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
