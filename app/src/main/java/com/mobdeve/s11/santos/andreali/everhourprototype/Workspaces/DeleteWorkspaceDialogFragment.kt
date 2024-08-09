package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DeleteWorkspaceDialogFragment(private val workspaceId: String) : DialogFragment() {

    interface OnWorkspaceDeletedListener {
        fun onWorkspaceDeleted()
    }

    private var listener: OnWorkspaceDeletedListener? = null

    fun setOnWorkspaceDeletedListener(listener: OnWorkspaceDeletedListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.workspace_delete_ol, null)

        val deleteButton: Button = view.findViewById(R.id.btnDelete)
        val cancelButton: Button = view.findViewById(R.id.btnCancel)

        deleteButton.setOnClickListener {
            deleteWorkspace()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }

    private fun deleteWorkspace() {
        // Reference to the specific workspace
        val workspaceRef = FirebaseDatabase.getInstance().reference.child("workspaces").child(workspaceId)

        Log.d("DeleteWorkspaceDialog", "Attempting to delete workspace with ID: $workspaceId")

        workspaceRef.removeValue()
            .addOnSuccessListener {
                Log.d("DeleteWorkspaceDialog", "Workspace and its child nodes deleted successfully")
                listener?.onWorkspaceDeleted() // Notify the listener
                dismiss() // Dismiss the dialog after successful deletion
            }
            .addOnFailureListener { exception ->
                Log.e("DeleteWorkspaceDialog", "Failed to delete workspace: ${exception.message}")
                // Optionally, show an error message to the user
                dismiss() // Dismiss the dialog even if deletion fails
            }
    }
}
