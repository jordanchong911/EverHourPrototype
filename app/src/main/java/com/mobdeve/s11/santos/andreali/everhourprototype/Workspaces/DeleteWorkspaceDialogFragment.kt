package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
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
        return AlertDialog.Builder(requireContext())
            .setTitle("Delete Workspace")
            .setMessage("Are you sure you want to delete this workspace?")
            .setPositiveButton("Delete") { _, _ ->
                deleteWorkspace()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun deleteWorkspace() {
        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("workspaces").child(workspaceId).removeValue()
            .addOnSuccessListener {
                listener?.onWorkspaceDeleted()
            }
            .addOnFailureListener {
                // Handle failure
            }
    }
}
