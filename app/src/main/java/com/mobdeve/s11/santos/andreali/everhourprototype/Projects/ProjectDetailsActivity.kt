package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectDetailsBinding

class ProjectDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ProjectDetailsBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var projectId: String
    private lateinit var workspaceId: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProjectDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Log.e("ProjectDetailsActivity", "User ID not found")
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        projectId = intent.getStringExtra("PROJECT_ID") ?: ""
        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: ""

        if (projectId.isEmpty() || workspaceId.isEmpty()) {
            Toast.makeText(this, "Missing project or workspace ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchProjectDetails()

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

        binding.btnTimeEntries.setOnClickListener {
            val intent = Intent(this@ProjectDetailsActivity, TimeEntriesActivity::class.java).apply {
                putExtra("PROJECT_ID", projectId)
                putExtra("WORKSPACE_ID", workspaceId)
                putExtra("PROJECT_NAME", binding.tvProjectDetails.text.toString())
            }
            startActivity(intent)
            finish()
        }
    }

    private fun fetchProjectDetails() {
        dbRef.child("workspaces").child(workspaceId).child("projects").child(projectId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val project = snapshot.getValue(Project::class.java)
                if (project != null) {
                    displayProjectDetails(project)
                    fetchTimeEntryCount() // Fetch and display the time entry count
                    fetchTotalTrackedTime() // Fetch and display the total tracked time
                } else {
                    Toast.makeText(this@ProjectDetailsActivity, "Project not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProjectDetailsActivity, "Failed to load project details: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchTimeEntryCount() {
        dbRef.child("workspaces").child(workspaceId).child("projects").child(projectId).child("time_entries")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.childrenCount // Count the number of time entries
                    binding.tvCountNum.text = count.toString()
                    Log.d("ProjectDetailsActivity", "Number of time entries: $count")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ProjectDetailsActivity", "Failed to count time entries: ${error.message}")
                }
            })
    }

    private fun fetchTotalTrackedTime() {
        dbRef.child("workspaces").child(workspaceId).child("projects").child(projectId).child("time_entries")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalSeconds = 0L
                    for (entrySnapshot in snapshot.children) {
                        val timeElapsed = entrySnapshot.child("timeElapsed").getValue(String::class.java) ?: "00:00:00"
                        totalSeconds += timeToSeconds(timeElapsed)
                    }
                    val totalTime = secondsToTime(totalSeconds)
                    binding.tvTrackedNum.text = totalTime // Display the total time in tvTrackedNum
                    Log.d("ProjectDetailsActivity", "Total tracked time: $totalTime")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ProjectDetailsActivity", "Failed to calculate total tracked time: ${error.message}")
                }
            })
    }

    private fun timeToSeconds(time: String): Long {
        val parts = time.split(":").map { it.toLong() }
        return parts[0] * 3600 + parts[1] * 60 + parts[2]
    }

    private fun secondsToTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    private fun displayProjectDetails(project: Project) {
        binding.tvProjectDetails.text = project.name
        binding.tvClientName.text = project.client
        binding.tvRoleIC.text = project.roleIC
        // Other fields can be populated here
    }
}
