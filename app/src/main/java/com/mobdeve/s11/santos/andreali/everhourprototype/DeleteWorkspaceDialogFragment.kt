package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DeleteWorkspaceDialogFragment(private val workspaceId: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.workspace_delete_ol, null)

        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)

        val dialog = builder.create()

        btnDelete.setOnClickListener {
            deleteWorkspace()
            dialog.dismiss() // Close dialog after delete action
        }

        btnCancel.setOnClickListener {
            dialog.dismiss() // Close dialog without deleting
        }

        return dialog
    }

    private fun deleteWorkspace() {
        val activity = requireActivity() as WorkspaceActivity
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().reference.child("workspaces").child(userId).child(workspaceId)

        dbRef.removeValue().addOnSuccessListener {
            Toast.makeText(activity, "Workspace deleted.", Toast.LENGTH_SHORT).show()
            activity.fetchWorkspaces() // Refresh workspace list
        }.addOnFailureListener {
            Toast.makeText(activity, "Failed to delete workspace.", Toast.LENGTH_SHORT).show()
        }
    }
}
