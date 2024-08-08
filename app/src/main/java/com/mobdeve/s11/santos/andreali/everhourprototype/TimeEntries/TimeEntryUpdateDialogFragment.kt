package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.EntryUpdateOlBinding

class TimeEntryUpdateDialogFragment(
    private val timeEntry: TimeEntry,
    private val onUpdateSuccess: (TimeEntry) -> Unit
) : DialogFragment() {

    private var _binding: EntryUpdateOlBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EntryUpdateOlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Prepopulate the fields with the current time entry data
        binding.tilName.editText?.setText(timeEntry.name)
        binding.tilClient.editText?.setText(timeEntry.personInCharge)
        binding.tilTeam.editText?.setText(timeEntry.rate.toString())

        binding.btnSetWorkSpName.setOnClickListener {
            val updatedName = binding.tilName.editText?.text.toString().trim()
            val updatedPersonInCharge = binding.tilClient.editText?.text.toString().trim()
            val updatedRate = binding.tilTeam.editText?.text.toString().trim().toIntOrNull() ?: 0

            if (updatedName.isNotEmpty() && updatedPersonInCharge.isNotEmpty() && updatedRate >= 0) {
                // Update the timeEntry object
                val updatedTimeEntry = timeEntry.copy(
                    name = updatedName,
                    personInCharge = updatedPersonInCharge,
                    rate = updatedRate
                )

                // Update in Firebase
                val databaseReference = FirebaseDatabase.getInstance().getReference("time_entries")
                    .child(timeEntry.projectID).child(timeEntry.timeEntryID)

                databaseReference.setValue(updatedTimeEntry).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Time entry updated successfully", Toast.LENGTH_SHORT).show()
                        onUpdateSuccess(updatedTimeEntry)
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update time entry", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
