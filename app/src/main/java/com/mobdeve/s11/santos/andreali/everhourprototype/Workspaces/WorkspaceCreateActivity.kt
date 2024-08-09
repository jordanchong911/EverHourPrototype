package com.mobdeve.s11.santos.andreali.everhourprototype

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.WorkspaceCreateBinding

class WorkspaceCreateActivity : AppCompatActivity() {

    private lateinit var binding: WorkspaceCreateBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WorkspaceCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid ?: return

        binding.btnCreateWS.setOnClickListener {
            val workspaceName = binding.etvWorkspaceName.text.toString().trim()

            if (workspaceName.isNotEmpty()) {
                createWorkspace(userId, workspaceName)
            } else {
                Toast.makeText(this, "Please enter a workspace name.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createWorkspace(userId: String, workspaceName: String) {
        val workspaceId = dbRef.child("workspaces").child(userId).push().key
        val workspace = Workspace(
            id = workspaceId ?: "",
            name = workspaceName,
            hours = 0,  // Default or initial value
            projectsCount = 0  // Default or initial value
        )

        workspaceId?.let {
            dbRef.child("workspaces").child(userId).child(it).setValue(workspace)
                .addOnSuccessListener {
                    Toast.makeText(this, "Workspace created successfully!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)  // Set result to OK to notify that workspace was created
                    finish()  // Close the activity and return to the previous one
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to create workspace: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "Failed to generate workspace ID.", Toast.LENGTH_SHORT).show()
        }
    }
}
