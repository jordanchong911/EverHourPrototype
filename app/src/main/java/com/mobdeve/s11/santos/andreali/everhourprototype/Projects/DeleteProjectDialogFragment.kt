package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DeleteProjectDialogFragment(
    private val workspaceId: String, // Add workspaceId parameter
    private val projectId: String
) : DialogFragment() {

    interface OnProjectDeletedListener {
        fun onProjectDeleted()
    }

    private var listener: OnProjectDeletedListener? = null

    fun setOnProjectDeletedListener(listener: OnProjectDeletedListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Delete Project")
            .setMessage("Are you sure you want to delete this project?")
            .setPositiveButton("Delete") { _, _ ->
                deleteProject()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun deleteProject() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            // Handle case where userId is not available
            return
        }

        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("workspaces").child(userId).child(workspaceId).child("projects").child(projectId).removeValue()
            .addOnSuccessListener {
                listener?.onProjectDeleted()
            }
            .addOnFailureListener {
                // Handle failure
            }
    }
}
