package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import android.util.Log
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("DeleteWorkspaceDialog", "User is not authenticated")
            return
        }

        Log.d("DeleteWorkspaceDialog", "Attempting to delete workspace with ID: $workspaceId")

        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("workspaces").child(userId).child(workspaceId).removeValue()
            .addOnSuccessListener {
                Log.d("DeleteWorkspaceDialog", "Workspace deleted successfully")
                if (isAdded) {
                    listener?.onWorkspaceDeleted()
                    dismiss()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DeleteWorkspaceDialog", "Failed to delete workspace: ${exception.message}")
                if (isAdded) {
                    dismiss()
                }
            }
    }
}
