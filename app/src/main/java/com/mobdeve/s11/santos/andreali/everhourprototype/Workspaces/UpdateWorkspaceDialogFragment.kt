package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UpdateWorkspaceDialogFragment : DialogFragment() {

    interface OnWorkspaceUpdatedListener {
        fun onWorkspaceUpdated()
    }

    private var listener: OnWorkspaceUpdatedListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val workspaceId = arguments?.getString(ARG_WORKSPACE_ID) ?: ""
        val currentName = arguments?.getString(ARG_CURRENT_NAME) ?: ""

        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.workspace_update_ol, null)

        val nameEditText = view.findViewById<EditText>(R.id.etWorkspaceName)
        nameEditText.setText(currentName)

        val setButton = view.findViewById<Button>(R.id.btnSetWorkSpName)
        val cancelButton = view.findViewById<Button>(R.id.btnCancel)

        // Create a Dialog and set the custom view
        val dialog = Dialog(requireContext())
        dialog.setContentView(view)

        // Set button click listeners
        setButton.setOnClickListener {
            val newName = nameEditText.text.toString()
            updateWorkspaceName(workspaceId, newName)
            dialog.dismiss() // Close the dialog after saving
        }

        cancelButton.setOnClickListener {
            dialog.dismiss() // Close the dialog without making changes
        }

        return dialog
    }

    private fun updateWorkspaceName(workspaceId: String, newName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val workspaceRef = FirebaseDatabase.getInstance().reference
            .child("workspaces").child(workspaceId)

        workspaceRef.child("name").setValue(newName).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener?.onWorkspaceUpdated()
            } else {
                // Handle error
                listener?.onWorkspaceUpdated() // Notify even on failure
            }
        }
    }

    companion object {
        private const val ARG_WORKSPACE_ID = "workspace_id"
        private const val ARG_CURRENT_NAME = "current_name"

        @JvmStatic
        fun newInstance(workspaceId: String, currentName: String) =
            UpdateWorkspaceDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_WORKSPACE_ID, workspaceId)
                    putString(ARG_CURRENT_NAME, currentName)
                }
            }
    }

    fun setOnWorkspaceUpdatedListener(listener: OnWorkspaceUpdatedListener) {
        this.listener = listener
    }
}
