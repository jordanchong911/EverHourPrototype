package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectDeleteOlBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DeleteProjectDialogFragment(
    private val workspaceId: String,
    private val projectId: String,
    private val onProjectDeletedListener: () -> Unit // Use a callback function
) : DialogFragment() {

    private lateinit var binding: ProjectDeleteOlBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = ProjectDeleteOlBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(requireContext()).apply {
            setContentView(binding.root)
        }

        // Handle button clicks
        binding.btnDelete.setOnClickListener {
            deleteProject()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        return dialog
    }

    private fun deleteProject() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("workspaces").child(workspaceId).child("projects").child(projectId).removeValue()
            .addOnSuccessListener {
                // Notify that the project has been deleted
                onProjectDeletedListener()
                dismiss()
            }
            .addOnFailureListener {
                // Handle failure, possibly show a message to the user
                Toast.makeText(requireContext(), "Failed to delete project. Please try again.", Toast.LENGTH_SHORT).show()
            }
    }
}
