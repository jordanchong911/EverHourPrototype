package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectOverviewBinding

class ProjectsActivity : AppCompatActivity() {

    private lateinit var binding: ProjectOverviewBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var workspaceId: String
    private lateinit var projectAdapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProjectOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference
        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: run {
            Log.e("ProjectsActivity", "Workspace ID not found in intent")
            Toast.makeText(this, "Workspace ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        Log.d("ProjectsActivity", "Received workspace ID: $workspaceId")

        setupRecyclerView()
        fetchProjects()

        binding.btnCreateProject.setOnClickListener {
            val intent = Intent(this, ProjectCreateActivity::class.java)
            intent.putExtra("WORKSPACE_ID", workspaceId)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        projectAdapter = ProjectAdapter(mutableListOf(), supportFragmentManager, workspaceId, this) // Pass context here
        binding.rvProjects.layoutManager = LinearLayoutManager(this)
        binding.rvProjects.adapter = projectAdapter
    }

    private fun fetchProjects() {
        dbRef.child("projects").child(workspaceId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val projectsList = mutableListOf<Project>()
                for (projectSnapshot in snapshot.children) {
                    val project = projectSnapshot.getValue(Project::class.java)
                    if (project != null) {
                        projectsList.add(project)
                    } else {
                        Log.e("ProjectsActivity", "Null project data found or data type mismatch")
                    }
                }

                if (projectsList.isEmpty()) {
                    val intent = Intent(this@ProjectsActivity, ProjectCreateActivity::class.java)
                    intent.putExtra("WORKSPACE_ID", workspaceId)
                    startActivity(intent)
                    finish()
                } else {
                    projectAdapter.updateProjects(projectsList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProjectsActivity, "Failed to load projects: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProject(project: Project, name: String, client: String, roleIC: String) {
        val updatedProject = project.copy(name = name, client = client, roleIC = roleIC)

        dbRef.child("projects").child(workspaceId).child(project.projectID).setValue(updatedProject)
            .addOnSuccessListener {
                Toast.makeText(this, "Project updated successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update project.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteProject(projectID: String) {
        dbRef.child("projects").child(workspaceId).child(projectID).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Project deleted successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete project.", Toast.LENGTH_SHORT).show()
            }
    }
}