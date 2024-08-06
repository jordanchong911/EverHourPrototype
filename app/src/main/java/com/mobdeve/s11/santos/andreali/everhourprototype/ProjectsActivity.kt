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
        Log.d("ProjectsActivity", "Received workspace ID: $workspaceId") // Debug log

        setupRecyclerView()
        fetchProjects()
    }

    private fun setupRecyclerView() {
        binding.rvProjects.layoutManager = LinearLayoutManager(this)
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
                        // Log if any project data is null
                        Log.e("ProjectsActivity", "Null project data found")
                    }
                }

                if (projectsList.isEmpty()) {
                    // Navigate to ProjectCreateActivity if the list is empty
                    val intent = Intent(this@ProjectsActivity, ProjectCreateActivity::class.java)
                    intent.putExtra("WORKSPACE_ID", workspaceId) // Pass the workspace ID
                    startActivity(intent)
                    finish() // Optionally finish this activity
                } else {
                    // Set the adapter to the RecyclerView
                    binding.rvProjects.adapter = ProjectAdapter(projectsList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProjectsActivity, "Failed to load projects: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        fetchProjects() // Refresh projects when coming back to this activity
    }
}
