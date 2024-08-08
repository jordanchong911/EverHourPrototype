package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
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
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.workspace_update_ol, null)
        dbRef = FirebaseDatabase.getInstance().reference

        val editText = dialogView.findViewById<TextInputEditText>(R.id.etWorkspaceName)
        editText.setText(currentName) // Set the current name in the EditText

        return AlertDialog.Builder(requireActivity())
            .setTitle("Update Workspace Name")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newName = editText.text.toString()
                if (newName.isNotBlank()) {
                    updateWorkspaceName(newName)
                } else {
                    Toast.makeText(requireContext(), "Workspace name cannot be empty.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun updateWorkspaceName(newName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        dbRef.child("workspaces").child(userId).child(workspaceId).child("name").setValue(newName)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Workspace name updated.", Toast.LENGTH_SHORT).show()
                // Notify the activity to refresh the workspace details if needed
                (targetFragment as? OnWorkspaceUpdatedListener)?.onWorkspaceUpdated()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to update workspace name: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Interface for notifying the activity
    interface OnWorkspaceUpdatedListener {
        fun onWorkspaceUpdated()
    }
}
