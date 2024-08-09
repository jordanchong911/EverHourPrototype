package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdateWorkspaceDialogFragment(
    private val workspaceId: String,
    private val currentName: String
) : DialogFragment() {

    private lateinit var dbRef: DatabaseReference

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflate the custom dialog view
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.workspace_update_ol, null)
        dbRef = FirebaseDatabase.getInstance().reference

        // Initialize the EditText and set the current workspace name
        val editText = dialogView.findViewById<TextInputEditText>(R.id.etWorkspaceName)
        editText.setText(currentName)

        // Set up the "Set" button to update the workspace name
        val btnSetWorkSpName = dialogView.findViewById<Button>(R.id.btnSetWorkSpName)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        btnSetWorkSpName.setOnClickListener {
            val newName = editText.text.toString()
            if (newName.isNotBlank()) {
                updateWorkspaceName(newName)
                dismiss() // Close the dialog after updating
            } else {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Workspace name cannot be empty.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCancel.setOnClickListener {
            dismiss() // Close the dialog without making any changes
        }

        // Build the dialog using AlertDialog.Builder
        return AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .create()
    }

    private fun updateWorkspaceName(newName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        dbRef.child("workspaces").child(userId).child(workspaceId).child("name").setValue(newName)
            .addOnSuccessListener {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Workspace name updated.", Toast.LENGTH_SHORT).show()
                    // Notify the activity to refresh the workspace details if needed
                    (targetFragment as? OnWorkspaceUpdatedListener)?.onWorkspaceUpdated()
                }
            }
            .addOnFailureListener { e ->
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to update workspace name: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Interface for notifying the activity
    interface OnWorkspaceUpdatedListener {
        fun onWorkspaceUpdated()
    }
}
