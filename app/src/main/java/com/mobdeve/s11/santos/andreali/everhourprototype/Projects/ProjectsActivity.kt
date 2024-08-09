package com.mobdeve.s11.santos.andreali.everhourprototype.Projects

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.AccountActivity
import com.mobdeve.s11.santos.andreali.everhourprototype.Project
import com.mobdeve.s11.santos.andreali.everhourprototype.ProjectAdapter
import com.mobdeve.s11.santos.andreali.everhourprototype.ProjectCreateActivity
import com.mobdeve.s11.santos.andreali.everhourprototype.WorkspaceActivity
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectOverviewBinding

class ProjectsActivity : AppCompatActivity() {

    private lateinit var binding: ProjectOverviewBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var workspaceId: String
    private lateinit var workspaceName: String
    private lateinit var projectAdapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProjectOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference

        // Get workspace ID and name from the intent
        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: run {
            Log.e("ProjectsActivity", "Workspace ID not found in intent")
            Toast.makeText(this, "Workspace ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        workspaceName = intent.getStringExtra("WORKSPACE_NAME") ?: run {
            Log.e("ProjectsActivity", "Workspace name not found in intent")
            Toast.makeText(this, "Workspace name missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Log.d("ProjectsActivity", "Received workspace ID: $workspaceId")
        Log.d("ProjectsActivity", "Received workspace name: $workspaceName")

        binding.tvProjectOv.text = workspaceName

        setupRecyclerView()
        fetchProjects()

        binding.btnCreateProject.setOnClickListener {
            val intent = Intent(this, ProjectCreateActivity::class.java)
            intent.putExtra("WORKSPACE_ID", workspaceId)
            startActivity(intent)
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

    private fun setupRecyclerView() {
        projectAdapter = ProjectAdapter(mutableListOf(), supportFragmentManager, workspaceId, this)
        binding.rvProjects.layoutManager = LinearLayoutManager(this)
        binding.rvProjects.adapter = projectAdapter
    }

    fun fetchProjects() {
        // Corrected path to access projects under the workspaceId directly
        dbRef.child("workspaces").child(workspaceId).child("projects").addValueEventListener(object : ValueEventListener {
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

                // Update the adapter with the fetched projects
                projectAdapter.updateProjects(projectsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProjectsActivity, "Failed to load projects: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateProject(project: Project, name: String, client: String, roleIC: String) {
        val updatedProject = project.copy(name = name, client = client, roleIC = roleIC)

        // Corrected path to update project directly under the workspaceId
        dbRef.child("workspaces").child(workspaceId).child("projects").child(project.projectID).setValue(updatedProject)
            .addOnSuccessListener {
                Toast.makeText(this, "Project updated successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update project.", Toast.LENGTH_SHORT).show()
            }
    }

    fun deleteProject(projectID: String) {
        // Corrected path to delete project directly under the workspaceId
        dbRef.child("workspaces").child(workspaceId).child("projects").child(projectID).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Project deleted successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete project.", Toast.LENGTH_SHORT).show()
            }
    }
}

