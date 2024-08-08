package com.mobdeve.s11.santos.andreali.everhourprototype

import Workspace
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.s11.santos.andreali.everhourprototype.Workspaces.WorkspaceActivity
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
                val workspaceId = dbRef.child("workspaces").child(userId).push().key
                val workspace = Workspace(
                    id = workspaceId ?: "",
                    name = workspaceName,
                    hours = 0,  // Default or initial value
                    projectsCount = 0  // Default or initial value
                )

                dbRef.child("workspaces").child(userId).child(workspaceId!!).setValue(workspace)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Workspace created successfully!", Toast.LENGTH_SHORT).show()
                        finish()  // Close the activity and return to the previous one
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to create workspace.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please enter a workspace name.", Toast.LENGTH_SHORT).show()
            }
        }

        // Navbar Buttons
        binding.ivHome.setOnClickListener {
            val intent = Intent(this, WorkspaceActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.ivReport.setOnClickListener{
            //TODO: place report activity here
        }
        binding.ivAccount.setOnClickListener{
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
