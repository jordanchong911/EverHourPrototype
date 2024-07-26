package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

        val editText = dialogView.findViewById<TextInputEditText>(R.id.editWorkspace)
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
        if (isAdded && context != null) { // Ensure the fragment is added and context is not null
            dbRef.child("workspaces").child(FirebaseAuth.getInstance().currentUser?.uid ?: "").child(workspaceId)
                .child("name").setValue(newName)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Workspace name updated.", Toast.LENGTH_SHORT).show()
                    // Notify the activity to refresh the workspace details
                    (requireActivity() as? WorkspaceDetailsActivity)?.fetchWorkspaceDetails(workspaceId)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to update workspace name: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Fragment or context not available. Unable to update workspace name.", Toast.LENGTH_SHORT).show()
        }
    }
}
