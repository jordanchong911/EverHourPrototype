package com.mobdeve.s11.santos.andreali.everhourprototype

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectDetailsBinding

class ProjectDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ProjectDetailsBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var projectId: String
    private lateinit var workspaceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProjectDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference
        projectId = intent.getStringExtra("PROJECT_ID") ?: ""
        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: ""

        if (projectId.isEmpty() || workspaceId.isEmpty()) {
            Toast.makeText(this, "Missing project or workspace ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchProjectDetails()

        binding.ivHome.setOnClickListener {
            // Navigate back to main activity
            finish() // Adjust this based on your navigation requirements
        }
    }

    private fun fetchProjectDetails() {
        dbRef.child("projects").child(workspaceId).child(projectId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val project = snapshot.getValue(Project::class.java)
                if (project != null) {
                    displayProjectDetails(project)
                } else {
                    Toast.makeText(this@ProjectDetailsActivity, "Project not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProjectDetailsActivity, "Failed to load project details: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayProjectDetails(project: Project) {
        binding.tvProjectDetails.text = project.name
        binding.tvClientName.text = project.client
        binding.tvRoleIC.text = project.roleIC
        // Populate other fields with project details
    }
}
