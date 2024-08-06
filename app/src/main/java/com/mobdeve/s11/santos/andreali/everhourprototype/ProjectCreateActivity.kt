package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectCreateBinding

class ProjectCreateActivity : AppCompatActivity() {

    private lateinit var binding: ProjectCreateBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var workplaceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProjectCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference
        workplaceId = intent.getStringExtra("WORKSPACE_ID") ?: run {
            Log.e("ProjectCreateActivity", "Workspace ID not found in intent")
            Toast.makeText(this, "Workspace ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        Log.d("ProjectCreateActivity", "Received workspace ID: $workplaceId") // Debug log

        binding.btnCreateProject.setOnClickListener {
            createProject()
        }

        binding.ivHome.setOnClickListener {
            // Navigate back to the previous activity
            finish() // This just finishes the current activity
        }
    }

    private fun createProject() {
        val projectName = binding.etvProjectName.text.toString().trim()
        val clientName = binding.etvClientName.text.toString().trim()
        val roleIC = binding.etvRoleIC.text.toString().trim()

        Log.d("ProjectCreateActivity", "Create Project clicked: $projectName, $clientName, $roleIC")

        if (projectName.isEmpty() || clientName.isEmpty() || roleIC.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val newProjectRef = dbRef.child("projects").child(workplaceId).push()
        val project = mapOf(
            "name" to projectName,
            "client" to clientName,
            "roleIC" to roleIC
        )

        newProjectRef.setValue(project)
            .addOnSuccessListener {
                Log.d("ProjectCreateActivity", "Project created successfully.")
                Toast.makeText(this, "Project created successfully.", Toast.LENGTH_SHORT).show()

                // Redirect to ProjectsActivity after creating the project
                val intent = Intent(this, ProjectsActivity::class.java)
                intent.putExtra("WORKSPACE_ID", workplaceId) // Pass the workspace ID
                startActivity(intent)
                finish() // Optionally finish this activity
            }
            .addOnFailureListener { e ->
                Log.e("ProjectCreateActivity", "Failed to create project: ${e.message}")
                Toast.makeText(this, "Failed to create project: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
