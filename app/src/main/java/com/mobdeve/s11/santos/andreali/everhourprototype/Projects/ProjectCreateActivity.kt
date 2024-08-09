package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mobdeve.s11.santos.andreali.everhourprototype.Projects.ProjectsActivity
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectCreateBinding

class ProjectCreateActivity : AppCompatActivity() {

    private lateinit var binding: ProjectCreateBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var workspaceId: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProjectCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase reference
        dbRef = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Log.e("ProjectCreateActivity", "User ID not found")
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: run {
            Log.e("ProjectCreateActivity", "Workspace ID not found in intent")
            Toast.makeText(this, "Workspace ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d("ProjectCreateActivity", "Received workspace ID: $workspaceId")

        binding.btnCreateProject.setOnClickListener {
            createProject()
        }

        // Navbar Buttons
        binding.ivHome.setOnClickListener {
            val intent = Intent(this, WorkspaceActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.ivReport.setOnClickListener {
            // TODO: place report activity here
        }
        binding.ivAccount.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun createProject() {
        val projectName = binding.etvProjectName.text.toString().trim()
        val clientName = binding.etvClientName.text.toString().trim()
        val roleIC = binding.etvRoleIC.text.toString().trim()

        if (projectName.isEmpty() || clientName.isEmpty() || roleIC.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        // Reference to the Firebase path
        val newProjectRef = dbRef.child("workspaces").child(workspaceId).child("projects").push()

        // Create a Project object
        val project = Project(
            name = projectName,
            client = clientName,
            roleIC = roleIC,
            projectHrs = 0,
            projectTasks = 0,
            workspaceId = workspaceId,
            projectID = newProjectRef.key ?: ""
        )

        // Save project to Firebase
        newProjectRef.setValue(project)
            .addOnSuccessListener {
                Toast.makeText(this, "Project created successfully.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProjectsActivity::class.java)
                intent.putExtra("WORKSPACE_ID", workspaceId)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to create project: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ProjectCreateActivity", "Error creating project", e)
            }
    }
}
